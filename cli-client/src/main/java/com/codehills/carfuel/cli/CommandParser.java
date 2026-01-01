package com.codehills.carfuel.cli;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    public ParsedCommand parse(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No command specified");
        }

        String commandName = args[0].toLowerCase();
        Map<String, String> parameters = parseParameters(args);

        return new ParsedCommand(commandName, parameters);
    }

    private Map<String, String> parseParameters(String[] args) {
        Map<String, String> parameters = new HashMap<>();

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String paramName = args[i].substring(2);

                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    parameters.put(paramName, args[i + 1]);
                    i++;
                } else {
                    throw new IllegalArgumentException("Flag " + args[i] + " requires a value");
                }
            }
        }

        return parameters;
    }

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

        public String getRequiredParameter(String name) {
            String value = parameters.get(name);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Missing required parameter: --" + name);
            }
            return value;
        }
    }
}
