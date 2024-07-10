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
    @JsonPropertyDescription(value = "the environment variables, if any environment variables are detected from report")
    private final List<EnvVariableTemplateContext> environments = new ArrayList<>();

    @Getter
    @JsonPropertyDescription(value = "the database configuration if database usage is detected from report,if not detected, set to empty" +
            " you can abstract a database configuration from a connection string, the connection string format like:" +
            " jdbc:{type}://{host}:{port}/{schema}?user={username}&password={password}&useSSL=false&serverTimezone=UTC")
    @JsonProperty("database")
    private final DbTemplateContext dbTemplateContext = new DbTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "the persistent storage configuration if local storage or local file is detected from report, if not detected, set to empty")
    @JsonProperty("persistent")
    private final PersistentStorageTemplateContext persistantStorageTemplateContext = new PersistentStorageTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "the workload configuration if any workload configuration is detected")
    @JsonProperty("workload")
    private final WorkloadTemplateContext workloadTemplateContext = new WorkloadTemplateContext();

}
