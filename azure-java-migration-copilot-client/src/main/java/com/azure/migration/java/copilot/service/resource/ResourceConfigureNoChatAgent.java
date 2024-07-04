package com.azure.migration.java.copilot.service.resource;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ResourceConfigureNoChatAgent {

    @SystemMessage(fromResource = "/prompts/resource/resource-abstract.txt")
    String abstractInfo(@V("schema") String schema,@V("orignalData") String orignalData,@UserMessage String userMessage);
}