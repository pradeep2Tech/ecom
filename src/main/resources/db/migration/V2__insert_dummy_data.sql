-- V2: Insert initial dummy data

INSERT INTO t_user (first_name, last_name, email, phone, user_role, created_at, updated_at)
VALUES ('Alice', 'Smith', 'alice@example.com', '555-0100', 'CUSTOMER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO t_user (first_name, last_name, email, phone, user_role, created_at, updated_at)
VALUES ('Bob', 'Jones', 'bob@example.com', '555-0101', 'CUSTOMER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Address for Alice (id = 1)
INSERT INTO t_address (id, street, city, state, county, zipcode, user_id)
VALUES (1, '123 Main St', 'Metropolis', 'State', 'County', '12345', 1);

-- Products
INSERT INTO t_product (name, price, stock_quantity, category, image_url, active, created_at, updated_at)
VALUES ('Wireless Headphones', 79.99, 50, 'Electronics', 'https://example.com/headphones.png', TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO t_product (name, price, stock_quantity, category, image_url, active, created_at, updated_at)
VALUES ('USB-C Charger', 19.99, 200, 'Electronics', 'https://example.com/charger.png', TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Cart item: Alice has 2 headphones
INSERT INTO t_cart_item (user_id, product_id, quantity, price, created_at, updated_at)
VALUES (1, 1, 2, 159.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Optionally add an order (empty for now)
