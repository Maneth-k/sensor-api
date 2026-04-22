package org.example.Resource;

import org.example.DataStore;
import org.example.Exceptions.SensorUnavailableException;
import org.example.models.Sensor;
import org.example.models.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private DataStore db = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // 1. GET / - Fetch the historical log
    @GET
    public Response getReadings() {
        // Fetch the list of readings for this sensor, or return an empty list if none exist yet
        List<SensorReading> readings = db.getSensorReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    // 2. POST / - Add a new reading and trigger the side-effect
    @POST
    public Response addReading(SensorReading reading) {
        // Step A: Validate the parent sensor actually exists
        Sensor parentSensor = db.getSensors().get(sensorId);
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Parent Sensor " + sensorId + " not found.\"}")
                    .build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus()) ||
                "OFFLINE".equalsIgnoreCase(parentSensor.getStatus())) {

            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is currently in " + parentSensor.getStatus() +
                            " mode. It cannot accept new readings."
            );
        }
        // Step B: Auto-generate ID and timestamp if the client didn't provide them
        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Step C: Save the reading to the historical log
        db.getSensorReadings().putIfAbsent(sensorId, new ArrayList<>());
        db.getSensorReadings().get(sensorId).add(reading);

        // Step D: THE SIDE EFFECT - Update the parent sensor
        parentSensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
