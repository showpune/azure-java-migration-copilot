package com.azure.migration.java.copilot.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Component
public class MigrationWorkflowTools {

    private String reportUrl;

    private String service;

    @Autowired
    private ChooseTargetServiceAgent chooseTargetServiceAgent;


    @Autowired
    private ConfigureResourceAgent configureResourceAgent;

    @Tool("Recommand the target service the application can be migrated to")
    public String recommendTargeService() throws IOException {
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        String result = chooseTargetServiceAgent.chooseService(content);
        return "Print the result: \n"+result;
    }

    @Tool("List all the resources used in the application according to the report")
    public String listResources() throws IOException {
        String content = "Technologies: \n"+ getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        String result = chooseTargetServiceAgent.listResources(content);
        return "Print the result: \n"+result;
    }

    @Tool("Configure the given resource in the service")
    public String configureResources(String resource) throws IOException {
        if (service == null) {
            return "Give the service name first";
        }
        String result =  configureResourceAgent.configureResource(resource, service);
        return "Print the result: \n"+result;
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
