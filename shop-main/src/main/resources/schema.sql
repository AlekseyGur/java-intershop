CREATE TABLE IF NOT EXISTS items (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    img_path VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    login varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
	active boolean NOT NULL DEFAULT TRUE,
	roles varchar(255) NOT NULL,
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    is_paid BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    delivery_address VARCHAR(255),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    item_id UUID REFERENCES items(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    UNIQUE (order_id, item_id)
);