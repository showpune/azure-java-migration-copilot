package com.azure.migration.java.copilot.service.resource;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.appcat.AppCatTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private AppCatTools appCatTools;

    @Autowired
    private MigrationContext migrationContext;

    public String listResource() throws IOException {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("AppCat report path cannot be null");
        }
        String details = appCatTools.getAllDetails() + "\n\n Application Properties:\n" + getApplicationProperties();
        return resourceConfigureAgent.listResources(details);
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

    public String resourceConfig(String userMessage) {
        return resourceConfigureAgent.configResource(userMessage);
    }

    private String getApplicationProperties() {
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
