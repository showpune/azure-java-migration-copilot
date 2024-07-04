package com.azure.migration.java.copilot.service.resource;

import com.azure.migration.java.copilot.service.MigrationContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceConfigTools {
    @Autowired
    MigrationContext migrationContext;

    @Tool({"Set variable in key value pair"})
    public void setVariable(String key, String value) {
        migrationContext.setVariable(key, value);
    }
}
