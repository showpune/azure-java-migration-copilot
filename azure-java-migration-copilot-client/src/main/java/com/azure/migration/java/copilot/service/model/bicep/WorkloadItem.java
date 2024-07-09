package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

public class WorkloadItem {

    @Getter
    @Setter
    private int instanceCount;

    @Getter
    @Setter
    private String cpu;

    @Getter
    @Setter
    private String memory;

}
