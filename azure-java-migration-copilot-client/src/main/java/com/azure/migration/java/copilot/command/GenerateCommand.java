package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.generate.AzdConfigFilesGenerator;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.util.JsonUtil;
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
            terminal.println(Ansi.ansi().bold().a("\nPlease select which option do you want to generate?").reset().toString());
            selectedCommand = textIO.newStringInputReader().withNumberedPossibleValues(AVAILABLE_COMMANDS).read(">");
        }
        try {
            switch (MigrationCommand.determineCommand(selectedCommand)) {
                case "bicep", "bicep:":
                    if (migrationContext.getTemplateContext() == null) {
                        terminal.println(Ansi.ansi().fg(Ansi.Color.YELLOW).bold().a("Using default template context to generate bicep templates").reset().toString());
                        migrationContext.setTemplateContext(MigrationContext.DEFAULT_TEMPLATE_CONTEXT);
                    }
                    TemplateContext templateContext = migrationContext.getTemplateContext();

                    String defaultAcaEnvName = StringUtils.hasText(templateContext.getAcaEnvName()) ? templateContext.getAcaEnvName(): "demo";
                    terminal.println(Ansi.ansi().bold().a("\nPlease tell me the Azure Container Apps environment name you want to use?").reset().toString());
                    String acaEnvName = textIO.newStringInputReader().withDefaultValue(defaultAcaEnvName).read("/generate/bicep>");
                    templateContext.setAcaEnvName(acaEnvName);

                    azdConfigFilesGenerator.generateBicepFiles(migrationContext);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized command: " + selectedCommand);
            }
            terminal.println("Scripts generated, use `git status` to check generated files, then check in");
        } catch (Exception e) {
            terminal.println(Ansi.ansi().fg(Ansi.Color.RED).a("Scripts generation failed, error: " + e.getMessage()).reset().toString());
        }

    }
}
