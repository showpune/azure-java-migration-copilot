package com.azure.migration.java.copilot.service.model.bicep;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class BicepParams {

    @Getter
    @Setter
    @SerializedName("$schema")
    private String schema;

    @Getter
    @Setter
    private String contentVersion;

    @Getter
    @Setter
    private Parameters parameters;
}
