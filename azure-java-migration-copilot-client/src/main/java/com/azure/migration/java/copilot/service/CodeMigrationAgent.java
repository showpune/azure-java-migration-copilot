package com.azure.migration.java.copilot.service;

import dev.langchain4j.service.SystemMessage;

public interface CodeMigrationAgent {

    @SystemMessage(fromResource = "classpath:list-migration-solutions-prompt.txt")
    public String listMigrationSolutions(String windupDescription);

    public String codeMigration(String code);
}
