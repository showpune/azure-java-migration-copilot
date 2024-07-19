package com.azure.migration.java.copilot.service.code;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface CodeMigrationChatAgent {

    @SystemMessage(fromResource = "prompts/code/code-robot.txt")
    String chat(@UserMessage String input);
}
