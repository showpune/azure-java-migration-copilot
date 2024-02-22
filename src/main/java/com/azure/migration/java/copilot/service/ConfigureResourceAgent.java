package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ConfigureResourceAgent {

    final static String CONFIGURE_RESOURCE_PROMPT=
            "You are an Azure expert, your responsibility is to help customers migrate their local Java applications to Azure cloud.\n";

    @SystemMessage({CONFIGURE_RESOURCE_PROMPT})
    @UserMessage({ "Please give the details steps to configure or use resource {{resource}} in an application deployed in service {{service}}"})
    public String configureResource(@V("resource") String resource,@V("service") String service);


}