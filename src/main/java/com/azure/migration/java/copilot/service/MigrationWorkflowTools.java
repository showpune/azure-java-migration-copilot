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
    private ServiceAnalysisAgent serviceAnalysisAgent;

    @Autowired
    private ConfigureResourceAgent configureResourceAgent;

    @Tool("Recommand the target service the application can be migrated to")
    public String recommendTargeService() throws IOException {
        if (reportUrl == null) {
            return "please give the report path first";
        }
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        //  System.out.println("======================Migration Copilot of Report Analysis======================:\n"+serviceAnalysisAgent.chooseService(content));
        return "Success";
    }

    @Tool("List all the resources used in the application according to the report")
    public String listResources() throws IOException {
        if (reportUrl == null) {
            return "please give the report url first";
        }
        String content = "Technologies: \n"+ getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        System.out.println("======================Migration Copilot of Report Analysis======================:\n"+serviceAnalysisAgent.listResources(content));
        return "Success";
    }

    @Tool("Answer other question about the about report")
    public String otherQuestion(String question) throws IOException {
        if (reportUrl == null) {
            return "please give the report url first";
        }
        String content = "Technologies: \n" + getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        System.out.println("======================Migration Copilot of Report Analysis======================:\n"+serviceAnalysisAgent.chat(content));
        return "Success";
    }

    @Tool("Configure the given resource in the service")
    public String configureResources(String resource, String service) throws IOException {
        if (service != null) {
            this.service = service;
        }
        if (this.service  == null) {
            return "please give the set the service first";
        }
        System.out.println("======================Migration Copilot of Configure Resource======================:\n"+configureResourceAgent.configureResource(resource, this.service));
        return "Success";
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
