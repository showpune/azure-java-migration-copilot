package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.analysis.ServiceFacade;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportCommand implements MigrationCommand {

    @Autowired
    private ServiceFacade serviceFacade;

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {
        try {
            terminal.println(serviceFacade.showReport());
        } catch (Exception e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getMessage()).reset().toString());
        }
    }
}
