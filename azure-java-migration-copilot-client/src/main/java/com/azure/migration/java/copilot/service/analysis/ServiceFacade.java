package com.azure.migration.java.copilot.service.analysis;

import com.azure.migration.java.copilot.service.MigrationContext;
import com.azure.migration.java.copilot.service.source.AppCatTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class ServiceFacade {

    @Autowired
    private ServiceAnalysisAgent serviceAnalysisAgent;

    @Autowired
    private AppCatTools appCatTools;

    @Autowired
    private MigrationContext migrationContext;

    public String recommendService() throws IOException {
        if (migrationContext.getWindupReportPath() == null) {
            throw new IllegalArgumentException("AppCat report path should not be null");
        }
        String content = "Applications: \n" + appCatTools.getApplications();
        return serviceAnalysisAgent.chooseService(content);
    }

    public String chooseService(String userMessage) throws IOException {
        return serviceAnalysisAgent.chooseService(userMessage);
    }

    public boolean isServiceSet() {
        return StringUtils.hasText(migrationContext.getService());
    }
}
