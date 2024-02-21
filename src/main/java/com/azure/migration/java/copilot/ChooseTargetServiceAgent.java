package com.azure.migration.java.copilot;

import dev.langchain4j.service.SystemMessage;

public interface ChooseTargetServiceAgent {

    final static String CHOOSE_SERVICE_PROMPT=
            "You are an Azure expert, your responsibility is to help customers migrate their local Java applications to Azure cloud.\n" +
                    "The services that can deploy Java applications on Azure include:\n" +
                    "1) Azure Spring Apps: It mainly deploys SpringBoot applications for customers and can provide additional value. In addition, it can help customers host service discovery eureka, configuration management spring cloud config server and other basic spring cloud services, and provide metric collection services based on micrometer, actuator, etc. to reduce user maintenance costs.\n" +
                    "2) Azure Jakartaee: Provides four services, including websphere, openliberty, weblogic, jboss. If customer using JNDI, ejb, JTA, the application must run in a JarkatEE service\n" +
                    "3) App Service: Provides basic java executator jar or java application deployment services based on tomcat\n" +
                    "4) Kubernetes: The most basic service deployment\n" +
            "Customers will provide a dependency list based on windup, please recommend Azure services that customers can use based on the list.";
    @SystemMessage({CHOOSE_SERVICE_PROMPT})
    public String chooseService(String windupDescription);

    final static String LIST_RESOURCE_PROMPT=
            "You are an Azure expert, your responsibility is to help customers migrate their local Java applications to Azure cloud.\n" +
            "The services that can deploy Java applications on Azure include:\n" +
            "Customers will provide application information include technologies, dependencies and issues, please list all the resources need to be created on azure according to the input." +
            "If the customer use local file in the application, please ask customer to create storage account and mount to the service";
    @SystemMessage({LIST_RESOURCE_PROMPT})
    public String listResources(String windupDescription);


}