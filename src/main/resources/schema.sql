CREATE TABLE restaurant
(
    id           LONG PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(64) NOT NULL,
    address      VARCHAR(64) NOT NULL,
    opening_time TIME        NOT NULL,
    closing_time TIME        NOT NULL
);

CREATE TABLE menu_item
(
    id            LONG PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(64) NOT NULL,
    price         DECIMAL     NOT NULL,
    description   VARCHAR(1024),
    restaurant_id LONG,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
);

CREATE TABLE orders
(
    id            LONG PRIMARY KEY AUTO_INCREMENT,
    order_time    TIME        NOT NULL,
    order_status  VARCHAR(64) NOT NULL,
    restaurant_id LONG        NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
);

CREATE TABLE order_item
(
    id              LONG PRIMARY KEY AUTO_INCREMENT,
    quantity        INTEGER NOT NULL,
    additional_info VARCHAR(1024),
    menu_item_id    LONG,
    FOREIGN KEY (menu_item_id) REFERENCES menu_item (id),
    order_id        LONG,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
