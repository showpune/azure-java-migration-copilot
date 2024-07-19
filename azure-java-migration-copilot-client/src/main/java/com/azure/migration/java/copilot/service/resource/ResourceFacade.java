package com.azure.migration.java.copilot.service.resource;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.common.ToolsAgent;
import com.azure.migration.java.copilot.service.model.resource.ResourceCategory;
import com.azure.migration.java.copilot.service.model.resource.ResourceProperty;
import com.azure.migration.java.copilot.service.model.resource.Resources;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import com.azure.migration.java.copilot.service.source.CFManifestTools;
import com.azure.migration.java.copilot.service.util.JsonUtil;
import dev.langchain4j.internal.Json;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.mjaron.etudes.Table;
import pl.mjaron.etudes.table.RenderContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ResourceFacade {

    @Autowired
    private ResourceConfigureAgent resourceConfigureAgent;

    @Autowired
    private ToolsAgent toolsAgent;

    @Autowired
    private AppCatTools appCatTools;

    @Autowired
    private CFManifestTools cfManifestTools;

    @Autowired
    private MigrationContext migrationContext;

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    private String memoryId;

    public void initApplicationConfiguration() throws IOException {
        if (migrationContext.getCfManifestPath() == null || !Files.exists(Path.of(migrationContext.getCfManifestPath()))) {
            return;
        }
        String result = toolsAgent.abstractInfo(ApplicationConfiguration.jsonSchema, Json.toJson(applicationConfiguration), cfManifestTools.getDetails());
        applicationConfiguration = Json.fromJson(result, ApplicationConfiguration.class);
    }

    public String listResource() throws IOException {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("AppCat report path cannot be null");
        }
        return resourceConfigureAgent.listResources(getApplicationReport());
    }

    public String resourceGuideSelect(String resources) {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("AppCat report path cannot be null");
        }
        return resourceConfigureAgent.resourceGuideSelect(resources);
    }

    public String resourceGuide(String resources) {
        if (migrationContext.getService() == null) {
            throw new IllegalArgumentException("Target service has not been determined");
        }
        return resourceConfigureAgent.resourceGuide(resources, migrationContext.getService());
    }

    public String resourceConfigAbstract() throws IOException {
        return resourceConfigureAgent.resourceConfigAbstract(getApplicationReport(), JsonUtil.toJson(Resources.create()));
    }

    public String resourceConfigChat(String userInput, String originalData) {
        return resourceConfigureAgent.resourceConfigChat(userInput, originalData);
    }

    public String resourceConfigTable(Resources resources) {
        StringBuilder builder = new StringBuilder();
        for (ResourceCategory c : resources.getCategories()) {
            builder.append(Ansi.ansi().bold().a("[" + StringUtils.capitalize(c.getName()) + "]").reset().toString());
            builder.append("\n");
            builder.append(StringUtils.capitalize(c.getDescription()));
            builder.append("\n");
            Table.render(c.getProperties(), ResourceProperty.class).
                    withColumns(
                            RenderContext.col("name", "Property")
                                    .col("value", "Current Value")
                                    .col("description", "Description")
                                    .col("sourceOfDetection", "Source of detection")
                    ).
                    markdown().
                    withoutEscaper().
                    to(builder).
                    run();
            builder.append("\n");
        }

        return builder.toString();
    }

    private String getApplicationProperties() {
        Path dir = Path.of(migrationContext.getSourceCodePath(), "src/main/resources");
        File[] files = dir.toFile().listFiles((base, name) ->
                Arrays.asList("application.properties", "application.yml", "application.yaml").contains(name)
        );
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

    private String getBuildFile() {
        Path dir = Path.of(migrationContext.getSourceCodePath());
        File[] files = dir.toFile().listFiles((base, name) ->
                Arrays.asList("pom.xml", "build.gradle").contains(name)
        );
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

    private String getApplicationReport() throws IOException {
        String details = "[[Applications]]\n" + appCatTools.getApplications()
//                + "\n\n[[POM or Gradle]]\n" + getBuildFile()
                + "\n\n[[AppCat Report]]\n" + appCatTools.getAllDetails()
//                + "\n\n[[Application Properties]]\n" + getApplicationProperties()
                ;
        if (migrationContext.getCfManifestPath() != null && Files.exists(Path.of(migrationContext.getCfManifestPath()))) {
            details += "\n\n[[Cloud Foundry Manifest]]\n" + Files.readString(Path.of(migrationContext.getCfManifestPath()));
            details += "\n\n[[Cloud Foundry Manifest file location]]\n" + migrationContext.getCfManifestPath();
        }
        return details;
    }
}
