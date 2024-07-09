package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class DbTemplateContext {
    @Getter
    @Setter
    @JsonPropertyDescription("specify whether database is used in application, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the database type detected from report, default to mysql")
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
    @JsonPropertyDescription("the database schema name, default to demo")
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
