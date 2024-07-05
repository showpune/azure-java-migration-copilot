package com.azure.migration.java.copilot.service.model.bicep;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    @Getter
    @Setter
    List<SettingItem> settings = new ArrayList<>();

}
