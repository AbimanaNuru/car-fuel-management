package com.codehills.carfuel.model;

import java.util.ArrayList;
import java.util.List;

public class Car {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private List<FuelEntry> fuelEntries;

    public Car() {
        this.fuelEntries = new ArrayList<>();
    }

    public Car(String brand, String model, Integer year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.fuelEntries = new ArrayList<>();
    }

    public Car(Long id, String brand, String model, Integer year, List<FuelEntry> fuelEntries) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.fuelEntries = fuelEntries != null ? fuelEntries : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<FuelEntry> getFuelEntries() {
        return fuelEntries;
    }

    public void setFuelEntries(List<FuelEntry> fuelEntries) {
        this.fuelEntries = fuelEntries;
    }
}
