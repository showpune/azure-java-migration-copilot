package com.azure.migration.java.copilot.service.analysis;

import com.azure.migration.java.copilot.service.MigrationContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceAnalysisTools {

    @Autowired
    private MigrationContext migrationContext;

    @Tool({"Set the target service"})
    public void setService(String service) {
        this.migrationContext.setService(service);
    }

    @Tool({"Set the application name"})
    public void setAppName(String appName) {
        this.migrationContext.setAppName(appName);
    }
}
