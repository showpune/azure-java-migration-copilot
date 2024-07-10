package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class WorkloadTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("the CPU of the application, the minimun value is 0.5 and maximun value is 4,  if memory is 1G then cpu is 0.5, if memory is 2G then cpu is 1 and so on. the CPU of the application, the minimun value is 0.5 and maximun value is 4,  if memory is 1G then cpu is 0.5, if memory is 2G then cpu is 1 and so on. ")
    private String cpu;

    @Getter
    @Setter
    @JsonPropertyDescription("the workload memory quota, default to 1Gi")
    private String memory = "1Gi";

    @Getter
    @Setter
    @JsonPropertyDescription("the workload instance count, default to 1")
    private int instanceCount = 1;
}
