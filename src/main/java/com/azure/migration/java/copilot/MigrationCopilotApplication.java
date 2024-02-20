package com.azure.migration.java.copilot;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Scanner;


@SpringBootApplication
public class MigrationCopilotApplication {





    @Bean
    ApplicationRunner interactiveChatRunner(MigrationWorkflow flow) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.println(flow.getAvaliableCommand()+"\n");
                    String userMessage = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        break;
                    } else if (userMessage.startsWith(MigrationWorkflow.COMMAND_SET_SERVICE)) {
                        String service = StringUtils.delete(userMessage, MigrationWorkflow.COMMAND_SET_SERVICE);
                        flow.setService(service);
                        System.out.println("Set Service: " + service);
                    } else if (userMessage.startsWith(MigrationWorkflow.COMMAND_SELECT_REPORT)) {
                        String path = StringUtils.delete(userMessage, MigrationWorkflow.COMMAND_SELECT_REPORT);
                        flow.setReportUrl(path);
                        String message = flow.chooseService();
                        System.out.println("Agent: " + message);
                    } else if (userMessage.startsWith(MigrationWorkflow.COMMAND_LIST_RESOURCES)) {
                        String message = flow.listResources();
                        System.out.println("Agent: " + message);
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
