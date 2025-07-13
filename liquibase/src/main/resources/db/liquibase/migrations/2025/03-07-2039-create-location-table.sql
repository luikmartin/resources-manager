--liquibase formatted sql
--changeset martinluik:03-07-2039-create-location-table
CREATE TABLE location
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    street_address VARCHAR(255) NOT NULL,
    city           VARCHAR(255) NOT NULL,
    postal_code    VARCHAR(32)  NOT NULL,
    country_code   CHAR(2)      NOT NULL
); 
