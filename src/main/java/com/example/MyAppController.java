package com.example;

import java.io.IOException;
import java.time.LocalDate;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class MyAppController {
	final String dataPath = "./tododata.json";

	DAO dao = new MemoryDAO(dataPath);

	@FXML
	private TableView<ToDo> tableView;
	
    @FXML
    private TableColumn<ToDo, Boolean> completedCol;

    @FXML
    private TableColumn<ToDo, String> titleCol;

    @FXML
    private TableColumn<ToDo, LocalDate> dateCol;

    @FXML
    private TableColumn<ToDo, ToDo> deleteCol;

    
	@FXML
	private Button addBtn;

	@FXML
	private DatePicker datePicker;

	@FXML
	private TextField titleField;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	private MenuItem menuItemClose;

	@FXML
	void onItemMenuAbout(ActionEvent event) {
		showInfo("ToDo App");
	}

	@FXML
	void onMenuItemClose(ActionEvent event) {
		Platform.exit();
	}

	private ObservableList<ToDo> tableViewItems;

	private void showInfo(String txt) {
		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setHeaderText(null);
		dialog.setContentText(txt);
		dialog.showAndWait();
	}

	private void showError(String txt) {
		Alert dialog = new Alert(AlertType.ERROR);
		dialog.setHeaderText(null);
		dialog.setContentText(txt);
		dialog.showAndWait();
	}

	/**
	 * Must be called in Application.start() after FXML is loaded
	 * to get stage.
	 */
	public void rendered(Stage stage) {
		// For set beforeunload,
		// use showingProperty instead of setOnCloseRequest
		// https://torutk.hatenablog.jp/entry/20170613/p1
		stage.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == true && newValue == false) {
				try {
					((MemoryDAO) dao).save(dataPath);
				} catch (IOException e1) {
					showError("Failed to write to " + dataPath);
					e1.printStackTrace();
				}
			}
		});
	}

	public void initialize() {
		// Set today
		datePicker.setValue(LocalDate.now());

		// Set table
		tableViewItems = tableView.getItems();
		completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		// p.getValue() means ToDo object,
		// so deleteCol is bound with ToDo object.
        deleteCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));

		// CheckBoxTableCell.forTableColumn() needs some arguments for type inference
		completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
//		dateCol.setCellFactory(new Callback<TableColumn<ToDo, LocalDate>, TableCell<ToDo, LocalDate>>() {  
//	        public TableCell<ToDo, LocalDate> call(TableColumn<ToDo, LocalDate> col) {  
//	            return new LocalDateTableCell<ToDo>(col);  
//	        }  
//	    });
		// In short
		dateCol.setCellFactory(col -> new LocalDateTableCell<ToDo>(col));

		deleteCol.setCellFactory(param -> new TableCell<ToDo, ToDo>() {
		    private final Button deleteBtn = new Button("🗑");

		    // deleteCol is bound with ToDo object by setCellValueFactory,
			// so the 1st param of updateItem() is ToDo type. 
		    protected void updateItem(ToDo todo, boolean empty) {
		        super.updateItem(todo, empty);
		        if (todo == null) {
		            setGraphic(null);
		            return;
		        }
		        setGraphic(deleteBtn);
				deleteBtn.setPrefWidth(35);
		        deleteBtn.setOnAction(
		            e -> {
		    			dao.delete(todo.getId());
		            	getTableView().getItems().remove(todo);
		            }
		        );
		    }
		});

		// Default order is sort by date column 
		tableView.getSortOrder().add(dateCol);
		
		// sortOrder is ObservableList that stores one or more TableColumns to be sorted.
		// Each TableColumn has 3 sort states: ascending, descending and not sorted.
		// If you would like to skip not sorted state, add removed TableColumn again to the sortOrder
		// by observing its change.
		// 
		// See also https://stackoverflow.com/questions/52567754/javafx-tableview-column-sorting-has-three-states-why
		tableView.getSortOrder().addListener((ListChangeListener.Change<? extends TableColumn<ToDo, ?>> change) -> {
			while(change.next()) {
				if(change.wasRemoved()) {
					// TableColumn has been removed.
					var removedSortCol = change.getRemoved().get(0);
					if (change.getList().size() == 0) {
						Platform.runLater(()->{
							// Add removed column again later.
							removedSortCol.setSortType(SortType.ASCENDING);
							tableView.getSortOrder().add(removedSortCol);
						});
					}
				}
			}
		});

		tableViewItems.addAll(dao.getAll());
		
	
		EventHandler<ActionEvent> handler = e -> {
			var title = titleField.getText();
			if (title.equals(""))
				return;
			LocalDate localDate = datePicker.getValue(); // 2022-12-01
			ToDo newToDo = dao.create(title, localDate);
			tableViewItems.add(newToDo);
			titleField.setText("");
			tableView.sort();
		};
		titleField.setOnAction(handler);
		addBtn.setOnAction(handler);
	}
}
