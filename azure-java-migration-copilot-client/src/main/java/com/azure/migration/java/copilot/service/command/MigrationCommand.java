package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.ApplicationContextUtils;
import com.azure.migration.java.copilot.service.model.Resources;

import java.util.function.Consumer;

public interface MigrationCommand {

    static MigrationCommand of(String commandText) {
        MigrationCommand command = switch (determineCommand(commandText)) {
            case "resource:", "resource" -> new ResourceCommand();
            case "code:", "code" -> new CodeCommand();
            case "generate:", "generate" -> new GenerateCommand();
            default -> throw new IllegalArgumentException("Unrecognized command " + commandText);
        };

        return ApplicationContextUtils.autowire(command);
    }

    static String[] availableCommands() {
        return new String[]{
                "resource: Detect resource usage (including database, file system, environment variables, etc.)",
                "code: Code Migration",
                "generate: Generate script"
        };
    }

    static String determineCommand(String text) {
        String[] splits = text.split(" ");
        if (splits.length == 0) {
            return "";
        }
        return splits[0];
    }

    void execute(Consumer<String> out);
}
