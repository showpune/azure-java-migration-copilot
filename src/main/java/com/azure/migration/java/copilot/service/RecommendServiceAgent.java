package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface RecommendServiceAgent {

    final static String CHOOSE_SERVICE_PROMPT="You are an Azure expert, your responsibility is to recommend Azure services(Azure Spring Apps, Azure Jakartaee, Azure App Service, Azure Kubernetes Service, Azure Container Apps) to deploy on for my Java app based provided report." +
        "(1) Based on migration gap and migration benefits, rate Azure services for my Java application to migrate to using a star rating system (up to five stars⭐⭐⭐⭐⭐), explain your rates " +
        "(2) Order these services by their rates.";


    @SystemMessage({CHOOSE_SERVICE_PROMPT})
    public String chooseService(String windupDescription);

    @UserMessage({ "Please tell me the most recommended service.\n {{recommendResult}}"})
    public String number1(@V("recommendResult") String recommendResult);


}