package com.azure.migration.java.copilot.service.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Data
public class RecommendedServices {

    private List<RecommendedService> recommendations;

    public Optional<RecommendedService> indexOf(String selected) {
        if (!StringUtils.hasText(selected)) {
            return Optional.empty();
        }
        for (RecommendedService service: recommendations) {
            if (selected.equalsIgnoreCase(service.format())) {
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }

    public List<String> formatToList() {
        return recommendations.stream().map(RecommendedService::format).toList();
    }
}
