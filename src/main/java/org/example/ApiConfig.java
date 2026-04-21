package org.example;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiConfig extends ResourceConfig {
    public ApiConfig() {
        // This tells Jersey to scan this specific folder for any classes with @Path
        packages("org.example");
    }
}
