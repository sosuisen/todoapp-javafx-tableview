package com.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LocalDateTableCell<T> extends TableCell<T, LocalDate> {

	private final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy/M/d");
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	private final DatePicker datePicker;

	public LocalDateTableCell(TableColumn<T, LocalDate> column) {

        this.datePicker = new DatePicker();

        // Manage the loss of focus of the textfield
        this.datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
        	if (newValue) {
        		// Get focus
        		final TableView<T> tableView = getTableView();
        		tableView.getSelectionModel().select(getTableRow().getIndex());
        		tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
        	} else {
        		// Lose focus
        		try {
        			LocalDate data = LocalDate.parse(datePicker.getEditor().textProperty().get(), parser);
        			commitEdit(data);
        		} catch (Exception e) {
        			// If error in parsing, set the previous value.
        			cancelEdit();
        			datePicker.getEditor().setText(formatter.format(datePicker.getValue()));
        		}
        	}
        });

        this.datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
        	if (isEditing()) {
        		commitEdit(newValue);
            }
        });

        /*
         * Show DatePicker only if the cell is editable.
         */
        // Bind this cells editable property to the whole column
        editableProperty().bind(column.editableProperty());
        // and then use this to configure the date picker
        contentDisplayProperty().bind(Bindings.when(editableProperty()).then(ContentDisplay.GRAPHIC_ONLY).otherwise(ContentDisplay.TEXT_ONLY));
    }

	@Override
	protected void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			// Datepicker can handle null values
			this.datePicker.setValue(item);
			setGraphic(this.datePicker);
			if (item == null) {
				setText(null);
			} else {
				setText(formatter.format(item));
			}
		}
	}
}