package com.azure.migration.java.copilot.service.util;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class BicepParamMeta {
    String $schema;

    String contentVersion;

    Parameters parameters;
}

class Parameters {
    Map<String, String> environmentName = new HashMap<>();
    Map<String, String> location = new HashMap<>();
    Map<String, String> springPetclinicExists = new HashMap<>();
    SpringPetclinicDefinition springPetclinicDefinition;

}

class SpringPetclinicDefinition {
    Map<String, SettingItem[]> values = new HashMap<>();
}

class SettingItem {
    String name;
    String value;
    String _comment_name;
    String _comment_value;

    public SettingItem(String name, String value, String _comment_name, String _comment_value) {
        this.name = name;
        this.value = value;
        this._comment_name = _comment_name;
        this._comment_value = _comment_value;
    }
}

public class BicepGenerator {
    public static void genereateBicepPramFile(String path, MigrationContext migrationContext) {
        Map<String, String> bicepInfo = migrationContext.getInputInfo();
        BicepParamMeta bicepParamMeta = new BicepParamMeta();
        bicepParamMeta.$schema = "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#";
        bicepParamMeta.contentVersion = "1.0.0.0";
        Parameters parameters = new Parameters();
        parameters.environmentName.put("value", "${AZURE_ENV_NAME}");
        parameters.location.put("value", "${AZURE_LOCATION}");
        parameters.springPetclinicExists.put("value", "${SERVICE_SPRING_PETCLINIC_RESOURCE_EXISTS=false}");
        bicepParamMeta.parameters = parameters;

        String defaultCommentName = "The name of the environment variable when running in Azure. If empty, ignored.";
        String defaultCommentValue = "The value to provide. This can be a fixed literal, or an expression like ${VAR} "
            + "to use the value of 'VAR' from the current environment.";
        SettingItem[] settingItems = {
            new SettingItem("spring.datasource.url", (String)bicepInfo.get("DB_NAME"), defaultCommentName, defaultCommentValue),
            new SettingItem("spring.datasource.username", (String)bicepInfo.get("DB_USER_NAME"), defaultCommentName, defaultCommentValue),
            new SettingItem("spring.datasource.password", (String)bicepInfo.get("DB_PASSWORD"), defaultCommentName, defaultCommentValue)
        };
        SpringPetclinicDefinition springPetclinicDefinition = new SpringPetclinicDefinition();
        bicepParamMeta.parameters.springPetclinicDefinition = springPetclinicDefinition;
        springPetclinicDefinition.values.put("settings", settingItems);

        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(bicepParamMeta);
                Files.write(Paths.get("D:/tmp/testBicep.txt"), jsonStr.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
