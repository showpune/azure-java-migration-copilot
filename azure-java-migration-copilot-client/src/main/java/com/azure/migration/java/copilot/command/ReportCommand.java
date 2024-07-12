package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.analysis.ServiceFacade;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.azure.migration.java.copilot.service.ConsoleContext.answer;
import static com.azure.migration.java.copilot.service.ConsoleContext.error;

@Component
public class ReportCommand implements MigrationCommand {

    @Autowired
    private ServiceFacade serviceFacade;

    @Autowired
    private TextTerminal<?> terminal;

    @Autowired
    private MigrationContext migrationContext;

    @Override
    public void execute(String commandText) {
        try {
            terminal.println(answer(serviceFacade.showReport()));
        } catch (Exception e) {
            terminal.println(error(e.getMessage()));
        }
    }
}
