package com.azure.migration.java.copilot.service.resource;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfigure {

    @Bean
    ResourceConfigureAgent resourceConfigureAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever, ResourceConfigTools tools) {
        return AiServices.builder(ResourceConfigureAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(tools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();
    }
}
