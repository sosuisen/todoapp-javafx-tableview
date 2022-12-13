package com.example;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ToDo {
	private IntegerProperty id = new SimpleIntegerProperty();
	private StringProperty title = new SimpleStringProperty();
	/**
	 * Local date string
	 * e.g. 2022-12-01
	 */
	private StringProperty date = new SimpleStringProperty();
	private BooleanProperty completed = new SimpleBooleanProperty();

	public ToDo(int id, String title, String date, boolean completed) {
		this.id.set(id);
		this.title.set(title);
		this.date.set(date);
		this.completed.set(completed);
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title .set(title);
	}

	/**
	 * @return date string e.g. 2022-12-01
	 */
	public String getDate() {
		return date.get();
	}

	/**
	 * @param date date string e.g. 2022-12-01
	 */
	public void setDate(String date) {
		this.date.set(date);
	}

	public LocalDate getLocalDate() {
		return LocalDate.parse(date.get());
	}

	public void setLocalDate(LocalDate localDate) {
		this.date.set(localDate.toString());
	}

	public boolean isCompleted() {
		return completed.get();
	}

	public void setCompleted(boolean completed) {
		this.completed.set(completed);
	}
	
	public IntegerProperty idProperty() { 
		return id; 
	}

	public BooleanProperty completedProperty() {
		return completed;
	}
	
	public StringProperty titleProperty() {
		return title;
	}
	
	public StringProperty dateProperty() {
		return date;
	}
}
