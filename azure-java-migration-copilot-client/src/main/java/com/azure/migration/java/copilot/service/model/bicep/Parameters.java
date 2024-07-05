package com.azure.migration.java.copilot.service.model.bicep;


import lombok.Getter;
import lombok.Setter;

public class Parameters {

    @Getter
    @Setter
    private CommonItem environmentName = new CommonItem();

    @Getter
    @Setter
    private CommonItem location = new CommonItem();

    @Getter
    @Setter
    private CommonItem springPetclinicExists = new CommonItem();

    @Getter
    @Setter
    private CommonItem<Settings> springPetclinicDefinition = new CommonItem();

    @Getter
    @Setter
    private CommonItem principalId = new CommonItem();

}
