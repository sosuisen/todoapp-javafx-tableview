package com.example;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MemoryDAO implements DAO {
	// Can omit generic type in right-hand operand (diamond operator)
	private ArrayList<ToDo> todos = new ArrayList<>() {
		// Use instance initializer of anonymouse class
		{
			add(new ToDo(1, "Design", LocalDate.parse("2022-12-01"), true));
			add(new ToDo(2, "Implementation", LocalDate.parse("2022-12-07"), false));
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
	public ToDo create(String title, LocalDate date) {
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
	public Optional<ToDo> updateDate(int id, LocalDate date) {
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

	/*
	 * LocalDate type adapter for gson
	 */
	private class LocalDateAdapter extends TypeAdapter<LocalDate> {
		@Override
		public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
			jsonWriter.value(localDate.toString());
		}

		@Override
		public LocalDate read(final JsonReader jsonReader) throws IOException {
			return LocalDate.parse(jsonReader.nextString());
		}
	}

	private Gson gson = FxGson.fullBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
			.create();

	private Gson gsonPretty = FxGson.fullBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
			.setPrettyPrinting()
			.create();

	public String toJson() {
		return toJson(false);
	}

	/**
	 * @param isPretty use more human-readable pretty printing
	 * @return JSON string
	 */
	public String toJson(boolean isPretty) {
		// Gson must be given a generic type as an argument 
		// if a target is a generic type object
		// 
		//	Java API's getClass() cannot return generic types.
		// com.google.gson.reflect.TypeToken<T> and getType()
		// provides a method to get generic types 
		// like as ArrayList<XXX>
		// 
		// Add "opens your.package.name to com.google.gson" in module-info.java
		// because this uses reflection. 
		Type todosType = new TypeToken<ArrayList<ToDo>>(){}.getType();
		if (isPretty) 
			return gsonPretty.toJson(todos, todosType);

		return gson.toJson(todos, todosType);
	}

	public ArrayList<ToDo> fromJson(String json) {
		try {
			Type todosType = new TypeToken<ArrayList<ToDo>>() {
			}.getType();

			return gson.fromJson(json, todosType);
			// return FxGson.create().fromJson(json, todosType);
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
