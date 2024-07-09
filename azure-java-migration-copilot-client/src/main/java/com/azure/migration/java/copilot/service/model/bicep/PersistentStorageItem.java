package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

public class PersistentStorageItem extends MetadataItem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String mountPath;
}
