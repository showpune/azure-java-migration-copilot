package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;

public class TemplateContext {

    @Getter
    @Setter
    private String acaEnvName;

    @Getter
    private DbTemplateContext dbTemplateContext = new DbTemplateContext();

    @Getter
    private StorageAccountTemplateContext storageAccountTemplateContext = new StorageAccountTemplateContext();

}
