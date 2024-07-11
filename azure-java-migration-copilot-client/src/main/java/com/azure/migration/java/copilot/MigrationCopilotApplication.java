package com.azure.migration.java.copilot;

import com.azure.migration.java.copilot.service.ConsoleContext;
import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.analysis.ServiceFacade;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static com.azure.migration.java.copilot.command.MigrationCommand.*;
import static com.azure.migration.java.copilot.service.ConsoleContext.*;

@SpringBootApplication
public class MigrationCopilotApplication {

    @Autowired
    private TextTerminal<?> terminal;

    @Autowired
    private TextIO textIO;

    @Autowired
    private MigrationContext migrationContext;

    @Autowired
    private ServiceFacade serviceFacade;

    public static void main(String[] args) {
        SpringApplication.run(MigrationCopilotApplication.class, args);
    }

    @Bean
    ApplicationRunner interactiveChatRunner() {
        return args -> {
            AnsiConsole.systemInstall();

            migrationContext.init(args);

            String hint = serviceFacade.recommendService();

            loop(
                    ConsoleContext.builder().defaultValue("1").prompt("/>").terminal(terminal).textIO(textIO).hint(answer(hint)).build(),
                    ConsoleContext::exited,
                    input -> {
                        try {
                            terminal.println(answer(serviceFacade.chooseService(input)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    serviceFacade::isServiceSet
            );

            loop(
                    ConsoleContext.builder().defaultValue("help").prompt("/>").terminal(terminal).textIO(textIO).hint(ask("Copilot: What do you want to do next? (type `help` to check available commands)")).build(),
                    ConsoleContext::exited,
                    input -> {
                        of(input).ifPresentOrElse(
                                cmd -> cmd.execute(restOfCommand(input)),
                                () -> terminal.println(error("Unrecognized command: " + input)));

                    }
            );


            AnsiConsole.systemUninstall();
        };
    }
}
