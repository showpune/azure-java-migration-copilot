package com.azure.migration.java.copilot.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;


@SpringBootApplication
public class MigrationCopilotApplication {


    public static void main(String[] args) {
        SpringApplication.run(MigrationCopilotApplication.class, args);
    }

    @Bean
    ApplicationRunner interactiveChatRunner(WorkflowChatAgent agent) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            System.out.println("======================Migration Copilot======================:\n" + agent.chat("migration"));

            while (true) {
                try {
                    System.out.print("User:");
                    String userMessage = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        break;
                    } else {
                        String message = agent.chat(userMessage);
                        System.out.println("======================Migration Copilot======================:\n" + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            scanner.close();
        };
    }

}
