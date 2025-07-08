CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED') NOT NULL DEFAULT 'ACTIVE',
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_product_name (name),
    INDEX idx_product_category (category_id),
    INDEX idx_product_price (price),
    INDEX idx_product_status (status),
    FULLTEXT INDEX idx_product_search (name, description)
);