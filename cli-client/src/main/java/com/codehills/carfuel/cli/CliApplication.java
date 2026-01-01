package com.codehills.carfuel.cli;

import com.codehills.carfuel.cli.CommandParser.ParsedCommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * CliApplication is the main entry point for the CLI client.
 * 
 * This application allows users to interact with the Car Fuel Management
 * backend
 * through command-line commands.
 * 
 * Supported commands:
 * 1. create-car --brand <brand> --model <model> --year <year>
 * 2. add-fuel --carId <id> --liters <liters> --price <price> --odometer
 * <odometer>
 * 3. fuel-stats --carId <id>
 * 
 * Architecture:
 * - CommandParser: Parses command-line arguments
 * - ApiClient: Handles HTTP communication with backend
 * - CliApplication: Coordinates parsing and API calls, displays results
 */
public class CliApplication {

    private final CommandParser parser;
    private final ApiClient apiClient;
    private final Gson gson;

    /**
     * Constructor initializes the parser and API client.
     */
    public CliApplication() {
        this.parser = new CommandParser();
        this.apiClient = new ApiClient();
        this.gson = new Gson();
    }

    /**
     * Main method - entry point of the CLI application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        CliApplication app = new CliApplication();

        try {
            app.run(args);
        } catch (Exception e) {
            // Print error message in red (ANSI color code)
            System.err.println("\n❌ Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Runs the CLI application.
     * 
     * @param args Command-line arguments
     * @throws Exception If command execution fails
     */
    public void run(String[] args) throws Exception {
        // Show help if no arguments provided
        if (args.length == 0) {
            showHelp();
            return;
        }

        // Parse the command
        ParsedCommand command = parser.parse(args);

        // Execute the appropriate command
        switch (command.getCommandName()) {
            case "create-car" -> executeCreateCar(command);
            case "add-fuel" -> executeAddFuel(command);
            case "fuel-stats" -> executeFuelStats(command);
            case "help" -> showHelp();
            default -> throw new IllegalArgumentException(
                    "Unknown command: " + command.getCommandName() + ". Use 'help' to see available commands.");
        }
    }

    /**
     * Executes the create-car command.
     * 
     * @param command Parsed command with parameters
     * @throws Exception If API call fails
     */
    private void executeCreateCar(ParsedCommand command) throws Exception {
        // Extract required parameters
        String brand = command.getRequiredParameter("brand");
        String model = command.getRequiredParameter("model");
        String yearStr = command.getRequiredParameter("year");

        // Parse year as integer
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a valid number");
        }

        // Call API
        System.out.println("Creating car...");
        String response = apiClient.createCar(brand, model, year);

        // Parse and display response
        JsonObject car = gson.fromJson(response, JsonObject.class);
        System.out.println("\n✅ Car created successfully!");
        System.out.println("Car ID: " + car.get("id").getAsLong());
        System.out.println("Brand: " + car.get("brand").getAsString());
        System.out.println("Model: " + car.get("model").getAsString());
        System.out.println("Year: " + car.get("year").getAsInt());
    }

    /**
     * Executes the add-fuel command.
     * 
     * @param command Parsed command with parameters
     * @throws Exception If API call fails
     */
    private void executeAddFuel(ParsedCommand command) throws Exception {
        // Extract required parameters
        String carIdStr = command.getRequiredParameter("carId");
        String litersStr = command.getRequiredParameter("liters");
        String priceStr = command.getRequiredParameter("price");
        String odometerStr = command.getRequiredParameter("odometer");

        // Parse parameters
        long carId;
        double liters;
        double price;
        int odometer;

        try {
            carId = Long.parseLong(carIdStr);
            liters = Double.parseDouble(litersStr);
            price = Double.parseDouble(priceStr);
            odometer = Integer.parseInt(odometerStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in parameters");
        }

        // Call API
        System.out.println("Adding fuel entry...");
        apiClient.addFuelEntry(carId, liters, price, odometer);

        // Display success message
        System.out.println("\n✅ Fuel entry added successfully!");
        System.out.println("Liters: " + liters + " L");
        System.out.println("Price: " + price);
        System.out.println("Odometer: " + odometer + " km");
    }

    /**
     * Executes the fuel-stats command.
     * 
     * @param command Parsed command with parameters
     * @throws Exception If API call fails
     */
    private void executeFuelStats(ParsedCommand command) throws Exception {
        // Extract required parameter
        String carIdStr = command.getRequiredParameter("carId");

        // Parse car ID
        long carId;
        try {
            carId = Long.parseLong(carIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Car ID must be a valid number");
        }

        // Call API
        System.out.println("Fetching fuel statistics...");
        String response = apiClient.getFuelStats(carId);

        // Parse and display response
        JsonObject stats = gson.fromJson(response, JsonObject.class);

        double totalFuel = stats.get("totalFuel").getAsDouble();
        double totalCost = stats.get("totalCost").getAsDouble();
        double avgConsumption = stats.get("averageConsumption").getAsDouble();

        System.out.println("\n📊 Fuel Statistics for Car #" + carId);
        System.out.println("═══════════════════════════════════");
        System.out.println("Total fuel: " + String.format("%.1f", totalFuel) + " L");
        System.out.println("Total cost: " + String.format("%.2f", totalCost));
        System.out.println("Average consumption: " + String.format("%.1f", avgConsumption) + " L/100km");
    }

    /**
     * Displays help information.
     */
    private void showHelp() {
        System.out.println("\n🚗 Car Fuel Management CLI");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("\nAvailable Commands:");
        System.out.println("\n1. Create a new car:");
        System.out.println("   create-car --brand <brand> --model <model> --year <year>");
        System.out.println("   Example: create-car --brand Toyota --model Corolla --year 2018");

        System.out.println("\n2. Add a fuel entry:");
        System.out.println("   add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("   Example: add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000");

        System.out.println("\n3. View fuel statistics:");
        System.out.println("   fuel-stats --carId <id>");
        System.out.println("   Example: fuel-stats --carId 1");

        System.out.println("\n4. Show this help:");
        System.out.println("   help");
        System.out.println("\n═══════════════════════════════════════════════════════════\n");
    }
}
