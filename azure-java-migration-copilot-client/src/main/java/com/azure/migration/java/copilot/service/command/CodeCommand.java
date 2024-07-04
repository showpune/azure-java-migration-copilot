package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.command.code.CodeMigrationChatAgent;
import org.apache.logging.log4j.util.Strings;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeCommand implements MigrationCommand {

    @Autowired
    TextTerminal<?> terminal;

    @Autowired
    private CodeMigrationChatAgent codeMigrationChatAgent;

    @Override
    public void execute(String commandText) {
        if(Strings.isEmpty(commandText) || commandText.equals("help")) {
            commandText = "list migration solutions";
        }
        terminal.println(codeMigrationChatAgent.chat(commandText));
    }
}
