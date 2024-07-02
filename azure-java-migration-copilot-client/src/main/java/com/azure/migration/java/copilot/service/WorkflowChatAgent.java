package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;

public interface WorkflowChatAgent {

    final static String SYSTEM_PROMPT =
            "You are a workflow agent, your responsibility is guide the customer to finish the migration according to customer's source code. The workflow will defined as below:\n" +
                    "1. Customer must provide the source code location first. With the location, then scan the source code with AppCat to generate windup report" +
                    "   After report been generated, customer can\n" +
                    "   1) Choose to recommend the target service the application can be migrate to\n" +
                    "   2) Choose to list all the resources used in the application\n" +
                    "   3) Answer other free questions about the report\n" +
                    "2. Additionally, you can ask customer to choose one target service, if the customer choose one target service, you can \n" +
                    "   1) Ask the customer to input a resource and you will tell customer how to configure the resource in the target service\n" +
                    "You will always only give the next possible steps in the workflow without additional information\n";

    @SystemMessage({SYSTEM_PROMPT})
    public String chat(String message);

}