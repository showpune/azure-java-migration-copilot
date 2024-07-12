package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class PersistentStorageTemplateContext {
    @Getter
    @Setter
    @JsonPropertyDescription("whether local file system usage or relative path is detected from report, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the file share or file device name of Storage, default to data")
    private String fileShare;

    @Getter
    @Setter
    @JsonPropertyDescription("the volume mount path inside container if persistent storage is required, known as container dir, default to /data")
    private String mountPath;

    @Getter
    @Setter
    @JsonPropertyDescription("the mount options for volume mount into the container")
    private String mountOptions;

}
