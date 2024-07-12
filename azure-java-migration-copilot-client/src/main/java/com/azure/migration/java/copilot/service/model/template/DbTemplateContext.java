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
    @JsonPropertyDescription("the database type, default to mysql")
    private String type;

    @Getter
    @Setter
    @JsonPropertyDescription("the database name, default to application name")
    private String name;

    @Getter
    @Setter
    @JsonPropertyDescription("the database port, default to 3306")
    private int port;

    @Getter
    @Setter
    @JsonPropertyDescription("the database schema name, default to schema")
    private String schema;

    @Getter
    @Setter
    @JsonPropertyDescription("the username used to connect to database, default to username")
    private String user;

    @Getter
    @Setter
    @JsonPropertyDescription("the password used to connect to database, default to password")
    private String pwd;
}
