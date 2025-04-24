/**
 * <p> Title: Article Creation Screen </p>
 * 
 * <p> Description: Provides a user interface for creating new help articles </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author William Sou
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

import core.Article;
import core.databaseInterface;

/**
 * This class represents the screen for creating new articles in the help system.
 * It provides a graphical user interface for inputting article details and saving them to the database.
 */
public class ArticleCreationScreen {
    private Stage stage;
    private TextField titleField, authorsField, keywordsField, groupField;
    private TextArea abstractArea, bodyArea, referencesArea;
    private databaseInterface dbMan;
    private String groupName;
    private boolean isSpecial;
    private HelpArticleSystem ref;
    
    /**
     * Constructs a new ArticleCreationScreen.
     * 
     * @param dbMan The DatabaseManager instance for saving articles
     */
    public ArticleCreationScreen(databaseInterface dbMan, String groupName, boolean isSpecial) {
        this.dbMan = dbMan;
        this.groupName = groupName;
        this.isSpecial = isSpecial;
        ref = new HelpArticleSystem();
    	
    	stage = new Stage();
        stage.setTitle("Create Article");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #6BB3E3;");

        titleField = createStylizedTextField("Title");
        authorsField = createStylizedTextField("Authors");
        keywordsField = createStylizedTextField("Keywords");
        abstractArea = createStylizedTextArea("Abstract");
        bodyArea = createStylizedTextArea("Body");
        referencesArea = createStylizedTextArea("References");

        Button saveButton = new Button("SAVE ARTICLE");
        saveButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> saveArticle());

        layout.getChildren().addAll(
            new Label("CREATE HELP ARTICLE"),
            titleField, authorsField, keywordsField,
            abstractArea, bodyArea, referencesArea, saveButton
        );

        Scene scene = new Scene(layout, 600, 800);
        stage.setScene(scene);
    }

    /**
     * Creates a styled text field with the given prompt text.
     * 
     * @param promptText The prompt text to display in the text field
     * @return A styled TextField instance
     */
    private TextField createStylizedTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        return field;
    }

    /**
     * Creates a styled text area with the given prompt text.
     * 
     * @param promptText The prompt text to display in the text area
     * @return A styled TextArea instance
     */
    private TextArea createStylizedTextArea(String promptText) {
        TextArea area = new TextArea();
        area.setPromptText(promptText);
        area.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: black; -fx-prompt-text-fill: gray;");
        return area;
    }

    /**
     * Saves the article to the database and closes the creation screen.
     * This method is called when the save button is clicked.
     */
    private void saveArticle() {
        // Create a new Article instance with the input data
    	
        Article newArticle = new Article(
        		titleField.getText().toCharArray(),
        		authorsField.getText().toCharArray(),
        		abstractArea.getText().toCharArray(),
        		keywordsField.getText().toCharArray(),
        		bodyArea.getText().toCharArray(),
        		referencesArea.getText().toCharArray(),
        		groupName.toCharArray()
        		);
    	try {
			dbMan.addArticle(newArticle);
			System.out.println("Article saved successfully.");
		} catch (Exception e) {
			System.out.println("FAILED TO SAVE ARTICLE!");
			e.printStackTrace();
		}
    	
        stage.close();
        try {
			ref.show();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Displays the article creation screen.
     */
    public void show() {
        stage.show();
    }
}