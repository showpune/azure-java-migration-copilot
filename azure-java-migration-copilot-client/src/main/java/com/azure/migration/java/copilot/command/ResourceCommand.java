package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.resource.ResourceFacade;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class ResourceCommand implements MigrationCommand {

    @Autowired
    private ResourceFacade resourceFacade;

    @Autowired
    private TextIO textIO;

    @Autowired
    private TextTerminal<?> terminal;

    private static final String[] AVAILABLE_COMMANDS = new String[]{"config: config the resources", "usage: detect resource usage"};

    @Override
    public void execute(String commandText) {
        String selectedCommand = commandText;
        if (!StringUtils.hasText(selectedCommand)) {
            terminal.println(Ansi.ansi().bold().a("\nCopilot: What do you want to do with the resources").reset().toString());
            selectedCommand = textIO.newStringInputReader().withNumberedPossibleValues(AVAILABLE_COMMANDS).read("");
        }

        try {
            String hint;
            String resources;
            switch (MigrationCommand.determineCommand(selectedCommand)) {
                case "usage", "usage:":
                    resources = resourceFacade.listResource();
                    terminal.println(resources);
                    hint = resourceFacade.resourceGuideSelect(resources);

                    MigrationCommand.loop(
                            ConsoleContext.builder().defaultValue("done").prompt("/resource/usage>").terminal(terminal).textIO(textIO).hint(hint).build(),
                            ConsoleContext::exited,
                            input -> terminal.println(resourceFacade.resourceGuide(input))
                    );
                    break;
                case "config", "config:":
                    hint = resourceFacade.resourceConfig();
                    MigrationCommand.loop(
                            ConsoleContext.builder().defaultValue("done").prompt("/resource/config>").terminal(terminal).textIO(textIO).hint(hint).build(),
                            ConsoleContext::exited,
                            input ->
                            {
                                try {
                                    terminal.println(resourceFacade.resourceConfigChat(input));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized command " + selectedCommand);
            }
        } catch (Exception e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Error: " + e.getMessage()).reset().toString());
        }
    }


}
