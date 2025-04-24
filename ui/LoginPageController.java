package ui;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import core.ROLE;
import core.Source;
import core.User;
import core.UserManager;
import core.InviteCodeManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LoginPageController {
	private UserManager userMan = new UserManager();
	private String inviteRole = null;
	private User loggedInUser = null;
	//Login pane
	@FXML
    private Pane loginPane;
	
	@FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    
    
    //Initialize account pane
    @FXML
    private Pane initializeAccountPane;
    
    @FXML
    private TextField createAccountUsernameField;
    
    @FXML
    private PasswordField createAccountPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button createAccountBtn;
    
    
    //Finialize account pane
    @FXML
    private Pane finalizeAccountPane;
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField middleNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField preferredNameField;
    
    @FXML
    private TextField emailAddressField;
    
    @FXML
    private Button finalizeAccountBtn;

    @FXML
    private Label messageLabel;

    @FXML
    private Button inviteCodeButton;

    @FXML
    private void initialize() {
        // This method is called automatically after the FXML file has been loaded
        messageLabel.setText("LOGIN");
    }

    @FXML
    private void handleLogin() {
    	String username = usernameField.getText();
        String password = passwordField.getText();
    	
        if(username.isEmpty() || password.isEmpty())
        {
        	return;
        }
        
        
        try {
			
        	//If no users in database, make initial admin user
			if(userMan.getAllUsers().size() == 0)
			{
				
				//Create user with the given username and password
				userMan.createUser(username, "", "", password);
				//Assign that user admin
				userMan.assignRoleToUser(username, "ADMIN");
				
				//Alert user that they have created the intial user
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "You are the first user and have been assigned ADMIN. \n Please login again.", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			
			//If there are users, check the entered username and password
			if(userMan.checkPassword(username, password)) {
				loggedInUser = userMan.getUserByUsername(username);
				for(String role : userMan.getRolesByUsername(username))
				{
					System.out.println(role);
					if(role.compareTo("ADMIN") == 0){
						System.out.println("USER HAS ROLE ADMIN");
						loggedInUser.addRole(ROLE.ADMIN);
					}
					if(role.compareTo("STUDENT") == 0) {
						System.out.println("USER HAS ROLE STUDENT");
						loggedInUser.addRole(ROLE.STUDENT);
					}
					if(role.compareTo("INSTRUCTOR") == 0) {
						System.out.println("USER HAS ROLE INSTRUCTOR");
						loggedInUser.addRole(ROLE.INSTRUCTOR);
					}
				}
				
				//Log user in with UIManager
				
				
				System.out.println("user first: " + loggedInUser.getFirstName() + "\n" +
									"user last: " + loggedInUser.getLastName() + "\n" + 
									"user middle: " + loggedInUser.getMiddleName() + "\n" +
									"user pref: " + loggedInUser.getPrefName() + "\n" +
									"user email: " + loggedInUser.getEmail());
				
				
				//Check if account finalization is needed
				if((loggedInUser.getFirstName() == null) || 
						(loggedInUser.getLastName() == null) || 
						(loggedInUser.getMiddleName() == null) ||
						(loggedInUser.getPrefName() == null) ||
						(loggedInUser.getEmail() == null)) {
					
					loginPane.setVisible(false);
					loginPane.setDisable(true);
					
					finalizeAccountPane.setVisible(true);
					finalizeAccountPane.setDisable(false);
                    messageLabel.setText("FINALIZE ACCOUNT");
                    return;
				}
				
				if (loggedInUser.getFirstName() == "") {
					DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
					try {
				        LocalDate dateTime = LocalDate.parse(loggedInUser.getEmail(), dateFormat);
				        
				        if (dateTime.isBefore(LocalDate.now())){
				        	userMan.deleteUser(loggedInUser.getUsername());
				        	return;
				        } else {
				        	
				        	Dialog<ButtonType> dialog = new Dialog<>();
				            dialog.setTitle("Set Up Password");
				            dialog.setHeaderText("Set New Password:");

				            // Create the content for the dialog
				            VBox content = new VBox(10);
				            content.setPadding(new Insets(10));
				            content.setAlignment(Pos.CENTER);
				            
				            TextField newPass = new TextField();
				            
				            content.getChildren().addAll(
				                newPass
				            );

				            // Set the content and add buttons
				            dialog.getDialogPane().setContent(content);
				            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

				            // Show the dialog and handle the result
				            dialog.showAndWait().ifPresent(buttonType -> {
				                if (buttonType == ButtonType.OK) {
				                	try {
										userMan.updatePassword(loggedInUser.getUsername(), newPass.getText());
										System.out.println(newPass.getText());
										userMan.updateUser(username, null, null, null, null, null);

										passwordField.clear();
										return;
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
				                }
				            });
							passwordField.clear();
				            return;
		                }
			        } catch (DateTimeParseException dtpe) {
			        }
				}
				
				// OLD
				//RoleSelectionPage roleSelectionPage = new RoleSelectionPage(loggedInUser);
				//roleSelectionPage.show();
				
				Source.getUIManager().logUserIn(loggedInUser);
				//Load the RoleSelectionPage
				Source.getUIManager().loadRoleSelectionPage();
				} else {
				
				//Clear passwordField
				passwordField.clear();
				//Give alert to user
				Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Login", ButtonType.OK);
				alert.showAndWait();
			}
			
			} catch (SQLException e) {
			
			e.printStackTrace();
		}
    }

    @FXML
    private void handleCreateAccount() {

    	
    	String username = createAccountUsernameField.getText();
    	String password = createAccountPasswordField.getText();
    	String confirmPassword = createAccountPasswordField.getText();
    	
    	
    	//Check if any field is null
    	if((username != null) && (password != null) && (confirmPassword != null)) {
    		try {
        		//Check if username already exists

				if(userMan.usernameExists(username)) {
					Alert alert = new Alert(AlertType.ERROR, "Username already exists, please choose another.", ButtonType.OK);
					alert.showAndWait();
					createAccountUsernameField.clear();
					return;
				}
				if(password.equals(confirmPassword)) {
					userMan.createUser(username, null, null, password);
					userMan.assignRoleToUser(username, inviteRole);
					Alert alert = new Alert(AlertType.CONFIRMATION, "Created account successfully!", ButtonType.OK);
					alert.showAndWait();
					initializeAccountPane.setVisible(false);
					initializeAccountPane.setDisable(true);
					
					loginPane.setVisible(true);
					loginPane.setDisable(false);
					return;
				} else {
					Alert alert = new Alert(AlertType.ERROR, "Passwords do not match! Please try again.", ButtonType.OK);
					alert.showAndWait();
				}
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.ERROR, "There was a problem with the database and account creation could not be completed.", ButtonType.OK);
				alert.showAndWait();
				e.printStackTrace();
				return;
			}	
    	}
    	
    	
    }
    
    
    @FXML
    private void handleFinializeAccount() {
    	
    	String firstName = firstNameField.getText();
    	String middleName = middleNameField.getText();
    	String lastName = lastNameField.getText();
    	String prefName = preferredNameField.getText();
    	String email = emailAddressField.getText();
    	
    	
    	if((firstName == null) ||
    			(middleName == null) ||
    			(lastName == null) ||
    			(prefName == null)  ||
    			(email == null)) {
    		
    		Alert alert = new Alert(AlertType.ERROR, "Please complete all fields", ButtonType.OK);
			alert.showAndWait();
    		return;
    	}
    	System.out.println("PREFERRED NAME IS: " + prefName);
    	loggedInUser.setFirstName(firstName);
    	loggedInUser.setMiddlename(middleName);
    	loggedInUser.setLastname(lastName);
    	loggedInUser.setPrefName(prefName);
    	loggedInUser.setEmail(email);
    	
    	//Try to update user.
    	try {
			userMan.updateUser(loggedInUser.getUsername(), firstName, middleName, lastName, prefName, email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Source.getUIManager().logUserIn(loggedInUser);
		//Load the RoleSelectionPage
		Source.getUIManager().loadRoleSelectionPage();
    	
    }
    
    
    @FXML
    private void handleInviteCode() {
    	 // Create the custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Invite Code");
        dialog.setHeaderText("Enter your invite code");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create and add text field for the code
        TextField codeField = new TextField();
        codeField.setPromptText("Enter invite code");
        content.getChildren().addAll(
            new Label("Code:"), 
            codeField
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String code = codeField.getText().trim();
                if (!code.isEmpty()) {
                    try {
                        InviteCodeManager inviteManager = new InviteCodeManager();
                        inviteRole = inviteManager.useInviteCode(code);
                        
                        if (inviteRole != null) {
                            
                        	loginPane.setDisable(true);
                        	loginPane.setVisible(false);
                        	
                        	initializeAccountPane.setVisible(true);
                        	initializeAccountPane.setDisable(false);
                            messageLabel.setText("CREATE ACCOUNT");


                        } else {
                            // Show error for invalid code
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Invalid invite code.");
                            alert.showAndWait();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // Show error alert
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("An error occurred while processing the invite code.");
                        alert.showAndWait();
                    }
                }
            }
        });
    }
}
