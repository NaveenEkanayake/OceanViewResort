package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "nekanayake789@gmail.com"; 
    private static final String PASSWORD = "tppm fwlb tngs ooxj"; // Ensure this is a Google App Password
    
    public static boolean sendEmail(String recipientEmail, String subject, String htmlBody) {
        return sendHTMLEmail(recipientEmail, subject, htmlBody);
    }
    
    public static boolean sendHTMLEmail(String recipientEmail, String subject, String htmlBody) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("HTML Email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendHTMLPaymentConfirmationEmail(String recipientEmail, int paymentId, 
            String guestName, String roomType, String checkIn, String checkOut, 
            double totalAmount, String paymentMethod) {
        
        try {
            String subject = "Payment Confirmation - Ocean View Resort";
            String htmlBody = String.format(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #0dcaf0 0%%, #0aa2c0 100%%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; margin: -30px -30px 20px -30px; }" +
                ".content { padding: 20px 0; }" +
                ".details { background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0; }" +
                ".footer { text-align: center; margin-top: 30px; color: #6c757d; font-size: 12px; }" +
                "h1 { margin: 0; font-size: 24px; }" +
                "h2 { color: #0dcaf0; margin-top: 0; }" +
                "ul { list-style: none; padding: 0; }" +
                "li { padding: 5px 0; border-bottom: 1px solid #eee; }" +
                "li:last-child { border-bottom: none; }" +
                ".amount { color: #198754; font-weight: bold; font-size: 18px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>OCEAN VIEW RESORT</h1>" +
                "<p>Payment Confirmation</p>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Hello %s!</h2>" +
                "<p>Thank you for your payment. Your transaction has been successfully processed.</p>" +
                "<div class='details'>" +
                "<h3>Payment Details</h3>" +
                "<ul>" +
                "<li><strong>Payment ID:</strong> %d</li>" +
                "<li><strong>Guest Name:</strong> %s</li>" +
                "<li><strong>Room Type:</strong> %s</li>" +
                "<li><strong>Check-in:</strong> %s</li>" +
                "<li><strong>Check-out:</strong> %s</li>" +
                "<li><strong>Payment Method:</strong> %s</li>" +
                "<li><strong class='amount'>Total Amount:</strong> <span class='amount'>Rs. %.2f</span></li>" +
                "</ul>" +
                "</div>" +
                "<p>If you have any questions about your payment, please contact our support team.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Ocean View Resort<br>" +
                "Thank you for choosing us!</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                guestName, paymentId, guestName, roomType, checkIn, checkOut, paymentMethod, totalAmount
            );
            
            boolean result = sendHTMLEmail(recipientEmail, subject, htmlBody);
            System.out.println("Email sending result for " + recipientEmail + ": " + (result ? "SUCCESS" : "FAILED"));
            return result;
            
        } catch (Exception e) {
            System.err.println("Error creating payment confirmation email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}