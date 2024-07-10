package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

public class MetadataItem {

    @Getter
    @Setter
    private String appName;

    @Getter
    @Setter
    private DbItem db;

    @Getter
    @Setter
    private PersistentStorageItem persistent;

}
