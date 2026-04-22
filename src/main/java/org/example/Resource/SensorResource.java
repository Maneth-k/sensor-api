package org.example.Resource;

import org.example.DataStore;
import org.example.Exceptions.LinkedResourceNotFoundException;
import org.example.models.Room;
import org.example.models.Sensor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private DataStore db = DataStore.getInstance();

    @POST
    public Response registerSensor(Sensor sensor) {
        // 1. Validation: Does the Room exist? [cite: 129]
        Room room = db.getRooms().get(sensor.getRoomId());

        if (room == null) {
            // Throw the custom error!
            throw new LinkedResourceNotFoundException("Room ID '" + sensor.getRoomId() + "' does not exist. Cannot register sensor.");
        }

        // 2. Save the sensor
        db.getSensors().put(sensor.getId(), sensor);

        // 3. Update the Room's list of sensors (Linking) [cite: 58]
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        // We start with all sensors in a list
        java.util.List<Sensor> sensorList = new java.util.ArrayList<>(db.getSensors().values());

        // If the user provided a 'type' parameter (e.g., ?type=CO2)
        if (type != null && !type.trim().isEmpty()) {
            // Filter the list to only include matching types
            sensorList = sensorList.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(java.util.stream.Collectors.toList());
        }

        return Response.ok(sensorList).build();
    }
    // Notice there is no @GET or @POST here!
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
