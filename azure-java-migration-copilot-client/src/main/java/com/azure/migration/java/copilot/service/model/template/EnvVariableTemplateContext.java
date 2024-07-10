package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class EnvVariableTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("the environment variable name")
    private String key;

    @Getter
    @Setter
    @JsonPropertyDescription("the environment variable value")
    private String value;

    @Getter
    @Setter
    @JsonPropertyDescription("the source of detection of environment variables")
    private Set<SourceOfDetection> sourceOfDetections;
}
