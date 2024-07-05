package com.azure.migration.java.copilot.service;

import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Configure {

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    TextTerminal<?> textTerminal() {
        return TextIoFactory.getTextTerminal();
    }

    @Bean
    TextIO textIO() {
        return TextIoFactory.getTextIO();
    }
}
