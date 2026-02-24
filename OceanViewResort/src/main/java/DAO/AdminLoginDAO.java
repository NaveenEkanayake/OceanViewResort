package DAO;

import java.sql.*;
import DB.DBConnect; // Assuming this handles your URL, Uname, and Password
import Model.AdminLogin;
import org.mindrot.jbcrypt.BCrypt;

public class AdminLoginDAO {

    // --- LOGIN LOGIC (Integrated from LoginDao) ---
    public boolean validateAdmin(String username, String plainPassword) {
        if (username == null || plainPassword == null || username.isEmpty()) {
            return false; 
        }
        
        // We only select the password to verify it via BCrypt
        String sql = "SELECT password FROM admins WHERE username = ?";
        
        try (Connection con = DBConnect.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPw = rs.getString("password");
                    // Securely compare plain text input with hashed DB password
                    return BCrypt.checkpw(plainPassword, hashedPw);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }

    // --- REGISTRATION LOGIC (Integrated from RegisterDao) ---
    public String registerAdmin(AdminLogin admin) {
        String checkSql = "SELECT COUNT(*) FROM admins WHERE username = ? OR email = ?";
        String insertSql = "INSERT INTO admins (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnect.getConnection()) {
            
            // 1. Duplicate Check (Username or Email)
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, admin.getUsername());
                checkPs.setString(2, admin.getEmail());
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return "Error: Username or Email already exists";
                }
            }

            // 2. Hash Password and Insert
            String hashedPw = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, admin.getUsername());
                ps.setString(2, admin.getEmail());
                ps.setString(3, hashedPw);
                
                int result = ps.executeUpdate();
                return (result > 0) ? "Data entered successfully" : "Data not entered";
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return "Error: " + e.getMessage(); 
        }
    }
}