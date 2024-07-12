package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class DbTemplateContext {
    @Getter
    @Setter
    @JsonPropertyDescription("whether database is used in application, if database is detected, this value should be set to true")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the database connection string, default to jdbc:mysql://localhost/test")
    private String connectionString;

    @Getter
    @Setter
    @JsonPropertyDescription("the username used to connect to database, default to username")
    private String username;

    @Getter
    @Setter
    @JsonPropertyDescription("the password used to connect to database, default to password")
    private String password;
}
