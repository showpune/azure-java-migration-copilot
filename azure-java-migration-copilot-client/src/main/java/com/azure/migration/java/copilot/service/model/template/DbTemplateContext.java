package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;

public class DbTemplateContext {
    @Getter
    @Setter
    private boolean used;

    @Getter
    @Setter
    private String dbName;

    @Getter
    @Setter
    private String dbUser;

    @Getter
    @Setter
    private String dbPwd;
}
