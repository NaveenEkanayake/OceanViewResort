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
}