INSERT INTO restaurant(id, name, address, opening_time, closing_time) VALUES
    ('1000', 'La Fresh', 'Brace Ribnikar 10', '10:00:00', '23:00:00'),
    ('1001', 'Sef', 'Brace Ribnikar 17', '09:00:00', '23:00:00');


INSERT INTO menu_item(id, name, description, price, restaurant_id) VALUES
    ('500', 'Burger', 'junece meso, lepinja, kecap', '250.0', '1000'),
    ('501', 'Burito', 'junece meso, tortilja, pasulj', '300.0', '1000'),
    ('502', 'Sendvic piletina', 'piletina, lepinja, salata', '250.0', '1001');

