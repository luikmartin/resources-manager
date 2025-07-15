--liquibase formatted sql
--changeset martin.luik:03-07-2041-insert-sample-data

-- Resource 1 (Estonia)
INSERT INTO location (id, street_address, city, postal_code, country_code)
VALUES ('11111111-1111-1111-1111-111111111111', 'Main St 1', 'Tallinn', '10111', 'EE');
INSERT INTO resource (id, type, country_code, location_id)
VALUES ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'METERING_POINT', 'EE', '11111111-1111-1111-1111-111111111111');
INSERT INTO characteristic (id, code, type, value, resource_id)
VALUES ('c1a1a1a1-a1a1-a1a1-a1a1-c1a1a1a1a1a1', 'C01', 'CONSUMPTION_TYPE', 'Electricity',
        'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
       ('c1a1a1a2-a1a1-a1a1-a1a1-c1a1a1a1a1a2', 'CP1', 'CHARGING_POINT', 'Fast',
        'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1');

-- Resource 2 (Finland)
INSERT INTO location (id, street_address, city, postal_code, country_code)
VALUES ('22222222-2222-2222-2222-222222222222', 'Market Sq 2', 'Helsinki', '00100', 'FI');
INSERT INTO resource (id, type, country_code, location_id)
VALUES ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'CONNECTION_POINT', 'FI', '22222222-2222-2222-2222-222222222222');
INSERT INTO characteristic (id, code, type, value, resource_id)
VALUES ('c2a2a2a1-a2a2-a2a2-a2a2-c2a2a2a2a2a1', 'C02', 'CONSUMPTION_TYPE', 'Gas',
        'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
       ('c2a2a2a2-a2a2-a2a2-a2a2-c2a2a2a2a2a2', 'CP2', 'CONNECTION_POINT_STATUS', 'Active',
        'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2');

-- Resource 3 (Estonia)
INSERT INTO location (id, street_address, city, postal_code, country_code)
VALUES ('33333333-3333-3333-3333-333333333333', 'Lake Rd 3', 'Tartu', '51004', 'EE');
INSERT INTO resource (id, type, country_code, location_id)
VALUES ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'METERING_POINT', 'EE', '33333333-3333-3333-3333-333333333333');
INSERT INTO characteristic (id, code, type, value, resource_id)
VALUES ('c3a3a3a1-a3a3-a3a3-a3a3-c3a3a3a3a3a1', 'C03', 'CONSUMPTION_TYPE', 'Water',
        'aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
       ('c3a3a3a2-a3a3-a3a3-a3a3-c3a3a3a3a3a2', 'CP3', 'CHARGING_POINT', 'Slow',
        'aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3');
