package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class ResourcesContext {
    @Getter
    @Setter
    @JsonPropertyDescription("set to true only when database usage is detected from report, default to false")
    private boolean required;

}
