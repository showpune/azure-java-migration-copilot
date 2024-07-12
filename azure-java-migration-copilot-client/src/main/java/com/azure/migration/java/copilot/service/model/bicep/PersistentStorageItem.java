package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

public class PersistentStorageItem extends MetadataItem {

    @Getter
    @Setter
    private boolean required;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String mountPath;

    @Getter
    @Setter
    private String fileShare;

    @Getter
    @Setter
    private String mountOptions;

}
