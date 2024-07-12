package com.azure.migration.java.copilot.service.code;


import com.azure.migration.java.copilot.service.LocalCommandTools;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import dev.langchain4j.agent.tool.Tool;
import org.apache.logging.log4j.util.Strings;
import org.beryx.textio.TextTerminal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class CodeMigrationTools {

    public final static List<String> ALL_CODE_MIGRATION_SOLUTIONS =
            List.of("Upgrade JDK To Latest",
                    "Upgrade Spring Boot To Latest",
                    "Apply passwordless solution",
                    "Migrate MQ to Service Bus");
    @Autowired
    LocalCommandTools localCommandTools;
    @Autowired
    private AppCatTools appCatTools;

    @Autowired
    private MigrationContext migrationContext;

    @Value("${copilot.maven-home}")
    private String mvnHome;

    @Autowired
    private CodeMigrationAnalysisAgent codeMigrationAnalysisAgent;

    @Autowired
    TextTerminal<?> terminal;
    @Autowired
    private CodeOpenAIRewriteAgent codeOpenAIRewriteAgent;

    @Tool("List all the possible code migration solutions")
    private String listMigrationSolutions() throws IOException {
        return codeMigrationAnalysisAgent.listMigrationSolutions(ALL_CODE_MIGRATION_SOLUTIONS, appCatTools.getAllDetails());
    }

    @Tool("Apply a customer selected code migration solution ")
    private String applyMigrationSolution(String solution) throws IOException {
        int solutionIndex = ALL_CODE_MIGRATION_SOLUTIONS.indexOf(solution);
        switch (solutionIndex) {
            case 0:
                if (upgradeCodeForMavenProject("org.openrewrite.java.migrate.UpgradeToJava21")) {
                    return "Success";
                }
                break;
            case 1:
                if (upgradeCodeForMavenProject("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3")) {
                    return "Success";
                }
                break;
            case 3:
                if (rewriteWithOpenAI(Set.of("azure-mq-config-amqp-101000"), "You need to rewrite the code to use Azure Service Bus instead of RabbitMQ.")) {
                    return "Success";
                }
                break;
            default:
                return "Not Supported";
        }
        return "Failed";
    }

    private boolean rewriteWithOpenAI(Set<String> ruleIds, String purpose) throws IOException {
        Set<String> files = appCatTools.getIssuesFileList(ruleIds);
        List<String> filesToRewrite = getStrings(files);
        for (String fileString : filesToRewrite) {
            File file = new File(fileString);
            if (file.exists()) {
                String content = Files.readString(file.toPath());
                terminal.println("Try to rewrite code file: " + fileString + " ...");
                if (!Strings.isEmpty(content)){
                    String result = codeOpenAIRewriteAgent.rewriteCode(content, purpose);
                    if (!result.equalsIgnoreCase("false")) {
                        Files.writeString(file.toPath(), result);
                        terminal.println("Rewrote code file: " + fileString  + " ...");
                    }
                }
            }
        }
        return true;
    }

    private @NotNull List<String> getStrings(Set<String> files) {
        String base = migrationContext.getSourceCodePath();

        List<String> filesToRewrite = new ArrayList<>(files.stream().map(f -> Path.of(base, "..", f).toString()).toList());
        filesToRewrite.add((Path.of(base, "src/main/resources", "application.properties")).toString());
        filesToRewrite.add(Path.of(base, "pom.xml").toString());
        return filesToRewrite;
    }

    public boolean upgradeCodeForMavenProject(String recipe) throws IOException {
        Path cmdPath = Path.of(mvnHome, "bin", "mvn");
        String[] commands = {
                cmdPath.toString(),
                "-f",
                migrationContext.getSourceCodePath(),
                "org.openrewrite.maven:rewrite-maven-plugin:run",
                "-Dmaven.test.skip=true",
                "-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:RELEASE",
                "-Drewrite.activeRecipes=" + recipe,
                "--no-transfer-progress",
                "--batch-mode"
        };

        return localCommandTools.executeCommand(List.of(commands));
    }

}
