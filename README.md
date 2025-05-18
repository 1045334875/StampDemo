
Database  
```
create database demo;
use demo;
-- 创建用户表
CREATE TABLE Users (
    userId VARCHAR(255) PRIMARY KEY,
    userName VARCHAR(255));-- 创建签章表CREATE TABLE Stamps (
    stampId VARCHAR(255) PRIMARY KEY,
    userId VARCHAR(255),
    style ENUM('OFFICIAL', 'SPECIAL', 'OVAL', 'SQUARE'),
    color ENUM('RED', 'BLUE', 'BLACK'),
    wrapText TEXT,
    horizonText TEXT,
    handwritten TEXT,
    stampImage TEXT,
    isDefault BOOLEAN,FOREIGN KEY (userId) REFERENCES Users(userId));
    -- 创建文档表
    CREATE TABLE Documents (
    documentId VARCHAR(255) PRIMARY KEY,
    userId VARCHAR(255),
    documentName VARCHAR(255),
    documentFile LONGBLOB,FOREIGN KEY (userId) REFERENCES Users(userId));
    -- 创建签章位置表
    CREATE TABLE StampPositions (
    positionId INT AUTO_INCREMENT PRIMARY KEY,
    documentId VARCHAR(255),
    stampId VARCHAR(255),
    page INT,
    x FLOAT,
    y FLOAT,FOREIGN KEY (documentId) REFERENCES Documents(documentId),FOREIGN KEY (stampId) REFERENCES Stamps(stampId));
    -- 创建骑缝配置表
    CREATE TABLE SeamConfigurations (
    seamConfigId INT AUTO_INCREMENT PRIMARY KEY,
    documentId VARCHAR(255),
    seamType ENUM('cross-page', 'single-page'),
    crossPages JSON,FOREIGN KEY (documentId) REFERENCES Documents(documentId));

```
