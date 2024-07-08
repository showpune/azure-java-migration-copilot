package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class PersistantStorageTemplateContext {
    @Getter
    @Setter
    @JsonPropertyDescription("set to true only when file system usage is detected from report, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the mounting path in container if persistent storage is required, default to /mnt")
    private String mountPath;

}
