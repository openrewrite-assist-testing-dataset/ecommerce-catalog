# E-commerce Product Catalog Service

A Dropwizard-based REST API for managing products and categories in an e-commerce system.

## Overview

This service provides a comprehensive API for managing product catalogs, including:
- Product CRUD operations
- Category management with hierarchical support
- Product search and filtering
- OAuth2 authentication
- Swagger 2.0 API documentation

## Technology Stack

- **Framework**: Dropwizard 3.0.x
- **Java**: Java 21
- **Build Tool**: Gradle 8.1
- **Database**: MySQL with Flyway migrations
- **ORM**: Hibernate with Criteria Queries
- **Authentication**: OAuth2 (simplified implementation)
- **Documentation**: Swagger 2.0
- **Logging**: Logback
- **Testing**: JUnit 4

## API Endpoints

### Authentication
All endpoints require OAuth2 Bearer token:
```
Authorization: Bearer <oauth2-token>
```

### Product API (`/catalog/v2/products`)

#### Get All Products
```
GET /catalog/v2/products?offset=0&limit=20
```

Example:
```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/catalog/v2/products?offset=0&limit=20"
```

#### Get Product by ID
```
GET /catalog/v2/products/{id}
```

Example:
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/catalog/v2/products/1
```

#### Create Product
```
POST /catalog/v2/products
Content-Type: application/json

{
  "name": "New Laptop",
  "description": "High-performance laptop",
  "price": 1299.99,
  "stockQuantity": 50,
  "category": {"id": 1}
}
```

#### Update Product
```
PUT /catalog/v2/products/{id}
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 1199.99,
  "stockQuantity": 45,
  "category": {"id": 1}
}
```

#### Delete Product
```
DELETE /catalog/v2/products/{id}
```

#### Search Products
```
GET /catalog/v2/products/search?q=laptop&offset=0&limit=20
```

#### Get Products by Price Range
```
GET /catalog/v2/products/by-price?minPrice=100&maxPrice=1000&offset=0&limit=20
```

### Category API (`/catalog/v2/categories`)

#### Get All Categories
```
GET /catalog/v2/categories
```

#### Get Category by ID
```
GET /catalog/v2/categories/{id}
```

#### Create Category
```
POST /catalog/v2/categories
Content-Type: application/json

{
  "name": "New Category",
  "description": "Category description",
  "parent": {"id": 1}
}
```

#### Get Root Categories
```
GET /catalog/v2/categories/root
```

#### Get Child Categories
```
GET /catalog/v2/categories/{id}/children
```

#### Get Products in Category
```
GET /catalog/v2/categories/{id}/products?offset=0&limit=20
```

## Setup Instructions

### Prerequisites
- Java 21
- MySQL 8.0
- Gradle 8.1

### Database Setup
1. Create MySQL database:
```sql
CREATE DATABASE ecommerce_catalog;
CREATE USER 'catalog_user'@'localhost' IDENTIFIED BY 'catalog_pass';
GRANT ALL PRIVILEGES ON ecommerce_catalog.* TO 'catalog_user'@'localhost';
FLUSH PRIVILEGES;
```

### Running the Application

1. Build the project:
```bash
./gradlew build
```

2. Run the application:
```bash
./gradlew run
```

The API will be available at:
- Main API: `http://localhost:8080`
- Admin: `http://localhost:8081`
- Swagger UI: `http://localhost:8080/swagger`

### Configuration

The application uses `application.yml` for configuration. Key settings:

- Database connection details
- OAuth2 configuration
- Catalog-specific settings (page sizes, stock thresholds)
- Logging configuration

## Docker Deployment

### Build Docker Image
```bash
docker build -t ecommerce-catalog .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## Kubernetes Deployment

### Using Helm
```bash
cd helm
helm dependency update ecommerce-catalog
helm install catalog ./ecommerce-catalog
```

### Manual Deployment
```bash
kubectl apply -f k8s/
```

## API Documentation

Swagger 2.0 documentation is available at:
- Swagger JSON: `http://localhost:8080/swagger.json`
- Swagger YAML: `http://localhost:8080/swagger.yaml`

## Health Checks

- Application health: `http://localhost:8081/healthcheck`
- Database health: `http://localhost:8081/healthcheck`

## Testing

Run all tests:
```bash
./gradlew test
```

Run with coverage:
```bash
./gradlew jacocoTestReport
```

## OAuth2 Configuration

The application uses a simplified OAuth2 implementation. Configure the following:

```yaml
oauth2:
  clientId: "your-client-id"
  clientSecret: "your-client-secret"
  scope: "catalog:read catalog:write"
  tokenEndpoint: "https://your-auth-server/oauth/token"
  userInfoEndpoint: "https://your-auth-server/oauth/userinfo"
```

## Monitoring

The application exposes metrics at:
- `http://localhost:8081/metrics`

## Sample Data

The application includes sample data with:
- 6 categories (including subcategories)
- 8 sample products
- Category hierarchy examples

## Performance Features

- Hibernate criteria queries for type-safe database access
- Connection pooling with HikariCP
- Pagination support for large datasets
- Database indexes for optimal query performance
- Full-text search on product names and descriptions

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL is running
   - Verify connection details in `application.yml`
   - Ensure database and user exist

2. **OAuth2 Authentication Errors**
   - Verify token is valid and not expired
   - Check OAuth2 configuration
   - Ensure required scopes are present

3. **Port Already in Use**
   - Default ports: 8080 (API), 8081 (Admin)
   - Change in `application.yml` if needed

### Logs

Application logs are written to:
- Console output
- `./logs/ecommerce-catalog.log`

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes with appropriate tests
4. Run tests and ensure they pass
5. Submit pull request

## Database Schema

### Categories Table
- Hierarchical category structure
- Self-referencing foreign key for parent categories
- Indexes on name and parent_id

### Products Table
- Full product information with pricing
- Foreign key to categories
- Full-text search indexes
- Status enum for product lifecycle

## API Versioning

The API uses URL-based versioning (`/catalog/v2/`). This allows for:
- Backward compatibility
- Gradual migration to new versions
- Clear API contracts# Test commit
