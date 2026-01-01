package com.codehills.carfuel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.codehills.carfuel.exception.ResourceNotFoundException;
import com.codehills.carfuel.model.Car;
import com.codehills.carfuel.model.FuelEntry;
import com.codehills.carfuel.model.FuelStats;

/**
 * CarService handles all business logic for car and fuel management.
 * 
 * This is the SERVICE LAYER - it sits between the controllers and the data
 * storage.
 * 
 * Why use a service layer?
 * 1. Separation of Concerns: Business logic is separate from HTTP handling
 * 2. Reusability: Both REST API and Servlet can use the same service
 * 3. Testability: Easier to test business logic without HTTP layer
 * 4. Maintainability: Changes to business logic don't affect controllers
 * 
 * @Service - Spring annotation that marks this as a service component.
 *          Spring will automatically create an instance (bean) of this class
 *          and inject it wherever needed using @Autowired.
 */
@Service
public class CarService {

    /**
     * In-memory storage for cars.
     * 
     * ConcurrentHashMap is used instead of HashMap because:
     * - Thread-safe: Multiple requests can access it simultaneously
     * - Better performance than synchronized HashMap
     * 
     * Key: Car ID (Long)
     * Value: Car object
     */
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();

    /**
     * Counter for generating unique car IDs.
     * 
     * AtomicLong is used because:
     * - Thread-safe: Ensures no two cars get the same ID
     * - incrementAndGet() atomically increments and returns the new value
     */
    private final AtomicLong carIdCounter = new AtomicLong(0);

    /**
     * Counter for generating unique fuel entry IDs.
     */
    private final AtomicLong fuelEntryIdCounter = new AtomicLong(0);

    /**
     * Creates a new car and stores it in memory.
     * 
     * @param brand Car brand (e.g., "Toyota")
     * @param model Car model (e.g., "Corolla")
     * @param year  Manufacturing year (e.g., 2018)
     * @return The created car with assigned ID
     */
    public Car createCar(String brand, String model, Integer year) {
        // Generate a unique ID for the new car
        Long id = carIdCounter.incrementAndGet();

        // Create the car object
        Car car = new Car(id, brand, model, year, new ArrayList<>());

        // Store it in our in-memory map
        cars.put(id, car);

        return car;
    }

    /**
     * Retrieves all cars from storage.
     * 
     * @return List of all cars (empty list if no cars exist)
     */
    public List<Car> getAllCars() {
        // Convert the map values to a list
        // We create a new ArrayList to avoid exposing our internal collection
        return new ArrayList<>(cars.values());
    }

    /**
     * Retrieves a specific car by ID.
     * 
     * @param id Car ID
     * @return The car object
     * @throws ResourceNotFoundException if car doesn't exist
     */
    public Car getCarById(Long id) {
        Car car = cars.get(id);

        if (car == null) {
            // Throw custom exception that will be converted to 404 by the controller
            throw new ResourceNotFoundException("Car not found with ID: " + id);
        }

        return car;
    }

    /**
     * Adds a fuel entry to a specific car.
     * 
     * @param carId    Car ID
     * @param liters   Amount of fuel in liters
     * @param price    Total cost
     * @param odometer Odometer reading
     * @return The updated car object
     * @throws ResourceNotFoundException if car doesn't exist
     */
    public Car addFuelEntry(Long carId, Double liters, Double price, Integer odometer) {
        // First, get the car (this will throw exception if not found)
        Car car = getCarById(carId);

        // Validate that odometer reading is increasing
        // This ensures data quality for consumption calculations
        if (!car.getFuelEntries().isEmpty()) {
            // Get the latest fuel entry
            FuelEntry lastEntry = car.getFuelEntries().get(car.getFuelEntries().size() - 1);

            if (odometer <= lastEntry.getOdometer()) {
                throw new IllegalArgumentException(
                        "Odometer reading must be greater than the previous reading (" +
                                lastEntry.getOdometer() + ")");
            }
        }

        // Generate unique ID for the fuel entry
        Long entryId = fuelEntryIdCounter.incrementAndGet();

        // Create the fuel entry
        FuelEntry fuelEntry = new FuelEntry(entryId, liters, price, odometer, LocalDateTime.now());

        // Add it to the car's fuel entries list
        car.getFuelEntries().add(fuelEntry);

        return car;
    }

    /**
     * Calculates fuel consumption statistics for a specific car.
     * 
     * This is the core business logic for fuel tracking.
     * 
     * @param carId Car ID
     * @return Fuel statistics (total fuel, cost, average consumption)
     * @throws ResourceNotFoundException if car doesn't exist
     */
    public FuelStats getFuelStats(Long carId) {
        // Get the car
        Car car = getCarById(carId);

        List<FuelEntry> entries = car.getFuelEntries();

        // If no fuel entries, return zeros
        if (entries.isEmpty()) {
            return new FuelStats(0.0, 0.0, 0.0);
        }

        // Calculate total fuel and total cost
        double totalFuel = 0.0;
        double totalCost = 0.0;

        for (FuelEntry entry : entries) {
            totalFuel += entry.getLiters();
            totalCost += entry.getPrice();
        }

        // Calculate average consumption (L/100km)
        Double averageConsumption = calculateAverageConsumption(entries, totalFuel);

        return new FuelStats(totalFuel, totalCost, averageConsumption);
    }

    /**
     * Calculates average fuel consumption in liters per 100 kilometers.
     * 
     * Formula: (totalFuel / totalDistance) * 100
     * 
     * Where totalDistance = max odometer - min odometer
     * 
     * @param entries   List of fuel entries
     * @param totalFuel Total fuel consumed
     * @return Average consumption in L/100km, or 0.0 if cannot calculate
     */
    private Double calculateAverageConsumption(List<FuelEntry> entries, double totalFuel) {
        // Need at least 2 entries to calculate distance
        if (entries.size() < 2) {
            return 0.0;
        }

        // Find minimum and maximum odometer readings
        int minOdometer = Integer.MAX_VALUE;
        int maxOdometer = Integer.MIN_VALUE;

        for (FuelEntry entry : entries) {
            if (entry.getOdometer() < minOdometer) {
                minOdometer = entry.getOdometer();
            }
            if (entry.getOdometer() > maxOdometer) {
                maxOdometer = entry.getOdometer();
            }
        }

        // Calculate total distance traveled
        int totalDistance = maxOdometer - minOdometer;

        // Avoid division by zero
        if (totalDistance == 0) {
            return 0.0;
        }

        // Calculate consumption per 100km
        // Example: 120 liters over 2000 km = (120 / 2000) * 100 = 6.0 L/100km
        double consumption = (totalFuel / totalDistance) * 100;

        // Round to 2 decimal places for readability
        return Math.round(consumption * 100.0) / 100.0;
    }
}
