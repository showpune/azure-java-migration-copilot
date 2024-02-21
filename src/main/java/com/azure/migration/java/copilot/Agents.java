package com.azure.migration.java.copilot;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Agents {

    @Bean
    ChooseTargetServiceAgent chooseTargetServiceAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ChooseTargetServiceAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    ConfigureResourceAgent configureResourceAgent(ChatLanguageModel chatLanguageModel,ContentRetriever contentRetriever) {
        return AiServices.builder(ConfigureResourceAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(){
        return new ContentRetriever() {
            @Override
            public List<Content> retrieve(Query query) {
                //TODO query the ms doc
                return new ArrayList<>();
            }
        };
    }

    @Bean
    WorkflowChatAgent configureChatAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever, MigrationWorkflowTools migrationWorkflowTools) {
        return AiServices.builder(WorkflowChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(migrationWorkflowTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
