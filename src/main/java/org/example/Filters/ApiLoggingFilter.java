package org.example.Filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Part 5.5 — API Request & Response Logging Filter
 *
 * Implements both ContainerRequestFilter and ContainerResponseFilter to provide
 * full observability of every API interaction without touching individual resource methods.
 *
 * Uses java.util.logging.Logger as required by the coursework specification.
 */
@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(ApiLoggingFilter.class.getName());

    /**
     * Intercepts every INCOMING request before it reaches any resource method.
     * Logs the HTTP method and full request URI.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri    = requestContext.getUriInfo().getRequestUri().toString();

        logger.info(">>> INCOMING REQUEST  : [" + method + "] " + uri);
    }

    /**
     * Intercepts every OUTGOING response after the resource method has completed.
     * Logs the final HTTP status code.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        int    status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri    = requestContext.getUriInfo().getRequestUri().toString();

        logger.info("<<< OUTGOING RESPONSE : [" + method + "] " + uri + " → HTTP " + status);
    }
}
