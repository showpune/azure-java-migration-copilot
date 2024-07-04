package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.ApplicationContextUtils;

import java.util.Arrays;
import java.util.Optional;

public interface MigrationCommand {

    static Optional<MigrationCommand> of(String commandText) {
        MigrationCommand command = switch (determineCommand(commandText)) {
            case "help" -> new HelpCommand();
            case "resource:", "resource" -> new ResourceCommand();
            case "code:", "code" -> new CodeCommand();
            case "generate:", "generate" -> new GenerateCommand();
            default -> null;
        };

        if (command == null) {
            return Optional.empty();
        }
        return Optional.of(ApplicationContextUtils.autowire(command));
    }

    static String[] availableCommands() {
        return new String[]{
                "resource: detect resource usage (including database, file system, environment variables, etc.)",
                "code: code upgrade by OpenRewrite recipes",
                "generate: generate script to build and deploy"
        };
    }

    static String determineCommand(String text) {
        String[] splits = text.split(" ");
        if (splits.length == 0) {
            return "";
        }
        return splits[0];
    }

    static String determineCommandInput(String text) {
        String[] splits = text.split(" ");
        if (splits.length == 0) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(splits, 1, splits.length));
    }

    static String restOfCommand(String text) {
        String[] splits = text.split(" ");
        if (splits.length == 0) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(splits, 1, splits.length));
    }

    void execute(String commandText);
}
