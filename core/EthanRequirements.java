package core;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;

import ui.*;

public class EthanRequirements {
	
	private static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String TEST_USER = "sa";
    private static final String TEST_PASS = "";
	
    private databaseInterface dbInterface;
    private UserManager userMan;
    private HelpArticleSystem art;
    private User testUser;

    @Before
    public void setUp() throws Exception {
    	
    	dbInterface = new databaseInterface(TEST_DB_URL, TEST_USER, TEST_PASS);
    	
    	// Cleanup any previous database artifacts
        try (Connection connection = databaseInterface.getConnection()) {
            cleanupDatabase(connection);
            createTables(connection);
        }
    	
    	userMan = new UserManager(dbInterface);
    	art = new HelpArticleSystem(dbInterface);
    	
    	testUser = new User("TEST", "T", "T");
    	testUser.addRole(ROLE.ADMIN);
    	
    	insertTestUser();
    }
    
    @After
    public void tearDown() throws Exception {
        // Cleanup the database
        try (Connection connection = databaseInterface.getConnection()) {
            cleanupDatabase(connection);
        }
    }
    
    private void cleanupDatabase(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) {
            try (var stmt = connection.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
        }
    }
    
    private void createTables(Connection connection) throws Exception {
        dbInterface.createUsersTable(connection);
        dbInterface.createRolesTable(connection);
        dbInterface.populateRolesTable(connection);
        dbInterface.createUserRolesTable(connection);
        dbInterface.createSkillsTable(connection);
        dbInterface.createUserSkillsTable(connection);
        dbInterface.createArticleTables(connection);
        dbInterface.createGroupTable(connection);
        dbInterface.createGenArticlesTable(connection);
        dbInterface.createInviteCodesTable(connection);
        dbInterface.createGeneralQuestionsTable(connection);
        dbInterface.createSpecificQuestionsTable(connection);
        dbInterface.createSessionTable(connection);
        dbInterface.createSessionStudentsTable(connection);
    }
    
    private void insertTestUser() throws Exception {
        if (userMan.getUserByUsername("testuser") == null) {
            // Create a new test user
            userMan.createUser("testuser", "test@asu.edu", "test", "password");
            // Assign role to the test user
            userMan.assignRoleToUser("testuser", "STUDENT");
        }
    }
    
    @Test
    public void testCreateGroupWithViewingRights() throws Exception {
        // Create a general group and verify a student can be added
    	
        dbInterface.createTestGroup("General Group", false, testUser);
        dbInterface.addUserGroup(userMan.getUserByUsername("testuser"), false, "General Group");

        boolean isInGroup = dbInterface.userInGroup("testuser", "General Group");
        assertTrue("Student should be part of the general group", isInGroup);
    }

    @Test
    public void testAddUserToGroup() throws Exception {
        // Add user and assign them to a group
        User testUser = new User("testuser", "test@asu.edu", "test");
        dbInterface.addUserGroup(testUser, false, "General Group");

        // Verify that the user is in the group
        boolean isInGroup = dbInterface.userInGroup("testuser", "General Group");
        assertTrue("User should be added to the group", isInGroup);
    }

    @Test
    public void testUserAccessLevelInGroup() throws Exception {
        // Add user and assign them viewing rights in a group
        User testUser = new User("testuser", "test@asu.edu", "test");
        dbInterface.addUserGroup(testUser, false, "General Group");

        // Verify that the user has viewing rights (access level 0)
        int accessLevel = dbInterface.groupAccess("testuser", "General Group");
        assertEquals("User should have viewing rights", 0, accessLevel);
    }
    
  //Students may send generic or specific help messages to the help system. - 2nd requirement tested in JUnit
    @Test
    public void testAddGeneralQuestion() throws Exception {
        // Add a general question to the system
        dbInterface.addGeneralQuestion("testuser", "How does encryption work?");

        // Retrieve the list of questions and verify it contains the added question
        List<String> questions = dbInterface.getGeneralQuestions();
        assertFalse("The questions list should not be empty", questions.isEmpty());
        assertTrue("The questions list should contain the added question",
                questions.get(0).contains("How does encryption work?"));
    }

    @Test
    public void testAddSpecificQuestion() throws Exception {
        // Add a specific question to the system
        dbInterface.addSpecificQuestion("testuser", "Explain polymorphism in Java.");

        // Retrieve the list of specific questions and verify it contains the added question
        List<String> questions = dbInterface.getSpecificQuestions();
        assertFalse("The specific questions list should not be empty", questions.isEmpty());
        assertTrue("The specific questions list should contain the added question",
                questions.get(0).contains("Explain polymorphism in Java."));
    }

    @Test
    public void testGetGeneralQuestions() throws Exception {
        // Add multiple general questions
        dbInterface.addGeneralQuestion("testuser", "What is Java?");
        dbInterface.addGeneralQuestion("testuser", "Explain OOP.");

        // Retrieve the questions and verify them
        List<String> questions = dbInterface.getGeneralQuestions();
        assertEquals("There should be two general questions", 2, questions.size());
        assertTrue("First question should be 'What is Java?'", questions.get(1).contains("What is Java?"));
        assertTrue("Second question should be 'Explain OOP.'", questions.get(0).contains("Explain OOP."));
    }

    @Test
    public void testGetSpecificQuestions() throws Exception {
        // Add multiple specific questions
        dbInterface.addSpecificQuestion("testuser", "What is abstraction?");
        dbInterface.addSpecificQuestion("testuser", "What is inheritance?");

        // Retrieve the specific questions and verify them
        List<String> questions = dbInterface.getSpecificQuestions();
        assertEquals("There should be two specific questions", 2, questions.size());
        assertTrue("First question should be 'What is abstraction?'", questions.get(1).contains("What is abstraction?"));
        assertTrue("Second question should be 'What is inheritance?'", questions.get(0).contains("What is inheritance?"));
    }
    
    @Test
    public void testInstrRights() throws Exception {
    	User instructor = new User("Instructor", "test", "I");
    	instructor.addRole(ROLE.INSTRUCTOR);
    	
    	// Create a group with the instructor as the creator.
    	dbInterface.createTestGroup("TESTGROUP", true, instructor);
    	
    	// Because the instructor created the group, they should be an admin.
    	assertEquals(art.testAccess("TESTGROUP", instructor, dbInterface), 2);
    }
    
    @Test
    public void testAdminRights() throws Exception {
    	User admin = new User("Admin", "test", "A");
    	admin.addRole(ROLE.ADMIN);
    	
    	dbInterface.createTestGroup("GENGROUP", false, admin);
    	dbInterface.createTestGroup("ACCESSGROUP", true, admin);
    	
    	assertEquals(art.testAccess("GENGROUP", admin, dbInterface), 3);
    	assertEquals(art.testAccess("ACCESSGROUP", admin, dbInterface), 1);
    	assertEquals(art.testAccess("TESTGROUP", admin, dbInterface), -1);
    }
}
