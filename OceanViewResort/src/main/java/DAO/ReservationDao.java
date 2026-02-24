package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnect;
import Model.Reservation;
import Model.RoomStatus;

public class ReservationDao {
    
    // CREATE: Adds a new reservation
    public boolean addReservation(Reservation reservation) {
        // First check if status column exists
        try (Connection connection = DBConnect.getConnection()) {
            boolean hasStatusColumn = checkStatusColumnExists(connection);
            
            String sql;
            if (hasStatusColumn) {
                sql = "INSERT INTO reservations (guestName, address, contactNumber, roomType, checkIn, checkOut, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO reservations (guestName, address, contactNumber, roomType, checkIn, checkOut) VALUES (?, ?, ?, ?, ?, ?)";
            }
            
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, reservation.getGuestName());
                pst.setString(2, reservation.getAddress());
                pst.setString(3, reservation.getContactNumber());
                pst.setString(4, reservation.getRoomType());
                pst.setDate(5, reservation.getCheckIn());
                pst.setDate(6, reservation.getCheckOut());
                
                if (hasStatusColumn) {
                    pst.setString(7, "Payment Pending"); // Set initial status
                }

                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // READ: Fetches all reservations for the table
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        
        try (Connection connection = DBConnect.getConnection()) {
            // First check if status column exists
            boolean hasStatusColumn = checkStatusColumnExists(connection);
            
            String sql;
            if (hasStatusColumn) {
                sql = "SELECT id, guestName, address, contactNumber, roomType, checkIn, checkOut, status FROM reservations ORDER BY id DESC";
            } else {
                sql = "SELECT id, guestName, address, contactNumber, roomType, checkIn, checkOut FROM reservations ORDER BY id DESC";
            }

            try (PreparedStatement pst = connection.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Reservation res = new Reservation();
                    res.setId(rs.getInt("id")); 
                    res.setGuestName(rs.getString("guestName"));
                    res.setAddress(rs.getString("address"));
                    res.setContactNumber(rs.getString("contactNumber"));
                    res.setRoomType(rs.getString("roomType"));
                    res.setCheckIn(rs.getDate("checkIn"));
                    res.setCheckOut(rs.getDate("checkOut"));
                    
                    // Set status - either from DB or default to "Payment Pending"
                    if (hasStatusColumn) {
                        res.setStatus(rs.getString("status") != null ? rs.getString("status") : "Payment Pending");
                    } else {
                        res.setStatus("Payment Pending");
                    }
                    
                    list.add(res);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching reservations: " + e.getMessage());
        }
        return list;
    }
    
    // Helper method to check if status column exists
    private boolean checkStatusColumnExists(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, null, "reservations", "status")) {
                return rs.next(); // Returns true if column exists
            }
        } catch (SQLException e) {
            System.out.println("Error checking status column: " + e.getMessage());
            return false;
        }
    }

    // UPDATE: Modifies an existing record by ID
    public boolean updateReservation(Reservation reservation) {
        String sql = "UPDATE reservations SET guestName=?, address=?, contactNumber=?, roomType=?, checkIn=?, checkOut=? WHERE id=?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            
            pst.setString(1, reservation.getGuestName());
            pst.setString(2, reservation.getAddress());
            pst.setString(3, reservation.getContactNumber());
            pst.setString(4, reservation.getRoomType());
            pst.setDate(5, reservation.getCheckIn());
            pst.setDate(6, reservation.getCheckOut());
            pst.setInt(7, reservation.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReservationStatus(int id, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // This is where your error was appearing
            return false;
        }
    }

    // DELETE: Removes a record from the database
    public boolean deleteReservation(int id) {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection con = DBConnect.getConnection(); 
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // DASHBOARD LOGIC: Gets current usage counts for all room types
    public RoomStatus getCurrentRoomStatus() {
        RoomStatus status = new RoomStatus();
        String sql = "SELECT roomType, COUNT(*) as count FROM reservations GROUP BY roomType";
        
        System.out.println("Executing room status query: " + sql);
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String type = rs.getString("roomType");
                int count = rs.getInt("count");
                
                System.out.println("Found room type: " + type + " with count: " + count);
                
                if ("Single Room".equalsIgnoreCase(type)) status.setSingleUsed(count);
                else if ("Double Room".equalsIgnoreCase(type)) status.setDoubleUsed(count);
                else if ("Ocean Suite".equalsIgnoreCase(type)) status.setSuiteUsed(count);
            }
            
            System.out.println("Final counts - Single: " + status.getSingleUsed() + 
                             ", Double: " + status.getDoubleUsed() + 
                             ", Suite: " + status.getSuiteUsed());
        } catch (SQLException e) {
            System.err.println("Error in getCurrentRoomStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return status;
    }

    // CAPACITY LOGIC: Check count for a specific type (Used in Servlet POST)
    public int getBookedCount(String roomType) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE roomType = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Reservation getReservationById(int reservationId) {
        try (Connection connection = DBConnect.getConnection()) {
            boolean hasStatusColumn = checkStatusColumnExists(connection);
            
            String sql;
            if (hasStatusColumn) {
                sql = "SELECT id, guestName, address, contactNumber, roomType, checkIn, checkOut, status FROM reservations WHERE id = ?";
            } else {
                sql = "SELECT id, guestName, address, contactNumber, roomType, checkIn, checkOut FROM reservations WHERE id = ?";
            }
            
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setInt(1, reservationId);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Reservation reservation = new Reservation();
                        reservation.setId(rs.getInt("id"));
                        reservation.setGuestName(rs.getString("guestName"));
                        reservation.setAddress(rs.getString("address"));
                        reservation.setContactNumber(rs.getString("contactNumber"));
                        reservation.setRoomType(rs.getString("roomType"));
                        reservation.setCheckIn(rs.getDate("checkIn"));
                        reservation.setCheckOut(rs.getDate("checkOut"));
                        
                        if (hasStatusColumn) {
                            reservation.setStatus(rs.getString("status"));
                        } else {
                            reservation.setStatus("Payment Pending"); // Default status
                        }
                        return reservation;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the reservation is not found
    }
}