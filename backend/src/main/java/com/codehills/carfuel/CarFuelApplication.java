package com.codehills.carfuel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.codehills.carfuel.servlet.FuelStatsServlet;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
public class CarFuelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarFuelApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServletRegistration() {
        FuelStatsServlet servlet = new FuelStatsServlet();
        ServletRegistrationBean<FuelStatsServlet> registration = new ServletRegistrationBean<>(servlet,
                "/servlet/fuel-stats");
        registration.setName("FuelStatsServlet");
        return registration;
    }

    @Bean
    public OpenAPI carFuelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car Fuel Management API")
                        .description("REST API for managing cars and tracking fuel consumption")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CodeHills Academy")
                                .email("info@code-hills.com")));
    }
}
