package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.model.resource.Resources;
import com.azure.migration.java.copilot.service.resource.ResourceFacade;
import com.azure.migration.java.copilot.service.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.azure.migration.java.copilot.service.ConsoleContext.answer;
import static com.azure.migration.java.copilot.service.ConsoleContext.error;

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

    private static final String comment = "Please check the configuration table, and you could change the property value by enter `change property to value`, and remember to enter `done` when you finish.";

    @Override
    public void execute(String commandText) {
        try {
            final String[] json = new String[1];
            json[0] = resourceFacade.resourceConfigAbstract();
            Resources resources = JsonUtil.fromJson(json[0], Resources.class);;
            migrationContext.setTemplateContext(resources.toContext());
            String hint = resourceFacade.resourceConfigTable(resources);
            hint += "\n" + comment;
            MigrationCommand.loop(
                    ConsoleContext.builder().defaultValue("done").prompt("/resource/config>").terminal(terminal).textIO(textIO).hint(answer(hint)).build(),
                    ConsoleContext::exited,
                    input ->
                    {
                        try {
                            json[0] = resourceFacade.resourceConfigChat(input, json[0]);
                            Resources res = JsonUtil.fromJson(json[0], Resources.class);

                            migrationContext.setTemplateContext(res.toContext());
                            String table = resourceFacade.resourceConfigTable(res);
                            table += "\n" + comment;
                            terminal.println(answer(table));
                        } catch (JsonProcessingException e) {
                            terminal.println(error("Unable to parse template context " + e.getMessage()));
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
            terminal.println(error("Error: " + e.getMessage()));
        }
    }


}
