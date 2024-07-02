package com.azure.migration.java.copilot.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.Map;
import java.util.Scanner;


@SpringBootApplication
public class MigrationCopilotApplication {


    public static void main(String[] args) {
        SpringApplication.run(MigrationCopilotApplication.class, args);
    }

    @Bean
    ApplicationRunner interactiveChatRunner(WorkflowChatAgent agent, DetectTools tools) {
        return args -> {

            // Get the source path with the current absolute directory as default
            String sourcePathString = new File("").getAbsolutePath();

            if (args.containsOption("path")) {
                sourcePathString = args.getOptionValues("path").get(0);
            }

            boolean force=false;
            if (args.containsOption("force")) {
                    force = true;
            }

            File sourcePath = new File(sourcePathString);
            if (!sourcePath.exists() || !sourcePath.isDirectory()) {
                System.out.println("Please provide the source path of the application to be migrated.");
                return;
            }
            Map<String, String> scanResult;
            try {
                scanResult = tools.scanInput(sourcePathString,force);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }

            System.out.println("The application has been scanned successfully. The report is saved at " + scanResult);

            if (scanResult != null) {
                return;
            }


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
