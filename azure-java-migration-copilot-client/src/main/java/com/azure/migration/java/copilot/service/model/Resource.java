package com.azure.migration.java.copilot.service.model;

import lombok.Data;

@Data
public class Resource {

    private String category;

    private String[] names;

    private String reason;

    private boolean used;

    public String format() {
        return String.format("[%s] %s\n%s", category, String.join(", ", names), reason);
    }
}
