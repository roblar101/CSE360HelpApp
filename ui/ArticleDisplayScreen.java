/**
 * <p> Title: Article Display Screen </p>
 * 
 * <p> Description: Provides a user interface for displaying help articles </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author William Sou
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package ui;

import core.Article;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * This class represents the screen for displaying articles in the help system.
 * It provides a graphical user interface for viewing the details of a specific article.
 */
public class ArticleDisplayScreen {
    private Stage stage;
    private Label titleLabel, authorsLabel, abstractLabel, keywordsLabel, bodyLabel, referencesLabel, groupLabel;
    private TextArea abstractArea, bodyArea, referencesArea;
 
    /**
     * Constructs a new ArticleDisplayScreen and initializes it with the given article.
     * 
     * @param article The Article object to be displayed
     */
    public ArticleDisplayScreen(Article article) {
        stage = new Stage();
        stage.setTitle("Article Display");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #6BB3E3;");

        // Initialize labels
        titleLabel = createStylizedLabel("Title");
        authorsLabel = createStylizedLabel("Author(s)");
        abstractLabel = createStylizedLabel("Abstract");
        keywordsLabel = createStylizedLabel("Keywords");
        bodyLabel = createStylizedLabel("Body");
        referencesLabel = createStylizedLabel("References");
        groupLabel = createStylizedLabel("Group");

        // Initialize text areas
        abstractArea = createStylizedTextArea();
        bodyArea = createStylizedTextArea();
        referencesArea = createStylizedTextArea();

        // Add all components to the layout
        layout.getChildren().addAll(
            titleLabel, authorsLabel, keywordsLabel,
            abstractLabel, abstractArea,
            bodyLabel, bodyArea,
            referencesLabel, referencesArea,
            groupLabel
        );
        
        // Display the article content
        displayArticle(article);
        
        Scene scene = new Scene(layout, 600, 800);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a styled label with the given text.
     * 
     * @param text The text to display in the label
     * @return A styled Label instance
     */
    private Label createStylizedLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");
        return label;
    }

    /**
     * Creates a styled text area.
     * 
     * @return A styled TextArea instance
     */
    private TextArea createStylizedTextArea() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: black;");
        return area;
    }

    /**
     * Displays the content of the given article in the UI.
     * 
     * @param article The Article object whose content is to be displayed
     */
    public void displayArticle(Article article) {
        try {
            if (article != null) {
                // Set the content of labels and text areas
                titleLabel.setText("Title: " + new String(article.getTitle()));
                authorsLabel.setText("Author(s): " + new String(article.getAuthors()));
                keywordsLabel.setText("Keywords: " + new String(article.getKeywords()));
                abstractArea.setText(new String(article.getAbstract()));
                bodyArea.setText(new String(article.getBody()));
                referencesArea.setText(new String(article.getReferences()));
                groupLabel.setText("Group: " + new String(article.getGroup()));
            } else {
                showAlert("Article Not Found", "The requested article could not be found.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while retrieving the article: " + e.getMessage() + e.getStackTrace());
        }
    }

    /**
     * Displays an alert dialog with the given title and content.
     * 
     * @param title The title of the alert dialog
     * @param content The content message of the alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}