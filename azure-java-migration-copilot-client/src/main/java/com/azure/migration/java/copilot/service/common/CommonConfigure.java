package com.azure.migration.java.copilot.service.common;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfigure {

    @Bean
    ToolsAgent resourceToolsAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ToolsAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
