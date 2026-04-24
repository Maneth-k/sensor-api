package org.example.Exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Part 5.2 — Dependency Validation (422 Unprocessable Entity)
 *
 * Maps LinkedResourceNotFoundException to HTTP 422.
 * Triggered when a POST /sensors references a roomId that doesn't exist.
 *
 * 422 is semantically more accurate than 404 here because the request URL
 * is perfectly valid — the problem is with the data payload content.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        return Response.status(422)
                .entity("{\"error\":\"Unprocessable Entity\", \"message\":\"" + exception.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
