package ui;

import core.User;

import java.io.IOException;
import java.sql.SQLException;

import core.Source;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UIManager {
	private static User loggedInUser;
	private static core.ROLE selectedRole;
	public UIManager(){
		loggedInUser = null;
		selectedRole = null;
	}
	
	
	
	public void loadLoginPage() {
		if(loggedInUser != null){
			logUserOut();
		}
		
		
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/loginpage.fxml"));
			Scene scene = new Scene(root);
			Source.getPrimaryStage().setTitle("Help Program");
			Source.getPrimaryStage().setScene(scene);
			Source.getPrimaryStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadRoleSelectionPage() {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/roleselectionpage.fxml"));
			Scene scene = new Scene(root);
			Source.getPrimaryStage().setScene(scene);
			Source.getPrimaryStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadAdminPage() {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/adminpage.fxml"));
			Scene scene = new Scene(root);
			Source.getPrimaryStage().setScene(scene);
			Source.getPrimaryStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadUserPage() {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/userpage.fxml"));
			Scene scene = new Scene(root);
			Source.getPrimaryStage().setScene(scene);
			Source.getPrimaryStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadInstructorPage() {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/instructorpage.fxml"));
			Scene scene = new Scene(root);
			Source.getPrimaryStage().setScene(scene);
			Source.getPrimaryStage().show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadArticlePage() {
		
		HelpArticleSystem articleSystem = new HelpArticleSystem();
	}
	
	
	public core.ROLE getSelectedRole() {
		return selectedRole;
	}
	
	public void selectRole(core.ROLE role) {
		selectedRole = role;
	}
	
	
	public User getUser() {
		return loggedInUser;
	}
	
	public void logUserIn(User user) {
		if(loggedInUser == null){
			loggedInUser = user;
		}else {
			System.out.println("USER IS NOT LOGGED OUT!");
		}
		
	};
	
	public void logUserOut() {
		if(loggedInUser.hasChanged()) {
			//Save changes to database
		}
		loggedInUser = null;
	}
	
}
