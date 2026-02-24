BEGIN;

TRUNCATE TABLE
    contract_employees,
    service_contracts,
    employee_contact_methods,
    employees,
    client_contact_methods,
    client_contacts,
    services,
    clients
RESTART IDENTITY
CASCADE;

INSERT INTO clients (client_id, client_type, display_name, note) VALUES
    (1, 'ORGANIZATION', 'ООО "АльфаСтрой"', 'строительная компания'),
    (2, 'ORGANIZATION', 'АО "Вектор Финанс"', 'финансовая группа'),
    (3, 'INDIVIDUAL', 'Иванов Иван Иванович', NULL),
    (4, 'INDIVIDUAL', 'Петрова Мария Сергеевна', 'частный инвестор'),
    (5, 'ORGANIZATION', 'ООО "Гамма-Трейд"', 'оптовая торговля');

INSERT INTO client_contacts (contact_id, client_id, full_name, role, comment) VALUES
    (1, 1, 'Смирнов Алексей Павлович', 'генеральный директор', NULL),
    (2, 1, 'Кузнецова Елена Викторовна', 'главный бухгалтер', 'вопросы по оплатам и закрывающим документам'),
    (3, 2, 'Орлова Наталья Игоревна', 'юрист компании', NULL),
    (4, 2, 'Морозов Денис Владимирович', 'контактное лицо', 'по вопросам сделок'),
    (5, 3, 'Иванов Иван Иванович', 'клиент', 'предпочитает связь по e-mail'),
    (6, 4, 'Петрова Мария Сергеевна', 'клиент', NULL),
    (7, 5, 'Михайлов Сергей Андреевич', 'директор по развитию', NULL);

INSERT INTO client_contact_methods (method_id, contact_id, method_type, value, is_primary) VALUES
    (1, 1, 'PHONE', '+7-495-111-11-11', TRUE),
    (2, 1, 'EMAIL', 'a.smirnov@alfastroi.example', TRUE),
    (3, 1, 'ADDRESS', 'Москва, ул. Строителей, 10', TRUE),

    (4, 2, 'PHONE', '+7-495-111-22-22', TRUE),
    (5, 2, 'EMAIL', 'e.kuznetsova@alfastroi.example', TRUE),

    (6, 3, 'EMAIL', 'n.orlova@vectorfin.example', TRUE),
    (7, 3, 'PHONE', '+7-812-222-33-44', FALSE),
    (8, 3, 'ADDRESS', 'Санкт-Петербург, Невский пр., 25', TRUE),

    (9, 4, 'PHONE', '+7-812-222-55-66', TRUE),
    (10, 4, 'EMAIL', 'd.morozov@vectorfin.example', FALSE),

    (11, 5, 'EMAIL', 'ivanov.iii@example.com', TRUE),
    (12, 5, 'PHONE', '+7-916-333-44-55', FALSE),

    (13, 6, 'PHONE', '+7-905-777-88-99', TRUE),
    (14, 6, 'EMAIL', 'petrova.ms@example.com', TRUE),

    (15, 7, 'PHONE', '+7-495-999-00-11', TRUE),
    (16, 7, 'EMAIL', 's.mikhailov@gammatrade.example', TRUE),
    (17, 7, 'ADDRESS', 'Москва, ул. Логистическая, 7', TRUE);

INSERT INTO employees (employee_id, full_name, position, education, home_address, note) VALUES
    (1, 'Климов Артём Олегович', 'старший юрист', 'МГЮА, 2015', 'Москва, ул. Профсоюзная, 15', NULL),
    (2, 'Власова Анна Дмитриевна', 'юрист', 'СПбГУ, 2018', 'Санкт-Петербург, ул. Литейная, 3', 'практика: банкротства'),
    (3, 'Громов Павел Сергеевич', 'помощник юриста', 'НИУ ВШЭ, 2023', 'Москва, ул. Академика, 8', NULL),
    (4, 'Федорова Ирина Андреевна', 'партнёр', 'МГУ, 2010', 'Москва, пр-т Мира, 101', 'судебные споры'),
    (5, 'Сафонов Николай Евгеньевич', 'специалист по корпоративному праву', 'МГИМО, 2016', 'Москва, ул. Тверская, 22', NULL);

INSERT INTO employee_contact_methods (method_id, employee_id, method_type, value, is_primary) VALUES
    (1, 1, 'PHONE', '+7-926-100-10-10', TRUE),
    (2, 1, 'EMAIL', 'klimov.ao@lawfirm.example', TRUE),

    (3, 2, 'PHONE', '+7-921-200-20-20', TRUE),
    (4, 2, 'EMAIL', 'vlasova.ad@lawfirm.example', TRUE),

    (5, 3, 'EMAIL', 'gromov.ps@lawfirm.example', TRUE),
    (6, 3, 'PHONE', '+7-985-300-30-30', FALSE),

    (7, 4, 'PHONE', '+7-903-400-40-40', TRUE),
    (8, 4, 'EMAIL', 'fedorova.ia@lawfirm.example', TRUE),
    (9, 4, 'ADDRESS', 'Москва, ул. Арбат, 1 (офис)', FALSE),

    (10, 5, 'EMAIL', 'safonov.ne@lawfirm.example', TRUE),
    (11, 5, 'PHONE', '+7-905-500-50-50', TRUE);

INSERT INTO services (service_id, name, base_cost) VALUES
    (1, 'Создание документов', 15000.00),
    (2, 'Восстановление документов', 20000.00),
    (3, 'Сопровождение документов', 10000.00),
    (4, 'Банкротство', 120000.00),
    (5, 'Эмиссия акций', 250000.00),
    (6, 'Сопровождение сделок', 80000.00),
    (7, 'Судебное представительство', 90000.00),
    (8, 'Консультации', 5000.00);
INSERT INTO service_contracts (
    contract_id,
    contract_number,
    client_id,
    service_id,
    signed_on,
    service_start,
    service_end,
    status,
    agreed_cost,
    comment
) VALUES
    (1, 'LF-2026-0001', 1, 6, DATE '2026-01-05', DATE '2026-01-10', NULL, 'ACTIVE', 80000.00, 'Сопровождение сделки по покупке оборудования'),
    (2, 'LF-2025-0107', 2, 5, DATE '2025-10-20', DATE '2025-11-01', DATE '2025-12-15', 'COMPLETED', 250000.00, 'Подготовка документов по эмиссии'),
    (3, 'LF-2025-0220', 3, 8, DATE '2025-12-01', DATE '2025-12-05', DATE '2025-12-05', 'CANCELLED', 5000.00, 'Консультация отменена клиентом'),
    (4, 'LF-2026-0005', 4, 7, DATE '2026-02-01', DATE '2026-03-01', NULL, 'DRAFT', 90000.00, 'Планируется представительство в суде'),
    (5, 'LF-2026-0009', 5, 4, DATE '2026-02-05', DATE '2026-02-10', NULL, 'ACTIVE', 120000.00, 'Запуск процедуры банкротства'),
    (6, 'LF-2025-0088', 1, 1, DATE '2025-08-25', DATE '2025-09-01', DATE '2025-09-20', 'COMPLETED', 15000.00, 'Подготовка комплекта договоров');

INSERT INTO contract_employees (contract_id, employee_id, role) VALUES
    (1, 4, 'ведущий юрист'),
    (1, 5, 'корпоративное право'),

    (2, 5, 'корпоративное право'),
    (2, 1, 'подготовка документов'),

    (3, 3, 'помощник'),

    (4, 4, 'представитель в суде'),

    (5, 2, 'банкротство'),
    (5, 4, 'куратор'),

    (6, 1, 'подготовка документов'),
    (6, 3, 'помощник');

SELECT setval(pg_get_serial_sequence('clients', 'client_id'), (SELECT max(client_id) FROM clients));
SELECT setval(pg_get_serial_sequence('client_contacts', 'contact_id'), (SELECT max(contact_id) FROM client_contacts));
SELECT setval(pg_get_serial_sequence('client_contact_methods', 'method_id'), (SELECT max(method_id) FROM client_contact_methods));
SELECT setval(pg_get_serial_sequence('employees', 'employee_id'), (SELECT max(employee_id) FROM employees));
SELECT setval(pg_get_serial_sequence('employee_contact_methods', 'method_id'), (SELECT max(method_id) FROM employee_contact_methods));
SELECT setval(pg_get_serial_sequence('services', 'service_id'), (SELECT max(service_id) FROM services));
SELECT setval(pg_get_serial_sequence('service_contracts', 'contract_id'), (SELECT max(contract_id) FROM service_contracts));

COMMIT;

