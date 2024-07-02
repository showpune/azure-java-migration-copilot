package com.azure.migration.java.copilot.service;

import com.azure.migration.java.copilot.service.model.RecommendedServices;
import com.azure.migration.java.copilot.service.model.Resources;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.internal.Json;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class MigrationWorkflowTools {

    private String reportUrl;

    private String service;

    private String sourceLocation;

    @Value("${copilot.appcat-home}")
    private String appCatHome;

    @Value("${copilot.maven-home}")
    private String mavenHome;

    private final String[] DEFAULT_TARGET_SERVICE = new String[]{
            "azure-spring-apps",
            "azure-aks",
            "openjdk17",
            "cloud-readiness",
            "linux"
    };

    private final String[] DEFAULT_AZURE_RECIPES = new String[] {
            "com.azure.spring.migration.UpgradeToAzureSpringApps",
            "com.azure.spring.migration.UpgradeSpringboot_2_7_SpringCloud_2021",
    };

    private final String DEFAULT_AZURE_COORDINATES = "com.azure.spring.migration:azure-spring-rewrite:LATEST";

    @Autowired
    private ServiceAnalysisAgent serviceAnalysisAgent;

    @Autowired
    private ConfigureResourceAgent configureResourceAgent;

    private Consumer<String> out = System.out::println;

    @Tool("Recommend the target service the application can be migrated to")
    public RecommendedServices recommendTargetService() throws IOException {
        if (reportUrl == null) {
            throw new IllegalArgumentException("reportUrl cannot be null");
        }
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        String response = serviceAnalysisAgent.chooseService(content);
        return Json.fromJson(response, RecommendedServices.class);
    }

    @Tool("List all the resources used in the application according to the report")
    public Resources listResources() throws IOException {
        if (reportUrl == null) {
            throw new IllegalArgumentException("reportUrl cannot be null");
        }
        String content = "Technologies: \n" + getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        String response = serviceAnalysisAgent.listResources(content);
        System.out.println(response);
        return Json.fromJson(response, Resources.class);
    }

    @Tool("Answer other question about the about report")
    public String otherQuestion(String question) throws IOException {
        if (reportUrl == null) {
            return "please give the report url first";
        }
        String content = "Technologies: \n" + getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        System.out.println("======================Migration Copilot of Report Analysis======================:\n" + serviceAnalysisAgent.chat(content));
        return "Success";
    }

    @Tool("Configure the given resource in the service")
    public String configureResources(String resource, String service) throws IOException {
        if (service != null) {
            this.service = service;
        }
        if (this.service == null) {
            return "please give the set the service first";
        }
        System.out.println("======================Migration Copilot of Configure Resource======================:\n" + configureResourceAgent.configureResource(resource, this.service));
        return "Success";
    }

    @Tool({"Set the source location for analysis"})
    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    @Tool({"Scan the code with AppCat"})
    public void scanCodeWithAppCat() throws IOException {
        Runtime rt = Runtime.getRuntime();
        this.reportUrl = Files.createTempDirectory("report-assessment").toFile().getAbsolutePath();
        out.accept("Start to generate AppCat report, please wait for a few minutes and DO NOT close this window");
        Path cmdPath = Path.of(appCatHome, "/bin", "/appcat");
        List<String> commands = new ArrayList<>(Arrays.asList(
                cmdPath.toString(),
                "--input", sourceLocation,
                "--output", this.reportUrl,
                "--batchMode",
                "--overwrite"
        ));
        for (String target: DEFAULT_TARGET_SERVICE) {
            commands.add("--target");
            commands.add(target);
        }

        Process proc = rt.exec(commands.toArray(new String[]{}));

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            out.accept(s);
        }

        while ((s = stdError.readLine()) != null) {
            out.accept(s);
        }
        out.accept("Generated AppCat report under: " + reportUrl);
    }

    public void upgradeCodeForMavenProject() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String pomFile = Path.of(this.sourceLocation, "pom.xml").toFile().getAbsolutePath();
        out.accept("Begin to upgrade source code with OpenRewrite recipes, please wait for a few minutes and DO NOT close this window");
        Path cmdPath = Path.of(mavenHome, "/bin", "/mvn");

        String[] commands = {
                cmdPath.toString(),
                "-f", pomFile,
                "-U",
                "-e",
                "-Pgithub",
                "org.openrewrite.maven:rewrite-maven-plugin:run",
                "-Drewrite.activeRecipes=" + String.join(",", DEFAULT_AZURE_RECIPES),
                "-Drewrite.recipeArtifactCoordinates=" + DEFAULT_AZURE_COORDINATES
        };
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            out.accept(s);
        }

        while ((s = stdError.readLine()) != null) {
            out.accept(s);
        }

        out.accept("Code has been upgraded, please use `git status` to check modified files first then commit");
    }

    @Tool({"Set the service name"})
    public void setService(String service) {
        this.service = service;
    }

    public void setOut(Consumer<String> out) {
        this.out = out;
    }

    public String getDependenciesSummary() throws IOException {
        Path path2 = Paths.get("api/dependencies.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode dependenciesNode = rootNode.get(0).get("dependencies");
        StringBuilder issues = new StringBuilder();
        for (JsonNode dependencyNode : dependenciesNode) {
            issues.append(dependencyNode.get("name") + "\n");
        }
        return issues.toString();
    }

    public String getTechnologiesSummary() throws IOException {
        Path path2 = Paths.get("api/technologies.json");
        return new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
    }


    public String getIssuesSummary() throws IOException {
        Path path2 = Paths.get("api/issues.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode issuesNode = rootNode.get(0).get("issues");
        StringBuilder issues = new StringBuilder();
        for (Iterator<String> it = issuesNode.fieldNames(); it.hasNext(); ) {
            String typeName = it.next();
            JsonNode typeNode = issuesNode.get(typeName);
            for (JsonNode detailNode : typeNode) {
                issues.append(typeName + " -> " + detailNode.get("name") + "\n");
            }
        }
        return issues.toString();
    }

}
