package com.codehills.carfuel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codehills.carfuel.exception.ResourceNotFoundException;
import com.codehills.carfuel.model.Car;
import com.codehills.carfuel.model.FuelStats;
import com.codehills.carfuel.service.CarService;

/**
 * CarController handles HTTP requests for car and fuel management.
 * 
 * This is the CONTROLLER LAYER - it handles HTTP requests and responses.
 * 
 * Key Spring annotations explained:
 * 
 * @RestController - Combines @Controller and @ResponseBody
 *                 Tells Spring this class handles REST API requests
 *                 Automatically converts return values to JSON
 * 
 * @RequestMapping - Defines the base URL path for all endpoints in this
 *                 controller
 *                 All endpoints will start with /api/cars
 * 
 * @Autowired - Tells Spring to inject the CarService instance
 *            Spring automatically creates CarService and provides it here
 *            This is called "Dependency Injection"
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    /**
     * CarService instance injected by Spring.
     * 
     * Why use @Autowired?
     * - We don't create the service ourselves (no "new CarService()")
     * - Spring manages the lifecycle and ensures we get the same instance
     * - This makes testing easier (we can inject a mock service)
     */
    @Autowired
    private CarService carService;

    /**
     * Creates a new car.
     * 
     * Endpoint: POST /api/cars
     * Request Body: { "brand": "Toyota", "model": "Corolla", "year": 2018 }
     * Response: Created car with ID (HTTP 201 Created)
     * 
     * @PostMapping - Handles HTTP POST requests
     * @RequestBody - Automatically converts JSON request body to Map
     * 
     * @param carData Map containing brand, model, and year
     * @return ResponseEntity with created car and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Map<String, Object> carData) {
        // Extract data from request body
        String brand = (String) carData.get("brand");
        String model = (String) carData.get("model");
        Integer year = (Integer) carData.get("year");

        // Validate required fields
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (year == null || year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 1900 and 2100");
        }

        // Call service to create the car
        Car car = carService.createCar(brand, model, year);

        // Return HTTP 201 Created with the car object
        // ResponseEntity allows us to set the HTTP status code
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    /**
     * Retrieves all cars.
     * 
     * Endpoint: GET /api/cars
     * Response: Array of all cars (HTTP 200 OK)
     * 
     * @GetMapping - Handles HTTP GET requests
     * 
     * @return List of all cars
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    /**
     * Adds a fuel entry to a specific car.
     * 
     * Endpoint: POST /api/cars/{id}/fuel
     * URL Example: POST /api/cars/1/fuel
     * Request Body: { "liters": 40, "price": 52.5, "odometer": 45000 }
     * Response: Updated car with new fuel entry (HTTP 201 Created)
     * 
     * @PathVariable - Extracts the {id} from the URL path
     *               Example: /api/cars/1/fuel -> id = 1
     * 
     * @param id       Car ID from URL
     * @param fuelData Map containing liters, price, and odometer
     * @return ResponseEntity with updated car and HTTP 201 status
     */
    @PostMapping("/{id}/fuel")
    public ResponseEntity<Car> addFuelEntry(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fuelData) {

        // Extract data from request body
        // Note: JSON numbers can be Integer or Double, so we handle both
        Double liters = convertToDouble(fuelData.get("liters"));
        Double price = convertToDouble(fuelData.get("price"));
        Integer odometer = convertToInteger(fuelData.get("odometer"));

        // Validate required fields
        if (liters == null || liters <= 0) {
            throw new IllegalArgumentException("Liters must be a positive number");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price must be a positive number");
        }
        if (odometer == null || odometer <= 0) {
            throw new IllegalArgumentException("Odometer must be a positive number");
        }

        // Call service to add fuel entry
        Car car = carService.addFuelEntry(id, liters, price, odometer);

        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    /**
     * Retrieves fuel consumption statistics for a specific car.
     * 
     * Endpoint: GET /api/cars/{id}/fuel/stats
     * URL Example: GET /api/cars/1/fuel/stats
     * Response: { "totalFuel": 120.0, "totalCost": 155.0, "averageConsumption": 6.4
     * }
     * 
     * @param id Car ID from URL
     * @return ResponseEntity with fuel statistics
     */
    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<FuelStats> getFuelStats(@PathVariable Long id) {
        FuelStats stats = carService.getFuelStats(id);
        return ResponseEntity.ok(stats);
    }

    /**
     * Exception handler for ResourceNotFoundException.
     * 
     * This method is called automatically when a ResourceNotFoundException is
     * thrown.
     * It converts the exception to an HTTP 404 Not Found response.
     * 
     * @ExceptionHandler - Tells Spring to call this method when the specified
     *                   exception occurs
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error message and HTTP 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        // Create error response
        Map<String, String> error = Map.of(
                "error", "Not Found",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Exception handler for IllegalArgumentException (validation errors).
     * 
     * Converts validation errors to HTTP 400 Bad Request.
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error message and HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> error = Map.of(
                "error", "Bad Request",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Exception handler for all other exceptions.
     * 
     * Catches any unexpected errors and returns HTTP 500 Internal Server Error.
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleInternalError(Exception ex) {
        Map<String, String> error = Map.of(
                "error", "Internal Server Error",
                "message", "An unexpected error occurred: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Helper method to convert Object to Double.
     * Handles both Integer and Double from JSON.
     */
    private Double convertToDouble(Object value) {
        if (value == null)
            return null;
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Integer)
            return ((Integer) value).doubleValue();
        if (value instanceof String)
            return Double.parseDouble((String) value);
        return null;
    }

    /**
     * Helper method to convert Object to Integer.
     * Handles both Integer and Double from JSON.
     */
    private Integer convertToInteger(Object value) {
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Double)
            return ((Double) value).intValue();
        if (value instanceof String)
            return Integer.parseInt((String) value);
        return null;
    }
}
