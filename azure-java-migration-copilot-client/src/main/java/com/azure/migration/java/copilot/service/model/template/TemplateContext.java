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
    @Setter
    @JsonPropertyDescription(value = "the azure container app environment to host multiple applications")
    private String appEnv;

    @Getter
    @JsonPropertyDescription(value = "environment variable list detected from report")
    private final List<EnvVariableTemplateContext> environments = new ArrayList<>();

    @Getter
    @JsonPropertyDescription(value = "the database configuration if database usage is detected from report,if not detected, set to empty" +
            " you can abstract a database configuration from a connection string, the connection string format like:" +
            " jdbc:{type}://{host}:{port}/{schema}?user={username}&password={password}&useSSL=false&serverTimezone=UTC" +
            "\n if the host is an azure host, it will looks like {database name}.mysql.database.azure.com ")
    @JsonProperty("database")
    private final DbTemplateContext dbTemplateContext = new DbTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "persistent storage configuration if local storage usage is detected from report, also known as volume-mount setting")
    @JsonProperty("persistent")
    private final PersistentStorageTemplateContext persistentStorageTemplateContext = new PersistentStorageTemplateContext();

    @Getter
    @JsonPropertyDescription(value = "workload configuration detected from report, for example Cloud Foundry manifest")
    @JsonProperty("workload")
    private final WorkloadTemplateContext workloadTemplateContext = new WorkloadTemplateContext();

}
