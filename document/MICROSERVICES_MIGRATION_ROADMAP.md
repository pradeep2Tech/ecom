# Microservices Migration Roadmap

## Current State: Monolithic Architecture

The current Ecom application is a single Spring Boot monolith handling:
- User management
- Product catalog
- Shopping cart
- Order processing

**Database**: PostgreSQL (relational database)  
**Communication**: In-process method calls  
**Scalability**: Limited to single instance  
**Deployment**: Single WAR/JAR file

---

## Vision: Microservices Architecture

Decompose into independent, scalable services each with:
- Separate database (Database per Service pattern)
- Independent deployment pipeline
- Asynchronous communication via message queues
- API Gateway for client routing
- Service discovery for dynamic routing

---

## Migration Strategy

### Phase 1: Infrastructure Preparation (Weeks 1-2)

#### 1.1 Containerization
**Goal**: Make application portable and deployable

**Steps**:
1. Create `Dockerfile` for current monolith
2. Create `docker-compose.yml` for local development
3. Set up Docker registry (DockerHub, ECR, or private)
4. Configure CI/CD for container builds

**Files to Create**:
```dockerfile
# Dockerfile
FROM openjdk:25-slim
WORKDIR /app
COPY target/ecom-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  ecom:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ecom
      SPRING_DATASOURCE_USERNAME: ecom_user
      SPRING_DATASOURCE_PASSWORD: password
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ecom
      POSTGRES_USER: ecom_user
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

#### 1.2 Database Migration to PostgreSQL
**Goal**: Move from H2 to production database

**Steps**:
1. Add PostgreSQL dependency to `pom.xml`
2. Create schema migration scripts (Flyway or Liquibase)
3. Deploy PostgreSQL instance
4. Migrate data (if existing)
5. Update `application.yml`

**Dependencies to Add**:
```xml
<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- Database Migrations -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.15.0</version>
</dependency>
```

#### 1.3 Add Spring Boot Actuator
**Goal**: Enable monitoring and health checks

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Configuration**:
```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
```

**Endpoints**:
- `http://localhost:8080/actuator/health` - Application health
- `http://localhost:8080/actuator/metrics` - Application metrics
- `http://localhost:8080/actuator/prometheus` - Prometheus metrics

---

### Phase 2: Service Decomposition (Weeks 3-6)

#### 2.1 Define Service Boundaries

**Option A: Domain-Driven Decomposition**

| Service | Responsibility | Port |
|---------|-----------------|------|
| **User Service** | User management, authentication, profiles | 8081 |
| **Product Service** | Product catalog, search, inventory | 8082 |
| **Order Service** | Orders, checkout, order history | 8083 |
| **Cart Service** | Shopping cart management | 8084 |
| **Payment Service** | Payment processing (future) | 8085 |
| **Notification Service** | Email, SMS notifications (future) | 8086 |
| **API Gateway** | Request routing, rate limiting, auth | 8080 |

**Recommended Starting Point**: Split into 3 core services
1. User Service
2. Product Service
3. Order Service (includes Cart logic)

#### 2.2 Extract User Service

**New Directory Structure**:
```
ecom-user-service/
├── src/
│   ├── main/java/com/app/ecom/user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── model/
│   │   └── UserServiceApplication.java
├── pom.xml
└── Dockerfile

ecom-shared/
├── pom.xml (shared DTOs, exceptions)
```

**User Service pom.xml**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

**User Service application.yml**:
```yaml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://postgres-user:5432/ecom_users
server:
  port: 8081
```

#### 2.3 Extract Product Service

Similar structure to User Service:
```
ecom-product-service/
├── src/main/java/com/app/ecom/product/
├── pom.xml
└── Dockerfile
```

#### 2.4 Extract Order Service

Order Service will need to communicate with:
- User Service (to validate users)
- Product Service (to validate products and get prices)
- Cart Service (initially co-located, then split)

```
ecom-order-service/
├── src/main/java/com/app/ecom/order/
├── pom.xml
└── Dockerfile
```

---

### Phase 3: Inter-Service Communication (Weeks 7-8)

#### 3.1 Synchronous Communication (REST)

**Option 1: RestTemplate**
```java
@Component
public class UserServiceClient {
    private final RestTemplate restTemplate;
    
    public UserDTO getUserById(Long userId) {
        return restTemplate.getForObject(
            "http://user-service:8081/users/" + userId,
            UserDTO.class
        );
    }
}
```

**Option 2: Feign Client** (Recommended)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
@FeignClient(name = "user-service", url = "http://user-service:8081")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
```

#### 3.2 Asynchronous Communication (Message Queue)

**Add RabbitMQ or Kafka**:
```xml
<!-- RabbitMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

**Example: Order Created Event**
```java
// Order Service publishes event
@Component
public class OrderEventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishOrderCreated(Order order) {
        rabbitTemplate.convertAndSend("orders.created", order);
    }
}

// Notification Service subscribes
@Component
@RabbitListener(queues = "orders.created")
public class OrderEventListener {
    public void handleOrderCreated(Order order) {
        // Send email/SMS notification
    }
}
```

#### 3.3 Service Discovery

**Spring Cloud Netflix Eureka**:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

**Configuration**:
```properties
eureka.client.service-url.default-zone=http://eureka:8761/eureka
spring.application.name=user-service
```

**Discovery Usage**:
```java
@FeignClient(name = "user-service")  // Uses Eureka, not hardcoded URL
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
```

---

### Phase 4: API Gateway (Weeks 9-10)

**Spring Cloud Gateway**:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

**Gateway Configuration**:
```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://user-service:8081
          predicates:
            - Path=/users/**
          filters:
            - RewritePath=/users/(?<segment>.*), /$\{segment}
            
        - id: product-service
          uri: http://product-service:8082
          predicates:
            - Path=/products/**
            
        - id: order-service
          uri: http://order-service:8083
          predicates:
            - Path=/orders/**,/cart/**

server:
  port: 8080
```

**Client calls API Gateway**:
```bash
# Instead of direct service calls
curl http://user-service:8081/users/1

# Clients now call gateway
curl http://api-gateway:8080/users/1
```

---

### Phase 5: Advanced Patterns (Weeks 11-12)

#### 5.1 Circuit Breaker (Resilience4j)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

```java
@Component
public class UserServiceClient {
    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public UserDTO getUserById(Long userId) {
        return restTemplate.getForObject(...);
    }
    
    public UserDTO getUserByIdFallback(Long userId, Exception e) {
        return new UserDTO(); // Return default
    }
}
```

#### 5.2 Distributed Tracing (Spring Cloud Sleuth + Zipkin)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

#### 5.3 Saga Pattern for Distributed Transactions
```java
@Service
public class OrderSagaOrchestrator {
    @Autowired private OrderService orderService;
    @Autowired private PaymentServiceClient paymentService;
    @Autowired private InventoryServiceClient inventoryService;
    
    @Transactional
    public Order createOrderSaga(OrderRequest request) {
        // Step 1: Create order
        Order order = orderService.createOrder(request);
        
        // Step 2: Process payment (with compensation)
        try {
            paymentService.processPayment(order);
        } catch (Exception e) {
            orderService.cancelOrder(order.getId());
            throw e;
        }
        
        // Step 3: Reserve inventory (with compensation)
        try {
            inventoryService.reserveItems(order);
        } catch (Exception e) {
            paymentService.refund(order);
            orderService.cancelOrder(order.getId());
            throw e;
        }
        
        return order;
    }
}
```

---

### Phase 6: Data Layer Optimization (Weeks 13-14)

#### 6.1 Polyglot Persistence

**PostgreSQL** (Relational):
- User data
- Orders
- Cart items

**MongoDB** (Document):
- Product catalog (flexible schema)
- User preferences
- Order history

**Elasticsearch** (Search):
- Product search index
- Order search

**Redis** (Cache):
- Session cache
- Product cache
- Leaderboards

```yaml
# docker-compose.yml - Extended
services:
  postgres-user:
    image: postgres:15
    
  postgres-order:
    image: postgres:15
    
  mongodb:
    image: mongo:6
    
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.5.0
    
  redis:
    image: redis:7
```

#### 6.2 Database Sharding (Future)
```
User Service:
├── postgres-shard-1 (user IDs 1-500K)
├── postgres-shard-2 (user IDs 500K-1M)
└── postgres-shard-3 (user IDs 1M+)

Sharding Key: user_id % num_shards
```

---

### Phase 7: Kubernetes Deployment (Weeks 15-16)

**Create Deployment Manifests**:

```yaml
# user-service-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: docker.io/ecom/user-service:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db-url
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 20
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
  - protocol: TCP
    port: 8081
    targetPort: 8081
  type: ClusterIP
```

**Helm Chart** (Recommended for templating):
```
ecom-helm-chart/
├── values.yaml
├── Chart.yaml
└── templates/
    ├── deployment.yaml
    ├── service.yaml
    └── configmap.yaml
```

---

### Phase 8: CI/CD Pipeline (Weeks 17-18)

**GitHub Actions Pipeline**:

```yaml
# .github/workflows/deploy.yml
name: Build and Deploy to Kubernetes

on:
  push:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 25
        uses: actions/setup-java@v2
        with:
          java-version: '25'
          
      - name: Build with Maven
        run: mvn clean package
        
      - name: Run Tests
        run: mvn test
        
      - name: SonarCloud Scan
        uses: SonarSource/sonarcloud-github-action@master
        
      - name: Build Docker Image
        run: |
          docker build -t ecom/user-service:${{ github.sha }} .
          docker push ecom/user-service:${{ github.sha }}
          
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/user-service \
            user-service=ecom/user-service:${{ github.sha }}
```

---

## Migration Timeline

| Phase | Duration | Key Deliverables |
|-------|----------|------------------|
| 1. Infrastructure | 2 weeks | Docker, PostgreSQL, Actuator |
| 2. Decomposition | 4 weeks | 3 core services extracted |
| 3. Communication | 2 weeks | REST + async messaging working |
| 4. API Gateway | 2 weeks | All traffic routed through gateway |
| 5. Advanced Patterns | 2 weeks | Circuit breaker, tracing, saga |
| 6. Data Optimization | 2 weeks | Polyglot persistence, caching |
| 7. Kubernetes | 2 weeks | K8s manifests, service mesh ready |
| 8. CI/CD | 2 weeks | Automated build, test, deploy |
| **Total** | **16 weeks** | **Production-ready microservices** |

---

## Risk Mitigation

### Testing Strategy
- Unit tests in each service (at least 80% coverage)
- Integration tests for API contracts
- Contract testing (Pact/Spring Cloud Contract)
- Performance tests before migration
- Chaos engineering tests (Gremlin, Pumba)

### Rollback Plan
- Feature flags for A/B testing
- Blue-green deployments
- Canary releases (5% → 25% → 100%)
- Database rollback scripts
- Service version compatibility checks

### Monitoring & Alerts
- Application performance monitoring (New Relic, DataDog)
- Log aggregation (ELK Stack)
- Distributed tracing (Zipkin, Jaeger)
- Custom metrics and dashboards
- Alert thresholds for SLAs

---

## Success Criteria

- ✅ All services independently deployable
- ✅ <1 second service discovery
- ✅ 99.9% availability
- ✅ P95 latency < 100ms
- ✅ Automated deployment pipeline
- ✅ All communication patterns documented
- ✅ Team trained on new architecture
- ✅ Cost optimized with auto-scaling

---

## Resources

### Documentation
- [Spring Cloud Microservices](https://spring.io/projects/spring-cloud)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)
- [Docker Guide](https://docs.docker.com/)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)

### Tools
- Docker Desktop
- Kubernetes (Minikube, Docker Desktop K8s, or managed services)
- Helm
- Prometheus & Grafana
- ELK Stack
- Zipkin
- GitOps (ArgoCD)

