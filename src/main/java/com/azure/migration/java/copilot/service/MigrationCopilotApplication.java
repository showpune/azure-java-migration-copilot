package com.azure.migration.java.copilot.service;

import com.azure.migration.java.copilot.rag.LocalFileToAISearchRAG;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;


@SpringBootApplication
public class MigrationCopilotApplication {


    @Bean
    ApplicationRunner interactiveChatRunner(WorkflowChatAgent agent, LocalFileToAISearchRAG ingest) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            System.out.println("======================Migration Copilot======================:\n"+agent.chat("migration"));

            while (true) {
                try {
                    System.out.print("User:");
                    String userMessage = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        break;
                    } else if (userMessage.startsWith("ingest ")) {
                        ingest.ingest(userMessage.replaceAll("ingest ", ""));
                    } else {
                        //String message = agent.chat(userMessage);
//                        if (agent.)
                        String message = agent.chat(userMessage);
                        System.out.println("======================Migration Copilot======================:\n"+message);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            scanner.close();
        };
    }


    public static void main(String[] args) {
        SpringApplication.run(MigrationCopilotApplication.class, args);
    }

}
