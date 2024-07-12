package com.azure.migration.java.copilot.service.model.resource;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

public class ResourceProperty {


    @Getter
    @Setter
    @JsonPropertyDescription("the property name")
    private String name;

    @Getter
    @Setter
    @JsonPropertyDescription("the current value")
    @JsonRawValue
    private String value;

    @Getter
    @Setter
    @JsonPropertyDescription("the description of this property")
    private String description;

    @Getter
    @Setter
    @JsonPropertyDescription("the source of detection for this property")
    private String sourceOfDetection;

    public void merge(ResourceProperty from) {
        this.value = from.getValue();
        this.description = from.getDescription();
        this.sourceOfDetection = from.getSourceOfDetection();
    }
}
