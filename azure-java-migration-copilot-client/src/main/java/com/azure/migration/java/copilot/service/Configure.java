package com.azure.migration.java.copilot.service;

import com.azure.search.documents.indexes.models.SearchIndex;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Configure {

    @Bean
    ServiceAnalysisAgent chooseServiceAnalysisAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ServiceAnalysisAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    ConfigureResourceAgent configureResourceAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever) {
        return AiServices.builder(ConfigureResourceAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    WorkflowChatAgent configureWorkflowChatAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever, MigrationWorkflowTools migrationWorkflowTools) {
        return AiServices.builder(WorkflowChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(migrationWorkflowTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

}
