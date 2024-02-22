package com.azure.migration.java.copilot.rag;

import com.azure.core.credential.AzureKeyCredential;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_4_32K;

@Configuration
public class Configure {
    @Bean
    AzureAiSearchEmbeddingStore azureAiSearchEmbeddingStore() throws IOException {
        return AzureAiSearchEmbeddingStore.builder()
                .endpoint(System.getenv("langchain4j.azure.ai-search.endpoint"))
                .apiKey(System.getenv("langchain4j.azure.ai-search.api-key"))
                .setupIndex(Boolean.parseBoolean(System.getenv("langchain4j.azure.ai-search.setup-index")))
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }


    @Bean
    EmbeddingStoreIngestor ingestor(AzureAiSearchEmbeddingStore azureAiSearchEmbeddingStore, EmbeddingModel embeddingModel) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new OpenAiTokenizer(GPT_4_32K));
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingStore(azureAiSearchEmbeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }
}
