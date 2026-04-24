package org.example.Resource;

import org.example.DataStore;
import org.example.Exceptions.LinkedResourceNotFoundException;
import org.example.models.Room;
import org.example.models.Sensor;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part 3 - Sensor Operations & Linking
 * Manages the /api/v1/sensors collection.
 *
 * Also provides the Sub-Resource Locator for /api/v1/sensors/{sensorId}/readings (Part 4).
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private DataStore db = DataStore.getInstance();

    // POST /sensors — Register a new sensor with roomId validation
    @POST
    public Response registerSensor(Sensor sensor) {
        // Validate required fields
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Bad Request\", \"message\":\"Sensor 'id' field is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Check for duplicate sensor ID
        if (db.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Conflict\", \"message\":\"Sensor with ID '" + sensor.getId() + "' already exists.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Validate that the referenced roomId actually exists (Part 3.1 integrity check)
        Room room = db.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Room ID '" + sensor.getRoomId() + "' does not exist in the system. " +
                "Cannot register sensor without a valid room assignment."
            );
        }

        // Default status to ACTIVE if not provided
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Save the sensor
        db.getSensors().put(sensor.getId(), sensor);

        // Link the sensor to its room
        room.getSensorIds().add(sensor.getId());

        URI location = URI.create("/api/v1/sensors/" + sensor.getId());
        return Response.created(location).entity(sensor).build();
    }

    // GET /sensors?type={type} — List all sensors, with optional type filter (Part 3.2)
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(db.getSensors().values());

        // If a 'type' query parameter is provided, filter the list
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return Response.ok(sensorList).build();
    }

    // GET /sensors/{sensorId} — Get a specific sensor by ID
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = db.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Not Found\", \"message\":\"Sensor '" + sensorId + "' does not exist.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Part 4.1 — Sub-Resource Locator
     *
     * No HTTP method annotation here — Jersey recognises this as a locator
     * and delegates further path resolution to SensorReadingResource.
     * This keeps the codebase modular: reading logic lives in its own class.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        // Validate parent sensor exists before delegating
        Sensor sensor = db.getSensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor '" + sensorId + "' not found.");
        }
        return new SensorReadingResource(sensorId);
    }
}
