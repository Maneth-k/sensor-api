package org.example.Resource;


import org.example.models.DiscoveryInfo;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {

        // 1. Build the map of available endpoints (HATEOAS links)
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");

        // 2. Populate the POJO with the required metadata
        DiscoveryInfo info = new DiscoveryInfo(
                "v1.0.0",
                "admin@smartcampus.westminster.ac.uk",
                links
        );

        // 3. Return a 200 OK response with the JSON body
        return Response.ok(info).build();
    }
}