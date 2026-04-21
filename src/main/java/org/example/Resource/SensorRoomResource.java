package org.example.Resource;

import org.example.DataStore;
import org.example.models.Room;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/rooms") // Maps to /api/v1/rooms
@Produces(MediaType.APPLICATION_JSON) // Everything this class returns is JSON
@Consumes(MediaType.APPLICATION_JSON) // Everything this class accepts (POST bodies) must be JSON
public class SensorRoomResource {

    // Grab our singleton database
    private DataStore db = DataStore.getInstance();

    // 1. GET / - List all rooms
    @GET
    public Response getAllRooms() {
        // Return the values from our ConcurrentHashMap
        return Response.ok(db.getRooms().values()).build();
    }

    // 2. GET /{roomId} - Fetch a specific room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);

        if (room == null) {
            // Return a standard 404 Not Found if the room doesn't exist
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}")
                    .build();
        }

        return Response.ok(room).build();
    }

    // 3. POST / - Create a new room
    @POST
    public Response createRoom(Room room) {
        // Basic validation
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }

        // Save to our "database"
        db.getRooms().put(room.getId(), room);

        // The rubric asks for "appropriate feedback upon success".
        // In REST, a successful POST should return a 201 Created status and a Location header
        // pointing to the new resource.
        return Response.created(URI.create("/api/v1/rooms/" + room.getId()))
                .entity(room)
                .build();
    }

    // 4. DELETE /{roomId} - Safely decommission a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);

        // 1. Check if the room actually exists
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}")
                    .build();
        }

        // 2. The Safety Logic: Prevent Data Orphans
        // Check if the sensor list has any items in it
        if (!room.getSensorIds().isEmpty()) {
            // We return a 409 Conflict because the request conflicts with the current state of the server
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Cannot delete room: It still contains active sensors.\"}")
                    .build();
        }

        // 3. If it's empty, we are safe to delete
        db.getRooms().remove(roomId);

        // A successful DELETE usually returns a 204 No Content (meaning success, but I have no data to send back)
        return Response.noContent().build();
    }
}