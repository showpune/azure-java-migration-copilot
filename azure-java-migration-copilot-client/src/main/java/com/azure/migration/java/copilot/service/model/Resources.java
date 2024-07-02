package com.azure.migration.java.copilot.service.model;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

@Data
public class Resources {

    private List<Resource> resources;

    public List<String> formatToList(boolean usedOnly) {
        return resources.stream().filter(resource -> {
            if (usedOnly) {
                return resource.isUsed();
            }
            return true;
        }).map(Resource::format).toList();
    }
}
