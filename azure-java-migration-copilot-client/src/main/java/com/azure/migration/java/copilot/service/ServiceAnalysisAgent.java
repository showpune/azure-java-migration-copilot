package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;

public interface ServiceAnalysisAgent {

    String CHOOSE_SERVICE_PROMPT =
            "You are an Azure expert, your responsibility is to help customers migrate their local Java applications to Azure cloud.\n" +
                    "The services that can deploy Java applications on Azure include:\n" +
                    "1) Azure Container Apps: It mainly deploys Java applications for customers and can provide additional value. " +
                    "In addition, it can help customers host service discovery eureka, configuration management spring cloud config server and other basic spring cloud services, and provide metric collection services based on micrometer, actuator, etc. to reduce user maintenance costs." +
                    "If the Application is a Spring Apps, we will recommend customer to deploy the application into Azure Container Apps\n" +
                    "2) Azure Kubernetes Service: The most basic service deployment\n" +
            "Customers will provide a detail analysis report based on windup, which will give the application details, please recommend Azure services above that customers can deploy into.\n" +
            "Please give the detail reason why you recommend the service and don't return plain text, always return content in JSON format with {type: object, properties: {recommendations: {type: array, properties: {index: {type: string}, service: {type: string}, reason: {type: string}, rating: {type: string, description: 'rating in five stars'}}}}}" +
                    "Just return the pure json string without any code mark \n";
    String LIST_RESOURCE_PROMPT =
            "You are an information organization expert. I will provide you a report about a java app migrating to Azure, your responsibility is to find related information and answer below questions. If no information is found about the use of the resource, just skip the question!!:\n" +
                    "- [Database] (1) is database used in the app? (2) what database(s) is/are used in the app? Please list the database names (3) what database on Azure is recommended to migrate to? You must give detailed reason for each type of database.\n" +
//                    "- [Message Queue] (1) is Message Queue used in the app? (2) which Message Queue is used in the app? (3) If any mq used, Azure Service Bus is recommended to migrate to.\n" +
//                    "- [Application Performance Management (APM)] (1) is APM used in the app? (2) which APM tool is used in the app? (3) You can connect you APM to the service on which your app gonna be deployed.\n" +
//                    "- [Eureka] (1) is Eureka used for service discovery in the app? (1) is Eureka used in the app? (2) which library is used in the app to connect to Eureka? (3) Azure Container Apps can host Eureka for you.\n" +
//                    "- [Cache] (1) is caching solution used in the app? (2) which caching solution is used in the app? (3) If any cache used, Azure Cache for Redis is recommended to migrate to. On Azure, you can connect your app to Azure Cache for Redis by Service Connector easily\n" +
//                    "- [Configuration Management] (1) is configuration managed used in app? (2) how is configuration management handled in the app? (3) Azure Key Vault is recommended.\n" +
                    "- [File System] (1) is local file system used in the app? (2) how is the local file system used in the app? please list the java package names (3) Suggest user to use Azure Storage Account - File shares and configure it in the service on which your app gonna be deployed\n" +
                    "- [Environment Variables] (1) is there any env variables used in the app? (2) which env variable should be explicitly specified when app start? please list the variable names (3) Suggest user continue to use environment variables for containers for insensitive information, and use secret to bind to containers for sensitive information\n" +
            "Please give the detail reason and don't return plain text, always return pure JSON with schema {type: object, properties: {resources: {type: array, properties: {category: {type: string, description: 'resource category for each item'}, names: {type: array, description: 'resource names', items: {type: string}}, reason: {type: string, description: 'if this resource category is detected, describe detailed reason how it's handled and the suggestion from third question'}, used: {type: boolean, description: 'whether this resource category is detected from report'}}}}}\n" +
            "Environment variable is in ${env name:default value} format\n" +
            "You should map the answer of question (1) to `used` field, map the answer of question (2) to `resource` field, map the answer of question (3) to `reason` field, map each item to `category` field";
    String OTHER_QUESTION_PROMPT =
            "You are an Azure expert, your responsibility is to help customers migrate their local Java applications to Azure cloud.\n" +
                    "The services that can deploy {description} Java applications on Azure include:\n" +
                    "Customers will provide application information include technologies, dependencies and issues to migration and you will answer question according to customer input \n";

    @SystemMessage({CHOOSE_SERVICE_PROMPT})
    String chooseService(String windupDescription);

    @SystemMessage({LIST_RESOURCE_PROMPT})
    String listResources(String windupDescription);

    @SystemMessage({OTHER_QUESTION_PROMPT})
    String chat(String windupDescription);


}