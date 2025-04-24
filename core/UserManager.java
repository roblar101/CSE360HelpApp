package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import core.databaseInterface;

public class UserManager {
	private databaseInterface dbInterface;
	
	// Create operation
    public void createUser(String username, String email, String name, String password) throws SQLException {
        String sql = "INSERT INTO USERS (USERNAME, EMAIL, FIRSTNAME, PASSWORD) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseInterface.getConnection();
        	PreparedStatement pstmt = conn.prepareStatement(sql)){
        		pstmt.setString(1, username);
        		pstmt.setString(2, email);
        		pstmt.setString(3, name);
        		pstmt.setString(4, password); // TODO HASH PASS
            
        		int affectedRows = pstmt.executeUpdate();
        		if (affectedRows == 0) {
        			throw new SQLException("Creating user failed, no rows affected.");
        		}
        		System.out.println("User created successfully.");
        	
        } catch (SQLException e){
        	e.printStackTrace();
        }
}
    
    // Read operation
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("USERNAME"),
                        rs.getString("EMAIL"),
                        rs.getString("FIRSTNAME"),
                        rs.getString("MIDDLENAME"),
                        rs.getString("LASTNAME"),
                        rs.getString("PREFERREDNAME"),
                        getRolesByUsername(username)
                    );
                } else {
                    return null; // User not found
                }
            }
        }
    }
    
    //Takes a username and checks if it already exists in the database
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    //Takes a username string and password string, then queries the database to try to find exactly one match.
    public boolean checkPassword(String username, String password) throws SQLException {
    	String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
    	try(Connection conn = databaseInterface.getConnection();
    			PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		pstmt.setString(1, username);
    		pstmt.setString(2, password);
    		
    		try (ResultSet rs = pstmt.executeQuery()) {
    			if(rs.next()) {
    				int count = rs.getInt(1);
    				if(count == 1){
        				System.out.println("LOGIN SUCCESS");
        				return true;
        			}
        			if(count == 0){
        				System.out.println("LOGIN FAILED");
        				return false;
        			} else {
        				System.out.println("LOGIN ATTEMPTED BUT MULTIPLE MATCHING USERS FOUND");
        				return false;
        			}
    			}
    			else
    			{
    				System.out.println("LOGIN ATTEMPTED BUT UserManager::checkPassword FAILED");
    				return false;
    			}
    			
    		}
    	}
    	
    }
    
    
    // Update operation
    public void updateUser(String username, String newEmail, String newPassword,String newName) throws SQLException {
        String sql = "UPDATE USERS SET EMAIL = ?, NAME = ?, PASSWORD = ? WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newName);
            pstmt.setString(3, newPassword);
            pstmt.setString(4, username);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("User updated sucessfully");
        }
    }
    
    public void updateUser(String username, String newEmail, String newName) throws SQLException {
        String sql = "UPDATE USERS SET EMAIL = ?, NAME = ? WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newName);
            pstmt.setString(3, username);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("User updated sucessfully");
        }
    }
    
    public void updatePassword(String username, String newPassword) throws SQLException {
    	String sql = "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("Password updated sucessfully");
        }
    }
    
    public void updateUser(String username, String firstName, String middleName, String lastName, String preferredName, String email) throws SQLException {
    	String sql = "UPDATE USERS SET FIRSTNAME = ?, MIDDLENAME = ?, LASTNAME = ?, PREFERREDNAME = ?, EMAIL = ? WHERE USERNAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, middleName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, preferredName);
            pstmt.setString(5, email);
            pstmt.setString(6, username);
            System.out.println("EXECUTING - " + pstmt.toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("User updated sucessfully");
        }
    	
    	
    }
    
    
    // Delete operation
    public void deleteUser(String username) throws SQLException {
        Connection conn = null;
        try {
            conn = databaseInterface.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // First, delete the user's roles
            String deleteRolesSql = "DELETE FROM USER_ROLES WHERE USER_ID = (SELECT ID FROM USERS WHERE USERNAME = ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteRolesSql)) {
                pstmt.setString(1, username);
                pstmt.executeUpdate();
            }

            // Then, delete the user
            String deleteUserSql = "DELETE FROM USERS WHERE USERNAME = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setString(1, username);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Deleting user failed, no user found with username: " + username);
                }
            }

            conn.commit();  // Commit transaction
            System.out.println("User deleted successfully.");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset to default
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Additional methods for user management
    
    public void assignRoleToUser(String username, String roleName) throws SQLException {
    	String sql = "INSERT INTO USER_ROLES (USER_ID, ROLE_ID) " +
                "SELECT U.ID, R.ID FROM USERS U, ROLES R " +
                "WHERE U.USERNAME = ? AND R.ROLE_NAME = ?";
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, roleName);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Assigning role to user failed, no rows affected.");
            }
        }
    }
    
    public boolean removeRoleFromUser(String username, String roleName) throws SQLException {
        String sql = "DELETE FROM USER_ROLES " +
                     "WHERE USER_ID = (SELECT ID FROM USERS WHERE USERNAME = ?) " +
                     "AND ROLE_ID = (SELECT ID FROM ROLES WHERE ROLE_NAME = ?)";
        
        try (PreparedStatement pstmt = databaseInterface.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, roleName);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Role '" + roleName + "' removed from user '" + username + "'");
                return true;
            } else {
                System.out.println("No role '" + roleName + "' found for user '" + username + "' or user not found");
                return false;
            }
        }
    }
    
    
    //method to get user roles by username
    public List<String> getRolesByUsername(String username) throws SQLException {
        String sql = "SELECT R.ROLE_NAME " +
                     "FROM USERS U " +
                     "JOIN USER_ROLES UR ON U.ID = UR.USER_ID " +
                     "JOIN ROLES R ON UR.ROLE_ID = R.ID " +
                     "WHERE U.USERNAME = ?";
        
        List<String> roles = new ArrayList<>();
        
        try (PreparedStatement pstmt = databaseInterface.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("ROLE_NAME"));
                }
            }
        }
        
        return roles;
    }
    
    public ArrayList<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM USERS";
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = databaseInterface.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getString("USERNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("FIRSTNAME")
                ));
            }
        }
        return users;
    }
    
    public UserManager() {
    	this.dbInterface = Source.getDatabase();
    }
    
    public UserManager(databaseInterface database) {
    	this.dbInterface = database;
    }
    
    public databaseInterface getDatabaseInterface() {
    	return dbInterface;
    }
}
