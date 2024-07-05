package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;
import lombok.Setter;

public class DbTemplateContext {
    @Getter
    @Setter
    private boolean used;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private String schema;

    @Getter
    @Setter
    private String user;

    @Getter
    @Setter
    private String pwd;
}
