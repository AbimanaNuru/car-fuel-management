package com.codehills.carfuel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.codehills.carfuel.servlet.FuelStatsServlet;

/**
 * Main application class for the Car Fuel Management backend.
 * 
 * This is the ENTRY POINT of the Spring Boot application.
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 *                        1. @Configuration - Marks this as a source of bean
 *                        definitions
 *                        2. @EnableAutoConfiguration - Tells Spring Boot to
 *                        auto-configure based on dependencies
 *                        3. @ComponentScan - Tells Spring to scan for
 *                        components in this package and sub-packages
 * 
 *                        What happens when you run this?
 *                        1. SpringApplication.run() starts the Spring Boot
 *                        application
 *                        2. Spring scans for components
 *                        (@Service, @RestController, @Component, etc.)
 *                        3. Spring creates instances (beans) of these
 *                        components
 *                        4. Spring injects dependencies where needed
 *                        (@Autowired)
 *                        5. Embedded Tomcat server starts on port 8080
 *                        (default)
 *                        6. Application is ready to handle HTTP requests
 */
@SpringBootApplication
public class CarFuelApplication {

    /**
     * Main method - entry point of the Java application.
     * 
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(CarFuelApplication.class, args);
    }

    /**
     * Registers the FuelStatsServlet with the servlet container.
     * 
     * @Bean - Tells Spring to manage this as a bean
     *       Spring will call this method and use the returned object
     * 
     *       Why do we need this?
     *       - Our servlet uses @Component, but we need to tell Spring what URL to
     *       map it to
     *       - ServletRegistrationBean does this mapping
     * 
     * @param fuelStatsServlet The servlet instance (injected by Spring)
     * @return ServletRegistrationBean that maps the servlet to /servlet/fuel-stats
     */
    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServletRegistration(
            FuelStatsServlet fuelStatsServlet) {

        // Create registration bean
        // Parameter 1: The servlet instance
        // Parameter 2: The URL pattern(s) to map to this servlet
        ServletRegistrationBean<FuelStatsServlet> registration = new ServletRegistrationBean<>(fuelStatsServlet,
                "/servlet/fuel-stats");

        // Optional: Set servlet name (for logging/debugging)
        registration.setName("FuelStatsServlet");

        return registration;
    }
}
