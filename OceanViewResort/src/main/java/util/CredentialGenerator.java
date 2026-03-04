package util;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CredentialGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final SecureRandom random = new SecureRandom();
    
    // Generate username based on first name and last name
    public static String generateUsername(String firstName, String lastName) {
        // Convert to lowercase and remove spaces
        String first = firstName.toLowerCase().replaceAll("\s+", "");
        String last = lastName.toLowerCase().replaceAll("\s+", "");
            
        // Create base username
        String username = first + "." + last;
            
        // Ensure username is not too long (max 20 characters)
        if (username.length() > 20) {
            username = username.substring(0, 20);
        }
            
        return username;
    }
    
    // Generate secure random password
    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        // 1 uppercase letter
        password.append(CHARACTERS.charAt(random.nextInt(26)));
        // 1 lowercase letter  
        password.append(CHARACTERS.charAt(26 + random.nextInt(26)));
        // 1 digit
        password.append(CHARACTERS.charAt(52 + random.nextInt(10)));
        // 1 special character
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        
        // Fill remaining 8 characters randomly
        for (int i = 4; i < 12; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        // Shuffle the password characters
        return shuffleString(password.toString());
    }
    
    // Hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, hashedBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            
            // Pad with leading zeros if necessary
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    // Verify password
    public static boolean verifyPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hashedPassword);
    }
    
    // Helper method to shuffle string characters
    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap characters
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
    
    // Generate employee ID (for display purposes)
    public static String generateEmployeeId(int databaseId) {
        return String.format("EMP%04d", databaseId);
    }
    
    // Generate professional HTML email for credentials
    public static String generateCredentialsEmail(String firstName, String lastName, String username, String password, String position) {
        String fullName = firstName + " " + lastName;
        String loginUrl = "http://localhost:8080/Ocean_View/Pages/EmployeeLogin.jsp";
        
        return "<!DOCTYPE html>"
            + "<html>"
            + "<head>"
            + "<style>"
            + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }"
            + ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }"
            + ".header { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
            + ".header h1 { margin: 0; font-size: 24px; }"
            + ".content { padding: 40px 30px; }"
            + ".welcome { font-size: 18px; color: #333; margin-bottom: 20px; }"
            + ".credentials-box { background-color: #f8f9fa; border-left: 4px solid #2a5298; padding: 20px; margin: 20px 0; border-radius: 5px; }"
            + ".credential-item { margin: 15px 0; font-size: 16px; }"
            + ".label { font-weight: bold; color: #555; display: inline-block; width: 100px; }"
            + ".value { color: #2a5298; font-family: monospace; font-size: 18px; font-weight: bold; }"
            + ".password { color: #d9534f; }"
            + ".instructions { background-color: #fff3cd; border: 1px solid #ffc107; padding: 15px; border-radius: 5px; margin: 20px 0; }"
            + ".instructions h3 { margin-top: 0; color: #856404; }"
            + ".instructions ul { margin: 10px 0; padding-left: 20px; }"
            + ".instructions li { margin: 5px 0; color: #856404; }"
            + ".login-button { text-align: center; margin: 30px 0; }"
            + ".login-button a { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); color: white; padding: 15px 40px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }"
            + ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 0 0 10px 10px; font-size: 12px; color: #777; }"
            + ".footer p { margin: 5px 0; }"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<div class='container'>"
            + "<div class='header'>"
            + "<h1>🌊 Ocean View Resort</h1>"
            + "<p>Welcome to Our Team!</p>"
            + "</div>"
            + "<div class='content'>"
            + "<div class='welcome'>"
            + "<p>Dear " + fullName + ",</p>"
            + "<p>Congratulations! Your employee account has been successfully created for the <strong>Ocean View Resort Management System</strong>.</p>"
            + "<p><strong>Position:</strong> " + position + "</p>"
            + "</div>"
            + "<div class='credentials-box'>"
            + "<h3 style='margin-top: 0; color: #1e3c72;'>🔐 Your Login Credentials</h3>"
            + "<div class='credential-item'>"
            + "<span class='label'>Username:</span>"
            + "<span class='value'>" + username + "</span>"
            + "</div>"
            + "<div class='credential-item'>"
            + "<span class='label'>Password:</span>"
            + "<span class='value password'>" + password + "</span>"
            + "</div>"
            + "</div>"
            + "<div class='instructions'>"
            + "<h3>⚠️ Important Security Instructions</h3>"
            + "<ul>"
            + "<li>Keep your credentials confidential and do not share with anyone.</li>"
            + "<li>Change your password after your first login for enhanced security.</li>"
            + "<li>Use a strong, unique password that includes letters, numbers, and symbols.</li>"
            + "<li>Never access the system from public or unsecured networks.</li>"
            + "<li>Log out properly when you finish your work session.</li>"
            + "</ul>"
            + "</div>"
            + "<div class='login-button'>"
            + "<a href='" + loginUrl + "'>Access Employee Portal</a>"
            + "</div>"
            + "<p style='color: #666; font-size: 14px;'>If you have any questions or need assistance, please contact the HR department or your system administrator.</p>"
            + "</div>"
            + "<div class='footer'>"
            + "<p><strong>Ocean View Resort</strong></p>"
            + "<p>123 Beach Road, Coastal City, Sri Lanka</p>"
            + "<p>Email: hr@oceanviewresort.com | Phone: +94 11 234 5678</p>"
            + "<p style='margin-top: 15px; font-size: 11px;'>This is an automated email. Please do not reply to this message.</p>"
            + "</div>"
            + "</div>"
            + "</body>"
            + "</html>";
    }
    
    // Generate email subject line
    public static String generateCredentialsEmailSubject(String firstName) {
        return "🌊 Ocean View Resort - Your Employee Portal Credentials";
    }
}