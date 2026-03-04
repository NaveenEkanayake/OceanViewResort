package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnect;
import Model.Payment;

public class PaymentDao {
    
    // CREATE: Add a new payment record
    public boolean addPayment(Payment payment, String createdBy) {
        String sql = "INSERT INTO payments (reservationId, guestName, roomType, checkIn, checkOut, nightsStayed, extraNights, roomRatePerNight, roomTotal, extraCharges, totalAmount, paymentMethod, cardLastDigits, paymentDate, guestEmail, isPaid, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setInt(1, payment.getReservationId());
            pst.setString(2, payment.getGuestName());
            pst.setString(3, payment.getRoomType());
            pst.setString(4, payment.getCheckIn());
            pst.setString(5, payment.getCheckOut());
            pst.setInt(6, payment.getNightsStayed());
            pst.setInt(7, payment.getExtraNights());
            pst.setDouble(8, payment.getRoomRatePerNight());
            pst.setDouble(9, payment.getRoomTotal());
            pst.setDouble(10, payment.getExtraCharges());
            pst.setDouble(11, payment.getTotalAmount());
            pst.setString(12, payment.getPaymentMethod());
            pst.setString(13, payment.getCardLastDigits());
            pst.setTimestamp(14, payment.getPaymentDate());
            pst.setString(15, payment.getGuestEmail());
            pst.setBoolean(16, payment.isPaid());
            pst.setString(17, createdBy);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                // Get the generated payment ID
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("=== PAYMENT INSERT ERROR ===");
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            // Don't re-throw, just return false and let servlet handle the failure
        }
        return false;
    }
    
    // READ: Get all payments
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY paymentDate DESC";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    
    // READ: Get payments created by a specific employee
    public List<Payment> getPaymentsByCreator(String createdBy) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE created_by = ? ORDER BY paymentDate DESC";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setString(1, createdBy);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Payment payment = mapResultSetToPayment(rs);
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    
    // Helper method to map ResultSet to Payment object
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setReservationId(rs.getInt("reservationId"));
        payment.setGuestName(rs.getString("guestName"));
        payment.setRoomType(rs.getString("roomType"));
        payment.setCheckIn(rs.getString("checkIn"));
        payment.setCheckOut(rs.getString("checkOut"));
        payment.setNightsStayed(rs.getInt("nightsStayed"));
        payment.setExtraNights(rs.getInt("extraNights"));
        payment.setRoomRatePerNight(rs.getDouble("roomRatePerNight"));
        payment.setRoomTotal(rs.getDouble("roomTotal"));
        payment.setExtraCharges(rs.getDouble("extraCharges"));
        payment.setTotalAmount(rs.getDouble("totalAmount"));
        payment.setPaymentMethod(rs.getString("paymentMethod"));
        payment.setCardLastDigits(rs.getString("cardLastDigits"));
        payment.setPaymentDate(rs.getTimestamp("paymentDate"));
        payment.setGuestEmail(rs.getString("guestEmail"));
        payment.setPaid(rs.getBoolean("isPaid"));
        payment.setCreatedBy(rs.getString("created_by"));
        return payment;
    }
    
    // READ: Get payment by ID
    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setReservationId(rs.getInt("reservationId"));
                    payment.setGuestName(rs.getString("guestName"));
                    payment.setRoomType(rs.getString("roomType"));
                    payment.setCheckIn(rs.getString("checkIn"));
                    payment.setCheckOut(rs.getString("checkOut"));
                    payment.setNightsStayed(rs.getInt("nightsStayed"));
                    payment.setExtraNights(rs.getInt("extraNights"));
                    payment.setRoomRatePerNight(rs.getDouble("roomRatePerNight"));
                    payment.setRoomTotal(rs.getDouble("roomTotal"));
                    payment.setExtraCharges(rs.getDouble("extraCharges"));
                    payment.setTotalAmount(rs.getDouble("totalAmount"));
                    payment.setPaymentMethod(rs.getString("paymentMethod"));
                    payment.setCardLastDigits(rs.getString("cardLastDigits"));
                    payment.setPaymentDate(rs.getTimestamp("paymentDate"));
                    payment.setGuestEmail(rs.getString("guestEmail"));
                    payment.setPaid(rs.getBoolean("isPaid"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // READ: Get payments by reservation ID
    public List<Payment> getPaymentsByReservationId(int reservationId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE reservationId = ? ORDER BY paymentDate DESC";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setInt(1, reservationId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setReservationId(rs.getInt("reservationId"));
                    payment.setGuestName(rs.getString("guestName"));
                    payment.setRoomType(rs.getString("roomType"));
                    payment.setCheckIn(rs.getString("checkIn"));
                    payment.setCheckOut(rs.getString("checkOut"));
                    payment.setNightsStayed(rs.getInt("nightsStayed"));
                    payment.setExtraNights(rs.getInt("extraNights"));
                    payment.setRoomRatePerNight(rs.getDouble("roomRatePerNight"));
                    payment.setRoomTotal(rs.getDouble("roomTotal"));
                    payment.setExtraCharges(rs.getDouble("extraCharges"));
                    payment.setTotalAmount(rs.getDouble("totalAmount"));
                    payment.setPaymentMethod(rs.getString("paymentMethod"));
                    payment.setCardLastDigits(rs.getString("cardLastDigits"));
                    payment.setPaymentDate(rs.getTimestamp("paymentDate"));
                    payment.setGuestEmail(rs.getString("guestEmail"));
                    payment.setPaid(rs.getBoolean("isPaid"));
                    
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    
    // UPDATE: Update payment details
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE payments SET reservationId=?, guestName=?, roomType=?, checkIn=?, checkOut=?, nightsStayed=?, extraNights=?, roomRatePerNight=?, roomTotal=?, extraCharges=?, totalAmount=?, paymentMethod=?, cardLastDigits=?, paymentDate=?, guestEmail=?, isPaid=? WHERE id=?";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setInt(1, payment.getReservationId());
            pst.setString(2, payment.getGuestName());
            pst.setString(3, payment.getRoomType());
            pst.setString(4, payment.getCheckIn());
            pst.setString(5, payment.getCheckOut());
            pst.setInt(6, payment.getNightsStayed());
            pst.setInt(7, payment.getExtraNights());
            pst.setDouble(8, payment.getRoomRatePerNight());
            pst.setDouble(9, payment.getRoomTotal());
            pst.setDouble(10, payment.getExtraCharges());
            pst.setDouble(11, payment.getTotalAmount());
            pst.setString(12, payment.getPaymentMethod());
            pst.setString(13, payment.getCardLastDigits());
            pst.setTimestamp(14, payment.getPaymentDate());
            pst.setString(15, payment.getGuestEmail());
            pst.setBoolean(16, payment.isPaid());
            pst.setInt(17, payment.getId());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // DELETE: Remove payment record
    public boolean deletePayment(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get total revenue
    public double getTotalRevenue() {
        String sql = "SELECT SUM(totalAmount) as total FROM payments WHERE isPaid = true";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get payment statistics
    public PaymentStats getPaymentStats() {
        PaymentStats stats = new PaymentStats();
        String sql = "SELECT " +
                    "COUNT(*) as totalPayments, " +
                    "SUM(CASE WHEN paymentMethod = 'Cash' THEN 1 ELSE 0 END) as cashPayments, " +
                    "SUM(CASE WHEN paymentMethod IN ('Visa', 'Mastercard', 'Card') THEN 1 ELSE 0 END) as cardPayments, " +
                    "SUM(totalAmount) as totalRevenue, " +
                    "SUM(CASE WHEN isPaid = true THEN 1 ELSE 0 END) as paidPayments " +
                    "FROM payments WHERE isPaid = true";
        
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            if (rs.next()) {
                stats.setTotalPayments(rs.getInt("totalPayments"));
                stats.setCashPayments(rs.getInt("cashPayments"));
                stats.setCardPayments(rs.getInt("cardPayments"));
                stats.setTotalRevenue(rs.getDouble("totalRevenue"));
                stats.setPaidPayments(rs.getInt("paidPayments"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Inner class for payment statistics
    public static class PaymentStats {
        private int totalPayments;
        private int cashPayments;
        private int cardPayments;
        private double totalRevenue;
        private int paidPayments;
        
        // Getters and setters
        public int getTotalPayments() { return totalPayments; }
        public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }
        
        public int getCashPayments() { return cashPayments; }
        public void setCashPayments(int cashPayments) { this.cashPayments = cashPayments; }
        
        public int getCardPayments() { return cardPayments; }
        public void setCardPayments(int cardPayments) { this.cardPayments = cardPayments; }
        
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getPaidPayments() { return paidPayments; }
        public void setPaidPayments(int paidPayments) { this.paidPayments = paidPayments; }
    }
}