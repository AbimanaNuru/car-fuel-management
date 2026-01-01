# Car Fuel Management System

A high-performance, RESTful Spring Boot application for tracking vehicle fuel consumption and costs, featuring a clean architecture, OpenAPI documentation, and a dedicated CLI client.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### 1. Start Backend
The backend runs on port `8080` with an H2 in-memory database.

```bash
cd backend
mvn spring-boot:run
```

### 2. Access API Documentation
Interactive Swagger UI is available at:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### 3. Run CLI Client
A robust command-line interface for interacting with the API.

```bash
# Register a car
java -jar cli-client/target/cli-client-1.0.0.jar create-car --brand Toyota --model Camry --year 2022

# Add fuel
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel --carId 1 --liters 45.5 --price 1.20 --odometer 5000

# View stats
java -jar cli-client/target/cli-client-1.0.0.jar fuel-stats --carId 1
```

## 🏗 Architecture

- **Backend**: Spring Boot 3.2, Spring MVC, H2 Database
- **API**: RESTful design, standard `ApiResponse` wrapper, OpenAPI/Swagger
- **Client**: Java CLI with `HttpClient` and `Gson`
- **Code Quality**: Stream API usage, standardized error handling, clean POJOs

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/cars` | Register a new vehicle |
| `POST` | `/api/cars/{id}/fuel` | Record a fuel transaction |
| `GET` | `/api/cars/{id}/fuel/stats` | Retrieve consumption analytics |
| `GET` | `/api/cars` | List all registered vehicles |

## 🛠 Build

To build the entire project from root:

```bash
mvn clean install
```
