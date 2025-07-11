--liquibase formatted sql
--changeset db:V25.6.19


CREATE TABLE IF NOT EXISTS items (
    -- id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    img_path VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    -- id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    delivery_address VARCHAR(255),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS order_items (
    -- id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    UNIQUE (order_id, item_id)
);
