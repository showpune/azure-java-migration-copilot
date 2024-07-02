package com.azure.migration.java.copilot.service.model;

import lombok.Data;

@Data
public class RecommendedService {

    private String index;

    private String service;

    private String reason;

    private String rating;

    public String format() {
        return String.join("\n", service + " " + rating, reason);
    }
}
