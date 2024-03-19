package com.azure.migration.java.copilot.rag;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class RAGApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(RAGApplication.class, args);
//    }

    @Bean
    ApplicationRunner interactiveChatRunner(LocalFileToAISearchRAG ingest) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.print("User:");
                    String userMessage = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        break;
                    }else if (userMessage.startsWith("ingest ")) {
                        ingest.ingest(userMessage.replaceAll("ingest ",""));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            scanner.close();
        };
    }


}
