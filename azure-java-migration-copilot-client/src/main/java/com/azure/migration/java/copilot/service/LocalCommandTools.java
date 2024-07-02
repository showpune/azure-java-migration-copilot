package com.azure.migration.java.copilot.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class LocalCommandTools {
    private List<String> getShellStartCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new ArrayList<>(Arrays.asList(
                    "cmd",
                    "/c"
            ));
        } else if (os.contains("linux")) {
            return new ArrayList<>(Arrays.asList(
                    "sh",
                    "-c"
            ));
        }
        return new ArrayList<>();
    }

    public boolean executeCommand(Consumer<String> out, List<String> commands) {
        try {
            List<String> osRelatedCommands = getShellStartCommand();
            osRelatedCommands.addAll(commands);
            out.accept("Execute command: " + String.join(" ", osRelatedCommands));

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(osRelatedCommands.toArray(new String[]{}));

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                out.accept(s);
            }

            while ((s = stdError.readLine()) != null) {
                out.accept(s);
            }
            return true;
        } catch (Exception e) {
            out.accept("Execution command failed: " + e.getMessage());
        }
        return false;
    }

}
