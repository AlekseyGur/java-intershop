--liquibase formatted sql
--changeset db:V25.6.20

-- Пользователи
CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
	active boolean NOT NULL DEFAULT TRUE,
	roles varchar(255) NOT NULL,
);


ALTER TABLE orders
    ADD COLUMN user_id UUID,
    ADD CONSTRAINT fk_user_id
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE;
