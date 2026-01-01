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

@Service
public class CarService {

    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    private final AtomicLong carIdCounter = new AtomicLong(0);
    private final AtomicLong fuelEntryIdCounter = new AtomicLong(0);

    public Car createCar(String brand, String model, Integer year) {
        Long id = carIdCounter.incrementAndGet();
        Car car = new Car(id, brand, model, year, new ArrayList<>());
        cars.put(id, car);
        return car;
    }

    public List<Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    public Car getCarById(Long id) {
        Car car = cars.get(id);
        if (car == null) {
            throw new ResourceNotFoundException("Car not found with ID: " + id);
        }
        return car;
    }

    public Car addFuelEntry(Long carId, Double liters, Double price, Integer odometer) {
        Car car = getCarById(carId);

        if (!car.getFuelEntries().isEmpty()) {
            FuelEntry lastEntry = car.getFuelEntries().get(car.getFuelEntries().size() - 1);
            if (odometer <= lastEntry.getOdometer()) {
                throw new IllegalArgumentException(
                        "Odometer must be greater than previous reading: " + lastEntry.getOdometer());
            }
        }

        Long entryId = fuelEntryIdCounter.incrementAndGet();
        FuelEntry fuelEntry = new FuelEntry(entryId, liters, price, odometer, LocalDateTime.now());
        car.getFuelEntries().add(fuelEntry);

        return car;
    }

    public FuelStats getFuelStats(Long carId) {
        Car car = getCarById(carId);
        List<FuelEntry> entries = car.getFuelEntries();

        if (entries.isEmpty()) {
            return new FuelStats(0.0, 0.0, 0.0);
        }

        double totalFuel = entries.stream().mapToDouble(FuelEntry::getLiters).sum();
        double totalCost = entries.stream().mapToDouble(FuelEntry::getPrice).sum();
        Double averageConsumption = calculateAverageConsumption(entries, totalFuel);

        return new FuelStats(totalFuel, totalCost, averageConsumption);
    }

    private Double calculateAverageConsumption(List<FuelEntry> entries, double totalFuel) {
        if (entries.size() < 2) {
            return 0.0;
        }

        int minOdometer = entries.stream().mapToInt(FuelEntry::getOdometer).min().orElse(0);
        int maxOdometer = entries.stream().mapToInt(FuelEntry::getOdometer).max().orElse(0);
        int totalDistance = maxOdometer - minOdometer;

        if (totalDistance == 0) {
            return 0.0;
        }

        double consumption = (totalFuel / totalDistance) * 100;
        return Math.round(consumption * 100.0) / 100.0;
    }
}
