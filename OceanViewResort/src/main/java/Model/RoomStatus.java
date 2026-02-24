package Model;

public class RoomStatus {
    private int singleUsed;
    private int doubleUsed;
    private int suiteUsed;

    // Default Constructor
    public RoomStatus() {}

    // Getters
    public int getSingleUsed() { return singleUsed; }
    public int getDoubleUsed() { return doubleUsed; }
    public int getSuiteUsed() { return suiteUsed; }
    
    // Setters (Crucial for the DAO)
    public void setSingleUsed(int singleUsed) { this.singleUsed = singleUsed; }
    public void setDoubleUsed(int doubleUsed) { this.doubleUsed = doubleUsed; }
    public void setSuiteUsed(int suiteUsed) { this.suiteUsed = suiteUsed; }
}