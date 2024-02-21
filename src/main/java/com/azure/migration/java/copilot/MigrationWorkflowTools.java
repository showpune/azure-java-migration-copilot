package com.azure.migration.java.copilot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class MigrationWorkflowTools {

    private String reportUrl;

    private String service;

    @Autowired
    private ChooseTargetServiceAgent agent;

    @Tool("Recommand the target service the application can be migrated to")
    public String recommendTargeService() throws IOException {
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        return agent.chooseService(content);
    }

    @Tool("List all the resources used in the application according to the report")
    public String listResources() throws IOException {
        String content = "Technologies: \n"+ getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        return agent.listResources(content);
    }

    @Tool({"Set the report path for analysis"})
    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    @Tool({"Set the service name"})
    public void setService(String service) {
        this.service = service;
    }

    public String getDependenciesSummary() throws IOException {
        Path path2 = Paths.get("api/dependencies.json");
        String content  = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode dependenciesNode = rootNode.get(0).get("dependencies");
        StringBuilder issues =new StringBuilder();
        for (JsonNode dependencyNode:dependenciesNode ) {
                issues.append(dependencyNode.get("name")+"\n");
        }
        return issues.toString();
    }

    public String getTechnologiesSummary() throws IOException {
        Path path2 = Paths.get("api/technologies.json");
        return new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
    }


    public String getIssuesSummary() throws IOException {
        Path path2 = Paths.get("api/issues.json");
        String content  = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode issuesNode = rootNode.get(0).get("issues");
        StringBuilder issues =new StringBuilder();
        for (Iterator<String> it = issuesNode.fieldNames(); it.hasNext(); ) {
            String typeName = it.next();
            JsonNode typeNode = issuesNode.get(typeName);
            for (JsonNode detailNode : typeNode) {
                issues.append(typeName+" -> "+detailNode.get("name")+"\n");
            }
        }
        return issues.toString();
    }

}
