-- Insert sample categories
INSERT INTO categories (name, description) VALUES 
('Electronics', 'Electronic devices and accessories'),
('Computers', 'Desktop and laptop computers'),
('Mobile Devices', 'Smartphones and tablets'),
('Home & Garden', 'Home improvement and garden supplies'),
('Books', 'Physical and digital books'),
('Clothing', 'Apparel and fashion accessories');

-- Insert subcategories
INSERT INTO categories (name, description, parent_id) VALUES 
('Laptops', 'Portable computers', 2),
('Desktops', 'Desktop computers', 2),
('Smartphones', 'Mobile phones', 3),
('Tablets', 'Tablet computers', 3),
('Fiction', 'Fiction books', 5),
('Non-Fiction', 'Non-fiction books', 5);

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES 
('MacBook Pro 13"', 'Apple MacBook Pro with M1 chip', 1299.99, 50, 7),
('Dell XPS 13', 'Ultra-thin laptop with Intel Core i7', 999.99, 30, 7),
('iMac 24"', 'Apple iMac with M1 chip and Retina display', 1499.99, 20, 8),
('iPhone 13', 'Apple iPhone with A15 Bionic chip', 799.99, 100, 9),
('Samsung Galaxy S21', 'Android smartphone with triple camera', 699.99, 75, 9),
('iPad Air', 'Apple tablet with M1 chip', 599.99, 60, 10),
('The Great Gatsby', 'Classic American novel', 12.99, 200, 11),
('Sapiens', 'A Brief History of Humankind', 16.99, 150, 12);

-- Update stock for some products to create low stock scenarios
UPDATE products SET stock_quantity = 5 WHERE name = 'iMac 24"';
UPDATE products SET stock_quantity = 8 WHERE name = 'iPad Air';