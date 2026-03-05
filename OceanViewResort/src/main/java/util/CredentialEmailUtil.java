package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class CredentialEmailUtil {
    
    private static final String SENDER_EMAIL = "nekanayake789@gmail.com";
    private static final String SENDER_PASSWORD = "tppm fwlb tngs ooxj"; // Google App Password
    
    public static void sendEmployeeCredentials(String recipientEmail, String employeeName, 
            String username, String password, String loginUrl) {
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });
            
            String htmlContent = createCredentialHTMLTemplate(employeeName, username, password, loginUrl);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Ocean View Resort HR"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Welcome to Ocean View Resort Team - Your Login Credentials");
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("✓ Employee credentials email sent successfully to: " + recipientEmail);
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send credentials email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });
            
            String htmlContent = createOTPHTMLTemplate(otp);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Ocean View Resort Security"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Verification Code - Ocean View Resort");
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("✓ OTP email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send OTP email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static String createCredentialHTMLTemplate(String employeeName, String username, 
            String password, String loginUrl) {
        return "<!DOCTYPE html>"
            + "<html><head><style>"
            + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
            + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
            + ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
            + ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }"
            + ".credentials-box { background: white; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0; }"
            + ".btn { display: inline-block; padding: 12px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }"
            + "</style></head><body>"
            + "<div class='container'>"
            + "<div class='header'><h1>Welcome to Ocean View Resort!</h1></div>"
            + "<div class='content'>"
            + "<p>Dear " + employeeName + ",</p>"
            + "<p>Welcome to the Ocean View Resort family! We're excited to have you on our team.</p>"
            + "<p>Your employee account has been created with the following credentials:</p>"
            + "<div class='credentials-box'>"
            + "<p><strong>Username:</strong> " + username + "</p>"
            + "<p><strong>Temporary Password:</strong> " + password + "</p>"
            + "</div>"
            + "<p style='text-align: center;'><a href='" + loginUrl + "' class='btn'>Login Here</a></p>"
            + "<p><strong>Important:</strong> Please change your password after your first login for security purposes.</p>"
            + "<p>If you have any questions, please contact the administration.</p>"
            + "<p>Best regards,<br><strong>Ocean View Resort Management</strong></p>"
            + "</div></div></body></html>";
    }
    
    private static String createOTPHTMLTemplate(String otp) {
        return "<!DOCTYPE html>"
            + "<html><head><style>"
            + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
            + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
            + ".header { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
            + ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }"
            + ".otp-box { background: white; border: 2px dashed #11998e; padding: 20px; margin: 20px 0; text-align: center; }"
            + ".otp-code { font-size: 32px; font-weight: bold; color: #11998e; letter-spacing: 5px; }"
            + ".warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }"
            + "</style></head><body>"
            + "<div class='container'>"
            + "<div class='header'><h1>Password Reset Request</h1></div>"
            + "<div class='content'>"
            + "<p>You have requested to reset your password for Ocean View Resort employee portal.</p>"
            + "<p>Your verification code is:</p>"
            + "<div class='otp-box'>"
            + "<p class='otp-code'>" + otp + "</p>"
            + "</div>"
            + "<div class='warning'>"
            + "<p><strong>⚠️ Important:</strong></p>"
            + "<ul>"
            + "<li>This code is valid for 10 minutes</li>"
            + "<li>Do not share this code with anyone</li>"
            + "<li>If you didn't request this, please ignore this email</li>"
            + "</ul>"
            + "</div>"
            + "<p>Best regards,<br><strong>Ocean View Resort Security Team</strong></p>"
            + "</div></div></body></html>";
    }
}
