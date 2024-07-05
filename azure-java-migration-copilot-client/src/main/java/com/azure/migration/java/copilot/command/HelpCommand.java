package com.azure.migration.java.copilot.command;

import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements MigrationCommand {

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {
        terminal.println(Ansi.ansi().bold().a("Available commands:").reset().toString());
        for (String cmd: MigrationCommand.availableCommands()) {
            terminal.println("  %s".formatted(cmd));
        }
    }
}
