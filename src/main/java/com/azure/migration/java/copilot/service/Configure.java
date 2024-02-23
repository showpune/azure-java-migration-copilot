package com.azure.migration.java.copilot.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class Configure {

    @Bean
    ServiceAnalysisAgent chooseServiceAnalysisAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ServiceAnalysisAgent.class)
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
    WorkflowChatAgent configureWorkflowChatAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever, MigrationWorkflowTools migrationWorkflowTools) {
        return AiServices.builder(WorkflowChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(migrationWorkflowTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

        // You will need to adjust these parameters to find the optimal setting, which will depend on two main factors:
        // - The nature of your data
        // - The embedding model you are using
        int maxResults = 5;
        double minScore = 0.6;

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    @Bean
    AzureAiSearchEmbeddingStore azureAiSearchEmbeddingStore() throws IOException {
        return AzureAiSearchEmbeddingStore.builder()
                .endpoint(System.getenv("langchain4j.azure.ai-search.endpoint"))
                .apiKey(System.getenv("langchain4j.azure.ai-search.api-key"))
                .dimensions(Integer.parseInt(System.getenv("langchain4j.azure.ai-search.dimensions")))
                .setupIndex(Boolean.parseBoolean(System.getenv("langchain4j.azure.ai-search.setup-index")))
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

}
