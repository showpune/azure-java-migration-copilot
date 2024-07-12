package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class DbServiceConnectTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("whether use this service connect for the application to connect to database, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the database type for the service connect, should be extracted from database type, default to mysql")
    private String type;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Database resource name, should be extracted from database host the first part, default to application name")
    private String resourceName;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Database schema name, should be extracted from database, default to test")
    private String database;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Subscription ID for service connect, default to empty")
    private String subscriptionId;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Resource Group name for service connect, default to empty")
    private String resourceGroup;
}
