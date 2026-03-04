package Model;

import java.sql.Date;

public class Reservation {
    private int id; 
    private String guestName;
    private String address;
    private String contactNumber;
    private String roomType;
    private Date checkIn;
    private Date checkOut;
    private String status;
    private String createdBy;

    // Default Constructor (Used for fetching from DB)
    public Reservation() {}

    // Constructor without ID (Used for creating new reservations)
    public Reservation(String guestName, String address, String contactNumber, String roomType, Date checkIn, Date checkOut) {
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // Standard Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // --- HELPER METHOD FOR UI ---
    // This allows you to call ${res.formattedId} in your JSP
    public String getFormattedId() {
        return String.format("%04d", id);
    }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }

    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}