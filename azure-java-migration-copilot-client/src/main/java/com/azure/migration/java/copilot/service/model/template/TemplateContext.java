package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateContext {

    @Getter
    @Setter
    private String acaEnvName;

    @Getter
    @Setter
    private String appName;

    @Getter
    private Map<String, String> envsMap = new HashMap<>();

    @Getter
    private DbTemplateContext dbTemplateContext = new DbTemplateContext();

    @Getter
    private PersistantStorageTemplateContext persistantStorageTemplateContext = new PersistantStorageTemplateContext();

    @Getter
    private WorkloadTemplateContext workloadTemplateContext = new WorkloadTemplateContext();

}
