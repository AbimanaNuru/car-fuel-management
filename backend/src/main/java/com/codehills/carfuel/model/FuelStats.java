package com.codehills.carfuel.model;

/**
 * FuelStats model representing calculated fuel consumption statistics.
 * 
 * This is a Data Transfer Object (DTO) used to return statistics to the client.
 * It's not stored in the database - it's calculated on-demand from fuel
 * entries.
 * 
 * The statistics include:
 * - Total fuel consumed
 * - Total cost spent on fuel
 * - Average fuel consumption per 100km
 */
public class FuelStats {

    /**
     * Total amount of fuel consumed across all entries (in liters).
     * Calculated by summing up all fuel entry liters.
     */
    private Double totalFuel;

    /**
     * Total cost spent on fuel across all entries.
     * Calculated by summing up all fuel entry prices.
     */
    private Double totalCost;

    /**
     * Average fuel consumption in liters per 100 kilometers.
     * 
     * Formula: (totalFuel / totalDistance) * 100
     * 
     * Where:
     * - totalFuel = sum of all liters
     * - totalDistance = difference between highest and lowest odometer readings
     * 
     * Example:
     * - If you used 120 liters to travel 2000 km:
     * - Average consumption = (120 / 2000) * 100 = 6.0 L/100km
     */
    private Double averageConsumption;

    /**
     * Default constructor.
     */
    public FuelStats() {
    }

    /**
     * Full constructor.
     * 
     * @param totalFuel          Total fuel consumed
     * @param totalCost          Total cost
     * @param averageConsumption Average consumption in L/100km
     */
    public FuelStats(Double totalFuel, Double totalCost, Double averageConsumption) {
        this.totalFuel = totalFuel;
        this.totalCost = totalCost;
        this.averageConsumption = averageConsumption;
    }

    // Getters and Setters

    public Double getTotalFuel() {
        return totalFuel;
    }

    public void setTotalFuel(Double totalFuel) {
        this.totalFuel = totalFuel;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(Double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }
}
