package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyApp extends Application {

	public void stop() {

	}

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("myapp.fxml"));
			Parent root = loader.load();
			MyAppController controller = (MyAppController) loader.getController();
			controller.rendered(stage);
			
			var scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

			stage.setTitle("ToDo App");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}