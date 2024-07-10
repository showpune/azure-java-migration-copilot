@description('Provide the location for all the resources.')
param location string

@description('Provide the administrator login username for the flexible server.')
param administratorLogin string = 'migrationtool'

@description('Provide the administrator login password for the flexible server.')
@secure()
param administratorLoginPassword string = 'Password@123'

@description('The tier of the particular SKU. High availability mode is available only in the GeneralPurpose and MemoryOptimized SKUs.')
@allowed([
  'Burstable'
  'GeneralPurpose'
  'MemoryOptimized'
])
param serverEdition string = 'Burstable'

@description('Server version')
@allowed([
  '5.7'
  '8.0.21'
])
param version string = '8.0.21'

@description('The availability zone information for the server.')
param availabilityZone string = '1'

@description('High availability mode for a server: Disabled, SameZone, or ZoneRedundant.')
@allowed([
  'Disabled'
  'SameZone'
  'ZoneRedundant'
])
param haEnabled string = 'Disabled'

@description('The availability zone of the standby server.')
param standbyAvailabilityZone string = '2'

param storageSizeGB int = 20
param storageIops int = 360
@allowed([
  'Enabled'
  'Disabled'
])
param storageAutogrow string = 'Enabled'

@description('The name of the SKU, such as Standard_D32ds_v4.')
param skuName string = 'Standard_B1ms'

param backupRetentionDays int = 7
@allowed([
  'Disabled'
  'Enabled'
])
param geoRedundantBackup string = 'Disabled'

param serverName string = 'migrationdemo'
param databaseName string = 'petclinic'

resource server 'Microsoft.DBforMySQL/flexibleServers@2021-12-01-preview' = {
  location: location
  name: serverName
  sku: {
    name: skuName
    tier: serverEdition
  }
  properties: {
    version: version
    administratorLogin: administratorLogin
    administratorLoginPassword: administratorLoginPassword
    availabilityZone: availabilityZone
    highAvailability: {
      mode: haEnabled
      standbyAvailabilityZone: standbyAvailabilityZone
    }
    storage: {
      storageSizeGB: storageSizeGB
      iops: storageIops
      autoGrow: storageAutogrow
    }
    backup: {
      backupRetentionDays: backupRetentionDays
      geoRedundantBackup: geoRedundantBackup
    }
  }
}

resource firewallRule 'Microsoft.DBforMySQL/flexibleServers/firewallRules@2023-06-30' = {
  name: 'default'
  parent: server
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '255.255.255.255'
  }
}

resource database 'Microsoft.DBforMySQL/flexibleServers/databases@2021-12-01-preview' = {
  parent: server
  name: databaseName
  properties: {
    charset: 'utf8'
    collation: 'utf8_general_ci'
  }
}