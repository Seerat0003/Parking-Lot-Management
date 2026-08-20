package com.parkinglot.parkinglot.exception;

public class UnauthorizedVenueAccessException extends RuntimeException {

    public UnauthorizedVenueAccessException(String message) {
        super(message);
    }
}
