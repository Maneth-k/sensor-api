package org.example.models;

public class Sensor {
    private String id;           // e.g., "TEMP-001" [cite: 76]
    private String type;         // e.g., "Temperature", "Occupancy", "CO2" [cite: 77]
    private String status;       // "ACTIVE", "MAINTENANCE", or "OFFLINE" [cite: 78]
    private double currentValue; // The most recent measurement [cite: 79]
    private String roomId;       // Foreign key linking to the Room [cite: 80]

    public Sensor() {}

    // Getters and Setters (Mandatory)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}