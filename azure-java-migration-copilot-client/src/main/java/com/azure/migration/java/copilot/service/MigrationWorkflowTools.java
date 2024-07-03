package com.azure.migration.java.copilot.service;

import com.azure.migration.java.copilot.service.model.RecommendedServices;
import com.azure.migration.java.copilot.service.model.Resources;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.internal.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.function.Consumer;

@Component
public class MigrationWorkflowTools {


    @Autowired
    MigrationContext migrationContext;

    private String service;

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

    public RecommendedServices recommendTargetService() throws IOException {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("reportUrl cannot be null");
        }
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
        String response = serviceAnalysisAgent.chooseService(content);
        return Json.fromJson(response, RecommendedServices.class);
    }

    @Tool("List all the resources used in the application according to the report")
    public Resources listResources() throws IOException {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("reportUrl cannot be null");
        }
        String content = "Technologies: \n" + getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        String response = serviceAnalysisAgent.listResources(content);
        System.out.println(response);
        return Json.fromJson(response, Resources.class);
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

    @Tool({"Set the service name"})
    public void setService(String service) {
        this.service = service;
    }

    public String getDependenciesSummary() throws IOException {
        Path path2 = Paths.get("api/dependencies.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
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
        return new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
    }


    public String getIssuesSummary() throws IOException {
        Path path2 = Paths.get("api/issues.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
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
