package com.azure.migration.java.copilot.service.model.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class SourceOfDetection {

    @Getter
    @Setter
    @JsonPropertyDescription("the property name")
    private String property;

    @Getter
    @Setter
    @JsonPropertyDescription("the source of detection")
    private String source;

    @Override
    @JsonIgnore
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceOfDetection that = (SourceOfDetection) o;
        return Objects.equals(property, that.property);
    }

    @Override
    @JsonIgnore
    public int hashCode() {
        return Objects.hashCode(property);
    }
}
