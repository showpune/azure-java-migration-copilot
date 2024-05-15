package com.azure.migration.java.copilot.rag;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.azure.search.AbstractAzureAiSearchEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_32K;


@Configuration
public class Configure {

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }


    @Bean
    EmbeddingStoreIngestor ingestor(AbstractAzureAiSearchEmbeddingStore azureAiSearchEmbeddingStore, EmbeddingModel embeddingModel) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new OpenAiTokenizer(GPT_4_32K));
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingStore(azureAiSearchEmbeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }
}
