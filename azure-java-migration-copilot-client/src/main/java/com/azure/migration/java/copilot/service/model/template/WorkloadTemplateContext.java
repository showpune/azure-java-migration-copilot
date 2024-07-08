package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class WorkloadTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("the workload cpu quota from cloud foundry, default to 1")
    private String cpu;

    @Getter
    @Setter
    @JsonPropertyDescription("the workload memory quota from cloud foundry, default to 1G")
    private String memory;

    @Getter
    @Setter
    @JsonPropertyDescription("the workload instance count from cloud foundry, default to 1")
    private int instanceCount;
}
