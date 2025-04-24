package core;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.UIManager;


public class Source extends Application {
	
	public static databaseInterface database = new databaseInterface();
	private static Stage primaryStage = new Stage();
	private static UIManager uiMan = new UIManager();
	
	public void start(Stage primaryStage) {
		uiMan.loadLoginPage();
	}
	
	public static databaseInterface getDatabase() {
		return database;
	}
    
	 public static Stage getPrimaryStage() {
	        return primaryStage;
	    }
	
	 public static UIManager getUIManager() {
		 return uiMan;
	 }
	 
    public static void main(String[] args) {
        launch(args);
    }
}
