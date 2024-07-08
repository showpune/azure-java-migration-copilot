param name string
param location string = resourceGroup().location
param tags object = {}

param logAnalyticsWorkspaceName string
param applicationInsightsName string = ''
param storageAccountName string = ''
param storageAccountShareName string = ''
param storageAccountKey string = ''

resource containerAppsEnvironment 'Microsoft.App/managedEnvironments@2022-10-01' = {
  name: name
  location: location
  tags: tags
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalyticsWorkspace.properties.customerId
        sharedKey: logAnalyticsWorkspace.listKeys().primarySharedKey
      }
    }
    daprAIConnectionString: applicationInsights.properties.ConnectionString
  }
}

resource logAnalyticsWorkspace 'Microsoft.OperationalInsights/workspaces@2022-10-01' existing = {
  name: logAnalyticsWorkspaceName
}

resource applicationInsights 'Microsoft.Insights/components@2020-02-02' existing = {
  name: applicationInsightsName
}

resource azurefileForEnv 'Microsoft.App/managedEnvironments/storages@2023-05-01' = {
  parent: containerAppsEnvironment
  name: 'test'
  properties: {
    azureFile: {
      accountName: storageAccountName
      shareName: storageAccountShareName
      accountKey: storageAccountKey
      accessMode: 'ReadWrite'
    }
  }
}

output name string = containerAppsEnvironment.name
output domain string = containerAppsEnvironment.properties.defaultDomain
