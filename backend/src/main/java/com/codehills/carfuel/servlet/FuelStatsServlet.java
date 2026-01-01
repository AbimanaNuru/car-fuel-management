package com.codehills.carfuel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codehills.carfuel.exception.ResourceNotFoundException;
import com.codehills.carfuel.model.FuelStats;
import com.codehills.carfuel.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FuelStatsServlet demonstrates manual servlet implementation.
 * 
 * This is a TRADITIONAL JAVA SERVLET - it shows understanding of the HTTP
 * request lifecycle.
 * 
 * Key differences from REST Controller:
 * 1. Extends HttpServlet (not using Spring's @RestController)
 * 2. Manually parses request parameters
 * 3. Manually sets response headers and status codes
 * 4. Manually writes JSON to response
 * 
 * Why implement this?
 * - Demonstrates understanding of how HTTP works at a lower level
 * - Shows knowledge of servlet lifecycle (init, service, destroy)
 * - Proves you understand what Spring Boot does behind the scenes
 * 
 * Endpoint: GET /servlet/fuel-stats?carId={id}
 * Example: GET /servlet/fuel-stats?carId=1
 * 
 * @Component - Makes this a Spring-managed bean so we can inject CarService
 */
@Component
public class FuelStatsServlet extends HttpServlet {

    /**
     * CarService instance injected by Spring.
     * 
     * Important: Even though this is a servlet, Spring can still inject
     * dependencies
     * because we registered it as a Spring bean using @Component.
     * 
     * This allows the servlet to use the SAME CarService instance as the REST API,
     * ensuring data consistency.
     */
    @Autowired
    private CarService carService;

    /**
     * ObjectMapper for converting Java objects to JSON.
     * 
     * This is from Jackson library (included with Spring Boot).
     * We use it to manually serialize FuelStats to JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles HTTP GET requests.
     * 
     * This method is called automatically by the servlet container (Tomcat)
     * when a GET request is received at /servlet/fuel-stats
     * 
     * The servlet lifecycle:
     * 1. Container creates servlet instance (once)
     * 2. Container calls init() (once)
     * 3. For each request, container calls service() which calls doGet() or
     * doPost()
     * 4. When shutting down, container calls destroy() (once)
     * 
     * @param req  HttpServletRequest - Contains request data (parameters, headers,
     *             etc.)
     * @param resp HttpServletResponse - Used to send response (status, headers,
     *             body)
     * @throws ServletException If servlet-specific error occurs
     * @throws IOException      If I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // STEP 1: Manually parse query parameter
            // Example: /servlet/fuel-stats?carId=1
            // req.getParameter("carId") returns "1" as a String
            String carIdParam = req.getParameter("carId");

            // Validate that carId parameter exists
            if (carIdParam == null || carIdParam.trim().isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Missing required parameter: carId");
                return;
            }

            // Convert String to Long
            Long carId;
            try {
                carId = Long.parseLong(carIdParam);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid carId format. Must be a number.");
                return;
            }

            // STEP 2: Call service to get fuel stats
            // This uses the SAME service instance as the REST API
            FuelStats stats = carService.getFuelStats(carId);

            // STEP 3: Manually set response headers
            // Content-Type tells the client what format the response is in
            resp.setContentType("application/json");

            // Character encoding ensures special characters are handled correctly
            resp.setCharacterEncoding("UTF-8");

            // STEP 4: Manually set HTTP status code
            // 200 = OK (success)
            resp.setStatus(HttpServletResponse.SC_OK);

            // STEP 5: Manually write JSON response
            // Convert FuelStats object to JSON string
            String jsonResponse = objectMapper.writeValueAsString(stats);

            // Get the response writer and write the JSON
            PrintWriter out = resp.getWriter();
            out.print(jsonResponse);
            out.flush();

        } catch (ResourceNotFoundException e) {
            // Handle case where car doesn't exist
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (Exception e) {
            // Handle any other unexpected errors
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Helper method to send error responses.
     * 
     * This demonstrates manual error handling in servlets.
     * 
     * @param resp       HttpServletResponse
     * @param statusCode HTTP status code (404, 400, 500, etc.)
     * @param message    Error message
     * @throws IOException If writing response fails
     */
    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message)
            throws IOException {

        // Set response type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Set the error status code
        resp.setStatus(statusCode);

        // Create error JSON object
        Map<String, String> error = new HashMap<>();
        error.put("error", getStatusText(statusCode));
        error.put("message", message);

        // Convert to JSON and write to response
        String jsonError = objectMapper.writeValueAsString(error);
        PrintWriter out = resp.getWriter();
        out.print(jsonError);
        out.flush();
    }

    /**
     * Helper method to get human-readable status text.
     * 
     * @param statusCode HTTP status code
     * @return Status text (e.g., "Not Found" for 404)
     */
    private String getStatusText(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> "Bad Request";
            case HttpServletResponse.SC_NOT_FOUND -> "Not Found";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Error";
        };
    }
}
