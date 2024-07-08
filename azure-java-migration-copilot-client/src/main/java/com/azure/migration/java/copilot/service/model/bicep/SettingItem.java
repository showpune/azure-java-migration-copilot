package com.azure.migration.java.copilot.service.model.bicep;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class SettingItem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean secret;

    @Getter
    @Setter
    @SerializedName("_comment_name")
    private String commentName;

    @Getter
    @Setter
    @SerializedName("_comment_value")
    private String commentValue;

    public SettingItem(String name, String value, Boolean secret, String _comment_name, String _comment_value) {
        this.name = name;
        this.value = value;
        this.secret = secret;
        this.commentName = _comment_name;
        this.commentValue = _comment_value;
    }

}
