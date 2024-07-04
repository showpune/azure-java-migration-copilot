package com.azure.migration.java.copilot.service;

import com.azure.migration.java.copilot.service.model.RecommendedServices;
import com.azure.migration.java.copilot.service.model.Resources;
import com.azure.migration.java.copilot.service.source.appcat.AppCatTools;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.internal.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class MigrationWorkflowTools {


    @Autowired
    MigrationContext migrationContext;

    @Autowired
    AppCatTools appCatTools;

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
        String content = "Technologies: \n" + appCatTools.getTechnologiesSummary() + "\n\n Issues:\n" + appCatTools.getIssuesSummary() + "\n\n Dependencies:\n" + appCatTools.getDependenciesSummary() + "\n\n Application Properties:\n" + getApplicationProperties();
        String response = serviceAnalysisAgent.listResources(content);
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


    public String getApplicationProperties() {
        Path dir = Path.of(migrationContext.getSourceCodePath(), "src/main/resources");
        File[] files = dir.toFile().listFiles((base, name) -> name.startsWith("application") && (name.endsWith(".properties") || name.endsWith(".yaml") || name.endsWith("yml")));
        if (files == null) {
            return "";
        }
        return Arrays.stream(files).map(f -> {
            try {
                return Files.readString(Path.of(f.getAbsolutePath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining("\n"));
    }
}
