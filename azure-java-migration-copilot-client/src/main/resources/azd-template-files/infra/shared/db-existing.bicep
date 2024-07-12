param serverName string

resource server 'Microsoft.DBforMySQL/flexibleServers@2021-12-01-preview' existing = {
  name: serverName
}

output dbId string = server.id
