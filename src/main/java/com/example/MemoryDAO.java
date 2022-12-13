package com.example;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MemoryDAO implements DAO {
	// Can omit generic type in right-hand operand (diamond operator)
	private ArrayList<ToDo> todos = new ArrayList<>() {
		// Use instance initializer of anonymouse class
		{
			add(new ToDo(1, "Design", "2022-12-01", true));
			add(new ToDo(2, "Implementation", "2022-12-07", false));
		}
	};
	// If you use MemoryDAO's instance initializer,
	// you need to write "storage." before "add" each time, which is a bit long.
	//	{
	//		storage.add(new ToDo(1, "", LocalDate.of(2022, 12, 1), false));
	//	}

	public MemoryDAO(String dataPath) {
		try {
			load(dataPath);
		} catch (IOException e) {
			System.out.println("Cannot find data file: " + dataPath);
		}
	}

	@Override
	public Optional<ToDo> get(int id) {
		Optional<ToDo> targetTodo = todos.stream().filter(todo -> todo.getId() == id).findFirst();
		return targetTodo;
	}

	@Override
	public ArrayList<ToDo> getAll() {
		return todos;
	}

	@Override
	public ToDo create(String title, String date) {
		int newId = todos.stream().max((todo1, todo2) -> todo1.getId() - todo2.getId()).get().getId() + 1;
		var newToDo = new ToDo(newId, title, date, false);
		todos.add(newToDo);

		// For checking current todos 
		System.out.println(toJson(true));

		return newToDo;
	}

	@Override
	public Optional<ToDo> updateTitle(int id, String title) {
		Optional<ToDo> targetTodo = todos.stream().filter(todo -> todo.getId() == id).findFirst();
		if (targetTodo.isPresent())
			targetTodo.get().setTitle(title);

		// For checking current todos 
		System.out.println(toJson(true));

		return targetTodo;
	}

	@Override
	public Optional<ToDo> updateDate(int id, String date) {
		Optional<ToDo> targetTodo = todos.stream().filter(todo -> todo.getId() == id).findFirst();
		if (targetTodo.isPresent())
			targetTodo.get().setDate(date);

		// For checking current todos 
		System.out.println(toJson(true));

		return targetTodo;
	}

	@Override
	public Optional<ToDo> updateCompleted(int id, boolean completed) {
		Optional<ToDo> targetTodo = todos.stream().filter(todo -> todo.getId() == id).findFirst();
		if (targetTodo.isPresent())
			targetTodo.get().setCompleted(completed);

		// For checking current todos 
		System.out.println(toJson(true));

		return targetTodo;
	}

	@Override
	public Optional<Integer> delete(int id) {
		boolean success = todos.removeIf(todo -> todo.getId() == id);

		// For checking current todos 
		System.out.println(toJson(true));

		return success ? Optional.of(id) : Optional.empty();
	}

	public String toJson() {
		return toJson(false);
	}

	/**
	 * @param isPretty use more human-readable pretty printing
	 * @return JSON string
	 */
	public String toJson(boolean isPretty) {
		Gson gson;
		if (isPretty) {
			gson = FxGson.fullBuilder()
					.setPrettyPrinting()
					.create();
		} else {
			gson = FxGson.create();
		}
		// Gson must be given a generic type as an argument 
		// if a target is a generic type object
		// 
		// Java API's getClass() cannot return generic types.
		// com.google.gson.reflect.TypeToken<T> and getType()
		// provides a method to get generic types 
		// like as ArrayList<XXX>
		// 
		// Add "opens your.package.name to com.google.gson" in module-info.java
		// because this uses reflection. 
		Type todosType = new TypeToken<ArrayList<ToDo>>() {
		}.getType();
		String json = gson.toJson(todos, todosType);
		return json;
	}

	public ArrayList<ToDo> fromJson(String json) {
		try {
			Type todosType = new TypeToken<ArrayList<ToDo>>() {
			}.getType();
			return FxGson.create().fromJson(json, todosType);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ToDo>();
		}

	}

	public void save(String path) throws IOException {
		Files.writeString(Paths.get(path), toJson());
	}

	public void load(String path) throws IOException {
		String json = Files.readString(Paths.get(path));
		todos = fromJson(json);
	}
}
