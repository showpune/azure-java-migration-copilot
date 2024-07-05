package com.azure.migration.java.copilot.service.util;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.constant.Constants;
import com.azure.migration.java.copilot.service.model.bicep.*;
import com.azure.migration.java.copilot.service.model.template.DbTemplateContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzdConfigFilesGenerator {

    @Autowired
    private MigrationContext migrationContext;

    public void genereateBicepParamsFsile() {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonStr = gson.toJson(this.assembleBicepParmas());
            Files.write(Paths.get("D:/tmp/testBicep.txt"), jsonStr.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BicepParams assembleBicepParmas() {
        BicepParams bicepParams = new BicepParams();
        bicepParams.setSchema("https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#");
        bicepParams.setContentVersion("1.0.0.0");
        Parameters parameters = new Parameters();
        bicepParams.setParameters(parameters);
        parameters.getEnvironmentName().setValue("${AZURE_ENV_NAME}");
        parameters.getLocation().setValue("${AZURE_LOCATION}");
        parameters.getSpringPetclinicExists().setValue("${SERVICE_SPRING_PETCLINIC_RESOURCE_EXISTS=false}");
        parameters.getPrincipalId().setValue("${AZURE_PRINCIPAL_ID}");

        String defaultCommentName = "The name of the environment variable when running in Azure. If empty, ignored.";
        String defaultCommentValue = "The value to provide. This can be a fixed literal, or an expression like ${VAR} "
                + "to use the value of 'VAR' from the current environment.";
        TemplateContext templdateContext = migrationContext.getTemplateContext();
        DbTemplateContext dbTemplateContext = templdateContext.getDbTemplateContext();
        dbTemplateContext.setType("mysql");
        dbTemplateContext.setName("mysql-szcza4m2d5pkk");
        dbTemplateContext.setPort(3306);
        dbTemplateContext.setSchema("petclinic");
        dbTemplateContext.setUser("migrationtool");
        dbTemplateContext.setPwd("Password@123");
        String dbUrl = Constants.JDBC + Constants.COLON
            + dbTemplateContext.getType() + Constants.COLON + Constants.DOUBLE_SLASH
            + dbTemplateContext.getName() + Constants.Azure_MYSQL_DOMAIN_SUFFIX + Constants.COLON
            + dbTemplateContext.getPort() + Constants.SLASH
            + dbTemplateContext.getSchema() + Constants.Azure_MYSQL_CONN_STRING_SUFFIX;
        List<SettingItem> settingItems = new ArrayList<>() {{
            add(new SettingItem(Constants.SPRING_DATASOURCE_URL, dbUrl, null, defaultCommentName, defaultCommentValue));
            add(new SettingItem(Constants.SPRING_DATASOURCE_USERNAME, dbTemplateContext.getUser(), true, defaultCommentName, defaultCommentValue));
            add(new SettingItem(Constants.SPRING_DATASOURCE_PASSWORD, dbTemplateContext.getPwd(), true, defaultCommentName, defaultCommentValue));
        }};
        CommonItem<Settings> springPetclinicDefinition = new CommonItem<>();
        springPetclinicDefinition.setValue(new Settings());
        springPetclinicDefinition.getValue().setSettings(settingItems);
        parameters.setSpringPetclinicDefinition(springPetclinicDefinition);

        return bicepParams;
    }
}
