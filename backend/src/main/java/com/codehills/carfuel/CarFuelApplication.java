package com.codehills.carfuel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.codehills.carfuel.servlet.FuelStatsServlet;

@SpringBootApplication
public class CarFuelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarFuelApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServlet(FuelStatsServlet servlet) {
        ServletRegistrationBean<FuelStatsServlet> registration = new ServletRegistrationBean<>(servlet,
                "/servlet/fuel-stats");
        registration.setName("FuelStatsServlet");
        return registration;
    }
}
