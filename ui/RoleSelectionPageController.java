package ui;

import core.ROLE;
import core.Source;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RoleSelectionPageController {

    @FXML
    private Button adminBtn;

    @FXML
    private Button studentBtn;

    @FXML
    private Button instructorBtn;

    @FXML
    private void initialize() {
    	
    	if(Source.getUIManager().getUser().hasRole(ROLE.ADMIN))
		{
			adminBtn.setVisible(true);
		}
		if(Source.getUIManager().getUser().hasRole(ROLE.INSTRUCTOR))
		{
			instructorBtn.setVisible(true);
		}
		if(Source.getUIManager().getUser().hasRole(ROLE.STUDENT))
		{
			studentBtn.setVisible(true);
		}
		
		
    }

    @FXML
    private void handleAdminBtn() {
    	
        Source.getUIManager().loadAdminPage();
        Source.getUIManager().selectRole(core.ROLE.ADMIN);
    }

    @FXML
    private void handleStudentBtn() {
    	Source.getUIManager().loadUserPage();
    	Source.getUIManager().selectRole(core.ROLE.STUDENT);
    }

    @FXML
    private void handleInstructorBtn() {
        Source.getUIManager().loadInstructorPage();
        Source.getUIManager().selectRole(core.ROLE.INSTRUCTOR);
    }
}
