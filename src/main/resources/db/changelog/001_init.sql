CREATE TABLE product (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    price DECIMAL(10, 2),
    stock INTEGER,
    created_at timestamptz NOT NULL,
    last_modified_at timestamptz NOT NULL,
    last_finalized_at timestamptz
);

