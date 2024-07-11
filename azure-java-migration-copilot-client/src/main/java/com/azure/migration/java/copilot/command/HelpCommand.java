package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.azure.migration.java.copilot.service.ConsoleContext.answer;

@Component
public class HelpCommand implements MigrationCommand {

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {
        terminal.println(ConsoleContext.ask("Available commands:"));

        String output = String.join("\n", MigrationCommand.availableCommands());

        terminal.println(answer(output));
    }
}
