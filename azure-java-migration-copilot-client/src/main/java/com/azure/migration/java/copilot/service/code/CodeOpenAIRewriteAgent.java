package com.azure.migration.java.copilot.service.code;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface CodeOpenAIRewriteAgent {

    @SystemMessage(fromResource = "prompts/code/code-rewrite.txt")
    public String rewriteCode(@UserMessage String input, @V("fileName") String code, @V("guideline") String guideline);
}
