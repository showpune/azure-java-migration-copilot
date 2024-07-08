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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Component
public class AzdConfigFilesGenerator {

    @Autowired
    private MigrationContext migrationContext;

    public void genereateBicepParamsFiles() {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonStr = gson.toJson(this.assembleBicepParmas());
            String filePath = migrationContext.getSourceCodePath() + "\\infra\\main.parameters.json";
            Files.write(Paths.get(filePath), jsonStr.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BicepParams assembleBicepParmas() {
        BicepParams bicepParams = new BicepParams();
        bicepParams.setSchema("https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#");
        bicepParams.setContentVersion("1.0.0.0");
        String dbName = "migrationdemosql";
        Parameters parameters = new Parameters();
        bicepParams.setParameters(parameters);
        parameters.getEnvironmentName().setValue("${AZURE_ENV_NAME}");
        parameters.getLocation().setValue("${AZURE_LOCATION}");
        parameters.getSpringPetclinicExists().setValue("${SERVICE_SPRING_PETCLINIC_RESOURCE_EXISTS=false}");
        parameters.getPrincipalId().setValue("${AZURE_PRINCIPAL_ID}");
        parameters.getDbName().setValue(dbName);

        String defaultCommentName = "The name of the environment variable when running in Azure. If empty, ignored.";
        String defaultCommentValue = "The value to provide. This can be a fixed literal, or an expression like ${VAR} "
                + "to use the value of 'VAR' from the current environment.";
        TemplateContext templdateContext = migrationContext.getTemplateContext();
        DbTemplateContext dbTemplateContext = templdateContext.getDbTemplateContext();
        dbTemplateContext.setType("mysql");
        dbTemplateContext.setName(dbName);
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

    public void copyBicepFiles() {
        // Define the source and destination directories
        String sourcePath = "D:\\code\\sourceCode\\migrationDemo\\azure-java-migration-copilot\\azure-java-migration-copilot-client\\src\\main\\resources\\azd-template-fils";
        String targetPath = migrationContext.getSourceCodePath();
        FileUtil.copyFiles(sourcePath, targetPath);
    }
}
