package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription(value = "the application name")
    private String appName;

    @Getter
    @JsonPropertyDescription(value = "environment variable list detected from report")
    private final List<EnvVariableTemplateContext> environments = new ArrayList<>();

    @Getter
    @JsonPropertyDescription(value = "database related configuration detected from report")
    @JsonProperty("database")
    private final DbTemplateContext dbTemplateContext = new DbTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "service connect info for Azure Database")
    @JsonProperty("databaseConnect")
    private final DbServiceConnectTemplateContext dbServiceConnectTemplateContext = new DbServiceConnectTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "persistent storage configuration if local storage usage is detected from report")
    @JsonProperty("persistent")
    private final PersistentStorageTemplateContext persistentStorageTemplateContext = new PersistentStorageTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "workload configuration detected from report, for example Cloud Foundry manifest")
    @JsonProperty("workload")
    private final WorkloadTemplateContext workloadTemplateContext = new WorkloadTemplateContext();

}
