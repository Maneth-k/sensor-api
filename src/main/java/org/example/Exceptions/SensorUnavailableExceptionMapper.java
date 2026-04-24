package org.example.Exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Part 5.3 — State Constraint (403 Forbidden)
 *
 * Maps SensorUnavailableException to HTTP 403 Forbidden.
 * Triggered when a POST reading is attempted on a sensor in MAINTENANCE or OFFLINE state.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"Forbidden\", \"message\":\"" + exception.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
