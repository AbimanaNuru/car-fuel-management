package com.codehills.carfuel.model;

import java.time.LocalDateTime;

public class FuelEntry {

    private Long id;
    private Double liters;
    private Double price;
    private Integer odometer;
    private LocalDateTime createdAt;

    public FuelEntry() {
    }

    public FuelEntry(Double liters, Double price, Integer odometer) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.createdAt = LocalDateTime.now();
    }

    public FuelEntry(Long id, Double liters, Double price, Integer odometer, LocalDateTime createdAt) {
        this.id = id;
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLiters() {
        return liters;
    }

    public void setLiters(Double liters) {
        this.liters = liters;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
