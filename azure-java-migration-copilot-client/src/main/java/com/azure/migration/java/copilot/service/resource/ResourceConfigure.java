package com.azure.migration.java.copilot.service.resource;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfigure {

    @Bean
    ResourceConfigureAgent resourceConfigureAgent(ChatLanguageModel chatLanguageModel, ChatMemoryProvider chatMemoryProvider, ResourceConfigTools tools) {
        return AiServices.builder(ResourceConfigureAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(tools)
                .chatMemoryProvider(chatMemoryProvider)
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                //to improve the performance, the guide process skipped first
//                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryStore store = new InMemoryChatMemoryStore();
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(5)
                .chatMemoryStore(store)
                .build();
    }
}
