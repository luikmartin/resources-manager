--liquibase formatted sql
--changeset martinluik:03-07-2038-create-resource-table
CREATE TABLE resource
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type         VARCHAR(32) NOT NULL,
    country_code CHAR(2)     NOT NULL,
    location_id  UUID        NOT NULL UNIQUE,
    CONSTRAINT fk_resource_location FOREIGN KEY (location_id) REFERENCES location (id) ON DELETE CASCADE
);
