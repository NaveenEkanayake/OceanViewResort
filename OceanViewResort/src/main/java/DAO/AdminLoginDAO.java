package DAO;

import java.sql.*;
import DB.DBConnect;
import Model.AdminLogin;
import org.mindrot.jbcrypt.BCrypt;

public class AdminLoginDAO {

    // --- LOGIN LOGIC ---
    public boolean validateAdmin(String username, String plainPassword) {
        String sql = "SELECT password FROM admin_login WHERE username = ?";
        
        try (Connection con = DBConnect.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String hashedPw = rs.getString("password");
                return BCrypt.checkpw(plainPassword, hashedPw);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }

    // --- REGISTRATION LOGIC ---
    public String registerAdmin(AdminLogin admin) {
        String checkSql = "SELECT COUNT(*) FROM admin_login WHERE username = ?";
        String insertSql = "INSERT INTO admin_login (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnect.getConnection()) {
            
            // 1. Duplicate Username Check
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, admin.getUsername());
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return "Error: Username already exists";
                }
            }

            // 2. Hash Password and Insert
            String hashedPw = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, admin.getUsername());
                ps.setString(2, admin.getEmail());
                ps.setString(3, hashedPw);
                
                int result = ps.executeUpdate();
                
                // Get generated ID
                if (result > 0) {
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        admin.setId(generatedKeys.getInt(1));
                    }
                    return "Data entered successfully";
                }
                return "Data not entered";
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return "Error: " + e.getMessage(); 
        }
    }
    
    // Get admin by email
    public AdminLogin getAdminByEmail(String email) {
        String sql = "SELECT id, username, email, password FROM admin_login WHERE email = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                AdminLogin admin = new AdminLogin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setEmail(rs.getString("email"));
                admin.setPassword(rs.getString("password"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get admin by username
    public AdminLogin getAdminByUsername(String username) {
        String sql = "SELECT id, username, email, password FROM admin_login WHERE username = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                AdminLogin admin = new AdminLogin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setEmail(rs.getString("email"));
                admin.setPassword(rs.getString("password"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAdminPassword(String username, String hashedPassword) {
        String sql = "UPDATE admin_login SET password = ? WHERE username = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}