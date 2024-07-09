package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

public class CommonItem<T> {

    @Getter
    @Setter
    private T value;

}
