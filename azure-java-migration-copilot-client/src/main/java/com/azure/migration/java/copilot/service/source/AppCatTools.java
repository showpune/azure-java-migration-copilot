package com.azure.migration.java.copilot.service.source;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class AppCatTools {

    @Autowired
    private MigrationContext migrationContext;

    public String getApplications() throws IOException {
        Path path2 = Paths.get("api/applications.json");
        return new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
    }

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

    public Set<String> getIssuesFileList(Set<String> ruleIds) throws IOException {
        Path path2 = Paths.get("api/issues.json");
        String content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode issuesNode = rootNode.get(0).get("issues");
        Set<String> issues = new TreeSet<>();
        for (Iterator<String> it = issuesNode.fieldNames(); it.hasNext(); ) {
            String typeName = it.next();
            JsonNode typeNode = issuesNode.get(typeName);
            for (JsonNode detailNode : typeNode) {
                JsonNode affectedFiles = detailNode.get("affectedFiles");
                String id = detailNode.get("ruleId").asText();
                if(!ruleIds.contains(id)){
                    continue;
                }
                for (JsonNode affectedFile : affectedFiles) {
                    JsonNode files = affectedFile.get("files");
                    for (JsonNode file : files) {
                        issues.add(file.get("fileId").asText());
                    }
                }

            }
        }

        Path filesJsonPath = Paths.get("api/files.json");
        Set<String> result = new TreeSet<>();
        content = new String(Files.readAllBytes(Paths.get(migrationContext.getWindupReportPath()).resolve(filesJsonPath)));mapper = new ObjectMapper();
        rootNode = mapper.readTree(content);
        JsonNode finalRootNode = rootNode;

        finalRootNode.forEach(node -> {
            String id = node.get("id").asText();
            if(issues.contains(id)){
                result.add(node.get("fullPath").asText());
            }
        });

        return result;
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
