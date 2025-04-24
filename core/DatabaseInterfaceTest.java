package core;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.*;
import java.util.List;

public class DatabaseInterfaceTest {
    private static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String TEST_USER = "sa";
    private static final String TEST_PASS = "";
    
    private Connection connection;
    private databaseInterface dbInterface;
    private UserManager userMan;

    @Before
    public void setUp() throws Exception {
        // Initialize the database interface with test configuration
        dbInterface = new databaseInterface(TEST_DB_URL, TEST_USER, TEST_PASS);
        
        // Get connection for test setup
        connection = databaseInterface.getConnection();
        
        
        cleanupDatabase();
        

        // Create an instance of UserManager
        userMan = new UserManager();
        
        // 
        
       
        
        // Create necessary tables
        createTables();

        // Insert test user
        insertTestUser();
    }



    
    
    
    
    @After
    public void tearDown() throws Exception { 
      
      
        cleanupDatabase();
        // Close the connection
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    private void cleanupDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop all objects in the test database
            stmt.execute("DROP ALL OBJECTS");
        }
    }

   
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create ROLES table first since it's referenced by USER_ROLES
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ROLES (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "ROLE_NAME VARCHAR(20) UNIQUE NOT NULL" +
                ")"
            );

            // Create USERS table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS USERS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "USERNAME VARCHAR(50) NOT NULL UNIQUE, " +
                "EMAIL VARCHAR(100), " +
                "FIRSTNAME VARCHAR(100), " +
                "PASSWORD VARCHAR(64) NOT NULL" +
                ")"
            );

            // Create USER_ROLES table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS USER_ROLES (" +
                "USER_ID INT, " +
                "ROLE_ID INT, " +
                "PRIMARY KEY (USER_ID, ROLE_ID), " +
                "FOREIGN KEY (USER_ID) REFERENCES USERS(ID), " +
                "FOREIGN KEY (ROLE_ID) REFERENCES ROLES(ID)" +
                ")"
            );

            // Create GENERAL_QUESTIONS table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS GENERAL_QUESTIONS (" +
                "QUESTION_ID INT, " +
                "STUDENT_ID INT, " +
                "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
                "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
                "question TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Create SPECIFIC_QUESTIONS table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS SPECIFIC_QUESTIONS (" +
                "QUESTION_ID INT, " +
                "STUDENT_ID INT, " +
                "PRIMARY KEY (QUESTION_ID, STUDENT_ID), " +
                "FOREIGN KEY (STUDENT_ID) REFERENCES USERS(ID), " +
                "question TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Insert required roles
            stmt.execute("INSERT INTO ROLES (ROLE_NAME) VALUES ('STUDENT')");
            stmt.execute("INSERT INTO ROLES (ROLE_NAME) VALUES ('INSTRUCTOR')");
            stmt.execute("INSERT INTO ROLES (ROLE_NAME) VALUES ('ADMIN')");
        }
    }


    
    private void insertTestArticle() throws SQLException {
    	Article testArticle = new Article(
    			"Test Title".toCharArray(),
    	        "Test Author".toCharArray(),
    	        "Test Abstract".toCharArray(),
    	        "Test Keywords".toCharArray(),
    	        "Sensitive Content".toCharArray(),
    	        "Test References".toCharArray(),
    	        "General".toCharArray()
    			);
    	
    	try {
			dbInterface.addArticle(testArticle);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private void insertTestUser() throws SQLException {
        if (userMan.getUserByUsername("testuser") == null) {
            userMan.createUser("testuser", "test@asu.edu", "test", "test");
        }
    }

	    @Test
	    public void testAddGeneralQuestionValid() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", "What is Java?");
	        // Verify that the question was added
	        verifyQuestionCount("GENERAL_QUESTIONS", (getTableCount("GENERAL_QUESTIONS") + 1));
	    }

	    @Test
	    public void testAddGeneralQuestionInvalidUser() {
	        try {
				dbInterface.addGeneralQuestion("invaliduser", "What is Java?");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fail("Expected SQLException was not thrown");
	    }

	    @Test
	    public void testAddGeneralQuestionEmptyBody() throws Exception {
	       
	    	try {
	    		dbInterface.addGeneralQuestion("testuser", "");
	    		fail("Excepted Exception was not thrown");
	    	}catch (Exception e) {
	    		assertTrue(e.getMessage().contains("addGeneralQuestion was called with blank question body!"));
	    	}
	    	
	    }

	    @Test(expected = NullPointerException.class)
	    public void testAddGeneralQuestionNullBody() throws Exception {
	        dbInterface.addGeneralQuestion("testuser", null);
	    }

	    @Test
	    public void testAddSpecificQuestionValid() throws Exception {
	        dbInterface.addSpecificQuestion("testuser", "Explain polymorphism.");
	        // Verify that the question was added
	        verifyQuestionCount("SPECIFIC_QUESTIONS", getTableCount("SPECIFIC_QUESTIONS") + 1);
	    }

	    @Test
	    public void testAddSpecificQuestionInvalidUser() {
	        try {
	            dbInterface.addSpecificQuestion("invaliduser", "Explain polymorphism.");
	            fail("Expected SQLException was not thrown");
	        } catch (Exception e) {
	            assertTrue(e.getMessage().contains("User not found: invaliduser"));
	        }
	    }

	    @Test
	    public void testGetGeneralQuestionsNoQuestions() throws Exception {
	        List<String> questions = dbInterface.getGeneralQuestions();
	        assertNotNull(questions);
	        assertEquals(0, questions.size());
	    }

	    @Test
	    public void testGetGeneralQuestionsWithQuestions() throws Exception {
	    	dbInterface.addGeneralQuestion("testuser", "What is Java?");
	        dbInterface.addGeneralQuestion("testuser", "Explain OOP.");
	        List<String> questions = dbInterface.getGeneralQuestions();
	        assertNotNull(questions);
	        assertEquals(getTableCount("GENERAL_QUESTIONS"), questions.size());
	        assertTrue(questions.get(0).contains("Explain OOP."));
	        assertTrue(questions.get(1).contains("What is Java?"));
	    }

	    private void verifyQuestionCount(String tableName, int expectedCount) throws SQLException {
	        String query = "SELECT COUNT(*) FROM " + tableName + ";";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            assertTrue(rs.next());
	            int count = rs.getInt(1);
	            assertEquals(expectedCount, count);
	        }
	    }
	    
	    private int getTableCount(String tableName) throws SQLException {
	        String query = "SELECT COUNT(*) FROM " + tableName + ";";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            // Move to first row if it exists
	            if (rs.next()) {
	                // Get the count from first column
	                return rs.getInt(1);
	            }
	            // Return 0 if no results
	            return 0;
	        }
	    }

}

