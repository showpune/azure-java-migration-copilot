package com.azure.migration.java.copilot.service.model;

import lombok.Data;

@Data
public class Resource {

    private String type;

    private String resource;

    private String recommendation;

    private boolean used;

    public String format() {
        return String.join(" ", "[" + type + "]", resource, "recommendation:", recommendation);
    }
}
