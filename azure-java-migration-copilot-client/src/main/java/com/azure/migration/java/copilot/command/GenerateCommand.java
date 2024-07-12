package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.generate.AzdConfigFilesGenerator;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.azure.migration.java.copilot.service.ConsoleContext.*;

@Component
public class GenerateCommand implements MigrationCommand {

    @Autowired
    private TextTerminal<?> terminal;

    @Autowired
    private TextIO textIO;

    @Autowired
    private AzdConfigFilesGenerator azdConfigFilesGenerator;

    @Autowired
    private MigrationContext migrationContext;

    private static final String[] AVAILABLE_COMMANDS = new String[] {"bicep: generate bicep templates under source code location"};

    @Override
    public void execute(String commandText) {
        String selectedCommand = commandText;
        if (!StringUtils.hasText(selectedCommand)) {
            terminal.println(ConsoleContext.ask("Copilot: Please select which option do you want to generate?"));
            selectedCommand = textIO.newStringInputReader().withNumberedPossibleValues(AVAILABLE_COMMANDS).read(">");
        }
        try {
            switch (MigrationCommand.determineCommand(selectedCommand)) {
                case "bicep", "bicep:":
                    if (migrationContext.getTemplateContext() == null) {
                        terminal.println(warn("Copilot: Using default template context to generate bicep templates"));
                        migrationContext.setTemplateContext(MigrationContext.DEFAULT_TEMPLATE_CONTEXT);
                    }

                    terminal.println(ask("\nCopilot: Please tell me the environment name you want to use?"));
                    String envName = textIO.newStringInputReader().withDefaultValue(migrationContext.getTemplateContext().getAppEnv()).read("/generate/bicep>");

                    TemplateContext templateContext = migrationContext.getTemplateContext();
                    if (!StringUtils.hasText(templateContext.getAppName())) {
                        terminal.println(ask("Copilot: Please tell me the application name you want to use?"));
                        String defaultAppName = StringUtils.hasText(migrationContext.getAppName())? migrationContext.getAppName() : "demoApp";
                        String appName = textIO.newStringInputReader().withDefaultValue(defaultAppName).read("/generate/bicep>");
                        templateContext.setAppName(appName);
                    }

                    azdConfigFilesGenerator.generateBicepFiles(envName, migrationContext);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized command: " + selectedCommand);
            }
            terminal.println(answer("Scripts generated under you source code path, use `git status` to check generated files, then check in"));
        } catch (Exception e) {
            terminal.println(error("Scripts generation failed, error: " + e.getMessage()));
        }

    }
}
