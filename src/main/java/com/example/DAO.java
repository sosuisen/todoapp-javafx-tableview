package com.example;

import java.util.ArrayList;
import java.util.Optional;

/**
 * DAO for ToDo App
 */
public interface DAO {	
	/**
	 * @return specified ToDo
	 */
	public Optional<ToDo> get(int id);	
	/**
	 * @return all ToDos
	 */
	public ArrayList<ToDo> getAll();
	/**
	 * @return created ToDo
	 */
	public ToDo create(String title, String date);	
	/**
	 * @return updated ToDo
	 */
	public Optional<ToDo> updateTitle(int id, String title);
	/**
	 * @return updated ToDo
	 */
	public Optional<ToDo> updateDate(int id, String date);
	/**
	 * @return updated ToDo
	 */
	public Optional<ToDo> updateCompleted(int id, boolean completed);
	/**
	 * @return deleted id
	 */
	public Optional<Integer> delete(int id);
}
