package com.azure.migration.java.copilot.service.generate;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.constant.Constants;
import com.azure.migration.java.copilot.service.model.bicep.*;
import com.azure.migration.java.copilot.service.model.template.DbTemplateContext;
import com.azure.migration.java.copilot.service.model.template.EnvVariableTemplateContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.model.template.WorkloadTemplateContext;
import com.azure.migration.java.copilot.service.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class AzdConfigFilesGenerator {

    @Value("${copilot.bicep-tempalte-path}")
    String bicepTemplatePath;

    String defaultCommentName = "The name of the environment variable when running in Azure. If empty, ignored.";

    String defaultCommentValue = "The value to provide. This can be a fixed literal, or an expression like ${VAR} "
        + "to use the value of 'VAR' from the current environment.";;

    public void generateBicepFiles(String envName, MigrationContext migrationContext) throws Exception {
        copyBicepFiles(migrationContext.getSourceCodePath());
        generateBicepParamsFiles(migrationContext);
        generateAzdConfig(envName, migrationContext.getSourceCodePath());
    }

    public void generateBicepParamsFiles(MigrationContext migrationContext) throws IOException {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonStr = gson.toJson(this.assembleBicepParams(migrationContext.getTemplateContext()));
            Path filePath = Paths.get(migrationContext.getSourceCodePath(),"/infra", "/main.parameters.json");
            Files.write(filePath, jsonStr.getBytes());

    }

    public BicepParams assembleBicepParams(TemplateContext templateContext) {
        BicepParams bicepParams = new BicepParams();
        bicepParams.setSchema("https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#");
        bicepParams.setContentVersion("1.0.0.0");

        Parameters parameters = new Parameters();
        bicepParams.setParameters(parameters);
        parameters.getEnvironmentName().setValue("${AZURE_ENV_NAME}");
        parameters.getLocation().setValue("${AZURE_LOCATION}");
        parameters.getSpringPetclinicExists().setValue("${SERVICE_SPRING_PETCLINIC_RESOURCE_EXISTS=false}");
        parameters.getPrincipalId().setValue("${AZURE_PRINCIPAL_ID}");
        parameters.setMetadata(assembleMetadata(templateContext));
        parameters.setWorkload(assembleWorkload(templateContext.getWorkloadTemplateContext()));;
        List<SettingItem> settingItems = new ArrayList<>();
        assembleDbEnvParams(settingItems, templateContext.getDbTemplateContext());
        assembleEnvParams(settingItems, templateContext.getEnvironments());
        CommonItem<Settings> springPetclinicDefinition = new CommonItem<>();
        springPetclinicDefinition.setValue(new Settings());
        springPetclinicDefinition.getValue().setSettings(settingItems);
        parameters.setSpringPetclinicDefinition(springPetclinicDefinition);

        return bicepParams;
    }

    private CommonItem assembleWorkload(WorkloadTemplateContext workloadTemplateContext) {
        CommonItem<WorkloadItem> commonItem = new CommonItem<>();
        WorkloadItem workloadItem = new WorkloadItem();
        commonItem.setValue(workloadItem);
        workloadItem.setInstanceCount(workloadTemplateContext.getInstanceCount());
        workloadItem.setCpu(workloadTemplateContext.getCpu());
        workloadItem.setMemory(workloadTemplateContext.getMemory());
        return commonItem;
    }

    private CommonItem assembleMetadata(TemplateContext templateContext) {
        CommonItem<MetadataItem> commonItem = new CommonItem<>();
        MetadataItem resourceItem = new MetadataItem();
        commonItem.setValue(resourceItem);

        resourceItem.setAppName(templateContext.getAppName());

        AcaItem acaItem = new AcaItem();
        resourceItem.setAca(acaItem);
        acaItem.setName("${AZURE_ENV_NAME}");

        DbItem db = new DbItem();
        resourceItem.setDb(db);
        // TODO: should consider what to set here
//        db.setName(templateContext.getDbTemplateContext().getHost());

        PersistentStorageItem persistent = new PersistentStorageItem();
        resourceItem.setPersistent(persistent);
        persistent.setMountPath(templateContext.getPersistentStorageTemplateContext().getMountPath());
        persistent.setMountOptions(templateContext.getPersistentStorageTemplateContext().getMountOptions());
        persistent.setFileShare(templateContext.getPersistentStorageTemplateContext().getFileShare());
        persistent.setRequired(templateContext.getPersistentStorageTemplateContext().isRequired());
        persistent.setResourceGroup(templateContext.getPersistentStorageTemplateContext().getResourceGroup());

        return commonItem;
    }

    private void assembleEnvParams(List<SettingItem> settingItems, List<EnvVariableTemplateContext> envsList) {
        for(EnvVariableTemplateContext item : envsList) {
            settingItems.add(new SettingItem(item.getKey(), item.getValue(), false, defaultCommentName, defaultCommentValue));
        }
    }

    @NotNull
    private List<SettingItem> assembleDbEnvParams(List<SettingItem> settingItems, DbTemplateContext dbTemplateContext) {
        settingItems.add(new SettingItem(Constants.SPRING_DATASOURCE_URL, dbTemplateContext.getConnectionString(), null, defaultCommentName, defaultCommentValue));
        settingItems.add(new SettingItem(Constants.SPRING_DATASOURCE_USERNAME, dbTemplateContext.getUsername(), true, defaultCommentName, defaultCommentValue));
        settingItems.add(new SettingItem(Constants.SPRING_DATASOURCE_PASSWORD, dbTemplateContext.getPassword(), true, defaultCommentName, defaultCommentValue));
        return settingItems;
    }

    public void copyBicepFiles(String targetPath) throws IOException {

        //create directories
        FileUtil.createFiles(bicepTemplatePath, targetPath, true);

        //create files
        FileUtil.createFiles(bicepTemplatePath, targetPath, false);
    }


    private void generateAzdConfig(String envName, String targetPath) throws IOException {
        Path envDir = Path.of(targetPath, ".azure", envName);
        if (Files.exists(envDir)) {
            return;
        }

        Files.createDirectories(envDir);

        String envContent = "AZURE_ENV_NAME=%s".formatted(envName);

        Files.writeString(Paths.get(envDir.toString(), ".env"), envContent);
    }
}
