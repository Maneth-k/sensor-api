package org.example.Resource;

import org.example.DataStore;
import org.example.models.Room;
import org.example.Exceptions.RoomNotEmptyException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Part 2 - Room Management
 * Manages the /api/v1/rooms collection.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {

    private DataStore db = DataStore.getInstance();

    // GET /rooms — List all rooms
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(db.getRooms().values());
        return Response.ok(roomList).build();
    }

    // GET /rooms/{roomId} — Get a specific room by ID
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Not Found\", \"message\":\"Room '" + roomId + "' does not exist.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.ok(room).build();
    }

    // POST /rooms — Create a new room
    @POST
    public Response createRoom(Room room) {
        // Validate required fields
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Bad Request\", \"message\":\"Room 'id' field is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Bad Request\", \"message\":\"Room 'name' field is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Check for duplicate ID
        if (db.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Conflict\", \"message\":\"Room with ID '" + room.getId() + "' already exists.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Ensure sensorIds list is initialised
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        db.getRooms().put(room.getId(), room);

        // 201 Created with Location header pointing to the new resource
        URI location = URI.create("/api/v1/rooms/" + room.getId());
        return Response.created(location).entity(room).build();
    }

    // DELETE /rooms/{roomId} — Decommission a room (blocked if sensors still assigned)
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Not Found\", \"message\":\"Room '" + roomId + "' does not exist.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Safety Logic: prevent data orphans — room cannot be deleted while it has sensors
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room '" + roomId + "'. It is currently occupied by " +
                room.getSensorIds().size() + " active hardware sensor(s). " +
                "Please reassign or remove all sensors before decommissioning this room."
            );
        }

        db.getRooms().remove(roomId);

        // 204 No Content — success, nothing to return
        return Response.noContent().build();
    }
}
