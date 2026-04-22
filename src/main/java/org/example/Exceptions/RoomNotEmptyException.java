package org.example.Exceptions;


// A simple custom exception that extends RuntimeException
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}