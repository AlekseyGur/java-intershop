-- Items

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 1', 'Описание item 1', '/images/item1.jpg', 100.0);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 2', 'Описание item 2', '/images/item2.jpg', 200);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 3', 'Описание item 3', '/images/item3.jpg', 300);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 4', 'Описание item 4', '/images/item4.jpg', 400);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 5', 'Описание item 5', '/images/item5.jpg', 500);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 6', 'Описание item 6', '/images/item6.jpg', 600);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 7', 'Описание item 7', '/images/item7.jpg', 700);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 8', 'Описание item 8', '/images/item8.jpg', 800);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 9', 'Описание item 9', '/images/item9.jpg', 900);

INSERT INTO items(title, description, img_path, price)
VALUES ('Item 1', 'Description item 1', '/images/item1.jpg', 100);

-- Users

INSERT INTO users (username, password, active, roles) 
VALUES 
    ('admin', '$2a$10$MfwZEA6sSlMYZ9pnu3moG.M1SQ7wrAWi3MKTNOKKLBKLvLJZ1a9Ya', TRUE, 'ROLE_ADMIN'),
    ('user', '$2a$10$MfwZEA6sSlMYZ9pnu3moG.M1SQ7wrAWi3MKTNOKKLBKLvLJZ1a9Ya', TRUE, 'ROLE_USER'); -- pass 123456