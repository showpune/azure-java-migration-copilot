package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class WorkloadTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("the workload cpu quota, default to 0.5")
    private String cpu = "0.5";

    @Getter
    @Setter
    @JsonPropertyDescription("the workload memory quota, default to 1Gi")
    private String memory = "1Gi";

    @Getter
    @Setter
    @JsonPropertyDescription("the workload instance count, default to 1")
    private int instanceCount = 1;
}
