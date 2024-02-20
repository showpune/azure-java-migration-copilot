package com.azure.migration.java.copilot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Data
public class MigrationWorkflow {


    public final static String COMMAND_SELECT_REPORT = "report path:";

    public final static String COMMAND_SET_SERVICE = "set service:";

    public final static String COMMAND_LIST_RESOURCES = "list resources";

    public final static String COMMAND_CONFIG_RESOURCES = "config resource:";

    public String getAvaliableCommand(){
        List<String> list = new ArrayList<String>();
        list.add(COMMAND_SELECT_REPORT);
        if(reportUrl!=null){
            list.add(COMMAND_SET_SERVICE);
            list.add(COMMAND_LIST_RESOURCES);
        }
        if(service!=null){
            list.add(COMMAND_CONFIG_RESOURCES);
        }
        StringBuilder result = new StringBuilder();
        for(String command:list){
            if(command.endsWith(":")){
                result.append(command+"<input parameters>\n");
            }else{
                result.append(command+"\n");
            }
        }
        return "possible command: \n"+result;
    }

    private String reportUrl;

    private String service;

    @Autowired
    private ChooseTargetServiceAgent agent;

    public String chooseService() throws IOException {
        Path path2 = Paths.get("api/applications.json");
        String content = new String(Files.readAllBytes(Paths.get(reportUrl).resolve(path2)));
        return agent.chooseService(content);
    }

    public String listResources() throws IOException {
        String content = "Technologies: \n"+ getTechnologiesSummary() + "\n\n Issues:\n" + getIssuesSummary() + "\n\n Dependencies:\n" + getDependenciesSummary();
        return agent.listResources(content);
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
