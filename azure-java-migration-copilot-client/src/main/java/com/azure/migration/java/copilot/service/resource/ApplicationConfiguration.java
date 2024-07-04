package com.azure.migration.java.copilot.service.resource;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@JsonClassDescription("The Application configuration")
public class ApplicationConfiguration {

    @JsonPropertyDescription("Application Name")
    public String appName;

    @JsonPropertyDescription("the memory of the application")
    public String memory;

    @JsonPropertyDescription("the instance number of the application")
    public int instanceCount;

    @JsonPropertyDescription("List of environment variables")
    public List<EnvConfiguration> envConfigurations;

    public static String jsonSchema;
    static {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                .with(new JacksonModule())
                .with(Option.FIELDS_DERIVED_FROM_ARGUMENTFREE_METHODS, Option.NONSTATIC_NONVOID_NONGETTER_METHODS);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        ObjectNode jsonSchemaNode = generator.generateSchema(ApplicationConfiguration.class);
        jsonSchema = jsonSchemaNode.toPrettyString();
    }

    public String jsonObject() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public void resetByString(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApplicationConfiguration configuration = objectMapper.readValue(json, ApplicationConfiguration.class);
            this.appName = configuration.appName;
            this.memory = configuration.memory;
            this.instanceCount = configuration.instanceCount;
            this.envConfigurations = configuration.envConfigurations;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}



@JsonClassDescription("A key-value pair of environment variable")
class EnvConfiguration{

    @JsonPropertyDescription("The key of the environment variable")
    public String key;

    @JsonPropertyDescription("The value of the environment variable")
    public String value;
}


