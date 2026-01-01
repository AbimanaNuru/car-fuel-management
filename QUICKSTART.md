# Quick Start Guide

## 🚀 Running the Application

### Terminal 1: Start the Backend

```bash
cd /Users/macbook/Codehills/car-fuel-management/backend
mvn spring-boot:run
```

Wait for: `Started CarFuelApplication in X.XXX seconds`

### Terminal 2: Test with CLI

```bash
cd /Users/macbook/Codehills/car-fuel-management

# Show help
java -jar cli-client/target/cli-client-1.0.0.jar help

# Create a car
java -jar cli-client/target/cli-client-1.0.0.jar create-car --brand Toyota --model Corolla --year 2018

# Add first fuel entry
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 10000

# Add second fuel entry
java -jar cli-client/target/cli-client-1.0.0.jar add-fuel --carId 1 --liters 45 --price 58.0 --odometer 10800

# View statistics
java -jar cli-client/target/cli-client-1.0.0.jar fuel-stats --carId 1
```

### Terminal 3: Test with cURL

```bash
# Create car
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{"brand":"Honda","model":"Civic","year":2020}'

# List all cars
curl http://localhost:8080/api/cars

# Add fuel entry
curl -X POST http://localhost:8080/api/cars/2/fuel \
  -H "Content-Type: application/json" \
  -d '{"liters":50,"price":65.0,"odometer":15000}'

# Get stats via REST API
curl http://localhost:8080/api/cars/2/fuel/stats

# Get stats via Servlet
curl http://localhost:8080/servlet/fuel-stats?carId=2
```

## 🎯 Expected Results

### CLI Output Example
```
📊 Fuel Statistics for Car #1
═══════════════════════════════════
Total fuel: 85.0 L
Total cost: 110.50
Average consumption: 10.6 L/100km
```

### API Response Example
```json
{
  "totalFuel": 85.0,
  "totalCost": 110.5,
  "averageConsumption": 10.6
}
```

## 🔧 Troubleshooting

**Port 8080 already in use?**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

**Build failed?**
```bash
# Clean and rebuild
mvn clean install
```

**CLI can't connect?**
- Ensure backend is running on http://localhost:8080
- Check for firewall issues

## 📧 Ready to Submit?

1. Initialize Git repository
2. Push to GitHub/GitLab
3. Test all functionality
4. Email info@code-hills.com with repository link

---

**Good luck! 🚀**
