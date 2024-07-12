package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ApplicationContextUtils;
import com.azure.migration.java.copilot.service.ConsoleContext;
import org.beryx.textio.StringInputReader;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MigrationCommand {

    static Optional<MigrationCommand> of(String commandText) {
        MigrationCommand command = switch (determineCommand(commandText)) {
            case "help" -> new HelpCommand();
            case "report:", "report" -> new ReportCommand();
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
                "report: show the AppCat report",
                "code: code upgrade and migration",
                "resource: resource detection and configuration",
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

    static String restOfCommand(String text) {
        String[] splits = text.split(" ");
        if (splits.length == 0) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(splits, 1, splits.length));
    }

    static void loop(ConsoleContext context,
                     Function<String, Boolean> quitFunc,
                     Consumer<String> inputConsumer) {
        loop(context, quitFunc, inputConsumer, () -> false);
    }

    static void loop(ConsoleContext context,
                     Function<String, Boolean> quitFunc,
                     Consumer<String> inputConsumer,
                     Supplier<Boolean> postChecker) {
        context.getTerminal().println(context.getHint());
        while(true) {
            try {
                StringInputReader reader = context.getTextIO().newStringInputReader();
                reader = reader.withMinLength(0);
                if (StringUtils.hasText(context.getDefaultValue())) {
                    reader = reader.withDefaultValue(context.getDefaultValue());
                }

                String userInput = reader.read(context.getPrompt());

                if (quitFunc.apply(userInput)) {
                    break;
                }

                inputConsumer.accept(userInput);

                if (postChecker.get()) {
                    break;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    void execute(String commandText);
}
