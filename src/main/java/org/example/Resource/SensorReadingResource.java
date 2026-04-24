package org.example.Resource;

import org.example.DataStore;
import org.example.Exceptions.SensorUnavailableException;
import org.example.models.Sensor;
import org.example.models.SensorReading;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Part 4.2 — Historical Data Management
 *
 * Handles GET and POST for /api/v1/sensors/{sensorId}/readings.
 * This class is instantiated by the Sub-Resource Locator in SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore db = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /sensors/{sensorId}/readings — Fetch historical readings for this sensor
    @GET
    public Response getReadings() {
        List<SensorReading> readings = db.getSensorReadings()
                .getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    // POST /sensors/{sensorId}/readings — Append a new reading + update parent sensor's currentValue
    @POST
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = db.getSensors().get(sensorId);

        // Parent sensor existence check (defence in depth — locator already validated this)
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Not Found\", \"message\":\"Sensor '" + sensorId + "' does not exist.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Part 5.3 — State Constraint: block readings for MAINTENANCE sensors (403 Forbidden)
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently in MAINTENANCE mode and cannot accept new readings. " +
                "Please wait until the sensor is restored to ACTIVE status."
            );
        }

        // Also block OFFLINE sensors
        if ("OFFLINE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE and cannot accept new readings."
            );
        }

        // Auto-generate ID and timestamp if not provided by client
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Persist the reading in the historical log
        db.getSensorReadings().putIfAbsent(sensorId, new ArrayList<>());
        db.getSensorReadings().get(sensorId).add(reading);

        // THE SIDE EFFECT (Part 4.2): Update the parent sensor's currentValue to maintain data consistency
        parentSensor.setCurrentValue(reading.getValue());

        URI location = URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId());
        return Response.created(location).entity(reading).build();
    }
}
