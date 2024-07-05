package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;

public class StorageAccountTemplateContext {
    @Getter
    @Setter
    private boolean used;

    @Getter
    @Setter
    private String storageAccountName;
}
