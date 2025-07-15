--liquibase formatted sql
--changeset martin.luik:03-07-2040-create-characteristic-table
CREATE TABLE characteristic
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(5)   NOT NULL,
    type        VARCHAR(32)  NOT NULL,
    value       VARCHAR(255) NOT NULL,
    resource_id UUID         NOT NULL,
    CONSTRAINT fk_characteristic_resource FOREIGN KEY (resource_id) REFERENCES resource (id) ON DELETE CASCADE
); 
