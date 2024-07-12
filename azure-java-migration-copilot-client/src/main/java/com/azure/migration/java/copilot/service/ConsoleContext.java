package com.azure.migration.java.copilot.service;

import lombok.Builder;
import lombok.Getter;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.fusesource.jansi.Ansi;

import java.util.Arrays;

@Builder
public class ConsoleContext {

    @Getter
    private TextTerminal<?> terminal;

    @Getter
    private TextIO textIO;

    @Getter
    private String hint;

    @Getter
    private String prompt;

    @Getter
    private String defaultValue;

    public static boolean exited(String userInput) {
        return Arrays.asList("done", "exit").contains(userInput);
    }

    public static String warn(String output) {
        return Ansi.ansi().newline().bold().fg(Ansi.Color.YELLOW).a(output).reset().toString();
    }

    public static String ask(String output) {
        return Ansi.ansi().newline().bold().a(output).reset().toString();
    }

    public static String error(String output) {
        return Ansi.ansi().newline().bold().fg(Ansi.Color.RED).a(output).reset().toString();
    }

    public static String answer(String output) {
        output = "\n========================================================================================================================================================================================================================\n" + output;
        output += "\n========================================================================================================================================================================================================================";
        return Ansi.ansi().a(output).reset().toString();
    }
}
