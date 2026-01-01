package com.codehills.carfuel.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandParser parses command-line arguments into structured commands.
 * 
 * This class handles the parsing of CLI commands like:
 * - create-car --brand Toyota --model Corolla --year 2018
 * - add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000
 * - fuel-stats --carId 1
 * 
 * Why separate parsing from execution?
 * - Single Responsibility: This class only parses, doesn't execute
 * - Testability: Easy to test parsing logic separately
 * - Maintainability: Easy to add new commands
 */
public class CommandParser {

    /**
     * Parses command-line arguments.
     * 
     * Expected format:
     * args[0] = command name (e.g., "create-car")
     * args[1..n] = flags and values (e.g., "--brand", "Toyota")
     * 
     * @param args Command-line arguments from main()
     * @return ParsedCommand object containing command name and parameters
     * @throws IllegalArgumentException If arguments are invalid
     */
    public ParsedCommand parse(String[] args) {
        // Validate that we have at least a command name
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No command specified. Use: create-car, add-fuel, or fuel-stats");
        }

        // First argument is the command name
        String commandName = args[0].toLowerCase();

        // Parse the remaining arguments as key-value pairs
        Map<String, String> parameters = parseParameters(args);

        return new ParsedCommand(commandName, parameters);
    }

    /**
     * Parses flag-value pairs from arguments.
     * 
     * Example: ["--brand", "Toyota", "--model", "Corolla"]
     * Result: {"brand": "Toyota", "model": "Corolla"}
     * 
     * @param args All command-line arguments
     * @return Map of parameter names to values
     */
    private Map<String, String> parseParameters(String[] args) {
        Map<String, String> parameters = new HashMap<>();

        // Start from index 1 (skip command name)
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            // Check if this is a flag (starts with --)
            if (arg.startsWith("--")) {
                // Remove the -- prefix to get the parameter name
                String paramName = arg.substring(2);

                // Check if there's a value following this flag
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    String paramValue = args[i + 1];
                    parameters.put(paramName, paramValue);
                    i++; // Skip the next argument since we've consumed it as a value
                } else {
                    throw new IllegalArgumentException("Flag " + arg + " requires a value");
                }
            }
        }

        return parameters;
    }

    /**
     * Inner class representing a parsed command.
     * 
     * This is a simple data holder (similar to a DTO).
     */
    public static class ParsedCommand {
        private final String commandName;
        private final Map<String, String> parameters;

        public ParsedCommand(String commandName, Map<String, String> parameters) {
            this.commandName = commandName;
            this.parameters = parameters;
        }

        public String getCommandName() {
            return commandName;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Gets a required parameter value.
         * Throws exception if parameter is missing.
         * 
         * @param name Parameter name
         * @return Parameter value
         * @throws IllegalArgumentException If parameter is missing
         */
        public String getRequiredParameter(String name) {
            String value = parameters.get(name);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Missing required parameter: --" + name);
            }
            return value;
        }

        /**
         * Gets an optional parameter value.
         * Returns null if parameter is not provided.
         * 
         * @param name Parameter name
         * @return Parameter value or null
         */
        public String getOptionalParameter(String name) {
            return parameters.get(name);
        }
    }
}
