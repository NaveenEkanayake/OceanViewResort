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
        // Default to employee OTP template
        return sendEmployeeOTPEmail(recipientEmail, otp);
    }
    
    // Send OTP for Employee password reset
    public static boolean sendEmployeeOTPEmail(String recipientEmail, String otp) {
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
            
            String htmlContent = createEmployeeOTPHTMLTemplate(otp);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Ocean View Resort Security"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Verification Code - Ocean View Resort");
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("✓ Employee OTP email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send OTP email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Send OTP for Admin password reset - Professional Executive Style
    public static boolean sendAdminOTPEmail(String recipientEmail, String otp) {
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
            
            String htmlContent = createAdminOTPHTMLTemplate(otp);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Ocean View Resort - Executive Administration"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confidential: Administrator Password Reset Authorization Code");
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("✓ Admin OTP email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Failed to send admin OTP email: " + e.getMessage());
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
    
    private static String createEmployeeOTPHTMLTemplate(String otp) {
        return "<!DOCTYPE html>"
            + "<html><head><style>"
            + "body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }"
            + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
            + ".header { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }"
            + ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }"
            + ".otp-box { background: white; border: 2px dashed #11998e; padding: 20px; margin: 20px 0; text-align: center; }"
            + ".otp-code { font-size: 32px; font-weight: bold; color: #11998e; letter-spacing: 5px; }"
            + ".warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }"
            + ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; font-size: 12px; color: #666; }"
            + "</style></head><body>"
            + "<div class='container'>"
            + "<div class='header'><h1>🔐 Password Reset Request</h1></div>"
            + "<div class='content'>"
            + "<p>Dear Team Member,</p>"
            + "<p>You have requested to reset your password for the Ocean View Resort employee portal.</p>"
            + "<p>Your verification code is:</p>"
            + "<div class='otp-box'>"
            + "<p class='otp-code'>" + otp + "</p>"
            + "</div>"
            + "<div class='warning'>"
            + "<p><strong>⚠️ Important Security Notice:</strong></p>"
            + "<ul>"
            + "<li>This code is valid for <strong>10 minutes</strong></li>"
            + "<li>Do not share this code with anyone</li>"
            + "<li>If you didn't request this reset, please ignore this email</li>"
            + "<li>For security reasons, this code can only be used once</li>"
            + "</ul>"
            + "</div>"
            + "<p>Best regards,<br><strong>Ocean View Resort Security Team</strong></p>"
            + "<div class='footer'>"
            + "<p>This is an automated message. Please do not reply to this email.<br>"
            + "© 2024 Ocean View Resort. All rights reserved.</p>"
            + "</div>"
            + "</div></div></body></html>";
    }
    
    private static String createAdminOTPHTMLTemplate(String otp) {
        return "<!DOCTYPE html>"
            + "<html><head><style>"
            + "body { font-family: 'Georgia', 'Times New Roman', serif; line-height: 1.8; color: #2c3e50; }"
            + ".container { max-width: 650px; margin: 0 auto; padding: 20px; background: #ffffff; }"
            + ".header { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); color: white; padding: 40px 30px; text-align: center; border-radius: 8px 8px 0 0; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }"
            + ".header h1 { margin: 0; font-size: 28px; font-weight: 600; letter-spacing: 1px; }"
            + ".header p { margin: 10px 0 0 0; font-size: 14px; opacity: 0.9; font-style: italic; }"
            + ".content { background: #fafafa; padding: 40px 30px; border-radius: 0 0 8px 8px; border: 1px solid #e0e0e0; }"
            + ".greeting { font-size: 16px; margin-bottom: 20px; color: #1e3c72; font-weight: 600; }"
            + ".message { font-size: 14px; margin-bottom: 25px; text-align: justify; }"
            + ".otp-section { background: white; border: 2px solid #1e3c72; padding: 30px; margin: 25px 0; text-align: center; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }"
            + ".otp-label { font-size: 12px; color: #666; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 15px; }"
            + ".otp-code { font-size: 36px; font-weight: bold; color: #1e3c72; letter-spacing: 8px; font-family: 'Courier New', monospace; background: #f0f4f8; padding: 15px 30px; display: inline-block; border-radius: 4px; }"
            + ".security-box { background: #fff8e1; border-left: 4px solid #ff9800; padding: 20px; margin: 25px 0; }"
            + ".security-box h3 { margin: 0 0 10px 0; color: #f57c00; font-size: 14px; text-transform: uppercase; }"
            + ".security-box ul { margin: 10px 0; padding-left: 20px; }"
            + ".security-box li { margin-bottom: 8px; font-size: 13px; }"
            + ".signature { margin-top: 30px; }"
            + ".signature-line { font-size: 14px; color: #1e3c72; font-weight: 600; margin-bottom: 5px; }"
            + ".signature-title { font-size: 12px; color: #666; font-style: italic; }"
            + ".footer { margin-top: 40px; padding-top: 20px; border-top: 2px solid #e0e0e0; font-size: 11px; color: #999; text-align: center; }"
            + ".confidential { background: #ffebee; color: #c62828; padding: 10px; text-align: center; font-size: 11px; font-weight: bold; letter-spacing: 1px; margin-bottom: 20px; border-radius: 3px; }"
            + "</style></head><body>"
            + "<div class='container'>"
            + "<div class='confidential'>🔒 CONFIDENTIAL & AUTHORIZED ACCESS ONLY</div>"
            + "<div class='header'>"
            + "<h1>ADMINISTRATOR PORTAL</h1>"
            + "<p>Password Reset Authorization</p>"
            + "</div>"
            + "<div class='content'>"
            + "<p class='greeting'>Dear Administrator,</p>"
            + "<p class='message'>"
            + "This communication serves as notification that a password reset has been initiated for your administrator account within the Ocean View Resort management system. The following authorization code has been generated in accordance with our security protocols."
            + "</p>"
            + "<div class='otp-section'>"
            + "<p class='otp-label'>Authorization Code</p>"
            + "<p class='otp-code'>" + otp + "</p>"
            + "</div>"
            + "<div class='security-box'>"
            + "<h3>⚠️ Security Protocol Notice</h3>"
            + "<ul>"
            + "<li><strong>Validity Period:</strong> This authorization code shall expire within <em>ten (10) minutes</em> of issuance</li>"
            + "<li><strong>Single Use:</strong> This code may only be utilized once and shall become void thereafter</li>"
            + "<li><strong>Confidentiality:</strong> Under no circumstances should this code be disclosed to any party</li>"
            + "<li><strong>Unauthorized Access:</strong> If you did not initiate this request, please contact the IT Security Division immediately</li>"
            + "<li><strong>Audit Trail:</strong> This transaction has been logged for security and compliance purposes</li>"
            + "</ul>"
            + "</div>"
            + "<div class='signature'>"
            + "<p class='signature-line'>Executive Administration Office</p>"
            + "<p class='signature-title'>Ocean View Resort Management System</p>"
            + "</div>"
            + "<div class='footer'>"
            + "<p>This is an automated system-generated message. Please do not reply directly to this communication.<br>"
            + "For assistance, contact the IT Support Division at ext. 1000 or support@oceanviewresort.com</p>"
            + "<p>© 2024 Ocean View Resort | Confidential | Proprietary Information</p>"
            + "</div>"
            + "</div></div></body></html>";
    }
}
