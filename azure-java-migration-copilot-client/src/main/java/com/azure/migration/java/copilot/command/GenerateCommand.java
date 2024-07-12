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

                    String envName = askForEnvName(migrationContext);

                    TemplateContext templateContext = migrationContext.getTemplateContext();
                    if (!StringUtils.hasText(templateContext.getAppName())) {
                        templateContext.setAppName(askForAppName(migrationContext));
                    }

                    if (templateContext.getDbServiceConnectTemplateContext().isRequired()) {
                        templateContext.getDbServiceConnectTemplateContext().setSubscriptionId(askForSub(migrationContext));
                        templateContext.getDbServiceConnectTemplateContext().setResourceGroup(askForRg(migrationContext));
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

    private String askForEnvName(MigrationContext migrationContext) {
        terminal.println(ask("\nCopilot: Please tell me the environment name you want to use?"));
        return textIO.newStringInputReader().withDefaultValue("demoEnv").read("/generate/bicep>");
    }

    private String askForAppName(MigrationContext migrationContext) {
        terminal.println(ask("Copilot: Please tell me the application name you want to use?"));
        String defaultAppName = StringUtils.hasText(migrationContext.getAppName())? migrationContext.getAppName() : "demoApp";
        return textIO.newStringInputReader().withDefaultValue(defaultAppName).read("/generate/bicep>");
    }

    private String askForSub(MigrationContext migrationContext) {
        if (!StringUtils.hasText(migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getSubscriptionId())) {
            terminal.println(ask("Copilot: Please tell me the subscription Id you want to use for the database service connect?"));
            String defaultSub = StringUtils.hasText(migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getSubscriptionId()) ?
                    migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getSubscriptionId() : "00000000-0000-0000-0000-000000000000";
            return textIO.newStringInputReader().withDefaultValue(defaultSub).read("/generate/bicep>");
        }

        return migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getSubscriptionId();
    }

    private String askForRg(MigrationContext migrationContext) {
        if (!StringUtils.hasText(migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getResourceGroup())) {
            terminal.println(ask("Copilot: Please tell me the resource group name you want to use for the database service connect?"));
            String defaultRg = StringUtils.hasText(migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getResourceGroup()) ?
                    migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getResourceGroup() : "resourceGroup";
            return textIO.newStringInputReader().withDefaultValue(defaultRg).read("/generate/bicep>");
        }

        return migrationContext.getTemplateContext().getDbServiceConnectTemplateContext().getResourceGroup();
    }
}
