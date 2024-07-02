package com.azure.migration.java.copilot.service;

import com.azure.migration.java.copilot.service.command.MigrationCommand;
import com.azure.migration.java.copilot.service.model.RecommendedService;
import com.azure.migration.java.copilot.service.model.RecommendedServices;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class MigrationCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MigrationCopilotApplication.class, args);
    }

    @Bean
    ApplicationRunner interactiveChatRunner(TextTerminal<?> terminal, TextIO textIO, MigrationWorkflowTools tools) {
        return args -> {
            tools.setOut(terminal::println);

            terminal.println("I‘m your migration assistant. Could you please provide me with the location of your source code?");

            String sourceCodeLocation = textIO.
                    newStringInputReader().
                    withDefaultValue(System.getProperty("user.dir")).
                    read(">");

            tools.setSourceLocation(sourceCodeLocation);

            tools.scanCodeWithAppCat();
            //TODO: detect cloud foundry manifest

            RecommendedServices services = tools.recommendTargetService();

            String selectedItem = textIO.newStringInputReader().
                    withNumberedPossibleValues(services.formatToList()).
                    read("\nPlease select the target service you want to migrate");

            Optional<RecommendedService> recommendedServiceOptional = services.indexOf(selectedItem);
            if (recommendedServiceOptional.isEmpty()) {
                throw new RuntimeException("Wrong index selected: " + selectedItem);
            }

            RecommendedService targetService = recommendedServiceOptional.get();
            tools.setService(targetService.getService());
            terminal.println("Target service has been set to " + targetService.getService());

            String selectedText = textIO.newStringInputReader().
                    withNumberedPossibleValues(MigrationCommand.availableCommands()).
                    read("\nPlease select next step you want to perform");

            MigrationCommand command = MigrationCommand.of(selectedText);
            command.execute(terminal::println);
        };
    }
}
