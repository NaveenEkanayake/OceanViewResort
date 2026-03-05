package WEB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;
import DB.DBConnect;
import util.CredentialEmailUtil;
import util.CredentialGenerator;

@WebServlet("/AdminForgotPasswordNewServlet")
public class AdminForgotPasswordNewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Map<String, String> otpStore = new HashMap<>();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if ("sendOTP".equals(action)) {
                handleSendOTP(request, response);
            } else if ("verifyOTP".equals(action)) {
                handleVerifyOTP(request, response);
            } else if ("resetPassword".equals(action)) {
                handleResetPassword(request, response);
            } else {
                sendErrorResponse(response, false, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, false, "Server error: " + e.getMessage());
        }
    }
    
    private void handleSendOTP(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        
        // Validate email exists in database (check admins table)
        if (!adminExistsByEmail(email)) {
            sendErrorResponse(response, false, "No admin found with this email address");
            return;
        }
        
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        // Store OTP with email (expires in 10 minutes in production)
        otpStore.put(email, otp);
        
        System.out.println("Generated OTP for admin " + email + ": " + otp);
        
        // Send professional OTP email for ADMIN
        boolean emailSent = CredentialEmailUtil.sendAdminOTPEmail(email, otp);
        
        if (emailSent) {
            // Return success (OTP is shown for demo - remove in production)
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Verification code sent successfully to your email");
            responseData.put("otpCode", otp); // REMOVE IN PRODUCTION
            
            response.getWriter().write(gson.toJson(responseData));
        } else {
            sendErrorResponse(response, false, "Failed to send verification email. Please try again.");
        }
    }
    
    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        String enteredOTP = request.getParameter("otp");
        
        String storedOTP = otpStore.get(email);
        
        if (storedOTP != null && storedOTP.equals(enteredOTP)) {
            // OTP verified successfully
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Verification code validated");
            response.getWriter().write(gson.toJson(responseData));
            
            // Clear OTP after successful verification
            otpStore.remove(email);
        } else {
            sendErrorResponse(response, false, "Invalid verification code");
        }
    }
    
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        
        // Get admin by email to find their username
        String adminUsername = getAdminUsernameByEmail(email);
        
        if (adminUsername == null) {
            sendErrorResponse(response, false, "Admin not found");
            return;
        }
        
        // Hash the new password using SHA-256
        String hashedPassword = CredentialGenerator.hashPassword(newPassword);
        
        // Update password in admin_login table using username
        boolean updated = updateAdminPasswordByUsername(adminUsername, hashedPassword);
        
        if (updated) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Password reset successfully");
            response.getWriter().write(gson.toJson(responseData));
        } else {
            sendErrorResponse(response, false, "Failed to update password");
        }
    }
    
    // Check if admin exists by email (in admins table)
    private boolean adminExistsByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    // Get admin username by email
    private String getAdminUsernameByEmail(String email) throws SQLException {
        String sql = "SELECT username FROM admin_login WHERE admin_id = (SELECT admin_id FROM admins WHERE email = ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }
    
    // Update password by username (in admin_login table)
    private boolean updateAdminPasswordByUsername(String username, String hashedPassword) throws SQLException {
        String sql = "UPDATE admin_login SET password = ? WHERE username = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setString(2, username);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Generate 6-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    // Send JSON error response
    private void sendErrorResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", success);
        responseData.put("message", message);
        response.getWriter().write(gson.toJson(responseData));
    }
}
