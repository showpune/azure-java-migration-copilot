package com.azure.migration.java.copilot.service.generate;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.constant.Constants;
import com.azure.migration.java.copilot.service.model.bicep.*;
import com.azure.migration.java.copilot.service.model.template.DbTemplateContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzdConfigFilesGenerator {

    @Value("${copilot.bicpe.tempalte.path}")
    String bicepTemplatePath;

    @Autowired
    ResourceLoader resourceLoader;

    public void generateBicepFiles(MigrationContext migrationContext) throws Exception {
        copyBicepFiles(migrationContext.getSourceCodePath());
        generateBicepParamsFiles(migrationContext);
    }

    public void generateBicepParamsFiles(MigrationContext migrationContext) throws IOException {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonStr = gson.toJson(this.assembleBicepParmas(migrationContext.getTemplateContext()));
            Path filePath = Paths.get(migrationContext.getSourceCodePath(),"/infra", "/main.parameters.json");
            Files.write(filePath, jsonStr.getBytes());

    }

    public BicepParams assembleBicepParmas(TemplateContext templateContext) {
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
        DbTemplateContext dbTemplateContext = templateContext.getDbTemplateContext();
        String dbName = dbTemplateContext.getName();
        parameters.getDbName().setValue(dbName);
        String dbUrl = Constants.JDBC + Constants.COLON
            + dbTemplateContext.getType() + Constants.COLON + Constants.DOUBLE_SLASH
            + dbName + Constants.Azure_MYSQL_DOMAIN_SUFFIX + Constants.COLON
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

    public void copyBicepFiles(String targetPath) throws IOException {
        // Define the source and destination directories
        Resource resource = resourceLoader.getResource(bicepTemplatePath);
        File file = resource.getFile();
        FileUtil.copyFiles(file.getAbsolutePath(), targetPath);
    }
}
