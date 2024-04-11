az deployment group create --resource-group apmdemo-rg --template-file myflex.bicep 
  --parameters serverName=empsqldbdev adminUsername=<user>> adminPassword=<pwd>>


CREATE DATABASE IF NOT EXISTS employees;




CREATE TABLE IF NOT EXISTS employees (
    employeeID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(100) NOT NULL,
    startDate DATE,
    endDate DATE
);

