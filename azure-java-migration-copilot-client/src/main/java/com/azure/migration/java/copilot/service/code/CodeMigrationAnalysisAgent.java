package com.azure.migration.java.copilot.service.code;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface CodeMigrationAnalysisAgent {

    @SystemMessage(fromResource = "prompts/code/list-migration-solutions-prompt.txt")
    String listMigrationSolutions(@V("solutions") List<String> supportedSolutions, @UserMessage String windupDescription);
}
