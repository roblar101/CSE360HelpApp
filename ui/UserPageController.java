package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.util.List;

import core.*;

/***
 * 
 * <p> UserPageController </p>
 * 
 * <p> Description: Class to implement UI for a Student/User from userpage.fxml. Currently only has a logout button.</p>
 * 
 * 
 * @author Ethan MacTough
 * 
 * @version 1.00	2024-10-28
 * 
 */

public class UserPageController {
	
	
	
	@FXML
    private Button questionBtn;

    @FXML
    private Button viewArticlesBtn;

    @FXML
    private AnchorPane questionPane;

    @FXML
    private ComboBox<String> questionTypeMenu;

    @FXML
    private TextArea questionTextArea;

    @FXML
    private Button sendButton;
		
	@FXML
	private Button logoutBtn;

	User selectedUser;
	core.databaseInterface dbMan;
	
	@FXML
    private void initialize() {
    
		questionTypeMenu.getItems().addAll("General Help", "Specific Help");
	
		dbMan = new core.databaseInterface();
	
	}

    @FXML
    private void handleQuestion() {
        // Handle question button click
    	
    	questionPane.setVisible(!(questionPane.isVisible()));
    	questionPane.setDisable(!questionPane.isDisabled());
    	
    	
    	
    }

    @FXML
    private void handleViewArticles() {
    	Source.getUIManager().loadArticlePage();
    }


    @FXML
    private void handleSendQuestion() {
    	
    	String qBodyString = questionTextArea.getText();
    	String qType = questionTypeMenu.getValue();
    	
    	//If there is no text in the question area
    	if(qBodyString == null || qBodyString.isEmpty()) {
    		
    		Alert alert = new Alert(AlertType.ERROR, "The text area is empty, please ask a question!",ButtonType.OK);
    		alert.showAndWait();
    		return;
    	}
    	
    	
    	if(qType == null || qType.isEmpty()) {
    		Alert alert = new Alert(AlertType.ERROR, "A question type was not specified!",ButtonType.OK);
    		alert.showAndWait();
    		return;
    	}
    	
    	//Check what question type is selcted, if any.
    	if(qType.equals("General Help")) {
    		
    		try {
				dbMan.addGeneralQuestion(Source.getUIManager().getUser().getUsername(), qBodyString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		//TODO REMOVE THIS TEST
    		try {
				List<String> questions = dbMan.getGeneralQuestions();
				System.out.println("NUMBER OF GEN QUESTIONS IN DB:" + questions.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
    	}
    	
    	else if(qType.equals("Specific Help")) {
    		try {
				dbMan.addSpecificQuestion(Source.getUIManager().getUser().getUsername(), qBodyString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		//TODO REMOVE THIS TEST
    		try {
				List<String> questions = dbMan.getSpecificQuestions();
				System.out.println("NUMBER OF GEN QUESTIONS IN DB:" + questions.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	

    	
    	
    }

   
	// Logout Button implementation. Logs user out of system.
	@FXML
    private void handleLogout() {
        core.Source.getUIManager().loadLoginPage();
    }
}