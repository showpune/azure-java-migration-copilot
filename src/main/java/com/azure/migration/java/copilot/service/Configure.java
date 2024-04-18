package com.azure.migration.java.copilot.service;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


@Configuration
public class Configure {

    @Bean
    ListResourceAgent listResourceAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ListResourceAgent.class)
            .chatLanguageModel(chatLanguageModel)
            .build();
    }

    @Bean
    RecommendServiceAgent recommendServiceAgent(ChatLanguageModel chatLanguageModel, @Qualifier("recommendServiceContentRetriever") ContentRetriever contentRetriever) {
        return AiServices.builder(RecommendServiceAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    ConfigureResourceAgent configureResourceAgent(ChatLanguageModel chatLanguageModel, @Qualifier("configureResourceContentRetriever") ContentRetriever contentRetriever) {
        return AiServices.builder(ConfigureResourceAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    WorkflowChatAgent configureWorkflowChatAgent(ChatLanguageModel chatLanguageModel, MigrationWorkflowTools migrationWorkflowTools) {
        return AiServices.builder(WorkflowChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(migrationWorkflowTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    @Qualifier("recommendServiceContentRetriever")
    ContentRetriever recommendServiceContentRetriever(@Qualifier("recommendServiceEmbeddingStore") EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return createContentRetriever(embeddingStore, embeddingModel);
    }

    @Bean
    @Qualifier("configureResourceContentRetriever")
    ContentRetriever configureResourceContentRetriever(@Qualifier("configureResourceEmbeddingStore") EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return createContentRetriever(embeddingStore, embeddingModel);
    }


    private ContentRetriever createContentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

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
    @Qualifier("recommendServiceEmbeddingStore")
    EmbeddingStore<TextSegment> recommendServiceEmbeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {

        // Normally, you would already have your embedding store filled with your data.
        // However, for the purpose of this demonstration, we will:

        // 1. Create an in-memory embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 2. Load an example document ("Miles of Smiles" terms of use)
        Resource resource = resourceLoader.getResource("azure-service-recommendation-document.txt");
        Document document = loadDocument(resource.getFile().toPath(), new TextDocumentParser());

        // 3. Split the document into segments 100 tokens each
        // 4. Convert segments into embeddings
        // 5. Store embeddings into embedding store
        // All this can be done manually, but we will use EmbeddingStoreIngestor to automate this:
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new OpenAiTokenizer("gpt-3.5-turbo"));
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .documentSplitter(documentSplitter)
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();
        ingestor.ingest(document);

        return embeddingStore;
    }

    @Bean
    @Qualifier("configureResourceEmbeddingStore")
    AzureAiSearchEmbeddingStore configureResourceEmbeddingStore() throws IOException {
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
