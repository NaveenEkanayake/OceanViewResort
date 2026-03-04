package Model;

import java.sql.Timestamp;

public class Payment {
    private int id;
    private int reservationId;
    private String guestName;
    private String roomType;
    private String checkIn;
    private String checkOut;
    private int nightsStayed;
    private int extraNights;
    private double roomRatePerNight;
    private double roomTotal;
    private double extraCharges;
    private double totalAmount;
    private String paymentMethod;
    private String cardLastDigits;
    private Timestamp paymentDate;
    private String guestEmail;
    private boolean isPaid;
    private String createdBy;

    // Default Constructor
    public Payment() {
        this.isPaid = false; // Default to not paid
    }

    // Constructor for new payments
    public Payment(int reservationId, String guestName, String roomType, String checkIn, String checkOut, 
                   int nightsStayed, double roomRatePerNight, String paymentMethod, String guestEmail) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nightsStayed = nightsStayed;
        this.extraNights = 0;
        this.roomRatePerNight = roomRatePerNight;
        this.roomTotal = nightsStayed * roomRatePerNight;
        this.extraCharges = 0.0;
        this.totalAmount = this.roomTotal;
        this.paymentMethod = paymentMethod;
        this.guestEmail = guestEmail;
        this.isPaid = false; // Default to not paid
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }

    // Full constructor
    public Payment(int id, int reservationId, String guestName, String roomType, String checkIn, String checkOut,
                   int nightsStayed, int extraNights, double roomRatePerNight, double roomTotal, double extraCharges,
                   double totalAmount, String paymentMethod, String cardLastDigits, Timestamp paymentDate, 
                   String guestEmail, boolean isPaid) {
        this.id = id;
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nightsStayed = nightsStayed;
        this.extraNights = extraNights;
        this.roomRatePerNight = roomRatePerNight;
        this.roomTotal = roomTotal;
        this.extraCharges = extraCharges;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.cardLastDigits = cardLastDigits;
        this.paymentDate = paymentDate;
        this.guestEmail = guestEmail;
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public int getNightsStayed() { return nightsStayed; }
    public void setNightsStayed(int nightsStayed) { this.nightsStayed = nightsStayed; }

    public int getExtraNights() { return extraNights; }
    public void setExtraNights(int extraNights) { this.extraNights = extraNights; }

    public double getRoomRatePerNight() { return roomRatePerNight; }
    public void setRoomRatePerNight(double roomRatePerNight) { this.roomRatePerNight = roomRatePerNight; }

    public double getRoomTotal() { return roomTotal; }
    public void setRoomTotal(double roomTotal) { this.roomTotal = roomTotal; }

    public double getExtraCharges() { return extraCharges; }
    public void setExtraCharges(double extraCharges) { this.extraCharges = extraCharges; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCardLastDigits() { return cardLastDigits; }
    public void setCardLastDigits(String cardLastDigits) { this.cardLastDigits = cardLastDigits; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    // Utility methods
    public void calculateTotalAmount() {
        this.roomTotal = (this.nightsStayed + this.extraNights) * this.roomRatePerNight;
        this.totalAmount = this.roomTotal + this.extraCharges;
    }

    public void addExtraNights(int extraNights) {
        this.extraNights += extraNights;
        calculateTotalAmount();
    }

    public void addExtraCharges(double charges) {
        this.extraCharges += charges;
        calculateTotalAmount();
    }

    // Get room rate based on room type
    public static double getRoomRate(String roomType) {
        switch (roomType) {
            case "Single Room": return 5000.00;
            case "Double Room": return 8000.00;
            case "Ocean Suite": return 15000.00;
            default: return 0.00;
        }
    }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}