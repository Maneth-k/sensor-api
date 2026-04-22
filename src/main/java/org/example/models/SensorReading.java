package org.example.models;

public class SensorReading {
    private String id;        // Unique reading event ID
    private long timestamp;   // Epoch time (ms) when the reading was captured
    private double value;     // The actual metric value recorded

    public SensorReading() {}

    // Getters and Setters (Mandatory)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}