package com.azure.migration.java.copilot.service;

import org.beryx.textio.TextTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class LocalCommandTools {

    @Autowired
    TextTerminal<?> terminal;

    private List<String> getShellStartCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new ArrayList<>(Arrays.asList(
                    "cmd",
                    "/c"
            ));
        }
        return new ArrayList<>();
    }

    public boolean executeCommand(List<String> commands) {
        try {
            List<String> osRelatedCommands = getShellStartCommand();
            osRelatedCommands.addAll(commands);
            terminal.println("Execute command: " + String.join(" ", osRelatedCommands));

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(osRelatedCommands.toArray(new String[]{}));

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                terminal.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                terminal.println(s);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
