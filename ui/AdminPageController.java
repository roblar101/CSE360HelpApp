package ui;

import core.BackupManager;
import core.databaseInterface;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import core.InviteCodeManager;
import core.ROLE;
import core.Source;
import core.User;
import core.UserManager;

public class AdminPageController {
	
	//Delcaring need variables
	UserManager userMan;
	User selectedUser;
	BackupManager backupManage;
	
	
	//javaFX will initialize these variables
	
	/*
	@FXML
	private Button createArticleBtn;
	
	@FXML
	private Button backupArticleBtn;
	*/
    @FXML
    private Button listUsersBtn;

    @FXML
    private Button editUsersBtn;

    @FXML
    private Button inviteUserBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private AnchorPane searchUserView;

    @FXML
    private BorderPane editUserView;
    
    
    @FXML
    private TextField selectUserField;

    @FXML
    private Button searchUser;

    @FXML
    private ListView<String> targetUserView;

    @FXML
    private Button resetUserBtn;

    @FXML
    private Button deleteUserBtn;

    @FXML
    private Button changleRolesBtn;

    @FXML
    private VBox roleControls;

    @FXML
    private TextField desiredRoleField;

    @FXML
    private Button addRoleBtn;

    @FXML
    private Button removeRoleBtn;

    @FXML
    private ListView<String> listUsersView;

    @FXML
    public void initialize() {
    	
    	userMan = new UserManager();
    	selectedUser = null;
    	
    	//selected user view intial text
    	ObservableList<String> content = FXCollections.observableArrayList();
    	content.add("No user selected, search for a user.");
    	targetUserView.setItems(content);
    	
    	//
    
    }

    //handleChangeRoles is called by the changeRolesBtn, it toggles the menu after checking to see if a user has been selected.

    @FXML
   private void handleChangeRoles() {
    	
    	if(selectedUser != null) {
    		roleControls.setVisible(!roleControls.isVisible());
            roleControls.setDisable(!roleControls.isDisabled());
    	}else {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No user was specified \n please search for a user. ", ButtonType.OK);
        	alert.showAndWait();
    	}
    }

    
    @FXML
    private void handleRemoveRole() {
        String desiredRole = desiredRoleField.getText();
        
        if(desiredRole.isEmpty()) {
        	Alert alert = new Alert(Alert.AlertType.ERROR, "No role was specified, please type in the role you would like to remove", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }else {
        	desiredRole = desiredRole.toUpperCase();
        	
        	
        	//TODO overload user role methods to accept strings
        	switch(desiredRole) {
        	case "ADMIN":
        		if(selectedUser.hasRole(ROLE.ADMIN)) {
        			selectedUser.removeRole(ROLE.ADMIN);
        		}
        		break;
        	case "INSTRUCTOR":
        		if(selectedUser.hasRole(ROLE.INSTRUCTOR)) {
        			selectedUser.removeRole(ROLE.INSTRUCTOR);
        		}
        		break;
        	case "STUDENT":
        		if(selectedUser.hasRole(ROLE.STUDENT)) {
        			selectedUser.removeRole(ROLE.STUDENT);
        		}
        		break;
        	default:
        		Alert alert = new Alert(Alert.AlertType.ERROR, "Not a valid role, please enter a valid role", ButtonType.OK);
        		alert.showAndWait();
        		return;
        	}

        	try {
				userMan.removeRoleFromUser(selectedUser.getUsername(), desiredRole);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	updateTargetUserView();
        	
        }
        
        
    }
    
    @FXML
    private void handleAddRole() {
    	String desiredRole = desiredRoleField.getText();
        
        if(desiredRole.isEmpty()) {
        	Alert alert = new Alert(Alert.AlertType.ERROR, "No role was specified, please type in the role you would like to add", ButtonType.OK);
        	alert.showAndWait();
        	return;
        }else {
        	desiredRole = desiredRole.toUpperCase();
        	System.out.println(desiredRole);
        	switch(desiredRole) {
        	case "ADMIN":
        		if(!selectedUser.hasRole(ROLE.ADMIN)) {
        			selectedUser.addRole(ROLE.ADMIN);
        		}
        		break;
        	case "INSTRUCTOR":
        		if(!selectedUser.hasRole(ROLE.INSTRUCTOR)) {
        			selectedUser.addRole(ROLE.INSTRUCTOR);
        		}
        		break;
        	case "STUDENT":
        		if(!selectedUser.hasRole(ROLE.STUDENT)) {
        			selectedUser.addRole(ROLE.STUDENT);
        		}
        		break;
        	default:
        		Alert alert = new Alert(Alert.AlertType.ERROR, "Not a valid role, please enter a valid role", ButtonType.OK);
        		alert.showAndWait();
        		return;
        	}

        	try {
				userMan.assignRoleToUser(selectedUser.getUsername(), desiredRole);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	updateTargetUserView();
        	
        }
    	
    	
    	
    	
    	
    }
    
    
    @FXML
    private void handleDeleteUser() {
    	if(selectedUser != null) {
    		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedUser.getUsername(), ButtonType.YES,ButtonType.NO);
    		alert.setTitle("DELETING USER");
    		alert.showAndWait();
    		if(alert.getResult() == ButtonType.YES) {
    			try {
					userMan.deleteUser(selectedUser.getUsername());
		    		Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, selectedUser.getUsername() + " was deleted!", ButtonType.OK);
		    		alert2.showAndWait();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}else {
    			return;
    		}

    		
    		
    	}else {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "No user was specified \n please search for a user. ", ButtonType.OK);
        	alert.showAndWait();
    	}
    }

    @FXML
    private void handleEditUsers() {
        
    	if(!listUsersView.isDisabled()) {
        	listUsersView.setDisable(true);
        	listUsersView.setVisible(false);
        }
    	
    	
    	editUserView.setDisable(false);
        editUserView.setVisible(true);
    }

    @FXML
    private void handleInviteUser() {
    	// Create the custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Generate Invite Code");
        dialog.setHeaderText("Select role for the invite code");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ADMIN", "INSTRUCTOR", "STUDENT");
        roleComboBox.setValue("STUDENT"); // Default value
        
        content.getChildren().addAll(
            new Label("Role:"),
            roleComboBox
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String selectedRole = roleComboBox.getValue();
                try {
                    InviteCodeManager inviteManager = new InviteCodeManager();
                    String generatedCode = inviteManager.generateInviteCode(selectedRole);
                    
                    // Show the generated code in a new dialog
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invite Code Generated");
                    alert.setHeaderText("Share this code with the new user");
                    alert.setContentText("Code: " + generatedCode + "\nRole: " + selectedRole);
                    System.out.println(generatedCode);
                    
                    // Make the code selectable
                    Label codeLabel = new Label(generatedCode);
                    codeLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 16px;");
                    VBox alertContent = new VBox(10);
                    alertContent.getChildren().addAll(
                        new Label("Share this code with the new user:"),
                        codeLabel
                    );
                    alert.getDialogPane().setContent(alertContent);
                    
                    alert.showAndWait();
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error occurred while generating the invite code.");
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    private void handleListUsers() {
    	
    	//Disable the editUserView if its showing
    	if(!editUserView.isDisabled()) {
    		editUserView.setDisable(true);
    		editUserView.setVisible(false);
        }
    	
    	//Make the listUsersView visible
    	listUsersView.setDisable(false);
    	listUsersView.setVisible(true);
    	
    	//List all users
    	//TODO MAKE QUERY THE RETURNS ARRAYLIST OF STRINGS CONTAINING
    	//ALL USERS NAMES
    	
    	try {
			ArrayList<User> users = userMan.getAllUsers();
			ObservableList<String> content = FXCollections.observableArrayList();
			
		
			for(User i : users) {
				
				content.add(i.getUsername());
			}
		
		listUsersView.setItems(content);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    }

    @FXML
    private void handleLogout() {
        core.Source.getUIManager().loadLoginPage();
    }


    @FXML
    private void handleResetUser() throws SQLException {
    	
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("One-Time Password");
        dialog.setHeaderText("Set Date for Password to Expire:");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);
        
        DatePicker date = new DatePicker();
        
        content.getChildren().addAll(
            date
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                LocalDate lDate = date.getValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
                String sDate = lDate.format(formatter);
                System.out.println(sDate);
                
	            String generatedCode = InviteCodeManager.generateRandomCode();
	            
	            String usname = selectedUser.getUsername();
	            try {
					
		            userMan.deleteUser(usname);
		            
		            userMan.createUser(usname, "", "", generatedCode);
		            userMan.updateUser(usname, "", "", "", "", sDate);
		            
		            if (selectedUser.hasRole(ROLE.STUDENT)) {
		            	userMan.assignRoleToUser(usname, "STUDENT");
		            }
		            if (selectedUser.hasRole(ROLE.INSTRUCTOR)) {
		            	userMan.assignRoleToUser(usname, "INSTRUCTOR");
		            }
		            if (selectedUser.hasRole(ROLE.ADMIN)) {
		            	userMan.assignRoleToUser(usname, "ADMIN");
		            }
		            
		            // Show the generated code in a new dialog
		            Alert alert = new Alert(Alert.AlertType.INFORMATION);
		            alert.setTitle("User Reset");
		            alert.setHeaderText("Share this password with the user");
		            alert.setContentText("Code: " + generatedCode);
		            
		            // Make the code selectable
		            TextArea codeLabel = new TextArea(generatedCode);
		            codeLabel.setEditable(false);
		            codeLabel.setWrapText(true);
		
		            codeLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 16px;");
		            VBox alertContent = new VBox(10);
		            alertContent.getChildren().addAll(
		                new Label("Share this password with the user:"),
		                codeLabel
		            );
		            alert.getDialogPane().setContent(alertContent);
		            
		            alert.showAndWait();
		            
	            } catch (SQLException e) {
	            	e.printStackTrace();
	                Alert alert = new Alert(Alert.AlertType.ERROR);
	                alert.setTitle("Error");
	                alert.setHeaderText(null);
	                alert.setContentText("An error occurred while resetting the user.");
	                alert.showAndWait();
				}
            }
        });
    }

    @FXML
    private void handleSearchUser() {
        String selectedUsername = selectUserField.getText();
        selectedUser = null;
        ObservableList<String> content = FXCollections.observableArrayList();
        
        if(selectedUsername.isEmpty()) {
        	return;
        }
        try {
        	//GET USER
        	selectedUser = userMan.getUserByUsername(selectedUsername);
        	
			if(selectedUser ==  null)
			{
				content.add("USER NOT FOUND!");
				targetUserView.setItems(content);
				return;
			}else {
				
				content.add("USERNAME: " + selectedUser.getUsername());
				content.add("EMAIL: " + selectedUser.getEmail());
				content.add("NAME: " + selectedUser.getFirstName());
				content.add("PREFFERED NAME: " + selectedUser.getPrefName());
				content.add("ROLES:");
				for(core.ROLE role : selectedUser.getRoles()){
					
					if(role == core.ROLE.ADMIN) {
						content.add("ADMIN");
					}else if(role == core.ROLE.INSTRUCTOR) {
						content.add("INSTRUCTOR");
					}else if(role == core.ROLE.STUDENT) {
						content.add("STUDENT");
					}
				}
				targetUserView.setItems(content);
				return;
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
    }
    
    
    private void updateTargetUserView() {
        ObservableList<String> content = FXCollections.observableArrayList();
        content.add("USERNAME: " + selectedUser.getUsername());
		content.add("EMAIL: " + selectedUser.getEmail());
		content.add("FIRST NAME: " + selectedUser.getFirstName());
		content.add("MIDDLE NAME: " + selectedUser.getMiddleName());
		content.add("LAST NAME: " + selectedUser.getLastName());
		content.add("PREFFERED NAME: " + selectedUser.getPrefName());
		content.add("ROLES:");
		for(core.ROLE role : selectedUser.getRoles()){
			
			if(role == core.ROLE.ADMIN) {
				content.add("ADMIN");
			}else if(role == core.ROLE.INSTRUCTOR) {
				content.add("INSTRUCTOR");
			}else if(role == core.ROLE.STUDENT) {
				content.add("STUDENT");
			}
		}
		targetUserView.setItems(content);
    }
    
    @FXML
    private void handleArticle() {
    	Source.getUIManager().loadArticlePage();
    }
    
    
    /*
    
    @FXML
    private void handleCreateArticle() {
    	ArticleCreationScreen articleCreateScreen = new ArticleCreationScreen(userMan.getDatabaseInterface()); 
    	articleCreateScreen.show();
    }
    
    @FXML
    private void handleBackupArticle() {
    Optional<String> filename;
    
    TextInputDialog file = new TextInputDialog();
    file.setTitle("Back Up Article");
    file.setHeaderText("Enter the file name");
    file.setContentText("File name: ");
    
    filename = file.showAndWait();
    
    //To get if there is a file name given 
    if(filename.isPresent()) {
    	
    	String enteredName = filename.get(); //To get the file name
    	
    	try {
    		
    		BackupManager backupManage = new BackupManager(userMan.getDatabaseInterface());
    		backupManage.backupArticles(enteredName);//The file name given
    		Alert alert = new Alert(Alert.AlertType.INFORMATION,"Articles are backed up",ButtonType.OK);
    		alert.showAndWait();
    		
    	}catch (Exception e) {
    		
    		Alert alert = new Alert(Alert.AlertType.ERROR,"Failed to back up the file"+e.getMessage(),ButtonType.OK);
    		alert.showAndWait();
    		e.printStackTrace();
    	}
    	
    	}
    
    else {
    	
    	Alert alert = new Alert(Alert.AlertType.WARNING,"Please enter a file name",ButtonType.OK);
    	alert.showAndWait();
    	
    }
    
    }
    
    */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
