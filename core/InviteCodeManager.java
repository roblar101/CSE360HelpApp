package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class InviteCodeManager {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    
    // Generate a new invite code
    public String generateInviteCode(String role) throws SQLException {
        String code = generateRandomCode();
        
        String sql = "INSERT INTO INVITE_CODES (CODE, ROLE) VALUES (?, ?)";
                     
        try (Connection conn = databaseInterface.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, role);
            
            pstmt.executeUpdate();
            return code;
        }
    }
    
    // Use an invite code - returns the role if valid, null if invalid
    public String useInviteCode(String code) throws SQLException {
        Connection conn = databaseInterface.getConnection();
        conn.setAutoCommit(false);
        
        try {
            // Check if code exists and get its role
            String checkSql = "SELECT ROLE FROM INVITE_CODES WHERE CODE = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, code);
                ResultSet rs = pstmt.executeQuery();
                
                if (!rs.next()) {
                    return null; // Code doesn't exist
                }
                
                String role = rs.getString("ROLE");
                
                // Delete the used code
                String deleteSql = "DELETE FROM INVITE_CODES WHERE CODE = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, code);
                    deleteStmt.executeUpdate();
                }
                
                conn.commit();
                return role;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        return code.toString();
    }
}
