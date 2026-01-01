package com.codehills.carfuel.exception;

/**
 * Custom exception thrown when a requested resource (e.g., Car) is not found.
 * 
 * This exception is used to signal a 404 Not Found error in the REST API.
 * 
 * Why create a custom exception?
 * - It makes the code more readable and explicit about what went wrong
 * - It allows us to handle this specific error differently from other
 * exceptions
 * - Spring Boot can automatically convert this to a 404 HTTP response
 * 
 * Example usage:
 * if (car == null) {
 * throw new ResourceNotFoundException("Car not found with ID: " + carId);
 * }
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor that accepts an error message.
     * 
     * @param message Descriptive error message (e.g., "Car not found with ID: 123")
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor that accepts an error message and a cause.
     * Useful when wrapping another exception.
     * 
     * @param message Descriptive error message
     * @param cause   The underlying exception that caused this error
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
