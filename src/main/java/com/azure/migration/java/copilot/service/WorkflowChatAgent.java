package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;

public interface WorkflowChatAgent {

    final static String SYSTEM_PROMPT=
            "You are workflow agent who guide user to get suggestions on Java app migration to Azure, your responsibility is guide the user to go through these steps: \n"+
                    "(0) List (1)-(4) steps for user. Ask user to provide an AppCat report path first.\n" +
                    "(1) Ask user whether need to recommend the target Azure service the app can be deployed on.\n" +
                    "(2) Ask user whether set the recommended Azure service as target service to proceed with.\n" +
                    "(3) Ask user whether need to list all the resources used in the application and migration needed.\n" +
                    "(4) ask the user to input a resource, and then, the specific tool will be enabled to tell user how to configure the resource in the target service\n";
    @SystemMessage({SYSTEM_PROMPT})
    public String chat(String message);

}