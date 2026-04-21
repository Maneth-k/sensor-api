package org.example;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;

public class Main {
    // The coursework requires the entry point to be /api/v1
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) {
        try {
            // 1. Load our configuration
            ApiConfig config = new ApiConfig();

            // 2. Start the Grizzly server at the specified URI
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

            System.out.println("Smart Campus API started.");
            System.out.println("Test it at: " + BASE_URI + "test");
            System.out.println("Press Ctrl+C to stop the server...");

            // Keep the server running
            Thread.currentThread().join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}