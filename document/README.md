# Ecom Application - Complete Documentation

Welcome to the Ecom E-Commerce Platform documentation. This is a monolithic Spring Boot application currently designed for a single deployment. Below is a complete guide to the project structure, setup, and future migration paths.

## 📚 Documentation Files

This documentation package contains:

### 1. **PROJECT_OVERVIEW.md** 
High-level architecture review of the monolithic system.
- Technology stack
- Project structure
- Design patterns used
- Current database (PostgreSQL)
- Future roadmap phases
- Key observations & recommendations

**Read this first** to understand the overall architecture.

---

### 2. **DATABASE_ERD.md**
Complete database entity relationship diagram and schema documentation.
- Mermaid ERD diagram
- Table schemas with constraints
- Relationship types and cardinalities
- Data flow scenarios
- SQL schema definitions
- Migration path to PostgreSQL + MongoDB

**Use this** for understanding data models and database design.

---

### 3. **API_ENDPOINTS_REFERENCE.md**
Complete REST API documentation with examples.
- All endpoints organized by domain
- Request/response formats
- HTTP status codes
- Validation rules
- cURL examples
- Complete workflow walkthrough
- Future endpoints planned

**Reference this** when building client applications or testing APIs.

---

### 4. **DEVELOPMENT_SETUP.md**
Step-by-step guide for setting up local development environment.
- Prerequisites and installation
- Project setup instructions
- IDE configuration
- Build commands
- Testing APIs
- Debugging guide
- Common issues & solutions
- Environment configuration

**Follow this** to get the application running locally.

---

### 5. **MICROSERVICES_MIGRATION_ROADMAP.md**
Detailed 16-week plan to migrate from monolith to microservices.
- Phase-by-phase breakdown
- Service boundaries definition
- Communication patterns (sync/async)
- API Gateway setup
- Advanced patterns (Circuit Breaker, Saga)
- Database optimization (Polyglot Persistence)
- Kubernetes deployment
- CI/CD pipeline setup
- Risk mitigation strategies

**Study this** when planning future architecture evolution.

---

## 🚀 Quick Start

### Prerequisites
- Java 25 JDK
- Maven 3.8.0+
- IDE (IntelliJ IDEA, VS Code, or Eclipse)

### Build & Run
```bash
# Clone repository
git clone <repo-url>
cd ecom

# Build
mvn clean install

# Run
mvn spring-boot:run

# Application starts at http://localhost:8080
```

### Verify Setup
```bash
# Test API
curl http://localhost:8080/products
```

### PostgreSQL Connection
# JDBC URL: jdbc:postgresql://localhost:5432/ecom
# Driver Class: org.postgresql.Driver
# Username: ecom_user
# Password: ecom_password

---

## 📊 Project Structure at a Glance

```
ecom/
├── document/              # Documentation files (this folder)
│   ├── PROJECT_OVERVIEW.md
│   ├── DATABASE_ERD.md
│   ├── API_ENDPOINTS_REFERENCE.md
│   ├── DEVELOPMENT_SETUP.md
│   └── MICROSERVICES_MIGRATION_ROADMAP.md
│
├── src/main/java/com/app/ecom/mono/
│   ├── controller/        # REST endpoints (4 controllers)
│   ├── service/          # Business logic (interfaces + implementations)
│   ├── Repo/            # Data access layer (5 repositories)
│   ├── entity/          # JPA entities (7 entities)
│   ├── model/           # DTOs for API (7 models + 2 enums)
│   └── EcomApplication.java
│
├── src/main/resources/
│   └── application.yml
│
├── pom.xml               # Maven dependencies
└── Dockerfile           # (to be created for containerization)
```

---

## 🏗️ Architecture Layers

```
┌─────────────────────────────────────┐
│      REST Controllers               │  (4 controllers)
│  /users, /products, /cart, /orders  │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      Service Layer                  │  (8 services)
│  Business logic & validations       │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      Repository Layer               │  (5 repositories)
│  JPA queries & data access          │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      Entity Layer                   │  (7 entities)
│  Database models with relationships │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      PostgreSQL Database           │
│  (PostgreSQL for production)        │
└─────────────────────────────────────┘
```

---

## 📦 Core Components

### Controllers (4)
- `UserController` - User CRUD + management
- `ProductController` - Product catalog + search
- `CartController` - Shopping cart operations
- `OrderController` - Order creation & history

### Services (8)
- `UserService` - User business logic
- `ProductService` - Product operations & filtering
- `CartItemService` - Cart management logic
- `OrderService` - Order creation & retrieval
- Plus interfaces: `IUserService`, `IProductService`, `ICartItemService`, `IOrderService`

### Repositories (5)
- `IUserRepo` - User data access
- `IProductRepo` - Product queries (with custom finders)
- `ICartItemRepo` - Cart item data access
- `IOrderRepo` - Order data access
- `IOrderItemRepo` - Order item data access

### Entities (7)
- `BaseEntity` - Shared lifecycle (createdAt, updatedAt)
- `UserEntity` - User profile
- `AddressEntity` - User address
- `ProductEntity` - Product catalog
- `CartItemEntity` - Shopping cart items
- `OrderEntity` - Orders
- `OrderItemEntity` - Items in orders

### Models (7 + 2 Enums)
- `User` - DTO for User API
- `Product` - DTO for Product API
- `CartItem` - DTO for Cart API
- `Order` - DTO for Order API
- `OrderItem` - DTO for order items
- `Address` - DTO for address
- Plus: `UserRole` enum (ADMIN, CUSTOMER)
- Plus: `OrderStatus` enum (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

---

## 🔄 Data Flow Examples

### Adding Item to Cart
```
Client
  ↓
POST /cart/{productId}?quantity=2 (Header: X-User-Id: 1)
  ↓
CartController
  ↓
CartItemService.addCartItem(userId, productId, quantity)
  ↓
Validate: User exists, Product exists, quantity > 0
  ↓
ICartItemRepo.findByUserIdAndProductId()
  ↓
Database
  ├─ If exists: UPDATE quantity and price
  └─ If new: INSERT cart item
  ↓
Return CartItem DTO
```

### Creating Order
```
Client
  ↓
POST /orders (Header: X-User-Id: 1)
  ↓
OrderController
  ↓
OrderService.createOrder(userId)
  ↓
Validate: User exists, Cart not empty
  ↓
Get all cart items for user
  ↓
Create OrderEntity with status=PENDING
  ↓
Create OrderItemEntity for each cart item
  ↓
Calculate totalAmount
  ↓
Save order to database
  ↓
Clear user's cart
  ↓
Return Order DTO with items
```

---

## 🔐 Security & Validation

### Current Implementation
- User ID passed via `X-User-Id` header
- Basic entity existence validation
- Quantity validation (> 0)

### Recommended Additions
- Spring Security for authentication/authorization
- Input validation beans (@Valid, @NotBlank, etc.)
- Global exception handler
- Rate limiting
- CORS configuration

See `PROJECT_OVERVIEW.md` for detailed recommendations.

---

## 🗄️ Database Schema

### Tables (6 main + join tables)
1. `t_user` - Users with roles
2. `t_address` - User addresses (1:1 with user)
3. `t_product` - Product catalog
4. `t_cart_item` - Shopping cart items (user + product)
5. `t_order` - Orders with status
6. `t_order_item` - Items in orders (product + order)

### Key Relationships
- User → Address (1:1)
- User → CartItem (1:M) → Product (M:1)
- User → Order (1:M) → OrderItem (M:1) → Product (M:1)

See `DATABASE_ERD.md` for complete schema, SQL definitions, and ERD diagrams.

---

## 📡 API Overview

### Base URL
`http://localhost:8080`

### Endpoints (by domain)

**Users** (5 endpoints)
- GET /users
- POST /users
- GET /users/{id}
- PUT /users/{id}
- DELETE /users/{id}

**Products** (6 endpoints)
- GET /products (active + stock > 0)
- GET /products/search?keyword=...
- GET /products/{id}
- POST /products
- PUT /products/{id}
- DELETE /products/{id}

**Cart** (3 endpoints)
- POST /cart/{productId}?quantity=... (add)
- GET /cart (view)
- DELETE /cart/{productId} (remove)

**Orders** (3 endpoints)
- POST /orders (create/checkout)
- GET /orders/{orderId}
- GET /orders/user/orders (history)

See `API_ENDPOINTS_REFERENCE.md` for complete documentation with cURL examples.

---

## 📋 Current Features

✅ **User Management**
- Create, read, update, delete users
- User addresses (1:1 relationship)
- User roles (ADMIN, CUSTOMER)

✅ **Product Catalog**
- Full CRUD operations
- Search with keyword filtering
- Active product filtering
- Stock availability checks
- Category organization

✅ **Shopping Cart**
- Add items (create or update)
- Remove items
- View cart
- Auto-quantity aggregation
- Price snapshot storage

✅ **Orders**
- Create orders from cart
- Auto cart clearing
- Order status tracking (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- Order history per user
- Total amount calculation

---

## 🎯 Future Enhancements

### Short Term (Weeks 1-4)
- [ ] Add input validation annotations
- [ ] Create global exception handler
- [ ] Add pagination to list endpoints
- [ ] Add sorting capabilities
- [ ] Implement Spring Security

### Medium Term (Weeks 5-12)
- [ ] Add payment integration
- [ ] Email notifications
- [ ] Inventory management
- [ ] Product reviews & ratings
- [ ] User wishlist

### Long Term (Weeks 13+)
- [ ] Microservices architecture
- [ ] PostgreSQL + MongoDB
- [ ] Kubernetes deployment
- [ ] Spring Cloud setup
- [ ] Message queue integration
- [ ] Distributed tracing
- [ ] Advanced caching

See `MICROSERVICES_MIGRATION_ROADMAP.md` for detailed 16-week plan.

---

## 🛠️ Development Commands

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Test
mvn test

# Package
mvn clean package

# Run JAR
java -jar target/ecom-0.0.1-SNAPSHOT.jar

# View dependency tree
mvn dependency:tree

# Skip tests during build
mvn clean package -DskipTests
```

See `DEVELOPMENT_SETUP.md` for comprehensive guide.

---

## 📖 Reading Guide

### By Role

**Product Manager**
1. Start: PROJECT_OVERVIEW.md (sections: Business Domains, Current Features)
2. Then: API_ENDPOINTS_REFERENCE.md (to understand capabilities)
3. Future: MICROSERVICES_MIGRATION_ROADMAP.md (for scaling strategy)

**Backend Developer**
1. Start: DEVELOPMENT_SETUP.md (to set up environment)
2. Then: PROJECT_OVERVIEW.md (understand architecture)
3. Then: DATABASE_ERD.md (understand data model)
4. Reference: API_ENDPOINTS_REFERENCE.md (for API contracts)

**DevOps Engineer**
1. Start: DEVELOPMENT_SETUP.md (current setup)
2. Then: PROJECT_OVERVIEW.md (technology stack)
3. Then: MICROSERVICES_MIGRATION_ROADMAP.md (containerization & K8s)
4. Reference: DATABASE_ERD.md (database considerations)

**Architect**
1. Start: PROJECT_OVERVIEW.md (overview & design patterns)
2. Then: DATABASE_ERD.md (data model & relationships)
3. Then: MICROSERVICES_MIGRATION_ROADMAP.md (evolution path)
4. Reference: API_ENDPOINTS_REFERENCE.md (API contracts)

---

## 📊 Technology Stack Summary

| Layer | Technology | Version |
|-------|-----------|---------|
| Framework | Spring Boot | 4.1.0 |
| Language | Java | 25 |
| ORM | JPA/Hibernate | (auto-managed) |
| Database | H2 → PostgreSQL | Current: PostgreSQL |
| Build Tool | Maven | 3.8.0+ |
| Annotations | Lombok | (latest) |
| Testing | JUnit 5 | (via starter) |

**Planned Additions**:
- Spring Security
- Spring Cloud
- Docker
- Kubernetes
- MongoDB
- Redis
- RabbitMQ/Kafka
- Elasticsearch

---

## 🔗 Quick Links

- **Repository**: [Link to git repo]
- **Issue Tracker**: [Link to issues]
- **Wiki**: [Link to wiki]
- **Slack Channel**: [Link to channel]
- **Build Pipeline**: [Link to CI/CD]

---

## 📞 Support & Questions

### Troubleshooting
- See `DEVELOPMENT_SETUP.md` - Troubleshooting Checklist section
- See `PROJECT_OVERVIEW.md` - Key Observations section

### Getting Help
1. Check relevant documentation file
2. Search existing issues/tickets
3. Contact project maintainer
4. Create new issue with details

---

## 📜 License

[Add your license information here]

---

## 👥 Contributors

[Add contributor information]

---

## 📅 Document Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-06-14 | Initial comprehensive documentation suite |

**Last Updated**: 2026-06-14

---

## Next Steps

1. **New to the project?**
   → Start with `DEVELOPMENT_SETUP.md` and get it running locally

2. **Want to understand architecture?**
   → Read `PROJECT_OVERVIEW.md` and `DATABASE_ERD.md`

3. **Building a client?**
   → Reference `API_ENDPOINTS_REFERENCE.md`

4. **Planning to scale?**
   → Study `MICROSERVICES_MIGRATION_ROADMAP.md`

---

**Happy coding! 🚀**

