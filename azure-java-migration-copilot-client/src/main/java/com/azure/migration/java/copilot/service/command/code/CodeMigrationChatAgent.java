package com.azure.migration.java.copilot.service.command.code;

import dev.langchain4j.service.SystemMessage;

public interface CodeMigrationChatAgent {

    @SystemMessage(fromResource = "prompts/code/code-robot.txt")
    String chat(String input);
}
