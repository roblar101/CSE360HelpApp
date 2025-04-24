/**
 * <p> Title: Help Article System </p>
 * 
 * <p> Description: Main application class for the Help Article Management System </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author William Sou
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package ui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import core.*;

/**
 * This class represents the main application for the Help Article Management System.
 * It provides a graphical user interface for managing help articles, including
 * creation, display, deletion, and backup/restore functionality.
 */
public class HelpArticleSystem{

    private static final String BLUE = "#1C1C1C";
    private static final String WHITE = "#6BB3E3";    
    private Label titleLabel;
    private VBox mainLayout;
    private HBox buttonBox;

    private TableView<Article> articleTable;
    HBox searchBox;
    private databaseInterface dbMan;
    private BackupManager backupMan;
    private UserManager userMan;
    private String currentGroup = "General";
    private boolean isSpecial = false;
    private User currentUser;
    
    private boolean altFile;
    
    public HelpArticleSystem() {
    	
    	// Initialize database and backup managers
    	altFile = false;
    	
    	try {
			dbMan = Source.getDatabase();
			backupMan = new BackupManager(dbMan);
			userMan = new UserManager();
			currentUser = Source.getUIManager().getUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	try {
			show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public HelpArticleSystem(databaseInterface database) {
    	
    	// Initialize database and backup managers
    	altFile = false;
    	
    	try {
			dbMan = database;
			backupMan = new BackupManager(dbMan);
			userMan = new UserManager(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * The main entry point for the JavaFX application.
     * 
     * @param primaryStage The primary stage for this application
     * @throws Exception 
     * @throws SQLException 
     */
    public void show() throws SQLException, Exception {
        
        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: " + WHITE + ";");

        titleLabel = new Label("ARTICLES");
        titleLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(BLUE));

        setupArticleTable();
        setupSearchBox();
        
        Button addButton = createStylizedButton("ADD ARTICLE");
        Button displayButton = createStylizedButton("DISPLAY ARTICLE");
        Button deleteButton = createStylizedButton("DELETE ARTICLE");
        Button refreshButton = createStylizedButton("REFRESH ARTICLE LIST");
        Button backupButton = createStylizedButton("BACKUP ARTICLES");
        Button restoreButton = createStylizedButton("RESTORE ARTICLES");
        Button openButton = createStylizedButton("OPEN FILE");
        Button searchButton = createStylizedButton("SEARCH");
        Button deleteAllArticlesButton = createStylizedButton("ERASE ALL ARTICLES");
        Button quitButton = createStylizedButton("EXIT");
       
        
        addButton.setOnAction(e -> showArticleCreationScreen());
        displayButton.setOnAction(e -> displaySelectedArticle());
        deleteButton.setOnAction(e -> deleteSelectedArticle());
        refreshButton.setOnAction(e -> refreshArticleList());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());
        openButton.setOnAction(e -> openFile());
        searchButton.setOnAction(e -> search());
        deleteAllArticlesButton.setOnAction(e -> deleteAllArticles());
        quitButton.setOnAction(e-> handleQuit());
        

        buttonBox = new HBox(10);
        
        int access = dbMan.groupAccess(currentUser.getUsername(), currentGroup);
//        System.out.println(access);
        
        if (access == 2
        		|| ((!currentUser.hasRole(ROLE.STUDENT)) && access == 3)
        		|| (currentUser.hasRole(ROLE.INSTRUCTOR)) && access == -1) {
        	buttonBox.getChildren().add(addButton);
        }
        
        if(currentUser.hasRole(ROLE.ADMIN)) {
        	buttonBox.getChildren().addAll(deleteButton, deleteAllArticlesButton, refreshButton, backupButton, restoreButton, openButton, searchButton, quitButton);
        }
        else if(currentUser.hasRole(ROLE.INSTRUCTOR)) {
            buttonBox.getChildren().addAll(displayButton, deleteButton, deleteAllArticlesButton, refreshButton, backupButton, restoreButton, openButton, searchButton, quitButton);
        }
        else if(currentUser.hasRole(ROLE.STUDENT))
        {
            buttonBox.getChildren().addAll(displayButton,refreshButton, searchButton, quitButton);

        }
        
        
       // buttonBox.getChildren().addAll(displayButton, deleteButton, deleteAllArticlesButton, refreshButton, backupButton, restoreButton, searchButton, quitButton);

        mainLayout.getChildren().addAll(titleLabel,searchBox, articleTable, buttonBox);

        Scene scene = new Scene(mainLayout, 1400, 700);
        Source.getPrimaryStage().setScene(scene);
        Source.getPrimaryStage().show();
        refreshArticleList();
        }

    /**
     * Creates a styled button with the given text.
     * 
     * @param text The text to display on the button
     * @return A styled Button instance
     */
    private Button createStylizedButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px 20px;");
        return button;
    }
    
    /**
     * Sets up the table view for displaying articles.
     */
	private void setupArticleTable() {
        articleTable = new TableView<>();
        articleTable.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white;");

        TableColumn<Article, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Article, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(new String(cellData.getValue().getTitle())));

        TableColumn<Article, String> authorColumn = new TableColumn<>("Author(s)");
        authorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(new String(cellData.getValue().getAuthors())));
        
        TableColumn<Article, String> groupColumn = new TableColumn<>("Group");
        groupColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(new String(cellData.getValue().getGroup())));

        articleTable.getColumns().addAll(idColumn,titleColumn, authorColumn, groupColumn);
    }
    
	// Function for choosing a new group to display articles from.
    private void displayGroup() throws Exception {
    	
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Group Selection");
        dialog.setHeaderText("Select a Group to Filter:");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(dbMan.listGroups());
        roleComboBox.setValue("General"); // Default value
        
        content.getChildren().addAll(
            new Label("Groups:"),
            roleComboBox
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                currentGroup = roleComboBox.getValue();
            }
        });
        show();
    }
    
    // Admin function for creating a new group.
    private void createGroup() throws Exception {
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Group Creation");
        dialog.setHeaderText("Create a Group:");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField nGroupName = new TextField();
        CheckBox specCheck = new CheckBox("Special Group");
        
        content.getChildren().addAll(
            new Label("Group Name:"),
            nGroupName,
            specCheck
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                currentGroup = nGroupName.getText();
                isSpecial = specCheck.isSelected();
                
                try {
					dbMan.createGroup(currentGroup, isSpecial);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        show();
    }
    
    // Admin function for adding a user to a special group.
    private void addToGroup() throws Exception {
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add User to Group");
        dialog.setHeaderText("User Name to Add:");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField userName = new TextField();
        CheckBox admin = new CheckBox("Admin");
        
        content.getChildren().addAll(
            new Label("User Name:"),
            userName,
            admin
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
            	User user = null;
                try {
					user = userMan.getUserByUsername(userName.getText());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                boolean isAdmin = admin.isSelected();
                
                try {
                	if (user == null) {
                		System.out.println("User Does not Exist.");
                	} else {
                		dbMan.addUserGroup(user, isAdmin, currentGroup);
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        show();
    } 
    
    // Admin function for managing users in a special group.
    private void manageGroup() throws SQLException, Exception {
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Manage User in Group");
        dialog.setHeaderText("User Name to Manage:");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField userName = new TextField();
        
        ChoiceBox<String> choice = new ChoiceBox<String>();
        choice.getItems().addAll("Delete", "Toggle Admin");
        
        content.getChildren().addAll(
            new Label("User Name:"),
            userName,
            choice
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
            	User user = null;
                try {
					user = userMan.getUserByUsername(userName.getText());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                String choiceS = choice.getSelectionModel().getSelectedItem();
                
                try {
                	if (user == null) {
                		System.out.println("User Does not Exist.");
                	} else if (choiceS.equals("Delete")) {
                		dbMan.deleteUserFromGroup(user.getUsername(), currentGroup);
                	} else if (choiceS.equals("Toggle Admin")) {
                		dbMan.toggleAdmin(user, currentGroup);
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        show();
    }
	
    // GUI function to give buttons for searching and group functionalitits.
    private void setupSearchBox() throws SQLException, Exception {
        searchBox = new HBox(10);
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter keyword to search");
        searchField.setStyle("-fx-background-color: #1C1C1C; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setPrefWidth(200);
        
        Button searchButton = createStylizedButton("SEARCH");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        Button chooseGroup = createStylizedButton("Choose Group");
        chooseGroup.setOnAction(e -> {
			try {
				displayGroup();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        Button createGroup = createStylizedButton("Create Group");
        createGroup.setOnAction(e -> {
			try {
				createGroup();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        Button addToGroup = createStylizedButton("Add User to Group");
        addToGroup.setOnAction(e -> {
			try {
				addToGroup();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        Button manageGroup = createStylizedButton("Manage Group Users");
        manageGroup.setOnAction(e -> {
			try {
				manageGroup();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        

        Button resetFile = createStylizedButton("Restore Original File");
        resetFile.setOnAction(e -> {
        	try {
				dbMan.newConnection("programDatabase");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	altFile = false;
        	try {
				show();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        if(currentUser.hasRole(ROLE.ADMIN)) {
            searchBox.getChildren().setAll(new Label("SEARCH BY KEYWORD:"), searchField, searchButton, chooseGroup, createGroup);

        }
        else if(currentUser.hasRole(ROLE.INSTRUCTOR)) {


            searchBox.getChildren().setAll(new Label("SEARCH BY KEYWORD:"), searchField, searchButton, chooseGroup, createGroup);

        }
        else if(currentUser.hasRole(ROLE.STUDENT))
        {
            searchBox.getChildren().setAll(new Label("SEARCH BY KEYWORD:"), searchField, searchButton, chooseGroup);

        }
        
        
        //searchBox.getChildren().setAll(new Label("SEARCH BY KEYWORD:"), searchField, searchButton, chooseGroup, createGroup);
        
        User currentUser = Source.getUIManager().getUser();
        int access = dbMan.groupAccess(currentUser.getUsername(), currentGroup);
        
        if (access == 1 || access == 2) {
        	searchBox.getChildren().addAll(addToGroup, manageGroup);
        }
        
        if (altFile) {
        	searchBox.getChildren().add(resetFile);
        }
    }
    
    
    private void handleQuit() {
    	
    	switch(Source.getUIManager().getSelectedRole()) {
    		case core.ROLE.INSTRUCTOR:
    			Source.getUIManager().loadInstructorPage();
    			break;
    		case core.ROLE.STUDENT:
    			Source.getUIManager().loadUserPage();
    			break;
    		case core.ROLE.ADMIN:
    			Source.getUIManager().loadAdminPage();
    			break;
    		default:
    			System.out.println("ARTICLE SYSTEM QUIT BUT COULD NOT FIND VALID ROLE TO RETURN TO");
    			return;
    	}
    	
    }
    
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            showAlert("Search Error", "Please enter a keyword to search.");
            return;
        }
        
        try {
            List<Article> searchResults = dbMan.searchByKeyword(keyword);
            articleTable.setItems(FXCollections.observableArrayList(searchResults));
            
            if (searchResults.isEmpty()) {
                showAlert("Search Results", "No articles found matching the keyword: " + keyword);
            }
        } catch (Exception e) {
            showAlert("Search Error", "An error occurred while searching: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the article creation screen.
     */
    private void showArticleCreationScreen() {
        ArticleCreationScreen creationScreen = new ArticleCreationScreen(dbMan, currentGroup, isSpecial);
        creationScreen.show();
    }

    /**
     * Displays the selected article in a new window.
     */
    private void displaySelectedArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
           try {
        	   if (dbMan.groupAccess(Source.getUIManager().getUser().getUsername(), currentGroup) != 1) {
        		   ArticleDisplayScreen dispArticle = new ArticleDisplayScreen(dbMan.getArticle(selectedArticle.getId()));
        	   } else {
        		   ArticleDisplayScreen dispArticle = new ArticleDisplayScreen(dbMan.getEncArticle(selectedArticle.getId()));
        	   }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        } else {
            showAlert("No Article Selected", "Please select an article to view.");
        }
    }

    
    private void deleteAllArticles() {
    	try {
			dbMan.clearAllArticles();
			refreshArticleList();
			showAlert("Deleted all articles", "Success!");
		} catch (SQLException e) {
			showAlert("Delete All Articles", "Error: Could not delete articles");
			e.printStackTrace();
		}
    	
    	
    	
    }
    
    
    /**
     * Deletes the selected article from the database.
     */
    private void deleteSelectedArticle() {
        Article selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
           try {
			dbMan.deleteArticle(selectedArticle.getId());
			refreshArticleList();
		} catch (Exception e) {
			showAlert("Delete Article", "Error: Could not delete article");
			e.printStackTrace();
		}
        } else {
            showAlert("No Article Selected", "Please select an article to delete.");
        }
    }
    
    /**
     * Refreshes the article list in the table view.
     */
    public void refreshArticleList() {
        try {
            List<Article> articles = dbMan.filterGroup(currentGroup);
            articleTable.setItems(FXCollections.observableArrayList(articles));
            
        } catch (Exception e) {
            showAlert("Refresh Error", "Failed to refresh article list: " + e.getMessage());
        }
    }

    
//    private void backupArticles() {
//    	System.out.println("INITIATING BACKUP");
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save Backup File");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Files", "*.bak"));
//        File file = fileChooser.showSaveDialog(null);
//        
//        if (file != null) {
//            try {
//            	System.out.println("CALLING BACKUP MANAGER");
//                backupMan.backupArticles(file.getAbsolutePath());
//                showAlert("Backup Successful", "Articles have been backed up successfully.");
//            } catch (Exception e) {
//                showAlert("Backup Failed", "Failed to backup articles: " + e.getMessage());
//            }
//        }
//    }
    
    /**
     * Initiates the backup process for articles or groups of articles.
     */
    private void backupArticles() {
    	
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Article Backup");
        dialog.setHeaderText("Back Up Articles");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField fileField = new TextField();
        CheckBox overWrite = new CheckBox("Overwrite File Content?");
        CheckBox groupC = new CheckBox("Only Backup Current Group?");
        Label newFile = new Label("Write to New File");
        Label doNot = new Label("Cannot Write to Main File");
        
        content.getChildren().addAll(
            new Label("File Name:"),
            fileField,
            groupC,
            newFile
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Text Field Listener checks for properties of the filename as it is typed.
        fileField.textProperty().addListener((observable, oldValue, newValue)
				-> {
					String filename = fileField.getText();
					File f = new File("Database/" + filename + ".mv.db");
					
					// The filename already exists. The option to overwrite or add
					// to the file will appear.
			    	if (f.exists() && !content.getChildren().contains(overWrite)
			    			&& !(filename.equals("programDatabase"))) {
			    		
			    		overWrite.setSelected(false);
			    		
			    		if (!content.getChildren().contains(overWrite)) {
			    			content.getChildren().add(overWrite);
			    		}
			    		content.getChildren().remove(newFile);
			    		content.getChildren().remove(doNot);
			    		
			    	// The filename is the default filename. Users will be unable to 
			    	// write to this file.
			    	} else if (filename.equals("programDatabase")) {
			    		
			    		overWrite.setSelected(false);
			    		
			    		if (!content.getChildren().contains(doNot)) {
			    			content.getChildren().add(doNot);
			    		}
			    		content.getChildren().remove(newFile);
			    		content.getChildren().remove(overWrite);
			    		
			    	// The filename does not yet exist.
			    	} else {
			    	
			    		overWrite.setSelected(false);
			    		
			    		if (!content.getChildren().contains(newFile)) {
			    			content.getChildren().add(newFile);
			    		}
			    		content.getChildren().remove(doNot);
			    		content.getChildren().remove(overWrite);
			    	}
				});
    	
        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                	
                	// The filename cannot be empty or be the default database name.
                	String file = fileField.getText();
                	if (!file.equals("") && !file.equals("programDatabase")) {
                		
                		altFile = true;
                		backupMan.backupArticles(fileField.getText(), overWrite.isSelected(),
                				groupC.isSelected(), currentGroup);
                		show();
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
    
    private void openFile() {
    	
    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Article File");
        dialog.setHeaderText("Open File");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField fileField = new TextField();
        
        content.getChildren().addAll(
            new Label("File Name:"),
            fileField
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                	
                	// The filename cannot be empty or be the default database name.
                	String file = fileField.getText();
                	if (!file.equals("") && !file.equals("programDatabase")) {
                		
                		altFile = true;
                		dbMan.newConnection(file);
                		show();
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
    
    /**
     * Initiates the restore process for articles from a backup file.
     */
    private void restoreArticles() {

    	Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Article Restoration");
        dialog.setHeaderText("Restore From File");

        // Create the content for the dialog
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.CENTER);

        // Create role selection combo box
        TextField fileField = new TextField();
        
        content.getChildren().addAll(
            new Label("File Name:"),
            fileField
        );

        // Set the content and add buttons
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                	
                	// The filename cannot be empty or be the default database name.
                	String file = fileField.getText();
                	if (!file.equals("") && !file.equals("programDatabase")) {
                		
                		altFile = false;
                		backupMan.restoreArticles(file);
                		show();
                	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
    
    private void search() {
    	
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
    
    public int testAccess(String group, User user, databaseInterface dbMan) throws SQLException, Exception {
    	
    	currentUser = user;
    	currentGroup = group;
    	
    	int test = -2;
    	
    	// Any admin 
    	if (currentUser.hasRole(ROLE.ADMIN)) {
    		test = 1;
    	// Any instructor has the ability to the 'Create Group' button. This means 
    	// that a 3 symbolizes the ability to create a new special group.
    	} else if (currentUser.hasRole(ROLE.INSTRUCTOR)) {
    		test = 3;
    	}
    	
    	/**
    	 * If the user has access to a special group, the groupAccess() function will return
    	 * a value based on their access level:
    	 * 
    	 * -1 - No Rights (We will return the regular values above in this case)
    	 * 0 - Standard Viewing Rights (Student, Basic Instructor)
    	 * 1 - Admin rights without viewing rights (Admin)
    	 * 2 - Instructor with Admin Rights (Instructor)
    	 * 3 - The group is general. Normal roles apply.
    	 */
    	test = dbMan.groupAccess(currentUser.getUsername(), group);
    	
    	return test;
    }

}