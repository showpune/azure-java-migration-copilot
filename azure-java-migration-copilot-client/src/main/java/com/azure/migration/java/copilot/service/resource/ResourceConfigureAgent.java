package com.azure.migration.java.copilot.service.resource;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ResourceConfigureAgent {

    @SystemMessage(fromResource = "/prompts/resource/resource-list.txt")
    String listResources(String windupDescription);

    @SystemMessage({"Let user to choose which resource they want to show the guide"})
    String resourceGuideSelect(@UserMessage String userMessage);

    @SystemMessage(fromResource = "/prompts/resource/resource-guide.txt")
    String resourceGuide(@UserMessage String userMessage, @V("service") String service);

    /**
     * @param report
     * @param schem
     * @param memoryId each round will trigger a new memory loop
     * @return
     */
    @SystemMessage(fromResource = "/prompts/resource/resource-config.txt")
    String resourceConfig(@UserMessage String report, @V("schema") String schem, @MemoryId String memoryId);

    @SystemMessage(fromResource = "/prompts/resource/resource-config-chat.txt")
    String resourceConfigChat(@UserMessage String userInput, @V("schema") String schema,@MemoryId String memoryId);
}