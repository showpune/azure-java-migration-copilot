package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.util.BicepGenerator;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GenerateCommand implements MigrationCommand {

    @Autowired
    private TextTerminal<?> terminal;

    @Autowired
    private TextIO textIO;

    private static final String[] AVAILABLE_OPTIONS = new String[] {"azd bicep"};

    @Override
    public void execute(String commandText) {
        terminal.println(Ansi.ansi().bold().a("\nPlease select which option do you want to generate?").reset().toString());
        String selectedOption = textIO.newStringInputReader().withNumberedPossibleValues(AVAILABLE_OPTIONS).read(">");
        MigrationContext migrationContext = new MigrationContext();
        Map<String, String> inputInfo  = new HashMap<>();
        migrationContext.setInputInfo(inputInfo);
        inputInfo.put("DB_NAME", "mysql-szcza4m2d5pkk");
        inputInfo.put("DB_USER_NAME", "migrationtool");
        inputInfo.put("DB_PASSWORD", "Password@123");
        switch (selectedOption) {
            case "azd bicep":
                // TODO: generate bicep scripts here
                BicepGenerator.genereateBicepPramFile(null, migrationContext);
                break;
            default:
        }

        terminal.println("Scripts generated under ??, use `git status` to check generated files, then check in");
    }
}
