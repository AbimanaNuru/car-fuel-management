# Interview Preparation Guide

This guide will help you explain your code confidently during the CodeHills Academy interview.

## 🎯 Core Concepts to Understand

### 1. Spring Boot Auto-Configuration

**Question**: "How does Spring Boot work?"

**Answer**:
```
Spring Boot uses @SpringBootApplication which combines three annotations:
1. @Configuration - Marks the class as a source of bean definitions
2. @EnableAutoConfiguration - Tells Spring to auto-configure based on dependencies
3. @ComponentScan - Scans for components in the package and sub-packages

When the application starts:
- SpringApplication.run() initializes the Spring container
- Spring scans for @Service, @RestController, @Component annotations
- Spring creates instances (beans) of these classes
- Spring injects dependencies where @Autowired is used
- Embedded Tomcat server starts on port 8080
```

**Code Reference**: `CarFuelApplication.java`

---

### 2. Dependency Injection

**Question**: "What is dependency injection and why use it?"

**Answer**:
```
Dependency Injection is when Spring provides (injects) dependencies instead of 
us creating them manually.

Example in CarController:
@Autowired
private CarService carService;

Benefits:
1. Loose Coupling - Controller doesn't create CarService
2. Testability - Easy to inject mock services for testing
3. Single Instance - Spring ensures one CarService instance is shared
4. Lifecycle Management - Spring handles creation and destruction

Without DI, we'd do:
private CarService carService = new CarService(); // Bad!

This creates tight coupling and makes testing harder.
```

**Code Reference**: `CarController.java`, `FuelStatsServlet.java`

---

### 3. Service Layer Pattern

**Question**: "Why separate business logic into a service layer?"

**Answer**:
```
The Service Layer pattern separates business logic from HTTP handling.

Architecture:
Controller (HTTP) → Service (Business Logic) → Storage (Data)

Benefits:
1. Separation of Concerns - Each layer has one responsibility
2. Reusability - Both REST API and Servlet use the same CarService
3. Testability - Can test business logic without HTTP layer
4. Maintainability - Changes to logic don't affect controllers

Example:
- CarController handles HTTP requests/responses
- CarService handles fuel calculation and validation
- Both use the same service instance for data consistency
```

**Code Reference**: `CarService.java`

---

### 4. Thread-Safe In-Memory Storage

**Question**: "Why use ConcurrentHashMap and AtomicLong?"

**Answer**:
```
ConcurrentHashMap:
- Thread-safe without synchronizing the entire map
- Multiple requests can read/write simultaneously
- Better performance than Collections.synchronizedMap()

AtomicLong:
- Ensures unique ID generation across threads
- incrementAndGet() is atomic (thread-safe)
- Prevents two cars from getting the same ID

Why this matters:
- Web servers handle multiple requests concurrently
- Without thread-safety, data could be corrupted
- Example: Two requests creating cars at the same time

Code:
private final Map<Long, Car> cars = new ConcurrentHashMap<>();
private final AtomicLong carIdCounter = new AtomicLong(0);
```

**Code Reference**: `CarService.java` lines 40-48

---

### 5. REST API Design

**Question**: "Explain your REST API design choices"

**Answer**:
```
RESTful Principles Applied:

1. Resource-Based URLs:
   - /api/cars (collection)
   - /api/cars/{id} (specific resource)
   - /api/cars/{id}/fuel (sub-resource)

2. HTTP Methods:
   - POST /api/cars - Create (201 Created)
   - GET /api/cars - Read all (200 OK)
   - POST /api/cars/{id}/fuel - Create sub-resource (201 Created)
   - GET /api/cars/{id}/fuel/stats - Read (200 OK)

3. Status Codes:
   - 200 OK - Successful GET
   - 201 Created - Successful POST
   - 400 Bad Request - Validation error
   - 404 Not Found - Resource doesn't exist
   - 500 Internal Server Error - Unexpected error

4. JSON Format:
   - Request/Response bodies use JSON
   - Content-Type: application/json header
```

**Code Reference**: `CarController.java`

---

### 6. Servlet vs REST Controller

**Question**: "What's the difference between your servlet and REST controller?"

**Answer**:
```
REST Controller (@RestController):
- High-level abstraction
- Spring handles parameter parsing automatically
- Automatic JSON conversion
- Exception handlers convert exceptions to HTTP responses
- Less code, more productivity

Servlet (extends HttpServlet):
- Low-level HTTP handling
- Manual parameter parsing: req.getParameter("carId")
- Manual header setting: resp.setContentType("application/json")
- Manual status codes: resp.setStatus(HttpServletResponse.SC_OK)
- Manual JSON serialization: objectMapper.writeValueAsString()

Why implement both?
- Demonstrates understanding of what Spring does behind the scenes
- Shows knowledge of HTTP request lifecycle
- Proves you can work at different abstraction levels

Both use the same CarService instance for data consistency.
```

**Code Reference**: `FuelStatsServlet.java` vs `CarController.java`

---

### 7. Servlet Lifecycle

**Question**: "Explain the servlet lifecycle"

**Answer**:
```
Servlet Lifecycle:

1. init() - Called once when servlet is first loaded
   - Initialize resources (database connections, etc.)
   
2. service() - Called for each request
   - Determines HTTP method (GET, POST, etc.)
   - Calls doGet(), doPost(), etc.
   
3. doGet()/doPost() - Handles specific HTTP methods
   - Processes request
   - Generates response
   
4. destroy() - Called once when servlet is unloaded
   - Clean up resources

In our code:
- We override doGet() to handle GET requests
- Container calls doGet() for each request to /servlet/fuel-stats
- We manually parse parameters and write JSON response

Thread Safety:
- One servlet instance handles all requests
- Must be thread-safe (don't use instance variables for request data)
```

**Code Reference**: `FuelStatsServlet.java`

---

### 8. HTTP Client (Java 11+)

**Question**: "How does your CLI communicate with the backend?"

**Answer**:
```
Modern Java HTTP Client (java.net.http):

1. Create HttpClient (reusable):
   HttpClient client = HttpClient.newBuilder()
       .connectTimeout(Duration.ofSeconds(10))
       .build();

2. Build HttpRequest:
   HttpRequest request = HttpRequest.newBuilder()
       .uri(URI.create("http://localhost:8080/api/cars"))
       .header("Content-Type", "application/json")
       .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
       .build();

3. Send and get HttpResponse:
   HttpResponse<String> response = client.send(
       request, 
       HttpResponse.BodyHandlers.ofString()
   );

4. Check status and parse response:
   if (response.statusCode() == 200) {
       String body = response.body();
   }

Benefits over old HttpURLConnection:
- Cleaner API
- HTTP/2 support
- Async support
- Better error handling
```

**Code Reference**: `ApiClient.java`

---

### 9. Fuel Consumption Calculation

**Question**: "How do you calculate average fuel consumption?"

**Answer**:
```
Formula: (totalFuel / totalDistance) * 100

Where:
- totalFuel = sum of all liters from fuel entries
- totalDistance = max odometer - min odometer

Example:
Entry 1: 40L at 10,000 km
Entry 2: 50L at 10,800 km
Entry 3: 30L at 11,500 km

totalFuel = 40 + 50 + 30 = 120L
totalDistance = 11,500 - 10,000 = 1,500 km
averageConsumption = (120 / 1500) * 100 = 8.0 L/100km

Edge Cases Handled:
1. No fuel entries → return 0.0
2. Only one entry → return 0.0 (can't calculate distance)
3. All entries same odometer → return 0.0 (avoid division by zero)

Code validates that odometer readings increase to ensure data quality.
```

**Code Reference**: `CarService.java` method `calculateAverageConsumption()`

---

### 10. Error Handling

**Question**: "How do you handle errors in your application?"

**Answer**:
```
Multi-Layer Error Handling:

1. Custom Exception (ResourceNotFoundException):
   - Thrown when car doesn't exist
   - Extends RuntimeException
   - Contains descriptive message

2. Controller Exception Handlers:
   @ExceptionHandler(ResourceNotFoundException.class)
   - Catches specific exceptions
   - Converts to HTTP 404 response
   - Returns JSON error message

3. Validation:
   - Check required fields
   - Validate data types and ranges
   - Throw IllegalArgumentException for bad input
   - Converted to HTTP 400 response

4. Generic Exception Handler:
   - Catches unexpected errors
   - Returns HTTP 500 response
   - Prevents exposing stack traces to clients

Example Error Response:
{
  "error": "Not Found",
  "message": "Car not found with ID: 999"
}
```

**Code Reference**: `CarController.java` exception handlers, `ResourceNotFoundException.java`

---

## 🎤 Common Interview Questions

### Q1: "Walk me through your project architecture"

**Answer**:
```
The system has two main components:

1. Backend (Spring Boot):
   - Entry Point: CarFuelApplication.java
   - REST API: CarController handles HTTP requests
   - Business Logic: CarService manages cars and fuel entries
   - Storage: ConcurrentHashMap for thread-safe in-memory storage
   - Servlet: FuelStatsServlet demonstrates manual HTTP handling
   
2. CLI Client (Standalone Java):
   - Entry Point: CliApplication.java
   - Parser: CommandParser processes command-line arguments
   - HTTP Client: ApiClient communicates with backend
   
Communication: CLI sends HTTP requests to backend REST API

Data Flow Example:
CLI → HTTP POST → CarController → CarService → ConcurrentHashMap
```

### Q2: "Why use in-memory storage instead of a database?"

**Answer**:
```
Assignment Requirements:
- Explicitly required in-memory storage
- No database or authentication needed

Benefits for this use case:
1. Simplicity - No database setup required
2. Fast - Direct memory access
3. Sufficient - Data doesn't need to persist

Thread-Safety Implementation:
- ConcurrentHashMap for concurrent access
- AtomicLong for unique ID generation

Limitations (acknowledged):
- Data lost on restart
- Not suitable for production
- Limited scalability

In production, I would use:
- PostgreSQL/MySQL for relational data
- Spring Data JPA for ORM
- Database migrations (Flyway/Liquibase)
```

### Q3: "How would you test this application?"

**Answer**:
```
Testing Strategy:

1. Unit Tests (Service Layer):
   - Test CarService methods in isolation
   - Mock dependencies if needed
   - Test edge cases (empty lists, null values)
   
2. Integration Tests (Controller):
   - Use @SpringBootTest
   - Test REST endpoints with MockMvc
   - Verify HTTP status codes and responses
   
3. Manual Testing (CLI):
   - Test each CLI command
   - Verify error handling
   - Test complete workflows
   
Example Unit Test:
@Test
void testCreateCar() {
    Car car = carService.createCar("Toyota", "Corolla", 2018);
    assertNotNull(car.getId());
    assertEquals("Toyota", car.getBrand());
}

Example Integration Test:
@Test
void testCreateCarEndpoint() {
    mockMvc.perform(post("/api/cars")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"brand\":\"Toyota\",\"model\":\"Corolla\",\"year\":2018}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
}
```

### Q4: "What would you improve in this project?"

**Answer**:
```
Potential Improvements:

1. Persistence:
   - Add database (PostgreSQL)
   - Use Spring Data JPA
   - Implement proper entity relationships

2. Validation:
   - Use Bean Validation (@Valid, @NotNull, @Min, @Max)
   - Custom validators for business rules
   
3. Security:
   - Add Spring Security
   - Implement JWT authentication
   - Role-based access control
   
4. Testing:
   - Add unit tests (JUnit 5)
   - Integration tests (MockMvc)
   - CLI tests
   
5. Documentation:
   - Add Swagger/OpenAPI for API docs
   - Javadoc for all public methods
   
6. Monitoring:
   - Add logging (SLF4J/Logback)
   - Metrics (Micrometer)
   - Health checks (Spring Actuator)
   
7. CLI Enhancements:
   - Interactive mode
   - Configuration file for backend URL
   - Colored output
   
8. Code Quality:
   - Add code coverage (JaCoCo)
   - Static analysis (SonarQube)
   - CI/CD pipeline
```

---

## 💡 Tips for the Interview

1. **Be Honest**: If you don't know something, say so. Then explain how you'd find out.

2. **Explain Trade-offs**: Every design decision has pros and cons. Discuss them.

3. **Use Examples**: Reference specific lines of code when explaining concepts.

4. **Show Learning**: Mention what you learned while building this project.

5. **Ask Questions**: Show curiosity about how they'd approach problems.

6. **Code Walkthrough**: Be ready to explain any line of code in detail.

7. **Debugging**: Be prepared to debug issues live if asked.

---

## 📚 Study Checklist

Before the interview, make sure you can explain:

- [ ] Spring Boot auto-configuration
- [ ] Dependency injection (@Autowired)
- [ ] REST API design principles
- [ ] HTTP status codes (200, 201, 400, 404, 500)
- [ ] Servlet lifecycle (init, service, doGet, destroy)
- [ ] Thread-safety (ConcurrentHashMap, AtomicLong)
- [ ] Service layer pattern
- [ ] Exception handling (@ExceptionHandler)
- [ ] HTTP Client usage (java.net.http)
- [ ] Command-line argument parsing
- [ ] Fuel consumption calculation formula
- [ ] JSON serialization/deserialization
- [ ] Maven project structure
- [ ] Why you made specific design choices

---

**Good luck! You've got this! 🚀**
