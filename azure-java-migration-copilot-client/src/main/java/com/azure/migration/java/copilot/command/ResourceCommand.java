package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.resource.ResourceFacade;
import com.azure.migration.java.copilot.service.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResourceCommand implements MigrationCommand {

    @Autowired
    private ResourceFacade resourceFacade;

    @Autowired
    private MigrationContext migrationContext;

    @Autowired
    private TextIO textIO;

    @Autowired
    private TextTerminal<?> terminal;

    private static final String[] AVAILABLE_COMMANDS = new String[]{"config: config the resources", "usage: detect resource usage"};

    @Override
    public void execute(String commandText) {
        try {
            final String[] json = new String[1];
            long start = System.currentTimeMillis();
            json[0] = resourceFacade.resourceConfigAbstract();
            System.out.println("cost1: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
            String hint = resourceFacade.resourceConfigTable(json[0]);
            System.out.println("cost2: " + (System.currentTimeMillis() - start));
            try {
                migrationContext.setTemplateContext(JsonUtil.fromJson(json[0], TemplateContext.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            MigrationCommand.loop(
                    ConsoleContext.builder().defaultValue("done").prompt("/resource/config>").terminal(terminal).textIO(textIO).hint(hint).build(),
                    ConsoleContext::exited,
                    input ->
                    {
                        json[0] = resourceFacade.resourceConfigChat(input, json[0]);
                        terminal.println(resourceFacade.resourceConfigTable(json[0]));
                        try {
                            migrationContext.setTemplateContext(JsonUtil.fromJson(json[0], TemplateContext.class));
                        } catch (JsonProcessingException e) {
                            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Unable to parse template context " + e.getMessage()).reset().toString());
                        }
                    }
            );
        } catch (Exception e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Error: " + e.getMessage()).reset().toString());
        }
    }


}
