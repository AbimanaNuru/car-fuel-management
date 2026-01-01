package com.codehills.carfuel.cli;

import com.codehills.carfuel.cli.CommandParser.ParsedCommand;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CliApplication {

    private final CommandParser parser;
    private final ApiClient apiClient;
    private final Gson gson;

    public CliApplication() {
        this.parser = new CommandParser();
        this.apiClient = new ApiClient();
        this.gson = new Gson();
    }

    public static void main(String[] args) {
        CliApplication app = new CliApplication();

        try {
            app.run(args);
        } catch (Exception e) {
            System.err.println("\n❌ Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            showHelp();
            return;
        }

        ParsedCommand command = parser.parse(args);

        switch (command.getCommandName()) {
            case "create-car" -> executeCreateCar(command);
            case "add-fuel" -> executeAddFuel(command);
            case "fuel-stats" -> executeFuelStats(command);
            case "help" -> showHelp();
            default -> throw new IllegalArgumentException("Unknown command: " + command.getCommandName());
        }
    }

    private void executeCreateCar(ParsedCommand command) throws Exception {
        String brand = command.getRequiredParameter("brand");
        String model = command.getRequiredParameter("model");
        int year = Integer.parseInt(command.getRequiredParameter("year"));

        System.out.println("Creating car...");
        String response = apiClient.createCar(brand, model, year);

        JsonObject car = gson.fromJson(response, JsonObject.class);
        System.out.println("\n✅ Car created successfully!");
        System.out.println("Car ID: " + car.get("id").getAsLong());
        System.out.println("Brand: " + car.get("brand").getAsString());
        System.out.println("Model: " + car.get("model").getAsString());
        System.out.println("Year: " + car.get("year").getAsInt());
    }

    private void executeAddFuel(ParsedCommand command) throws Exception {
        long carId = Long.parseLong(command.getRequiredParameter("carId"));
        double liters = Double.parseDouble(command.getRequiredParameter("liters"));
        double price = Double.parseDouble(command.getRequiredParameter("price"));
        int odometer = Integer.parseInt(command.getRequiredParameter("odometer"));

        System.out.println("Adding fuel entry...");
        apiClient.addFuelEntry(carId, liters, price, odometer);

        System.out.println("\n✅ Fuel entry added successfully!");
        System.out.println("Liters: " + liters + " L");
        System.out.println("Price: " + price);
        System.out.println("Odometer: " + odometer + " km");
    }

    private void executeFuelStats(ParsedCommand command) throws Exception {
        long carId = Long.parseLong(command.getRequiredParameter("carId"));

        System.out.println("Fetching fuel statistics...");
        String response = apiClient.getFuelStats(carId);

        JsonObject stats = gson.fromJson(response, JsonObject.class);

        System.out.println("\n📊 Fuel Statistics for Car #" + carId);
        System.out.println("═══════════════════════════════════");
        System.out.println("Total fuel: " + String.format("%.1f", stats.get("totalFuel").getAsDouble()) + " L");
        System.out.println("Total cost: " + String.format("%.2f", stats.get("totalCost").getAsDouble()));
        System.out.println("Average consumption: "
                + String.format("%.1f", stats.get("averageConsumption").getAsDouble()) + " L/100km");
    }

    private void showHelp() {
        System.out.println("\n🚗 Car Fuel Management CLI");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("\nCommands:");
        System.out.println("  create-car --brand <brand> --model <model> --year <year>");
        System.out.println("  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("  fuel-stats --carId <id>");
        System.out.println("  help");
        System.out.println();
    }
}
