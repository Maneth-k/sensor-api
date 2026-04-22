package org.example.Exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // Status 422 means the JSON payload is formatted correctly,
        // but the data inside it (the roomId) makes no sense to the server.
        return Response.status(422)
                .entity("{\"error\":\"Unprocessable Entity\", \"message\":\"" + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}
