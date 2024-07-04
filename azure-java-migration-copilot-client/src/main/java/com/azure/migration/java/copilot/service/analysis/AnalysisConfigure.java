package com.azure.migration.java.copilot.service.analysis;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfigure {

    @Bean
    ServiceAnalysisAgent chooseServiceAnalysisAgent(ChatLanguageModel chatLanguageModel, ServiceAnalysisTools tools) {
        return AiServices.builder(ServiceAnalysisAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
