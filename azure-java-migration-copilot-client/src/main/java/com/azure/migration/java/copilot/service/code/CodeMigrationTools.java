package com.azure.migration.java.copilot.service.code;


import com.azure.migration.java.copilot.service.LocalCommandTools;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import dev.langchain4j.agent.tool.Tool;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.beryx.textio.TextTerminal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


@Component
public class CodeMigrationTools {

    public final static List<String> ALL_CODE_MIGRATION_SOLUTIONS =
            List.of("Upgrade JDK To 17",
                    "Upgrade Spring To Spring3",
                    "Apply passwordless solution",
                    "Migrate MQ to Service Bus");

    public static final String SOLUTIONS_STRING= "Apply a customer selected code migration solution, the solution name must be one of:\n" +
    		        "Upgrade JDK To 17," +
                    "Upgrade SpringBoot To Spring3," +
                    "Apply passwordless solution," +
                    "Migrate MQ to Service Bus";

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

    @Tool("List the code migration solutions suitable for the project and reasons for the migration.")
    private String listMigrationSolutions() throws IOException {
        String analysisResult = codeMigrationAnalysisAgent.listMigrationSolutions(ALL_CODE_MIGRATION_SOLUTIONS, appCatTools.getAllDetails());
        return "The migration solutions suitable for the project and reasons are as: \n" + analysisResult;
    }

    @Tool(SOLUTIONS_STRING)
    private String applyMigrationSolution(String solution) throws Exception {
        int solutionIndex = ALL_CODE_MIGRATION_SOLUTIONS.indexOf(solution);
        switch (solutionIndex) {
            case 0:
                if (upgradeCodeForMavenProject("org.openrewrite.java.migrate.UpgradeToJava21")) {
                    return "Success";
                }
                break;
            case 1:
                if (upgradeCodeForMavenProject("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_1")) {
                    return "Success";
                }
                break;
            case 3:
                if (upgradeCodeForMavenProject("com.microsoft.azure.migration.RabbitmqToServiceBus")
                & rewriteWithOpenAI(
                        Set.of("azure-mq-config-amqp-101000")
                        , "mq2servicebus-afterrecipe",false))
                {
                    return "Success";
                }
                break;
            default:
                return "Not Supported";
        }
        return "Failed";
    }

    public boolean rewriteWithOpenAI(Set<String> ruleIds,String specificGuideline,boolean includePom) throws IOException {

        Set<String> files = appCatTools.getIssuesFileList(ruleIds);
        List<String> filesToRewrite = getStrings(files,includePom);
        InputStream reader = CodeMigrationTools.class.getResourceAsStream("/prompts/code/"+specificGuideline+".txt");
        String guideline = getText(reader);
        for (String fileString : filesToRewrite) {
            File file = new File(fileString);
            if (file.exists()) {
                String content = Files.readString(file.toPath());
                terminal.println("Try to rewrite code file: " + file.getCanonicalPath() + " ...");
                if (!Strings.isEmpty(content)){
                    String result = codeOpenAIRewriteAgent.rewriteCode(content, file.getName(),guideline);
                    if (!result.equalsIgnoreCase("false")) {
                        Files.writeString(file.toPath(), result);
                        terminal.println("Rewrote code file: " + fileString  + " ...");
                    }
                }
            }else{
                terminal.println("File not found: " + fileString + " ...");
            }
        }
        return true;
    }

    private static String getText(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try (Scanner scanner = new Scanner(inputStream);
             Scanner s = scanner.useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }


    private @NotNull List<String> getStrings(Set<String> files, boolean includePom) {
        String base = migrationContext.getSourceCodePath();
        List<String> filesToRewrite = new ArrayList<>();
        filesToRewrite.add((Path.of(base, "src/main/resources", "application.properties")).toString());
        if(includePom) {
            filesToRewrite.add((Path.of(base, "pom.xml")).toString());
        }
        filesToRewrite.addAll(files.stream().map(f -> Path.of(base, "..", f).toString()).toList());
        return filesToRewrite;
    }

    public boolean upgradeCodeForMavenProject(String recipe) throws Exception {
        // if recipe name is com.microsoft.azure.migration.RabbitmqToServiceBus, return RabbitmqToServiceBus
        if(StringUtils.isEmpty(recipe)){
            return true;
        }
        String fileName = recipe.substring(recipe.lastIndexOf('.') + 1)+".yml";
        //reource the path file as the resource stream /recipe/fileName
        File recipeFile = new File(CodeMigrationTools.class.getResource("/recipe/"+fileName).toURI());
        Path cmdPath = Path.of(mvnHome, "bin", "mvn");
        String[] commands = {
                cmdPath.toString(),
                "-f",
                new File((migrationContext.getSourceCodePath())).getCanonicalPath(),
                "org.openrewrite.maven:rewrite-maven-plugin:5.37.1:run",
                "-Dmaven.test.skip=true",
                "-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:RELEASE",
                "-Drewrite.activeRecipes=" + recipe,
                "-Drewrite.configLocation=" + recipeFile.getCanonicalPath(),
                "--no-transfer-progress",
                "--batch-mode",
                "-e"
        };

        return localCommandTools.executeCommand(List.of(commands));
    }

}
