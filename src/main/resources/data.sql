INSERT INTO restaurant(id, name, address, opening_time, closing_time) VALUES
    ('1', 'La Fresh', 'Brace Ribnikar 10', '10:00:00', '23:00:00'),
    ('2', 'Sef', 'Brace Ribnikar 17', '09:00:00', '23:00:00');


INSERT INTO menu_item(id, name, description, price, restaurant_id) VALUES
    ('1', 'Burger', 'junece meso, lepinja, kecap', '250.0', '1'),
    ('2', 'Burito', 'junece meso, tortilja, pasulj', '300.0', '1'),
    ('3', 'Sendvic piletina', 'piletina, lepinja, salata', '250.0', '2');
