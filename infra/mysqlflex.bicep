param resourceGroupName string
param serverName string
param adminUsername string
param adminPassword string

resource myVnet 'Microsoft.Network/virtualNetworks@2021-02-01' = {
  name: 'flexdb-vnet'
  location: 'eastus2'
  properties: {
    addressSpace: {
      addressPrefixes: [
        '10.0.0.0/16'
      ]
    }
  }
}

resource mySubnet 'Microsoft.Network/virtualNetworks/subnets@2021-02-01' = {
  parent: myVnet
  name: 'dbsubnet1'
  properties: {
    addressPrefix: '10.0.0.0/24'
  }
}

resource mySqlServer 'Microsoft.DBforMySQL/flexibleServers@2023-12-30' = {
  name: serverName
  location: 'eastus2'
  properties: {
    administratorLogin: adminUsername
    administratorLoginPassword: adminPassword
    sku: {
      name: 'Standard_B1ms'
      tier: 'Burstable'
    }
    storageProfile: {
      storageMB: 51200
    }
    publicNetworkAccess: 'Enabled'
    subnetId: mySubnet.id
  }
}

output serverInfo string = mySqlServer.id
