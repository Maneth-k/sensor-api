package org.example.Filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider // Crucial: Tells Jersey this is a global provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // The coursework strictly requires java.util.logging.Logger
    private static final Logger logger = Logger.getLogger(ApiLoggingFilter.class.getName());

    // 1. This intercepts the INCOMING request before it hits your Resource classes
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        logger.info("INCOMING REQUEST: [" + method + "] " + uri);
    }

    // 2. This intercepts the OUTGOING response after your Resource classes are done processing
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();

        logger.info("OUTGOING RESPONSE: Status Code [" + status + "]");
    }
}
