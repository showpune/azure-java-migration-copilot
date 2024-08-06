package com.azure.migration.java.copilot.service.code;

import com.azure.migration.java.copilot.service.Configure;
import com.azure.migration.java.copilot.service.LocalCommandTools;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import dev.langchain4j.azure.openai.spring.AutoConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.file.Path;
import java.util.Set;

import static java.lang.System.getProperty;

class CodeMigrationToolsTest {

    private static final String AZURE_OPENAI_KEY = System.getenv("AZURE_OPENAI_KEY");
    private static final String AZURE_OPENAI_ENDPOINT = System.getenv("AZURE_OPENAI_ENDPOINT");
    private static final String AZURE_DEPLOYMENT_NAME = System.getenv("AZURE_DEPLOYMENT_NAME");
    private static final String SOURCE_CODE_PATH = "C:\\IdeaProjects\\rabbitmq-servicebus";
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AutoConfig.class))
            .withConfiguration(AutoConfigurations.of(CodeMigrationConfigure.class))
            .withConfiguration(AutoConfigurations.of(Configure.class))
            .withBean(MigrationContext.class)
            .withBean(LocalCommandTools.class)
            .withBean(AppCatTools.class)
            .withBean(CodeMigrationTools.class);



    void testCodeRewrite(String sourceCodeUrl,String method,String recipe, Set<String> stringSet,String guideline) {
        contextRunner
                .withPropertyValues(
                        "langchain4j.azure-open-ai.chat-model.api-key=" + AZURE_OPENAI_KEY,
                        "langchain4j.azure-open-ai.chat-model.endpoint=" + AZURE_OPENAI_ENDPOINT,
                        "langchain4j.azure-open-ai.chat-model.deployment-name=" + AZURE_DEPLOYMENT_NAME,
                        "langchain4j.azure-open-ai.chat-model.temperature=0.4",
                        "copilot.appcat-home=C:/tools/azure-migrate-appcat-for-java-cli-6.3.0.7",
                        "copilot.maven-home=C:/tools/apache-maven-3.9.5"
                )
                .run(context -> {

                    CodeMigrationTools codeMigrationTools = context.getBean(CodeMigrationTools.class);
                    MigrationContext migrationContext = context.getBean(MigrationContext.class);
                    migrationContext.setSourceCodePath(sourceCodeUrl);
                    String tempDir = getProperty("java.io.tmpdir");
                    String basePathPrefix = "migration-pilot/" + migrationContext.generateMD5Hash(sourceCodeUrl);
                    migrationContext.setWindupReportPath(Path.of(tempDir, basePathPrefix, "appcat-report").toString());
                    if(method.contains("recipe")) {
                        codeMigrationTools.upgradeCodeForMavenProject(recipe);
                        if(method.contains("ai")) {
                            codeMigrationTools.rewriteWithOpenAI(stringSet,guideline,false);
                        }
                    }else{
                        codeMigrationTools.rewriteWithOpenAI(stringSet,guideline,true);
                    }
                });
    }

    @ParameterizedTest(name = "method {0}")
    @CsvSource({
            "ai",
    })
    void testMQtoServiceBusWithSimpleAI(String method) {
        testCodeRewrite(SOURCE_CODE_PATH,
                method,
                null,
                Set.of("azure-mq-config-amqp-101000"),
                "mq2servicebus-simpleai");
    }

    @ParameterizedTest(name = "method {0}")
    @CsvSource({
            "ai",
    })
    void testMQtoServiceBusWithAI(String method) {
        testCodeRewrite(SOURCE_CODE_PATH,
                method,
                null,
                Set.of("azure-mq-config-amqp-101000"),
                "mq2servicebus-onlyai");
    }

    @ParameterizedTest(name = "method {0}")
    @CsvSource({
            "recipe",
    })
    void testMQtoServiceBusWithRecipe(String method) {
        testCodeRewrite(SOURCE_CODE_PATH,
                method,
                "com.microsoft.azure.migration.RabbitmqToServiceBus",
                null,
                "");
    }
    @ParameterizedTest(name = "method {0}")
    @CsvSource({
            "recipe|ai",
    })
    void testMQtoServiceBusWithRecipeAI(String method) {
        testCodeRewrite(SOURCE_CODE_PATH,
                method,
                "com.microsoft.azure.migration.RabbitmqToServiceBus",
                Set.of("azure-mq-config-amqp-101000"),
                "mq2servicebus-afterrecipe");
    }
}