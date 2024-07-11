package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.generate.AzdConfigFilesGenerator;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import dev.langchain4j.internal.Json;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
            terminal.println(Ansi.ansi().bold().a("\nCopilot: Please select which option do you want to generate?").reset().toString());
            selectedCommand = textIO.newStringInputReader().withNumberedPossibleValues(AVAILABLE_COMMANDS).read(">");
        }
        try {
            switch (MigrationCommand.determineCommand(selectedCommand)) {
                case "bicep", "bicep:":
                    if (migrationContext.getTemplateContext() == null) {
                        terminal.println(Ansi.ansi().fg(Ansi.Color.YELLOW).bold().a("Copilot: Using default template context to generate bicep templates").reset().toString());
                        migrationContext.setTemplateContext(MigrationContext.DEFAULT_TEMPLATE_CONTEXT);
                    }

                    terminal.println(Ansi.ansi().bold().a("\nCopilot: Please tell me the environment name you want to use?").reset().toString());
                    String envName = textIO.newStringInputReader().withDefaultValue("demoEnv").read("/generate/bicep>");

                    TemplateContext templateContext = migrationContext.getTemplateContext();
                    if (!StringUtils.hasText(templateContext.getAppName())) {
                        terminal.println(Ansi.ansi().bold().a("\nCopilot: Please tell me the application name you want to use?").reset().toString());
                        String defaultAppName = StringUtils.hasText(migrationContext.getAppName())? migrationContext.getAppName() : "demoApp";
                        String appName = textIO.newStringInputReader().withDefaultValue(defaultAppName).read("/generate/bicep>");
                        templateContext.setAppName(appName);
                    }

                    azdConfigFilesGenerator.generateBicepFiles(envName, migrationContext);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized command: " + selectedCommand);
            }
            terminal.println("Scripts generated under you source code path, use `git status` to check generated files, then check in");
        } catch (Exception e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Scripts generation failed, error: " + e.getMessage()).reset().toString());
        }

    }
}
