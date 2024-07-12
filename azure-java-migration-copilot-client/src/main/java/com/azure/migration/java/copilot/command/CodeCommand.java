package com.azure.migration.java.copilot.command;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.code.CodeMigrationChatAgent;
import com.azure.migration.java.copilot.service.code.CodeMigrationTools;
import org.apache.logging.log4j.util.Strings;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.azure.migration.java.copilot.command.MigrationCommand.loop;
import static com.azure.migration.java.copilot.service.ConsoleContext.answer;

@Component
public class CodeCommand implements MigrationCommand {

    @Autowired
    TextTerminal<?> terminal;

    @Autowired
    TextIO textIO;

    @Autowired
    private CodeMigrationChatAgent codeMigrationChatAgent;

    @Override
    public void execute(String commandText) {
        if(Strings.isEmpty(commandText) || commandText.equals("help")) {
            commandText = "list migration solutions";
        }
        String hint = codeMigrationChatAgent.chat(commandText,CodeMigrationTools.ALL_CODE_MIGRATION_SOLUTIONS);
        loop(
                ConsoleContext.builder().hint(answer(hint)).prompt("/code>").defaultValue("done").terminal(terminal).textIO(textIO).build(),
                ConsoleContext::exited,
                input -> {
                    terminal.println(answer(codeMigrationChatAgent.chat(input, CodeMigrationTools.ALL_CODE_MIGRATION_SOLUTIONS)));
                }
        );
    }
}
