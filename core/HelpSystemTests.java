package core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HelpSystemTests {
    private databaseInterface db;
    private UserManager userManager;
    private BackupManager backupManager;
    private Article testArticle;
    private User adminUser;
    private User regularUser;

    @Before
    public void setup() throws Exception {
        // Initialize managers
        db = new databaseInterface();
        userManager = new UserManager(db);
        backupManager = new BackupManager(db);

        // Clean up any existing test data
        cleanupTestData();

        // Create test article
        db = new databaseInterface();
        testArticle = new Article(
            "Test Title".toCharArray(),
            "Test Author".toCharArray(),
            "Test Abstract".toCharArray(),
            "Test Keywords".toCharArray(),
            "Sensitive Content".toCharArray(),
            "Test References".toCharArray(),
            "General".toCharArray()
        );

        // Create test users
        try {
            userManager.createUser("admin1", "admin@test.com", "Admin User", "adminpass");
            userManager.createUser("user1", "user@test.com", "Regular User", "userpass");
            userManager.assignRoleToUser("admin1", "ADMIN");
            userManager.assignRoleToUser("user1", "STUDENT");

            adminUser = userManager.getUserByUsername("admin1");
            regularUser = userManager.getUserByUsername("user1");
        } catch (SQLException e) {
            // If creation fails, clean up and rethrow
            cleanupTestData();
            throw e;
        }
    }


 
    
    // Test Case 1: Data Storage Encryption
    @Test
    public void testDataEncryption() throws Exception {
        // Add article to database
        db.addArticle(testArticle);
        
        // Get raw data from database to verify encryption
        Article storedArticle = db.getArticle(testArticle.getId());
        String originalContent = new String(testArticle.getBody());
        String storedContent = new String(storedArticle.getBody());
        
        // Verify stored content is not plaintext
        assertNotEquals("Content should be encrypted", originalContent, storedContent);
    }

    // Test Case 2: Unauthorized Access Returns Encrypted Data
    @Test
    public void testUnauthorizedAccess() throws Exception {
        db.addArticle(testArticle);
        
        // Attempt to access article as regular user
        Article retrievedArticle = db.getArticle(testArticle.getId());
        String originalContent = "Sensitive Content";
        String retrievedContent = new String(retrievedArticle.getBody());
        
        // Verify content is still encrypted for unauthorized user
        assertNotEquals("Content should remain encrypted for unauthorized users", 
                       originalContent, retrievedContent);
    }

    // Test Case 3: Authorized Access Returns Decrypted Data
    @Test
    public void testAuthorizedAccess() throws Exception {
        db.addArticle(testArticle);
        
        // Access article with proper authorization
        Article retrievedArticle = db.getArticle(testArticle.getId());
        String expectedContent = "Sensitive Content";
        
        // Decrypt the content (assuming we're in authorized context)
        byte[] decrypted = new EncryptionHelper().decrypt(
            retrievedArticle.getBody().toString().getBytes(),
            EncryptionUtils.getInitializationVector(retrievedArticle.getTitle())
        );
        
        String decryptedContent = new String(decrypted);
        assertEquals("Content should be decrypted for authorized users", 
                    expectedContent, decryptedContent);
    }

    // Test Case 4: Special Access Group Creation
    @Test
    public void testSpecialGroupCreation() throws Exception {
        String groupName = "TestSpecialGroup";
        
        // Create special access group
        db.createGroup(groupName, true);
        
        // Verify group exists and has special access flag
        assertTrue("Special group should exist", db.groupExists(groupName));
        assertTrue("Group should have special access", db.isSpecialGroup(groupName));
    }

    // Test Case 5: Article Association with Special Group
    @Test
    public void testArticleGroupAssociation() throws Exception {
        String groupName = "TestSpecialGroup";
        db.createGroup(groupName, true);
        
        Article specialArticle = new Article(
            "Special Title".toCharArray(),
            "Special Author".toCharArray(),
            "Special Abstract".toCharArray(),
            "Special Keywords".toCharArray(),
            "Special Content".toCharArray(),
            "Special References".toCharArray(),
            groupName.toCharArray()
        );
        
        db.addArticle(specialArticle);
        
        // Verify article is associated with special group
        Article retrieved = db.getArticle(specialArticle.getId());
        assertEquals("Article should be in special group", 
                    groupName, new String(retrieved.getGroup()));
    }

    // Test Case 6: Special Group Access Controls
    @Test
    public void testSpecialGroupAccess() throws Exception {
        String groupName = "TestSpecialGroup";
        db.createGroup(groupName, true);
        
        // Create article in special group
        Article specialArticle = new Article(
            "Special Title".toCharArray(),
            "Special Author".toCharArray(),
            "Special Abstract".toCharArray(),
            "Special Keywords".toCharArray(),
            "Special Content".toCharArray(),
            "Special References".toCharArray(),
            groupName.toCharArray()
        );
        
        db.addArticle(specialArticle);
        
        // Test regular user access (should be denied)
        assertFalse("Regular user should not have access",
                   db.canAccessGroup(regularUser.getUsername(), groupName));
        
        // Grant access to regular user
        db.grantGroupAccess(regularUser.getUsername(), groupName);
        
        // Verify access after grant
        assertTrue("User should now have access",
                  db.canAccessGroup(regularUser.getUsername(), groupName));
    }

    @After
    public void tearDown() throws SQLException {
        cleanupTestData();
    }

    
    /**
     * Cleans up the test data from the database
     */
    private void cleanupTestData() throws SQLException {
        try (Connection conn = db.getConnection()) {
            // Delete test users and their roles
            String[] cleanupQueries = {
                "DELETE FROM USER_ROLES WHERE USER_ID IN (SELECT ID FROM USERS WHERE USERNAME IN ('admin1', 'user1'))",
                "DELETE FROM USERS WHERE USERNAME IN ('admin1', 'user1')",
                "DELETE FROM group_access WHERE USER_NAME IN (SELECT USER_NAME FROM USERS WHERE USERNAME IN ('admin1', 'user1'))",
                "DELETE FROM help_articles WHERE TITLE = 'Test Title'"
            };

            for (String query : cleanupQueries) {
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.executeUpdate();
                }
            }
        }
    }
}