package org.example.Exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual error to the console so YOU (the developer) can see it and fix it
        System.err.println("CRITICAL UNHANDLED ERROR: " + exception.getMessage());
        exception.printStackTrace();

        // But send a clean, generic JSON response to the client so they don't see the messy details
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Internal Server Error\", \"message\":\"An unexpected error occurred. Please contact the administrator.\"}")
                .type("application/json")
                .build();
    }
}
