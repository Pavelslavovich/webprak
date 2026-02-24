BEGIN;

DROP TABLE IF EXISTS contract_employees CASCADE;
DROP TABLE IF EXISTS service_contracts CASCADE;
DROP TABLE IF EXISTS employee_contact_methods CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS client_contact_methods CASCADE;
DROP TABLE IF EXISTS client_contacts CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS clients CASCADE;

DROP TYPE IF EXISTS contract_status;
DROP TYPE IF EXISTS contact_method_type;
DROP TYPE IF EXISTS client_type;

CREATE TYPE client_type AS ENUM ('INDIVIDUAL', 'ORGANIZATION');
CREATE TYPE contact_method_type AS ENUM ('PHONE', 'EMAIL', 'ADDRESS');
CREATE TYPE contract_status AS ENUM ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED');

CREATE TABLE clients (
    client_id BIGSERIAL PRIMARY KEY,
    client_type client_type NOT NULL,
    display_name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    note TEXT
);
CREATE TABLE client_contacts (
    contact_id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(client_id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    role TEXT,
    comment TEXT
);

CREATE TABLE client_contact_methods (
    method_id BIGSERIAL PRIMARY KEY,
    contact_id BIGINT NOT NULL REFERENCES client_contacts(contact_id) ON DELETE CASCADE,
    method_type contact_method_type NOT NULL,
    value TEXT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    position TEXT NOT NULL,
    education TEXT,
    home_address TEXT,
    note TEXT
);

CREATE TABLE employee_contact_methods (
    method_id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(employee_id) ON DELETE CASCADE,
    method_type contact_method_type NOT NULL,
    value TEXT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE services (
    service_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    base_cost NUMERIC(12, 2) NOT NULL CHECK (base_cost >= 0)
);
CREATE TABLE service_contracts (
    contract_id BIGSERIAL PRIMARY KEY,
    contract_number TEXT NOT NULL UNIQUE,
    client_id BIGINT NOT NULL REFERENCES clients(client_id) ON DELETE RESTRICT,
    service_id BIGINT NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT,
    signed_on DATE NOT NULL,
    service_start DATE NOT NULL,
    service_end DATE,
    status contract_status NOT NULL,
    agreed_cost NUMERIC(12, 2) NOT NULL CHECK (agreed_cost >= 0),
    comment TEXT
);
CREATE TABLE contract_employees (
    contract_id BIGINT NOT NULL REFERENCES service_contracts(contract_id) ON DELETE CASCADE,
    employee_id BIGINT NOT NULL REFERENCES employees(employee_id) ON DELETE RESTRICT,
    role TEXT,
    PRIMARY KEY (contract_id, employee_id)
);
CREATE INDEX idx_service_contracts_client_id ON service_contracts(client_id);
CREATE INDEX idx_service_contracts_service_id ON service_contracts(service_id);
CREATE INDEX idx_service_contracts_service_start ON service_contracts(service_start);
CREATE INDEX idx_service_contracts_service_end ON service_contracts(service_end);
CREATE INDEX idx_contract_employees_employee_id ON contract_employees(employee_id);

COMMIT;

