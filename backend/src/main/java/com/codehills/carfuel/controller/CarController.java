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
import com.codehills.carfuel.model.ApiResponse;
import com.codehills.carfuel.model.Car;
import com.codehills.carfuel.model.FuelStats;
import com.codehills.carfuel.service.CarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Management", description = "APIs for managing cars and fuel entries")
public class CarController {

    @Autowired
    private CarService carService;

    @Operation(summary = "Create a new car", description = "Register a new car in the system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Car created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Car>> createCar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Car details", required = true, content = @Content(schema = @Schema(example = "{\"brand\":\"Toyota\",\"model\":\"Corolla\",\"year\":2018}"))) @RequestBody Map<String, Object> carData) {

        String brand = (String) carData.get("brand");
        String model = (String) carData.get("model");
        Integer year = (Integer) carData.get("year");

        validateCarData(brand, model, year);

        Car car = carService.createCar(brand, model, year);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car created successfully", car));
    }

    @Operation(summary = "Get all cars", description = "Retrieve a list of all registered cars")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of cars")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Car>>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        String message = cars.isEmpty()
                ? "No cars found. Create your first car to get started!"
                : "Successfully retrieved " + cars.size() + " car(s)";
        return ResponseEntity.ok(ApiResponse.success(message, cars));
    }

    @Operation(summary = "Add fuel entry", description = "Add a new fuel entry for a specific car")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Fuel entry added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PostMapping("/{id}/fuel")
    public ResponseEntity<ApiResponse<Car>> addFuelEntry(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fuel entry details", required = true, content = @Content(schema = @Schema(example = "{\"liters\":40,\"price\":52.5,\"odometer\":45000}"))) @RequestBody Map<String, Object> fuelData) {

        Double liters = convertToDouble(fuelData.get("liters"));
        Double price = convertToDouble(fuelData.get("price"));
        Integer odometer = convertToInteger(fuelData.get("odometer"));

        validateFuelData(liters, price, odometer);

        Car car = carService.addFuelEntry(id, liters, price, odometer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fuel entry added successfully", car));
    }

    @Operation(summary = "Get fuel statistics", description = "Calculate and retrieve fuel consumption statistics for a car")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<ApiResponse<FuelStats>> getFuelStats(
            @Parameter(description = "Car ID", required = true) @PathVariable Long id) {
        FuelStats stats = carService.getFuelStats(id);
        return ResponseEntity.ok(ApiResponse.success("Fuel statistics calculated successfully", stats));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleInternalError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }

    private void validateCarData(String brand, String model, Integer year) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required");
        }
        if (year == null || year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 1900 and 2100");
        }
    }

    private void validateFuelData(Double liters, Double price, Integer odometer) {
        if (liters == null || liters <= 0) {
            throw new IllegalArgumentException("Liters must be positive");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (odometer == null || odometer <= 0) {
            throw new IllegalArgumentException("Odometer must be positive");
        }
    }

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
