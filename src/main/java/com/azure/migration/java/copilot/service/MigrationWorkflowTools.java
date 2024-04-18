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
    private RecommendServiceAgent recommendServiceAgent;

    @Autowired
    private ListResourceAgent listResourceAgent;

    @Autowired
    private ConfigureResourceAgent configureResourceAgent;

    @Tool("Recommand the target service the application can be migrated to")
    public String recommendTargeService() throws IOException {
        if (reportUrl == null) {
            return "please give the report path first";
        }
        Path path2 = Paths.get("api/technologies.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        String recommendResult = recommendServiceAgent.chooseService(content);
        System.out.println("======================Migration Copilot of Report Analysis======================:\n"+recommendResult);
        String number1service = recommendServiceAgent.number1(recommendResult);
        return number1service;
    }

    @Tool("List all the resources used in the application according to the report")
    public String listResources() throws IOException {
        if (reportUrl == null) {
            return "please give the report path first";
        }
        String content = getIssuesSummary();
        String listResourceResult = listResourceAgent.listResources(content);
        System.out.println("======================Migration Copilot of Report Analysis======================:\n"+listResourceResult);
//        String summary = listResourceAgent.summarizeResources(listResourceResult);
        return "Step(3) completed. Go to step (4)!!";
    }

    @Tool("Configure the given resource in the service")
    public String configureResources(String resource, String service) throws IOException {
        if (service != null) {
            this.service = service;
        }
        if (this.service  == null) {
            return "please set target service first";
        }
        System.out.println("======================Migration Copilot of Configure Resource======================:\n"+configureResourceAgent.configureResource(resource, this.service));
        return "Step(4) completed. Ask user whether need to configure another resource in the target service";
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
//                String issueMessage = detailNode.get("affectedFiles").get(0).get("description").toString();
                String issueMessage = detailNode.get("name").toString();
//                if (issueMessage.contains("Azure "))
                    issues.append(typeName+" -> "+issueMessage+"\n");
            }
        }
        return issues.toString();
    }

}
