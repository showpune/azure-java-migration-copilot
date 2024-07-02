package com.azure.migration.java.copilot.service.command;

import com.azure.migration.java.copilot.service.MigrationWorkflowTools;
import com.azure.migration.java.copilot.service.model.Resources;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class ResourceCommand implements MigrationCommand {

    @Autowired
    private MigrationWorkflowTools tools;

    @Autowired
    private TextIO textIO;

    @Override
    public void execute(Consumer<String> out) {
        Resources resources = null;
        try {
            resources = tools.listResources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String selectedItem = textIO.newStringInputReader().
                withNumberedPossibleValues(resources.formatToList(true)).
                read("\nPlease select the resource you want to configure");

        System.out.println(selectedItem);
        //TODO
    }
}
