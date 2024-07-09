targetScope = 'subscription'

param serverName string
param resourceGroupName string

resource server 'Microsoft.DBforMySQL/flexibleServers@2021-12-01-preview' existing = {
  scope: resourceGroup(resourceGroupName)
  name: serverName
}
