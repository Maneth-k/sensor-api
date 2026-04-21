package org.example.Resource;



import org.example.DataStore;
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
            // If the room doesn't exist, we cannot register the sensor
            // Part 5 will ask for a custom exception, but for now, 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID " + sensor.getRoomId() + " does not exist.\"}")
                    .build();
        }

        // 2. Save the sensor
        db.getSensors().put(sensor.getId(), sensor);

        // 3. Update the Room's list of sensors (Linking) [cite: 58]
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
}
