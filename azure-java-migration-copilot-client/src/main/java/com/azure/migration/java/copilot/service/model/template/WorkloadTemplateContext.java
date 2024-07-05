package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;

public class WorkloadTemplateContext {

    @Getter
    @Setter
    private float cpu;

    @Getter
    @Setter
    private float memory;

    @Getter
    @Setter
    private int instanceCount;
}
