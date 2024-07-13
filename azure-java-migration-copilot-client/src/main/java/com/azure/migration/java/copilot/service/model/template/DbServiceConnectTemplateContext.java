package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

public class DbServiceConnectTemplateContext {

    @Getter
    @Setter
    @JsonPropertyDescription("whether use Azure Service Connector for connecting database, default to false")
    private boolean required;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Database type for the service connector, should be extracted from connection string, default to mysql")
    private String type;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Database resource name, should be extracted from the first part of host from connection string, default to application name")
    private String resourceName;

    @Getter
    @Setter
    @JsonPropertyDescription("the Azure Database schema name, should be extracted from connection string, default to test")
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
