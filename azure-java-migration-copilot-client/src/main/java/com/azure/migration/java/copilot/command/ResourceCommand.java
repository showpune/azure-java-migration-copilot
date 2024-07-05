package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.resource.ResourceFacade;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceCommand implements MigrationCommand {

    @Autowired
    private ResourceFacade resourceFacade;

    @Autowired
    private TextIO textIO;

    @Autowired
    private TextTerminal<?> terminal;

    @Override
    public void execute(String commandText) {

        final String resources;
        try {
            resources = resourceFacade.listResource();
            resourceFacade.initApplicationConfiguration();
        } catch (Exception e) {
            terminal.println(Ansi.ansi().bg(Ansi.Color.RED).a("failed to list resources, error: " + e.getMessage()).reset().toString());
            return;
        }

        terminal.println(resources);

        String hint;
        switch (MigrationCommand.determineCommand(commandText)) {
            case "guide":
                hint = resourceFacade.resourceGuideSelect(resources);

                MigrationCommand.loop(
                        ConsoleContext.builder().defaultValue("").prompt("/resource/guide>").terminal(terminal).textIO(textIO).hint(hint).build(),
                        ConsoleContext::exited,
                        input -> terminal.println(resourceFacade.resourceGuide(resources))
                );
                break;
            case "config":
                hint = resourceFacade.resourceConfig(resources);
                MigrationCommand.loop(
                        ConsoleContext.builder().defaultValue("").prompt("/resource/config>").terminal(terminal).textIO(textIO).hint(hint).build(),
                        ConsoleContext::exited,
                        input -> terminal.println(resourceFacade.resourceConfig(input))
                );
                break;
        }
    }


}
