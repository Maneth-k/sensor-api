package org.example.Resource;




import org.example.models.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    // The parent resource will pass the sensorId into this constructor
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // We will add the GET and POST methods for the readings here in the next step!
    @GET
    public Response testSubResource() {
        return Response.ok("{\"message\":\"Sub-resource reached for sensor: " + sensorId + "\"}").build();
    }
}
