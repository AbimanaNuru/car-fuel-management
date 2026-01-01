# Car Fuel Management System

A comprehensive car management and fuel tracking system built with **Spring Boot** (backend) and **Java CLI** (client). This project demonstrates backend fundamentals, REST API design, servlet implementation, and HTTP client usage.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [CLI Usage](#cli-usage)
- [Project Structure](#project-structure)
- [Key Concepts](#key-concepts)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

This system allows users to:
- Register and manage cars
- Track fuel consumption with detailed entries
- Calculate fuel statistics (total fuel, cost, average consumption)
- Interact via REST API or command-line interface

**Assignment Context**: Built for CodeHills Academy technical assessment to demonstrate Java backend skills, problem-solving abilities, and code quality.

## ✨ Features

### Backend (Spring Boot)
- ✅ RESTful API with full CRUD operations
- ✅ Manual servlet implementation (demonstrates HTTP lifecycle knowledge)
- ✅ In-memory storage (thread-safe with ConcurrentHashMap)
- ✅ Comprehensive error handling (404, 400, 500)
- ✅ Input validation and data integrity checks
- ✅ Fuel consumption calculation (L/100km)

### CLI Client (Java)
- ✅ Standalone executable JAR
- ✅ Modern Java HTTP Client (java.net.http)
- ✅ Command-line argument parsing
- ✅ Formatted output with visual feedback
- ✅ Comprehensive error handling

## 🏗 Architecture

```
┌─────────────────┐
│   CLI Client    │  (Standalone Java Application)
│                 │
│  - CommandParser│
│  - ApiClient    │
│  - CliApp       │
└────────┬────────┘
         │ HTTP
         │
┌────────▼────────┐
│  Backend Server │  (Spring Boot)
│                 │
│  ┌───────────┐  │
│  │Controller │  │  (REST API)
│  └─────┬─────┘  │
│        │        │
│  ┌─────▼─────┐  │
│  │  Service  │  │  (Business Logic)
│  └─────┬─────┘  │
│        │        │
│  ┌─────▼─────┐  │
│  │  Storage  │  │  (In-Memory)
│  └───────────┘  │
│                 │
│  ┌───────────┐  │
│  │  Servlet  │  │  (Manual HTTP)
│  └───────────┘  │
└─────────────────┘
```

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java 17 or higher** ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))

### Verify Installation

```bash
java -version   # Should show Java 17+
mvn -version    # Should show Maven 3.6+
```

### Installing Maven (if not installed)

**macOS (Homebrew)**:
```bash
brew install maven
```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt update
sudo apt install maven
```

**Windows (Chocolatey)**:
```bash
choco install maven
```

## 🚀 Installation

1. **Clone or download the project**:
```bash
cd /Users/macbook/Codehills/car-fuel-management
```

2. **Build the project**:
```bash
mvn clean install
```

This will:
- Compile all Java source files
- Run tests (if any)
- Package the backend as `backend/target/backend-1.0.0.jar`
- Package the CLI as `cli-client/target/cli-client-1.0.0.jar`

## 🎮 Running the Application

### Step 1: Start the Backend Server

Open a terminal and run:

```bash
cd backend
mvn spring-boot:run
```

**Or** run the JAR directly:

```bash
java -jar backend/target/backend-1.0.0.jar
```

You should see:
```
Started CarFuelApplication in X.XXX seconds
```

The server is now running at **http://localhost:8080**

### Step 2: Use the CLI Client

Open a **new terminal** (keep the backend running) and navigate to the project root:

```bash
cd /Users/macbook/Codehills/car-fuel-management
```

Run CLI commands using:

```bash
java -jar cli-client/target/cli-client-1.0.0.jar <command>
```

## 📖 API Documentation

### Base URL
```
http://localhost:8080
```

### Endpoints

#### 1. Create Car
**POST** `/api/cars`

**Request Body**:
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Response** (201 Created):
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": []
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Toyota","model":"Corolla","year":2018}'
```

---

#### 2. List All Cars
**GET** `/api/cars`

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2018,
    "fuelEntries": []
  }
]
```

**cURL Example**:
```bash
curl http://localhost:8080/api/cars
```

---

#### 3. Add Fuel Entry
**POST** `/api/cars/{id}/fuel`

**Request Body**:
```json
{
  "liters": 40,
  "price": 52.5,
  "odometer": 45000
}
```

**Response** (201 Created):
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": [
    {
      "id": 1,
      "liters": 40.0,
      "price": 52.5,
      "odometer": 45000,
      "createdAt": "2025-12-31T12:00:00"
    }
  ]
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/cars/1/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":40,"price":52.5,"odometer":45000}'
```

---

#### 4. Get Fuel Statistics (REST API)
**GET** `/api/cars/{id}/fuel/stats`

**Response** (200 OK):
```json
{
  "totalFuel": 120.0,
  "totalCost": 155.0,
  "averageConsumption": 6.4
}
```

**cURL Example**:
```bash
curl http://localhost:8080/api/cars/1/fuel/stats
```

---

#### 5. Get Fuel Statistics (Servlet)
**GET** `/servlet/fuel-stats?carId={id}`

**Response** (200 OK):
```json
{
  "totalFuel": 120.0,
  "totalCost": 155.0,
  "averageConsumption": 6.4
}
```

**cURL Example**:
```bash
curl http://localhost:8080/servlet/fuel-stats?carId=1
```

**Note**: This endpoint uses a manual servlet implementation to demonstrate understanding of the HTTP request lifecycle.

---

### Error Responses

**404 Not Found** (Car doesn't exist):
```json
{
  "error": "Not Found",
  "message": "Car not found with ID: 999"
}
```

**400 Bad Request** (Invalid input):
```json
{
  "error": "Bad Request",
  "message": "Liters must be a positive number"
}
```

## 💻 CLI Usage

### Show Help
```bash
java -jar cli-client/target/cli-client-1.0.0.jar help
```

### 1. Create a Car
```bash
java -jar cli-client/target/cli-client-1.0.0.jar create-car \
  --brand Toyota \
  --model Corolla \
  --year 2018
```

**Output**:
```
Creating car...

✅ Car created successfully!
Car ID: 1
Brand: Toyota
Model: Corolla
Year: 2018
```

### 2. Add Fuel Entry
```bash
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel \
  --carId 1 \
  --liters 40 \
  --price 52.5 \
  --odometer 45000
```

**Output**:
```
Adding fuel entry...

✅ Fuel entry added successfully!
Liters: 40.0 L
Price: 52.5
Odometer: 45000 km
```

### 3. View Fuel Statistics
```bash
java -jar cli-client/target/cli-client-1.0.0.jar fuel-stats --carId 1
```

**Output**:
```
Fetching fuel statistics...

📊 Fuel Statistics for Car #1
═══════════════════════════════════
Total fuel: 120.0 L
Total cost: 155.00
Average consumption: 6.4 L/100km
```

### Complete Example Workflow

```bash
# 1. Create a car
java -jar cli-client/target/cli-client-1.0.0.jar create-car --brand Honda --model Civic --year 2020

# 2. Add first fuel entry
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel --carId 1 --liters 45 --price 60.0 --odometer 10000

# 3. Add second fuel entry
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel --carId 1 --liters 50 --price 65.0 --odometer 10800

# 4. View statistics
java -jar cli-client/target/cli-client-1.0.0.jar fuel-stats --carId 1
```

## 📁 Project Structure

```
car-fuel-management/
│
├── pom.xml                          # Parent POM
├── README.md                        # This file
├── .gitignore
│
├── backend/                         # Spring Boot backend
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/codehills/carfuel/
│       │   ├── CarFuelApplication.java       # Main class
│       │   ├── controller/
│       │   │   └── CarController.java        # REST API endpoints
│       │   ├── service/
│       │   │   └── CarService.java           # Business logic
│       │   ├── model/
│       │   │   ├── Car.java                  # Car entity
│       │   │   ├── FuelEntry.java            # Fuel entry entity
│       │   │   └── FuelStats.java            # Statistics DTO
│       │   ├── servlet/
│       │   │   └── FuelStatsServlet.java     # Manual servlet
│       │   └── exception/
│       │       └── ResourceNotFoundException.java
│       └── resources/
│           └── application.yml               # Configuration
│
└── cli-client/                      # Java CLI application
    ├── pom.xml
    └── src/main/java/com/codehills/carfuel/cli/
        ├── CliApplication.java              # Main CLI class
        ├── CommandParser.java               # Argument parser
        └── ApiClient.java                   # HTTP client
```

## 🧠 Key Concepts

### 1. **Spring Boot Auto-Configuration**
- `@SpringBootApplication` combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`
- Spring automatically configures Tomcat, Jackson (JSON), and other components

### 2. **Dependency Injection**
- `@Autowired` tells Spring to inject dependencies
- Promotes loose coupling and testability
- Example: `CarService` is injected into `CarController` and `FuelStatsServlet`

### 3. **Service Layer Pattern**
- Business logic is separated from HTTP handling
- Both REST API and Servlet use the same `CarService` instance
- Ensures data consistency and code reusability

### 4. **In-Memory Storage**
- `ConcurrentHashMap` provides thread-safe storage
- `AtomicLong` ensures unique ID generation across threads
- No database required for this assignment

### 5. **Servlet Lifecycle**
- `init()` → `service()` → `doGet()`/`doPost()` → `destroy()`
- Manual parameter parsing, header setting, and JSON serialization
- Demonstrates understanding of HTTP at a lower level

### 6. **HTTP Client (Java 11+)**
- Modern replacement for `HttpURLConnection`
- Supports HTTP/2, async requests, and better API design
- Used in CLI for backend communication

### 7. **Fuel Consumption Calculation**
Formula: `(totalFuel / totalDistance) * 100`

Where:
- `totalFuel` = sum of all liters
- `totalDistance` = max odometer - min odometer

Example:
- 120 liters over 2000 km = (120 / 2000) × 100 = **6.0 L/100km**

## 🔧 Troubleshooting

### Backend won't start

**Issue**: Port 8080 already in use

**Solution**: Kill the process using port 8080
```bash
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Or** change the port in `backend/src/main/resources/application.yml`:
```yaml
server:
  port: 8081
```

### CLI can't connect to backend

**Issue**: `Connection refused`

**Solution**: Ensure backend is running on http://localhost:8080

**Issue**: Using different port

**Solution**: Update `BASE_URL` in `ApiClient.java`:
```java
private static final String BASE_URL = "http://localhost:8081";
```

### Maven build fails

**Issue**: Java version mismatch

**Solution**: Ensure Java 17+ is installed
```bash
java -version
```

**Issue**: Dependencies not downloading

**Solution**: Clear Maven cache
```bash
rm -rf ~/.m2/repository
mvn clean install
```
---

**Built by Nuru **

