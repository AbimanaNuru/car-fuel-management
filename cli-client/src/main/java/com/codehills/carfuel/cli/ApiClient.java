package com.codehills.carfuel.cli;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * ApiClient handles all HTTP communication with the backend server.
 * 
 * This class demonstrates the use of Java's modern HTTP Client API (Java 11+).
 * 
 * Key concepts:
 * - HttpClient: Sends HTTP requests and receives responses
 * - HttpRequest: Represents an HTTP request (method, URL, headers, body)
 * - HttpResponse: Represents an HTTP response (status, headers, body)
 * 
 * Why separate this from the main CLI?
 * - Separation of concerns: HTTP logic is isolated
 * - Reusability: Can be used by different CLI commands
 * - Testability: Easier to test HTTP communication separately
 */
public class ApiClient {

    /**
     * Base URL of the backend server.
     * Change this if your server runs on a different host/port.
     */
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * HttpClient instance for sending requests.
     * 
     * We create it once and reuse it for all requests (more efficient).
     * HttpClient is thread-safe and can be shared.
     */
    private final HttpClient httpClient;

    /**
     * Gson instance for JSON parsing.
     * 
     * Gson is a library for converting between Java objects and JSON.
     * - toJson(): Converts Java object to JSON string
     * - fromJson(): Converts JSON string to Java object
     */
    private final Gson gson;

    /**
     * Constructor initializes the HTTP client and JSON parser.
     */
    public ApiClient() {
        // Build HTTP client with custom configuration
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)) // Timeout for establishing connection
                .build();

        this.gson = new Gson();
    }

    /**
     * Creates a new car on the backend.
     * 
     * Sends: POST /api/cars
     * Body: { "brand": "Toyota", "model": "Corolla", "year": 2018 }
     * 
     * @param brand Car brand
     * @param model Car model
     * @param year  Manufacturing year
     * @return Response body as String (JSON)
     * @throws Exception If request fails
     */
    public String createCar(String brand, String model, int year) throws Exception {
        // Create JSON request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("brand", brand);
        requestBody.addProperty("model", model);
        requestBody.addProperty("year", year);

        // Convert to JSON string
        String jsonBody = gson.toJson(requestBody);

        // Build HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars"))
                .header("Content-Type", "application/json") // Tell server we're sending JSON
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // Set request body
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString() // Convert response body to String
        );

        // Check if request was successful
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Failed to create car. Status: " + response.statusCode() +
                    ", Response: " + response.body());
        }
    }

    /**
     * Adds a fuel entry to a specific car.
     * 
     * Sends: POST /api/cars/{carId}/fuel
     * Body: { "liters": 40, "price": 52.5, "odometer": 45000 }
     * 
     * @param carId    Car ID
     * @param liters   Amount of fuel in liters
     * @param price    Total cost
     * @param odometer Odometer reading
     * @return Response body as String (JSON)
     * @throws Exception If request fails
     */
    public String addFuelEntry(long carId, double liters, double price, int odometer) throws Exception {
        // Create JSON request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("liters", liters);
        requestBody.addProperty("price", price);
        requestBody.addProperty("odometer", odometer);

        String jsonBody = gson.toJson(requestBody);

        // Build HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars/" + carId + "/fuel"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        // Check if request was successful
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Failed to add fuel entry. Status: " + response.statusCode() +
                    ", Response: " + response.body());
        }
    }

    /**
     * Retrieves fuel statistics for a specific car.
     * 
     * Sends: GET /api/cars/{carId}/fuel/stats
     * 
     * @param carId Car ID
     * @return Response body as String (JSON with statistics)
     * @throws Exception If request fails
     */
    public String getFuelStats(long carId) throws Exception {
        // Build HTTP GET request
        // GET requests don't have a body, so we just specify the URI
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/cars/" + carId + "/fuel/stats"))
                .GET() // HTTP GET method
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        // Check if request was successful
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Failed to get fuel stats. Status: " + response.statusCode() +
                    ", Response: " + response.body());
        }
    }
}
