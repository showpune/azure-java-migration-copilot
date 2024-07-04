package com.azure.migration.java.copilot.service.source.appcat;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Component
public class AppCatTools {

    @Autowired
    private MigrationContext migrationContext;

    public String getDependenciesSummary() throws IOException {
        Path path2 = Paths.get("api/dependencies.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode dependenciesNode = rootNode.get(0).get("dependencies");
        StringBuilder issues = new StringBuilder();
        for (JsonNode dependencyNode : dependenciesNode) {
            issues.append(dependencyNode.get("name") + "\n");
        }
        return issues.toString();
    }

    public String getTechnologiesSummary() throws IOException {
        Path path2 = Paths.get("api/technologies.json");
        return new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
    }


    public String getIssuesSummary() throws IOException {
        Path path2 = Paths.get("api/issues.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode issuesNode = rootNode.get(0).get("issues");
        StringBuilder issues = new StringBuilder();
        for (Iterator<String> it = issuesNode.fieldNames(); it.hasNext(); ) {
            String typeName = it.next();
            JsonNode typeNode = issuesNode.get(typeName);
            for (JsonNode detailNode : typeNode) {
                issues.append(typeName + " -> " + detailNode.get("name") + "\n");
            }
        }
        return issues.toString();
    }

    public String getAllDetails() throws IOException {
        return "Technologies: \n"
                + getTechnologiesSummary()
                + "\n\n Issues:\n"
                + getIssuesSummary()
                + "\n\n Dependencies:\n"
                + getDependenciesSummary();
    }

}
