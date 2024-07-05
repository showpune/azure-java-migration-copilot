package com.azure.migration.java.copilot.service;

import lombok.Builder;
import lombok.Getter;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

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
}
