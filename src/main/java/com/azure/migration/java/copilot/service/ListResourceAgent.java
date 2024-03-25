package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ListResourceAgent {
    final static String LIST_RESOURCE_PROMPT=
            "You are an information organization expert. I will provide you a report about a java app migrating to Azure, your responsibility is to find related information and answer below questions(if no answer for (1) under the question,skip the question):\n" +
            "1. [Database] (1) what database is used in the app? (2) what database on azure is recommended to migrate to?\n" +
            "2. [Message Queue] (1) what Message Queue is used in the app? (2) If any mq used, Azure Service Bus is recommended to migrate to.\n" +
            "3. [Application Performance Management (APM)] (1) what APM tool is used in the app? (2) You can connect you APM to the service on which your app gonna be deployed.\n" +
            "4. [Eureka] (1) is Eureka used for service discovery in the app? (2) Azure Spring Apps can host Eureka (Standard Tier) or Service Registry (Enterprise Tier) for you.\n" +
            "5. [Cache] (1) what caching solution is used in the app? (2) If any cache used, Azure Cache for Redis is recommended to migrate to. On Azure, you can connect your app to Azure Cache for Redis by Service Connector easily\n" +
            "6. [Configuration Management] (1) how is configuration management handled in the app? (2) Azure Key Vault is recommended.\n" +
            "7. [File System] (1) is local file system used in the app? (2) Use Azure File Storage and configure it in the service on which your app gonna be deployed..\n";


    @SystemMessage({LIST_RESOURCE_PROMPT})
    public String listResources(String windupDescription);

    @UserMessage({ "Please summarize what resources are used use concise words:\n {{resourceList}}"})
    public String summarizeResources(@V("resourceList") String resourceList);

}