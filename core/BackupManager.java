/**
 * <p> Title: Backup Manager </p>
 * 
 * <p> Description: Manages the backup and restoration of help system articles </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author Department of Defense Software Division
 * 
 * @version 1.0    2024-10-19    Initial implementation
 */
package core;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import core.databaseInterface;
import core.EncryptionHelper;
import core.EncryptionUtils;

/**
 * This class manages the backup and restoration of articles in the help system.
 * It provides functionality to securely backup articles to a file and restore them from a backup file.
 */
public class BackupManager {
    private databaseInterface dbMan;

    /**
     * Constructs a new BackupManager with the given DatabaseManager.
     * 
     * @param dbMan The DatabaseManager instance to use for article operations
     * @throws Exception If there's an error initializing the EncryptionHelper
     */
    public BackupManager(databaseInterface dbMan) throws Exception {
        this.dbMan = dbMan;
    }
    
    /**
     * Backs up all articles to a specified file.
     * 
     * @param fileName - The name of the file to save the backup to
     * @param overWrite - Whether or not the file will keep its original articles
     * @param onlyGroup - Whether or not only articles in a specific group will be backed up
     * @param groupName - The name of the group to back up (if onlyGroup is true)
     * 
     * @throws Exception If there's an error during the backup process
     */
    public void backupArticles(String filename, boolean overWrite, boolean onlyGroup, String groupName) throws SQLException, Exception {
    	
    	List<Article> articles = null;
    	
    	if (onlyGroup) {
    		articles = dbMan.filterGroup(groupName);
    	} else {
    		articles = dbMan.getAllArticles();
    	}
    	
    	if (overWrite) {
    		File f = new File("Database/" + filename + ".mv.db");
    		f.delete();
    	}
    	
    	setupFile(filename, onlyGroup, groupName);
    	for (Article article: articles) {
    		
    		dbMan.addArticle(article);
    	}
    }
    
    /**
     * Sets up the Database Interface to handle a new file.
     * 
     * @param fileName - The name of the file to save the backup to
     * @param onlyGroup - Whether or not only articles in a specific group will be backed up
     * @param groupName - The name of the group to back up (if onlyGroup is true)
     * 
     * @throws Exception If there's an error during the backup process
     */
    private void setupFile(String filename, boolean onlyGroup, String groupName) throws Exception {
    	
    	List<String> groups = dbMan.getGroups();
    	dbMan.newConnection(filename);
    	
    	if (onlyGroup) {
    		
    		dbMan.createGroup(groupName, false);
    	} else {
    		
    		for (String group: groups) {
    			
    			dbMan.createGroup(group, false);
    		}
    	}
    }
    
    /**
     * Restores articles from a separate file.
     * 
     * @param fileName - The name of the file to take from
     * 
     * @throws Exception If there's an error during the backup process
     */
    public void restoreArticles(String filename) throws Exception {
    	
    	dbMan.newConnection(filename);
    	
    	List<Article> articles = dbMan.getAllArticles();
    	List<String> groups = dbMan.getGroups();
    	
    	dbMan.newConnection("programDatabase");
    	
    	List<Article> exArticles = dbMan.getAllArticles();
    	List<String> exGroups = dbMan.getGroups();
    	
    	for (String group: groups) {
    		
    		if (!exGroups.contains(group)) {
    			
    			dbMan.createGroup(group, false);
    		}
    	}
    	for (Article article: articles) {
    		
    		if (!exArticles.contains(article)) {
    			
    			dbMan.addArticle(article);
    		}
    	}
    }
}