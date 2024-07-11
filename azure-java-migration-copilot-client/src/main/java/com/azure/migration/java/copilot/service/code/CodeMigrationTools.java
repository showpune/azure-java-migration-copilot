package com.azure.migration.java.copilot.service.code;


import com.azure.migration.java.copilot.service.LocalCommandTools;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


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
    private String appCatHome;
    @Autowired
    private CodeMigrationAnalysisAgent codeMigrationAnalysisAgent;

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
                if (upgradeCodeForMavenProject("org.openrewrite.java.migrate.MigrateJmsToSpringMessaging")) {
                    return "Success";
                }
                break;
            default:
                return "Not Supported";
        }
        return "Failed";
    }

    public boolean upgradeCodeForMavenProject(String recipe) throws IOException {
        Path cmdPath = Path.of(appCatHome, "bin", "mvn");
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
