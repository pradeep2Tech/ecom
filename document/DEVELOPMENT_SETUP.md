# Development Setup & Build Guide

## Prerequisites

### System Requirements
- **Java**: JDK 25 or higher
- **Maven**: 3.8.0 or higher
- **Git**: Latest version (for version control)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Installation

#### Windows

**Java 25**:
1. Download from [oracle.com](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
2. Install to `C:\Program Files\Java\jdk-25`
3. Set `JAVA_HOME` environment variable:
   - `JAVA_HOME=C:\Program Files\Java\jdk-25`
4. Add to `PATH`:
   - `C:\Program Files\Java\jdk-25\bin`

**Maven**:
1. Download from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract to `C:\Apache\maven-3.9.0`
3. Set `MAVEN_HOME=C:\Apache\maven-3.9.0`
4. Add to `PATH`: `C:\Apache\maven-3.9.0\bin`

#### Mac

**Java 25**:
```bash
# Using Homebrew
brew install openjdk@25
```

**Maven**:
```bash
# Using Homebrew
brew install maven
```

#### Linux (Ubuntu/Debian)

```bash
# Java 25
sudo apt-get install openjdk-25-jdk

# Maven
sudo apt-get install maven
```

## Project Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd ecom
```

### 2. Verify Setup
```bash
# Check Java version
java -version
# Expected: openjdk version "25" ...

# Check Maven version
mvn -version
# Expected: Apache Maven 3.8.0 or higher
```

### 3. Build Project
```bash
# Clean build
mvn clean install

# Or build without running tests
mvn clean install -DskipTests
```

### 4. Run Application

#### Using Maven Plugin
```bash
mvn spring-boot:run
```

#### Using JAR File
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/ecom-0.0.1-SNAPSHOT.jar
```

### 5. Access Application
- **Application**: http://localhost:8080

**PostgreSQL Connection**:
- Driver Class: `org.postgresql.Driver`
- JDBC URL: `jdbc:postgresql://localhost:5432/ecom`
- User Name: `ecom_user`
- Password: `ecom_password`

---

## Development Workflow

### IDE Setup (IntelliJ IDEA)

#### 1. Open Project
- File → Open
- Select `ecom` folder
- Trust project when prompted

#### 2. Configure SDK
- File → Project Structure → Project
- Select `Java 25` as SDK
- Click Apply

#### 3. Configure Run Configuration
- Run → Edit Configurations
- Click `+` → Spring Boot
- Main class: `com.app.ecom.mono.EcomApplication`
- VM options: `-Dspring.profiles.active=dev`
- Click OK

#### 4. Run Application
- Click Run or press Shift+F10
- View logs in console

### IDE Setup (VS Code)

#### Extensions Needed
- Extension Pack for Java
- Spring Boot Extension Pack
- REST Client (for testing APIs)

#### Running Application
- Open Terminal
- Run `mvn spring-boot:run`

---

## Testing APIs

### Using cURL (Command Line)

**Get All Products**:
```bash
curl -X GET http://localhost:8080/products
```

**Create User**:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com"}'
```

### Using REST Client (VS Code)

Create `requests.rest` file:
```
### Get all products
GET http://localhost:8080/products

### Create user
POST http://localhost:8080/users
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com"
}

### Get user cart
GET http://localhost:8080/cart
X-User-Id: 1
```

### Using Postman

1. Open Postman
2. Create new Collection: "Ecom API"
3. Import endpoints from documentation
4. Set base URL: http://localhost:8080
5. Add `X-User-Id` header in collection settings
6. Run requests individually or as collection

---

## Common Build Issues & Solutions

### Issue: Java Version Mismatch
**Error**: `java.lang.UnsupportedClassVersionError`  
**Solution**:
```bash
java -version  # Check version is 25
export JAVA_HOME=/path/to/java25
```

### Issue: Maven Central Repo Timeout
**Error**: `Could not transfer artifact...`  
**Solution**: Check internet connection and retry
```bash
mvn clean install -U  # Force update
```

### Issue: Port Already in Use
**Error**: `Address already in use: 8080`  
**Solution**:
```bash
# Find process using port 8080
# Windows: netstat -ano | findstr :8080
# Mac/Linux: lsof -i :8080

# Kill process or change port in application.yml:
# server.port=8081
```

---

## Environment Configuration

### Development (Default)
File: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: ecom
  datasource:
    url: jdbc:postgresql://localhost:5432/ecom
    driver-class-name: org.postgresql.Driver
    username: ecom_user
    password: ecom_password
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQL10Dialect
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
server:
  port: 8080
```

### Production (Recommended for future)
Create `src/main/resources/application-prod.yml`:

```properties
spring.application.name=ecom
spring.datasource.url=jdbc:postgresql://prod-db:5432/ecom
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect
server.port=8080
logging.level.root=INFO
```

**Run with production profile**:
```bash
java -jar target/ecom-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Maven Commands Reference

```bash
# Clean previous build
mvn clean

# Compile code
mvn compile

# Run tests
mvn test

# Package JAR
mvn package

# Install to local repo
mvn install

# Skip tests during build
mvn clean package -DskipTests

# Run specific test class
mvn test -Dtest=UserServiceTest

# View dependency tree
mvn dependency:tree

# Update dependencies
mvn dependency:update-snapshots

# Format code (requires plugin)
mvn spotless:apply

# Run spring boot app
mvn spring-boot:run

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

---

## Version Control

### Git Workflow

```bash
# Clone repository
git clone <repo-url>

# Create feature branch
git checkout -b feature/add-payment

# Make changes and commit
git add .
git commit -m "feat: add payment integration"

# Push to origin
git push origin feature/add-payment

# Create Pull Request on GitHub
# After approval, merge to main
```

### .gitignore
Ensure these are ignored:
```
target/
*.jar
.classpath
.project
.settings/
.idea/
*.iml
*.log
.DS_Store
node_modules/
```

---

## Debugging

### Enable Debug Mode in IDE

**IntelliJ IDEA**:
1. Run → Debug (or press Shift+F9)
2. Set breakpoints (click line number)
3. Inspect variables in Debug panel

**VS Code**:
1. Install Debugger for Java
2. Set breakpoints
3. Press F5 to start debugging
4. Use Debug Console for inspection

### Debug Logs

Add to `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.app.ecom: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## Performance Tips

### Development Mode Optimization
```properties
# Disable template caching
spring.thymeleaf.cache=false

# Reduce startup time
spring.devtools.restart.enabled=true

# Enable lazy initialization
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

### Production Mode Optimization
```properties
# Disable debug info
spring.jpa.show-sql=false
debug=false

# Use connection pooling
spring.datasource.hikari.maximum-pool-size=20

# Enable query caching
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
```

---

## Continuous Integration Setup

### GitHub Actions (Optional)

Create `.github/workflows/build.yml`:
```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 25
        uses: actions/setup-java@v2
        with:
          java-version: '25'
      - name: Build with Maven
        run: mvn clean package -DskipTests
      - name: Run tests
        run: mvn test
```

---

## Documentation

### Generate JavaDoc
```bash
mvn javadoc:javadoc

# View at target/site/apidocs/index.html
```

### Generate Maven Site Report
```bash
mvn site

# View at target/site/index.html
```

---

## Troubleshooting Checklist

- [ ] Java 25 installed: `java -version`
- [ ] Maven installed: `mvn -version`
- [ ] JAVA_HOME environment variable set
- [ ] Maven SETTINGS.XML configured correctly
- [ ] Internet connection for Maven Central Repo
- [ ] No firewall blocking port 8080
- [ ] Project files not corrupted
- [ ] IDE reindexed after clone
- [ ] Spring Boot dependencies downloaded
- [ ] Application started without errors in console

---

## Next Steps

1. **First Run**: Execute `mvn spring-boot:run` and verify no errors
2. **Explore API**: Use cURL or Postman to test endpoints
3. **Review Code**: Read through service layer to understand business logic
4. **Read Documentation**: Review PROJECT_OVERVIEW.md and DATABASE_ERD.md
5. **Make Changes**: Create feature branch and start development
6. **Test Changes**: Run `mvn test` before committing
7. **Review PR**: Request code review before merging to main

