package com.codehills.carfuel.cli;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private final Gson gson;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public String createCar(String brand, String model, int year) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("brand", brand);
        body.addProperty("model", model);
        body.addProperty("year", year);

        return post("/api/cars", body);
    }

    public String addFuelEntry(long carId, double liters, double price, int odometer) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("liters", liters);
        body.addProperty("price", price);
        body.addProperty("odometer", odometer);

        return post("/api/cars/" + carId + "/fuel", body);
    }

    public String getFuelStats(long carId) throws Exception {
        return get("/api/cars/" + carId + "/fuel/stats");
    }

    private String post(String endpoint, JsonObject body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return sendRequest(request);
    }

    private String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();

        return sendRequest(request);
    }

    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        }

        throw new Exception("Request failed: " + response.statusCode() + " - " + response.body());
    }
}
