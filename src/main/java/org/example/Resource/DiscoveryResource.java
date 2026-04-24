package org.example.Resource;

import org.example.models.DiscoveryInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 1.2 - Discovery Endpoint
 * Responds to both GET /api/v1 and GET /api/v1/
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms",   "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("test",    "/api/v1/test");

        DiscoveryInfo info = new DiscoveryInfo(
                "v1.0.0",
                "Smart Campus API — University of Westminster",
                "admin@smartcampus.westminster.ac.uk",
                "2025-26",
                resources
        );

        return Response.ok(info).build();
    }
}
