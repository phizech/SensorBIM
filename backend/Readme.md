## create db user:

CREATE USER 'sensorBIMuser'@'localhost' IDENTIFIED BY 'xA2W=6sw`9JY@v.<';

create database sensorBIMManagement;

grant all privileges on sensorBIMManagement.* to 'sensorBIMuser'@'localhost';

