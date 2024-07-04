package com.azure.migration.java.copilot.service.source;

import com.azure.migration.java.copilot.service.MigrationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class CFManifestTools {

    @Autowired
    private MigrationContext migrationContext;

    public String getDetails() throws IOException {
        if (migrationContext.getCfManifestPath() == null) {
            return "";
        }
        return new String(Files.readAllBytes(Paths.get(migrationContext.getCfManifestPath())));
    }
}
