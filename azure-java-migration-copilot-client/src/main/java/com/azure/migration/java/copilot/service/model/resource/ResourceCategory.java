package com.azure.migration.java.copilot.service.model.resource;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceCategory {

    @Getter
    @Setter
    @JsonPropertyDescription("the category name")
    private String name;

    @Getter
    @Setter
    @JsonPropertyDescription("the description of category")
    private String description;

    @Getter
    @JsonPropertyDescription("the properties of this category")
    private final List<ResourceProperty> properties = new ArrayList<>();

    public void addProperty(ResourceProperty property) {
        if (property == null || property.getName() == null) {
            throw new IllegalArgumentException("property or property name cannot be null");
        }
        Optional<ResourceProperty> propertyOptional = properties.stream().filter(p -> p.getName().equalsIgnoreCase(property.getName())).findFirst();
        if (propertyOptional.isEmpty()) {
            this.properties.add(property);
            return;
        }

        propertyOptional.get().merge(property);
    }

    public void apply(Object object) {
        this.properties.forEach(p -> {
            try {
                Field field = object.getClass().getDeclaredField(p.getName());
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)) {
                    field.set(object, Integer.valueOf(p.getValue()));
                } else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class)) {
                    field.set(object, Boolean.valueOf(p.getValue()));
                } else {
                    field.set(object, p.getValue());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
