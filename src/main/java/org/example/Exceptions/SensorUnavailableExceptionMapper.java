package org.example.Exceptions;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        // 403 Forbidden: The client is authenticated/valid, but the server refuses to authorize the action
        // based on the current state of the resource.
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"Forbidden\", \"message\":\"" + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}