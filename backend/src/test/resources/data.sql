--------------------------------------------------------USER--------------------------------------------------------

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (1, 'steverogers@somemail.com', 'Steve', 'Rogers',
        '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 0, 'steverogers');

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (2, 'hulk@gmail.com', 'Hulk', 'Hulky', '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 1,
        'hulk');

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (3, 'tonystark@gmail.com', 'Tony', 'Stark', '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 1,
        'tonystark');

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (4, 'peterparker@gmail.com', 'Peter', 'Parker', '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK',
        1, 'peterparker');

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (5, 'natasharomanoff@gmail.com', 'Natasha', 'Romanoff',
        '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 1, 'natasharomanoff');

INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (6, 'wanda@gmail.com', 'Wanda', 'Av', '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 1,
        'wanda');

-- TODO: create new test email
INSERT INTO user(id, email, first_name, last_name, password, user_role, username)
VALUES (7, 'melanie.ernst@student.uibk.ac.at', 'Sensor', 'BIM',
        '$2a$10$.jUeVePwb1mQ8HYxOIhPoOwmP74Fv8CzlCCjg/qQl1ifspxshOAoK', 0, 'sensorBIM');

--------------------------------------------------------BUILDINGS--------------------------------------------------------

INSERT INTO building (id, influx_token, influx_database_url, name, organization_name, user_id)
VALUES (1, '-0oyIPOsLHrNfenULtHU2QTgsBiMvIPBY143fDefzgxrXc7slyugil8kwaFt1wQ1qX1QBuRkEBJZ-jPWrssvxQ==',
        'http://qe-sensorbim.uibk.ac.at:8086/', 'Testgebäude', 'Quality Engineering', 1);

INSERT INTO building (id, influx_token, influx_database_url, name, organization_name, user_id)
VALUES (2, '-0oyIPOsLHrNfenULtHU2QTgsBiMvIPBY143fDefzgxrXc7slyugil8kwaFt1wQ1qX1QBuRkEBJZ-jPWrssvxQ==',
        'http://qe-sensorbim.uibk.ac.at:8086/', 'Testgebäude 2', 'Quality Engineering', 1);

INSERT INTO building (id, influx_token, influx_database_url, name, organization_name, user_id)
VALUES (3, '-0oyIPOsLHrNfenULtHU2QTgsBiMvIPBY143fDefzgxrXc7slyugil8kwaFt1wQ1qX1QBuRkEBJZ-jPWrssvxQ==',
        'http://qe-sensorbim.uibk.ac.at:8086/', 'Testgebäude 3', 'Quality Engineering', 5);


--------------------------------------------------------LEVELS--------------------------------------------------------
INSERT INTO level (id, name, uri, building_id)
VALUES (1, 'EG- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_138', 1);

INSERT INTO level (id, name, uri, building_id)
VALUES (2, 'OG1- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_144', 1);

INSERT INTO level (id, name, uri, building_id)
VALUES (3, 'EG- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_138', 2);

INSERT INTO level (id, name, uri, building_id)
VALUES (4, 'OG1- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_144', 2);

INSERT INTO level (id, name, uri, building_id)
VALUES (5, 'EG- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_138', 3);

INSERT INTO level (id, name, uri, building_id)
VALUES (6, 'OG1- OK RD', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingStorey_144', 3);

--------------------------------------------------------ROOMS--------------------------------------------------------

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (1,
        '[[6.57292264691302, 4.3845272421737], [9.07292264691302, 4.50052724217369], [11.696922646913, 4.50052724217368], [11.696922646913, 8.51452724217372], [8.17451791914894, 8.51452724217373], [8.05132737467711, 8.51452724217373], [6.57292264691302, 8.51452724217373], [6.57292264691302, 4.3845272421737]]',
        'Empfangsraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_14908', 1);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (2,
        '[[11.754922646913, 8.75452724217372], [11.932922646913, 8.75452724217372], [11.932922646913, 10.0245272421737], [8.17292264691302, 10.0245272421737], [8.17292264691303, 8.75452724217373], [11.754922646913, 8.75452724217372]]',
        'Technikraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15451', 1);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (3,
        '[[6.57292264691303, 4.3845272421737], [11.812922646913, 4.3845272421737], [11.812922646913, 8.51452724217372], [6.57292264691303, 8.51452724217372], [6.57292264691303, 4.3845272421737]]',
        'Senderaum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15196', 2);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (4,
        '[[6.57292264691302, 4.3845272421737], [9.07292264691302, 4.50052724217369], [11.696922646913, 4.50052724217368], [11.696922646913, 8.51452724217372], [8.17451791914894, 8.51452724217373], [8.05132737467711, 8.51452724217373], [6.57292264691302, 8.51452724217373], [6.57292264691302, 4.3845272421737]]',
        'Empfangsraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_14908', 3);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (5,
        '[[11.754922646913, 8.75452724217372], [11.932922646913, 8.75452724217372], [11.932922646913, 10.0245272421737], [8.17292264691302, 10.0245272421737], [8.17292264691303, 8.75452724217373], [11.754922646913, 8.75452724217372]]',
        'Technikraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15451', 3);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (6,
        '[[6.57292264691303, 4.3845272421737], [11.812922646913, 4.3845272421737], [11.812922646913, 8.51452724217372], [6.57292264691303, 8.51452724217372], [6.57292264691303, 4.3845272421737]]',
        'Senderaum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15196', 4);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (7,
        '[[6.57292264691302, 4.3845272421737], [9.07292264691302, 4.50052724217369], [11.696922646913, 4.50052724217368], [11.696922646913, 8.51452724217372], [8.17451791914894, 8.51452724217373], [8.05132737467711, 8.51452724217373], [6.57292264691302, 8.51452724217373], [6.57292264691302, 4.3845272421737]]',
        'Empfangsraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_14908', 5);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (8,
        '[[11.754922646913, 8.75452724217372], [11.932922646913, 8.75452724217372], [11.932922646913, 10.0245272421737], [8.17292264691302, 10.0245272421737], [8.17292264691303, 8.75452724217373], [11.754922646913, 8.75452724217372]]',
        'Technikraum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15451', 6);

INSERT INTO room (id, geometry, name, uri, level_id)
VALUES (9,
        '[[6.57292264691303, 4.3845272421737], [11.812922646913, 4.3845272421737], [11.812922646913, 8.51452724217372], [6.57292264691303, 8.51452724217372], [6.57292264691303, 4.3845272421737]]',
        'Senderaum', 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcLabel_15196', 6);


--------------------------------------------------------SENSORS--------------------------------------------------------

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)

VALUES (1, 'rfid_test', '000000A93C0000000008000B00000976', 70, -50, 'Sensor_01:Sensor:2394754', 30, 0,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24780', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (2, 'rfid_test', '000000A93C0000000008000B00000976', 70, -50, 'Sensor_01:Sensor:2394442', 30, 0,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24701', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (3, 'rfid_test', '000000A93C0000000008000B0000097B', 100, 0, 'Sensor_01:Sensor:2392163', 30, 2,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_23932', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (4, 'rfid_test', '000000A93C0000000008000B0000097C', 100, 0, 'Sensor_01:Sensor:2393261', 30, 1,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24057', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (5, 'rfid_test', '000000A93C0000000008000B00000987', 100, 0, 'Sensor_01:Sensor:2396133', 30,
        0, 0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24863', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (6, 'rfid_test', '000000A93C0000000008000B00000987', 100, 0, 'Sensor_01:Sensor:2393270', 30, 2,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24131', 1);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (7, 'rfid_test', '000000A93C0000000008000B000009BA', 70, -50, 'Sensor_01:Sensor:2394754', 30, 0,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24780', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (8, 'rfid_test', '000000A93C0000000008000B000009BD', 70, -50, 'Sensor_01:Sensor:2394442', 30, 0,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24701', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (9, 'scanntronic', 'Thermofox Universal TU146716', 100, 0, 'Sensor_01:Sensor:2392163', 30, 2,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_23932', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (10, 'scanntronic', 'Thermofox Universal TU146717', 100, 0, 'Sensor_01:Sensor:2393261', 30, 1,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24057', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (11, 'rfid_test', '000000A93C0000000008000B00000987', 100, 0, 'Sensor_01:Sensor:2396133', 30,
        0, 1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24863', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (12, 'scanntronic', 'Thermofox Universal TU146716', 100, 0, 'Sensor_01:Sensor:2393270', 30, 2,
        0, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24131', 4);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (13, 'scanntronic', '6', 70, -50, 'Sensor_01:Sensor:2394754', 30,
        0,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24780', 9);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (14, 'scanntronic', '5', 70, -50, 'Sensor_01:Sensor:2394442', 30,
        0,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24701', 9);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (15, 'scanntronic', '4', 100, 0, 'Sensor_01:Sensor:2392163', 30, 2,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_23932', 9);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (16, 'scanntronic', '3', 100, 0, 'Sensor_01:Sensor:2393261', 30, 1,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24057', 9);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (17, 'rfid_test', '2', 100, 0, 'Sensor_01:Sensor:2396133', 30,
        0, 1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24863', 9);

INSERT INTO sensor (id, bucket_name, influx_identifier, max_value, min_value, name, sampling_rate,
                    sensor_type, transmission_type, uri, room_id)
VALUES (18, 'scanntronic', '1', 100, 0, 'Sensor_01:Sensor:2393270', 30, 2,
        1, 'http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_24131', 9);


INSERT INTO switching_device (id, name, token, ip, on_path, off_path, status_path, slug, status, automatic, comment,
                              building_id)
VALUES (1, 'switching device 1', 'some token', '127.0.0.0', '/on', '/off', '/status', '', 1, 0,
        'This device measures some things', 1);

INSERT INTO switching_device (id, name, token, ip, on_path, off_path, status_path, slug, status, automatic, comment,
                              building_id)
VALUES (2, 'switching device 2', 'some token', '127.0.0.1', '/on', '/off', '/status', '', 1, 0,
        'This device measures some things', 1);

INSERT INTO switching_device (id, name, token, ip, on_path, off_path, status_path, slug, status, automatic, comment,
                              building_id)
VALUES (3, 'switching device 3', 'some token', '127.0.0.2', '/on', '/off', '/status', '', 1, 0,
        'This device measures some things', 2);

INSERT INTO switching_device (id, name, token, ip, on_path, off_path, status_path, slug, status, automatic, comment,
                              building_id)
VALUES (4, 'switching device 4', 'some token', '127.0.0.3', '/on', '/off', '/status', '', 1, 0,
        'This device measures some things', 3);