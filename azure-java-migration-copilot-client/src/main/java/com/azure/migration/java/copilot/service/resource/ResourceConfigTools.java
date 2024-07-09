package com.azure.migration.java.copilot.service.resource;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceConfigTools {
    @Autowired
    MigrationContext migrationContext;

    @Tool({"Set the template context"})
    public void setTemplateContext(String templateContextJson) throws JsonProcessingException {
        if (templateContextJson != null) {
            this.migrationContext.setTemplateContext(JsonUtil.fromJson(templateContextJson, TemplateContext.class));
        }
    }
}
