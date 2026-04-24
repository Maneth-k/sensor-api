package org.example;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS Application configuration.
 *
 * The versioned entry point /api/v1 is established by the servlet mapping
 * in web.xml (/api/v1/*). This class tells Jersey to scan the org.example
 * package for all @Path resource classes and @Provider classes automatically.
 *
 * Lifecycle note: By default, JAX-RS creates a NEW instance of each @Path
 * resource class per HTTP request (request-scoped). Instance fields are NOT
 * shared between requests. To share state (our in-memory DataStore), we use
 * a static Singleton (DataStore.getInstance()) backed by ConcurrentHashMap
 * to prevent race conditions when multiple requests hit the API simultaneously.
 */
public class ApiConfig extends ResourceConfig {
    public ApiConfig() {
        packages("org.example");
    }
}
