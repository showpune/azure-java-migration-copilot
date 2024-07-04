package com.azure.migration.java.copilot.service.analysis;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ServiceAnalysisAgent {

    @SystemMessage(fromResource = "/prompts/service/choose-service.txt")
    String chooseService(@UserMessage String userMessage);
}