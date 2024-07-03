package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.MigrationWorkflowTools;
import com.azure.migration.java.copilot.service.model.Resources;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResourceCommand implements MigrationCommand {

    @Autowired
    private MigrationWorkflowTools tools;

    @Autowired
    private TextIO textIO;

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {
        Resources resources = null;
        try {
            resources = tools.listResources();
        } catch (IOException e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Failed to detect resource usage, error: " + e.getMessage()).reset().toString());
            return;
        }

        String selectedItem = textIO.newStringInputReader().
                withNumberedPossibleValues(resources.formatToList(true)).
                read("Please select the resource you want to configure");

        System.out.println(selectedItem);
        //TODO
    }
}
