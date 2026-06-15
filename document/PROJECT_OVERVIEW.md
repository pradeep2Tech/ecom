# Ecom Application - Project Overview

## Architecture Type
**Monolithic Architecture** - Single Spring Boot application handling all business domains

## Project Structure

```
ecom/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/ecom/mono/
│   │   │       ├── controller/          # REST API endpoints
│   │   │       │   ├── UserController.java
│   │   │       │   ├── ProductController.java
│   │   │       │   ├── CartController.java
│   │   │       │   └── OrderController.java
│   │   │       ├── service/             # Business logic
│   │   │       │   ├── IUserService.java / UserService.java
│   │   │       │   ├── IProductService.java / ProductService.java
│   │   │       │   ├── ICartItemService.java / CartItemService.java
│   │   │       │   ├── IOrderService.java / OrderService.java
│   │   │       ├── Repo/                # Data access layer (JPA)
│   │   │       │   ├── IUserRepo.java
│   │   │       │   ├── IProductRepo.java
│   │   │       │   ├── ICartItemRepo.java
│   │   │       │   ├── IOrderRepo.java
│   │   │       │   └── IOrderItemRepo.java
│   │   │       ├── entity/              # JPA entities (Database models)
│   │   │       │   ├── BaseEntity.java          # Shared lifecycle (createdAt, updatedAt)
│   │   │       │   ├── UserEntity.java
│   │   │       │   ├── AddressEntity.java
│   │   │       │   ├── ProductEntity.java
│   │   │       │   ├── CartItemEntity.java
│   │   │       │   ├── OrderEntity.java
│   │   │       │   └── OrderItemEntity.java
│   │   │       ├── model/               # DTOs (Transfer objects for API)
│   │   │       │   ├── User.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   ├── CartItem.java
│   │   │       │   ├── UserRole.java    # Enum: ADMIN, CUSTOMER
│   │   │       │   ├── OrderStatus.java # Enum: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
│   │   │       │   └── Address.java
│   │   │       └── EcomApplication.java # Spring Boot entry point
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── static/
│   │       └── templates/
│   └── test/
└── pom.xml

document/
├── README.md
├── PROJECT_OVERVIEW.md
├── DATABASE_ERD.md
├── API_ENDPOINTS_REFERENCE.md
├── DEVELOPMENT_SETUP.md
└── MICROSERVICES_MIGRATION_ROADMAP.md
```

## Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 4.1.0 |
| **Language** | Java 25 |
| **ORM** | JPA/Hibernate |
| **Database** | PostgreSQL (recommended for development and production) |
| **Build Tool** | Maven |
| **Package Manager** | Maven Central |
| **Annotation Processing** | Lombok |
| **Testing** | JUnit 5 (via spring-boot-starter-test) |

## Current Database
- **PostgreSQL** (recommended for development and production)
- Configured via `application.yml`:
  - DDL Auto: `none` (managed via Flyway)
  - JDBC URL: `jdbc:postgresql://localhost:5432/ecom`
  - Driver: `org.postgresql.Driver`

## Core Business Domains

### 1. User Management
- **Entities**: UserEntity, AddressEntity
- **Models**: User, Address, UserRole
- **Endpoints**: 
  - GET/POST/PUT/DELETE users
  - User roles: ADMIN, CUSTOMER
- **Status**: Complete

### 2. Product Catalog
- **Entities**: ProductEntity
- **Models**: Product
- **Features**:
  - CRUD operations
  - Search by keyword (case-insensitive)
  - Filter by active status and stock quantity > 0
  - Pagination ready
- **Status**: Complete

### 3. Shopping Cart
- **Entities**: CartItemEntity
- **Models**: CartItem
- **Features**:
  - Add items to cart (create or update quantity)
  - Delete items from cart
  - Get user's cart
  - Auto-calculates price (product.price × quantity)
  - Stores snapshot of price at cart time
- **Status**: Complete

### 4. Orders
- **Entities**: OrderEntity, OrderItemEntity
- **Models**: Order, OrderItem, OrderStatus
- **Features**:
  - Create order from cart items
  - Track order status: PENDING → CONFIRMED → SHIPPED → DELIVERED/CANCELLED
  - Automatic cart clearing on order creation
  - Calculate total amount from cart
  - Order history per user
- **Status**: Complete

## Design Patterns Used

### 1. **Repository Pattern**
- Abstraction layer between service and database
- JpaRepository interface for CRUD operations
- Custom query methods (e.g., `findByUserIdAndProductId`)

### 2. **Service Layer Pattern**
- Business logic encapsulation
- Interface-based design (e.g., `IUserService`)
- Dependency injection via constructor
- Entity-to-DTO conversion (toModel/toEntity)

### 3. **DTO Pattern (Data Transfer Objects)**
- Entities for database (ProductEntity, UserEntity)
- Models for API transfer (Product, User)
- Prevents over-exposure of database structure

### 4. **MappedSuperclass Pattern**
- `BaseEntity` - Shared lifecycle management
- Auto-populated `createdAt` and `updatedAt` fields via `@PrePersist` and `@PreUpdate`
- Reduces code duplication across entities

### 5. **REST API Standards**
- RESTful endpoints following HTTP conventions
- Proper HTTP status codes (200, 201, 204, 400, 404, 500)
- Request/response JSON serialization

## API Request Convention

### Headers
- **X-User-Id** (required for user-specific operations):
  - Used in: Cart, Order endpoints
  - Format: Long integer representing user ID

### Query Parameters
- **keyword** (optional for search):
  - Product search filter
  - Case-insensitive name matching

### Request/Response Format
- Content-Type: `application/json`
- Response body: JSON object or array

## Error Handling

| Scenario | HTTP Status | Response |
|----------|------------|----------|
| Invalid input (e.g., quantity ≤ 0) | 400 Bad Request | Error message |
| User/Product not found | 404 Not Found | Error message |
| Empty cart on order creation | 400 Bad Request | "cart is empty" |
| Internal error | 500 Internal Server Error | "internal error" |

## Validation Rules

### Cart Operations
- `quantity` must be > 0
- User must exist
- Product must exist

### Order Creation
- User must exist
- Cart must not be empty

### Product Search
- Active products only (active = true)
- Stock quantity > 0
- Optional keyword filtering

## Future Roadmap

### Phase 1: Improvements
- [ ] Add pagination to list endpoints
- [ ] Add sorting capabilities
- [ ] Input validation beans (@Valid, @NotBlank, etc.)
- [ ] Global exception handler
- [ ] API versioning (/v1/products, /v2/products)
- [ ] Rate limiting

### Phase 2: Advanced Features
- [ ] Authentication & Authorization (Spring Security)
- [ ] User roles enforcement (ADMIN vs CUSTOMER)
- [ ] Order status update workflow
- [ ] Payment integration
- [ ] Email notifications
- [ ] Inventory management

### Phase 3: Operations & Monitoring
- [ ] Spring Boot Actuator (health, metrics, endpoints)
- [ ] Logging (SLF4J, ELK stack)
- [ ] Distributed tracing
- [ ] Circuit breaker pattern
- [ ] Caching (Redis)

### Phase 4: Microservices Architecture
- [ ] Split into services:
  - User Service (authentication, profiles, addresses)
  - Product Service (catalog, search, inventory)
  - Order Service (orders, cart, checkout)
  - Payment Service (payment processing)
  - Notification Service (email, SMS)
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Service-to-service communication (gRPC, RestTemplate, WebClient)
- [ ] Distributed transactions (Saga pattern)
- [ ] Event-driven architecture (Kafka, RabbitMQ)

### Phase 5: Data Layer Evolution
- [ ] PostgreSQL for relational data
- [ ] MongoDB for product catalog (flexible schema)
- [ ] Elasticsearch for search
- [ ] Redis for caching & sessions
- [ ] Database sharding strategy

### Phase 6: Containerization & Orchestration
- [ ] Docker images for each service
- [ ] Docker Compose for local development
- [ ] Kubernetes deployment manifests
- [ ] Helm charts for deployment
- [ ] CI/CD pipeline (GitHub Actions, GitLab CI, Jenkins)

## Key Observations

### Strengths
✅ Clean layered architecture  
✅ Separation of concerns (entity ≠ model)  
✅ Reusable base class for timestamps  
✅ Consistent naming conventions  
✅ Proper use of JPA relationships  
✅ Constructor injection for dependencies  
✅ Interface-based service design  

### Areas for Enhancement
⚠️ No global exception handler  
⚠️ No input validation annotations  
⚠️ Limited error messages  
⚠️ No pagination on list endpoints  
⚠️ No authentication/authorization  
⚠️ No logging framework configured  
⚠️ No API versioning  
⚠️ No transaction management (@Transactional)  

## Quick Start

```bash
# Build
mvn clean package

# Run
java -jar target/ecom-0.0.1-SNAPSHOT.jar

# Or using Maven
mvn spring-boot:run

# Access
# Application: http://localhost:8080
```

## Dependencies to Add (Recommended)

For production readiness, consider adding:

```xml
<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>

<!-- PostgreSQL Driver (for future migration) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- MongoDB Driver (for future use) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Docker support -->
<dependency>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.3.1</version>
</dependency>
```

