package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.MigrationWorkflowTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeCommand implements MigrationCommand {

    @Autowired
    private MigrationWorkflowTools tools;

    @Override
    public void execute(String commandText) {
//        try {
////            tools.upgradeCodeForMavenProject();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
