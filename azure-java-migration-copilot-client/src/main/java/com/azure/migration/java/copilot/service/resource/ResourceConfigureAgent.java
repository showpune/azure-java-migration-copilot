package com.azure.migration.java.copilot.service.resource;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ResourceConfigureAgent {

    @SystemMessage(fromResource = "/prompts/resource/resource-list.txt")
    String listResources(String windupDescription);

    @SystemMessage({"Let user to choose which resource they want to show the guide"})
    String resourceGuideSelect(@UserMessage String resourceList);

    @SystemMessage(fromResource = "/prompts/resource/resource-guide.txt")
    String resourceGuide(@UserMessage("resource") String userMessage, @V("service") String service);

    @SystemMessage(fromResource = "/prompts/resource/resource-config.txt")
    String configResource(@UserMessage String userMessage);
}