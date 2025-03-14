# Ghost Net Fishing - Fallstudie
Repository für die Fallstudie "Ghost Net Fishing"
Autor: Colin Sanne
Kurs: IPWA02-01   
Studiengang: B. Sc. Softwareentwicklung
Tutor: Alexander Christopher Bock

## Anwendungs- und Maven-Versionen

| Anwendung     | Version    |
|---------------|------------|
| IntelliJ IDEA | 2024.3.2.2 |
| Tomcat        | 10.1.28    |
| MySQL         | 9.2        |

**Maven** Versionen

| Paket                      | Version     |
|----------------------------|-------------|
| maven-war-plugin           | 3.3.1       |
| jakarta.servlet-api        | 5.0.0       |
| jakarta.faces              | 4.0.2       |
| jakarta.inject-api         | 2.0.0       |
| jakarta.enterprise.cdi-api | 3.0.0       |
| weld-servlet-core          | 4.0.2.Final |
| primefaces                 | 12.0.0      |
| hibernate-core             | 6.5.2.Final |
| mysql-connector-java       | 8.0.33      |


## Einrichtung der Datenbank
Um dieses Projekt ausführen zu können, wird eine MySQL 9.2-Datenbank benötigt. 
Die Anbindung erfolgt über die Datei persistence.xml mit den folgenden Zugangsdaten:

| Name      | Value             |
|-----------|-------------------|
| User      | root              |
| Passwort  | Sql3103           |
| Datenbank | ghost_net_fishing |

Die Datenbank kann manuell erstellt und mit dem bereitgestellten Dump-File importiert werden. (ghost_net_fishing_dump.sql)
Falls MySQL bereits installiert ist, öffnen Sie die Eingabeaufforderung (cmd) und erstellen Sie die Datenbank mit folgendem Befehl:

mysql -u root -p

Geben Sie das Passwort "Sql3103" ein und erstellen Sie die Datenbank mit:

CREATE DATABASE ghost_net_fishing;
ALTER DATABASE ghost_net_fishing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Falls die Dump-Datei ghost_net_fishing_dump.sql importiert werden soll, navigieren Sie im Terminal in den Ordner, in dem sich die Datei befindet. Falls die Datei auf dem Desktop liegt, verwenden Sie:

cd C:\Users\DEIN-BENUTZERNAME\Desktop

Dann führen Sie folgenden Befehl aus:

mysql -u root -p ghost_net_fishing < ghost_net_fishing_dump.sql

Falls Sie PowerShell nutzen:

Get-Content ghost_net_fishing_dump.sql | mysql -u root -p ghost_net_fishing

## Anbindung der Datenbank
Nach der erfolgreichen Einrichtung kann die Datenbank in IntelliJ angebunden werden. Dazu in IntelliJ unter View > Tool Windows > Database eine neue Verbindung erstellen. 
Als Datenquelle MySQL wählen und folgende Verbindungsdaten eintragen:

| Name     | Value             |
|----------|-------------------|
| Host     | localhost         |
| Port     | 3306              |
| User     | root              |
| Passwort | Sql3103           |
| Database | ghost_net_fishing |

Nach einem erfolgreichen Verbindungstest kann die Datenbank gespeichert und verwendet werden.

