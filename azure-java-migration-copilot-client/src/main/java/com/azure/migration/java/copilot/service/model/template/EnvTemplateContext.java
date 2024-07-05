package com.azure.migration.java.copilot.service.model.template;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class EnvTemplateContext {
    @Getter
    private Map<String, String> envMap = new HashMap<>();
}
