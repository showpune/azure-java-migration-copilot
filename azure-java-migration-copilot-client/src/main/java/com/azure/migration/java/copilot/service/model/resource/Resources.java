package com.azure.migration.java.copilot.service.model.resource;

import com.azure.migration.java.copilot.service.model.template.EnvVariableTemplateContext;
import com.azure.migration.java.copilot.service.model.template.TemplateContext;
import com.azure.migration.java.copilot.service.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Resources {

    @Getter
    @JsonPropertyDescription("the category list of resources")
    private final List<ResourceCategory> categories = new ArrayList<>();

    public ResourceCategory nameOf(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name of category cannot be null");
        }
        Optional<ResourceCategory> resourceCategoryOptional = this.categories.
                stream().
                filter(category -> name.equalsIgnoreCase(category.getName())).
                findFirst();

        if (resourceCategoryOptional.isEmpty()) {
           ResourceCategory category = new ResourceCategory();
           category.setName(name);
           this.categories.add(category);
           return category;
        }

        return resourceCategoryOptional.get();
    }

    public void addCategory(ResourceCategory category) {
        this.categories.add(category);
    }

    public static Resources create() {
        ObjectNode schema = JsonUtil.schemaOf(TemplateContext.class);
        Resources resources = new Resources();
        JsonNode schemaProps = schema.get("properties");
        ResourceCategory category = null;
        for (Iterator<String> it = schemaProps.fieldNames(); it.hasNext(); ) {
            String name = it.next();
            JsonNode node = schemaProps.get(name);
            String type = node.get("type").asText();
            String description = node.get("description").asText();
            switch (type) {
                case "string":
                    category = resources.nameOf("Basic Information");
                    category.setDescription("");
                    ResourceProperty property = new ResourceProperty();
                    property.setName(name);
                    property.setDescription(description);
                    category.addProperty(property);
                    break;
                case "object":
                    category = resources.nameOf(name);
                    category.setDescription(description);
                    JsonNode categoryProps = node.get("properties");
                    for (Iterator<String> itt = categoryProps.fieldNames(); itt.hasNext(); ) {
                        String propName = itt.next();
                        JsonNode propNode = categoryProps.get(propName);
                        String propDesc = propNode.get("description").asText();
                        ResourceProperty p = new ResourceProperty();
                        p.setName(propName);
                        p.setDescription(propDesc);
                        category.addProperty(p);
                    }
                    break;
                case "array":
                    category = resources.nameOf(name);
                    category.setDescription(description);
                    break;
                default:
                    throw new IllegalStateException("Unrecognized JsonNode type " + type);
            }

        }
        return resources;
    }

    public TemplateContext toContext() {
        TemplateContext templateContext = new TemplateContext();

        for (ResourceCategory resourceCategory : this.categories) {
            switch (resourceCategory.getName()) {
                case "Basic Information":
                    resourceCategory.apply(templateContext);
                    break;
                case "database":
                    resourceCategory.apply(templateContext.getDbTemplateContext());
                    break;
                case "persistent":
                    resourceCategory.apply(templateContext.getPersistentStorageTemplateContext());
                    break;
                case "workload":
                    resourceCategory.apply(templateContext.getWorkloadTemplateContext());
                    break;
                case "environments":
                    for (ResourceProperty p : resourceCategory.getProperties()) {
                        EnvVariableTemplateContext env = new EnvVariableTemplateContext();
                        env.setKey(p.getName());
                        env.setValue(p.getValue());
                        templateContext.getEnvironments().add(env);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized category: " + resourceCategory.getName());
            }
        }

        return templateContext;
    }
}
