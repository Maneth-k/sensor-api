package org.example.Exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Part 5.4 — Global Safety Net
 *
 * Catches ALL uncaught Throwable exceptions (NullPointerException,
 * IndexOutOfBoundsException, etc.) and returns a clean HTTP 500 JSON response.
 *
 * This ensures the API is completely "leak-proof" — no raw Java stack traces
 * are ever exposed to API consumers.
 *
 * WebApplicationExceptions (like 404 Not Found from Jersey itself) are
 * passed through with their own status codes so we don't accidentally
 * swallow legitimate HTTP errors.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // Pass through JAX-RS/HTTP exceptions (404, 405, 415, etc.) — they are intentional
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            Response original = wae.getResponse();
            // Wrap in JSON if the original response has no entity body
            if (original.getEntity() == null) {
                return Response.status(original.getStatus())
                        .entity("{\"error\":\"HTTP " + original.getStatus() + "\", \"message\":\"" + exception.getMessage() + "\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return original;
        }

        // Log the full stack trace server-side so developers can investigate
        logger.log(Level.SEVERE, "Unhandled exception caught by global safety net: " + exception.getMessage(), exception);

        // Return a clean, generic JSON 500 — NO stack trace exposed to the client
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Internal Server Error\", \"message\":\"An unexpected error occurred on the server. Please contact the system administrator.\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
