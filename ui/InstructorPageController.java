package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import core.*;

/***
 * 
 * <p> InstructorPageController </p>
 * 
 * <p> Description: Class that implements instructorpage.fxml. Creates UI page for Instructor Users.</p>
 * 
 * 
 * @author Ethan MacTough
 * 
 * @version 1.00	2024-10-28
 * 
 */

public class InstructorPageController {
	
	User selectedUser;
	
	// Implemented buttons on page
	@FXML
	private Button articleBtn;
	@FXML
	private Button logoutBtn;

	// On-action of logout button. Logs user out of the system.
	@FXML
    private void handleLogout() {
        core.Source.getUIManager().loadLoginPage();
    }
	
	@FXML
	private void handleArticle() {
		core.Source.getUIManager().loadArticlePage();
	}
}
