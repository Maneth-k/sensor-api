package org.example.Exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider // Tells Jersey: "Hey, use this class to translate exceptions!"
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        // When RoomNotEmptyException is thrown anywhere in the app,
        // Jersey intercepts it and returns this structured 409 JSON response.
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\":\"Conflict\", \"message\":\"" + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}
