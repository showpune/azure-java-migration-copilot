package com.azure.migration.java.copilot.service.command.code;

import com.azure.migration.java.copilot.service.MigrationWorkflowTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CodeMigrationConfigure {

    @Bean
    CodeMigrationAnalysisAgent codeMigrationAnalysisAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(CodeMigrationAnalysisAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    CodeMigrationChatAgent codeMigrationChatAgent(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever, MigrationWorkflowTools migrationWorkflowTools, CodeMigrationTools codeMigrationTools) {
        return AiServices.builder(CodeMigrationChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(codeMigrationTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
