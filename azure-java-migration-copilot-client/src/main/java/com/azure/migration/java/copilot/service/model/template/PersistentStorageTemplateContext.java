package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class PersistentStorageTemplateContext {
    @Getter
    @Setter
    @JsonPropertyDescription("whether local file system usage or relative path is detected from report, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the file share name of Azure Storage Account, default to data")
    private String fileShare;

    @Getter
    @Setter
    @JsonPropertyDescription("the mount path inside container if persistent storage is required, default to /data")
    private String mountPath;

    @Getter
    @Setter
    @JsonPropertyDescription("the mount options for volume mount into the container, default to 'uid=0,gid=0,file_mode=0777,dir_mode=0777,mfsymlinks,nobrl'")
    private String mountOptions;

    @Getter
    @Setter
    @JsonPropertyDescription("the source of detection of persistent storage properties")
    private Set<SourceOfDetection> sourceOfDetections;

}
