package org.example;

import org.example.models.Room;
import org.example.models.Sensor;
import org.example.models.SensorReading;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance;

    // Thread-safe maps acting as our database tables
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, java.util.List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();
    // Private constructor for Singleton
    private DataStore() {}

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public Map<String, Room> getRooms() { return rooms; }
    public Map<String, Sensor> getSensors() { return sensors; }
    public Map<String, java.util.List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }
}
