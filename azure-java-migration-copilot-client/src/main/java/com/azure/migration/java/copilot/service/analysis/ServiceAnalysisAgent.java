package com.azure.migration.java.copilot.service.analysis;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ServiceAnalysisAgent {

    @SystemMessage(fromResource = "/prompts/service/choose-service.txt")
    String chooseService(@UserMessage String userMessage);

    @SystemMessage(fromResource = "/prompts/service/show-report.txt")
    String showReport(@UserMessage String report, @V("reportUrl") String reportUrl);
}