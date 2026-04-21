package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;           // Unique identifier, e.g., "LIB-301" [cite: 53]
    private String name;         // Human-readable name, e.g., "Library Quiet Study" [cite: 54, 56]
    private int capacity;        // Maximum occupancy for safety [cite: 57]
    private List<String> sensorIds = new ArrayList<>(); // Collection of IDs of sensors deployed in this room [cite: 58-59]

    // Empty constructor for Jackson JSON conversion
    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // --- Getters and Setters (Mandatory for JSON to work!) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}
