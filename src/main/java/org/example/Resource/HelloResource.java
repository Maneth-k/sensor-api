package org.example.Resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// This defines the URL path for this specific controller
@Path("/test")
public class HelloResource {

    // This tells the server to trigger this method on a GET request
    @GET
    @Produces(MediaType.TEXT_PLAIN) // We are just sending plain text back
    public String sayHello() {
        return "Server is running! Hello from JAX-RS!";
    }
}