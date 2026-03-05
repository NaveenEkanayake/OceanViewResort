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
import DAO.EmployeeDAO;
import Model.Employee;

@WebServlet("/EmployeeForgotPasswordServlet")
public class EmployeeForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Map<String, String> otpStore = new HashMap<>();
    private Gson gson = new Gson();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
    
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
        
        // Validate email exists in database (check employees table)
        if (!employeeExistsByEmail(email)) {
            sendErrorResponse(response, false, "No employee found with this email address");
            return;
        }
        
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        // Store OTP with email (expires in 10 minutes in production)
        otpStore.put(email, otp);
        
        System.out.println("Generated OTP for " + email + ": " + otp);
        
        // Send professional OTP email
        boolean emailSent = CredentialEmailUtil.sendOTPEmail(email, otp);
        
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
        
        // Get employee by email to find their username
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Model.Employee employee = employeeDAO.getEmployeeByEmail(email);
        
        if (employee == null || employee.getUsername() == null) {
            sendErrorResponse(response, false, "Employee not found");
            return;
        }
        
        // Hash the new password
        String hashedPassword = CredentialGenerator.hashPassword(newPassword);
        
        // Update password in employee_login table using username
        boolean updated = updateEmployeePasswordByUsername(employee.getUsername(), hashedPassword);
        
        if (updated) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Password reset successfully");
            response.getWriter().write(gson.toJson(responseData));
        } else {
            sendErrorResponse(response, false, "Failed to update password");
        }
    }
    
    // Check if employee exists by email (in employees table)
    private boolean employeeExistsByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM employees WHERE email = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    // Update password by username (in employee_login table)
    private boolean updateEmployeePasswordByUsername(String username, String hashedPassword) throws SQLException {
        String sql = "UPDATE employee_login SET password = ? WHERE username = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, hashedPassword);
            ps.setString(2, username);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    private void sendErrorResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", success);
        responseData.put("message", message);
        response.getWriter().write(gson.toJson(responseData));
    }
}
