package org.example.Exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Part 5.1 — Resource Conflict (409)
 *
 * Maps RoomNotEmptyException to an HTTP 409 Conflict response with a structured JSON body.
 * Triggered when a DELETE is attempted on a room that still has sensors assigned to it.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\":\"Conflict\", \"message\":\"" + exception.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
