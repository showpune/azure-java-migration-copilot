package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.MigrationWorkflowTools;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CodeCommand implements MigrationCommand {

    @Autowired
    private MigrationWorkflowTools tools;

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {
        try {
            tools.upgradeCodeForMavenProject();
        } catch (IOException e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Failed to upgrade code, error: " + e.getMessage()).reset().toString());
        }
    }
}
