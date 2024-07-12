package com.azure.migration.java.copilot.service.resource;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@JsonClassDescription("The Application configuration")
public class ApplicationConfiguration {

    private static final String META_SUFFIX = "";

    @JsonPropertyDescription("Application Name")
    public String appName;

    @JsonPropertyDescription("the memory of the application, default is 2G")
    public String memory;

    @JsonPropertyDescription("the CPU of the application, the minimun value is 0.5 and maximun value is 4,  if memory is 1G then cpu is 0.5, if memory is 2G then cpu is 1 and so on. ")
    public double cpu;

    @JsonPropertyDescription("the instance number of the application, default is 1")
    public int instanceCount;

    @JsonPropertyDescription("The connection string of the database")
    public String databaseConnectionString;

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

    public Map<String,String> asMap() throws JsonProcessingException {
        Map<String,String> map= new HashMap<>();
        if(Strings.isNotEmpty(appName)){
            map.put(META_SUFFIX+"APP_NAME",appName);
        }
        if(Strings.isNotEmpty(memory)){
            map.put(META_SUFFIX+"APP_MEMORY",memory);
        }

        if(Strings.isNotEmpty(databaseConnectionString)){
            map.put("DATABASE_CONNECTION_STRING",databaseConnectionString);
        }
        if(instanceCount!=0){
            map.put(META_SUFFIX+"APP_INSTANCES",instanceCount+"");
        }

        if(cpu!=0){
            map.put(META_SUFFIX+"APP_CPU",cpu+"");
        }


        if(envConfigurations!=null){
            for(EnvConfiguration envConfiguration:envConfigurations){
                map.put("ENV_"+envConfiguration.key,envConfiguration.value);
            }
        }
        return map;
    }
}



@JsonClassDescription("A key-value pair of environment variable")
class EnvConfiguration{

    @JsonPropertyDescription("The key of the environment variable")
    public String key;

    @JsonPropertyDescription("The value of the environment variable")
    public String value;
}


