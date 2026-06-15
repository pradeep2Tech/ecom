# API Endpoints Reference

## Base URL
```
http://localhost:8080
```

## Authentication & Headers

### X-User-Id Header
Required for user-specific operations (cart, orders).

**Format**: Long integer  
**Example**: `X-User-Id: 1`

## User Management Endpoints

### 1. Get All Users
```
GET /users
```
**Description**: Retrieve all users  
**Response**: Array of User objects  
**Status**: 200 OK

**cURL**:
```bash
curl -X GET http://localhost:8080/users
```

---

### 2. Create User
```
POST /users
Content-Type: application/json
```
**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "userRole": "CUSTOMER",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "county": "New York",
    "zipcode": "10001"
  }
}
```

**Response**: 201 Created  
**Response Body**: Created User object with ID

**cURL**:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "userRole": "CUSTOMER"
  }'
```

---

### 3. Get User by ID
```
GET /users/{id}
```
**Path Parameters**: `id` - User ID  
**Response**: 200 OK (User object) or 404 Not Found

**cURL**:
```bash
curl -X GET http://localhost:8080/users/1
```

---

### 4. Update User
```
PUT /users/{id}
Content-Type: application/json
```
**Path Parameters**: `id` - User ID  
**Request Body**: User object with updated fields  
**Response**: 200 OK (updated User) or 404 Not Found

**cURL**:
```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane@example.com",
    "phone": "9876543210",
    "userRole": "CUSTOMER"
  }'
```

---

### 5. Delete User
```
DELETE /users/{id}
```
**Path Parameters**: `id` - User ID  
**Response**: 204 No Content or 404 Not Found

**cURL**:
```bash
curl -X DELETE http://localhost:8080/users/1
```

---

## Product Catalog Endpoints

### 1. Get All Active Products
```
GET /products
```
**Description**: Returns all active products with stock > 0  
**Response**: Array of Product objects  
**Status**: 200 OK

**cURL**:
```bash
curl -X GET http://localhost:8080/products
```

---

### 2. Search Products
```
GET /products/search?keyword={keyword}
```
**Query Parameters**:
- `keyword` (optional) - Search term for product name (case-insensitive)

**Description**: 
- If no keyword: returns all active products with stock > 0
- If keyword provided: filters by product name containing keyword

**Response**: Array of Product objects  
**Status**: 200 OK

**cURL Examples**:
```bash
# Get all active in-stock products
curl -X GET "http://localhost:8080/products/search"

# Search for products containing "phone"
curl -X GET "http://localhost:8080/products/search?keyword=phone"

# Search for products containing "laptop"
curl -X GET "http://localhost:8080/products/search?keyword=laptop"
```

---

### 3. Get Product by ID
```
GET /products/{id}
```
**Path Parameters**: `id` - Product ID  
**Response**: 200 OK (Product object) or 404 Not Found

**cURL**:
```bash
curl -X GET http://localhost:8080/products/1
```

---

### 4. Create Product
```
POST /products
Content-Type: application/json
```
**Request Body**:
```json
{
  "name": "Wireless Headphones",
  "price": 79.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/image.jpg",
  "active": true
}
```

**Response**: 201 Created  
**Response Body**: Created Product object

**cURL**:
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Headphones",
    "price": 79.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "imageUrl": "https://example.com/image.jpg",
    "active": true
  }'
```

---

### 5. Update Product
```
PUT /products/{id}
Content-Type: application/json
```
**Path Parameters**: `id` - Product ID  
**Request Body**: Product object with updated fields  
**Response**: 200 OK (updated Product) or 404 Not Found

**cURL**:
```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Wireless Headphones",
    "price": 99.99,
    "stockQuantity": 45,
    "category": "Electronics",
    "imageUrl": "https://example.com/image.jpg",
    "active": true
  }'
```

---

### 6. Delete Product
```
DELETE /products/{id}
```
**Path Parameters**: `id` - Product ID  
**Response**: 204 No Content or 404 Not Found

**cURL**:
```bash
curl -X DELETE http://localhost:8080/products/1
```

---

## Shopping Cart Endpoints

### 1. Add Item to Cart
```
POST /cart/{productId}?quantity={quantity}
Headers: X-User-Id: {userId}
```
**Path Parameters**: `productId` - Product ID  
**Query Parameters**: `quantity` - Item quantity (must be > 0)  
**Headers**: `X-User-Id` - User ID  
**Description**:
- If product doesn't exist in cart: creates new cart item
- If product exists: increments quantity and updates total price

**Response**: 201 Created  
**Response Body**: CartItem object

**cURL**:
```bash
curl -X POST "http://localhost:8080/cart/5?quantity=2" \
  -H "X-User-Id: 1"
```

---

### 2. Get User's Cart
```
GET /cart
Headers: X-User-Id: {userId}
```
**Headers**: `X-User-Id` - User ID  
**Description**: Retrieve all cart items for the user

**Response**: 200 OK  
**Response Body**: Array of CartItem objects

**cURL**:
```bash
curl -X GET http://localhost:8080/cart \
  -H "X-User-Id: 1"
```

---

### 3. Delete Cart Item
```
DELETE /cart/{productId}
Headers: X-User-Id: {userId}
```
**Path Parameters**: `productId` - Product ID to remove  
**Headers**: `X-User-Id` - User ID  
**Description**: Remove specific product from user's cart

**Response**: 204 No Content or 404 Not Found

**cURL**:
```bash
curl -X DELETE http://localhost:8080/cart/5 \
  -H "X-User-Id: 1"
```

---

## Order Management Endpoints

### 1. Create Order (Checkout)
```
POST /orders
Headers: X-User-Id: {userId}
```
**Headers**: `X-User-Id` - User ID  
**Description**:
- Creates order from all cart items
- Validates user exists and cart not empty
- Sets initial status to PENDING
- Clears user's cart after order creation
- Calculates total from cart items

**Response**: 201 Created  
**Response Body**: Order object with items

**Errors**:
- 400 Bad Request: "cart is empty"
- 404 Not Found: "user not found"

**cURL**:
```bash
curl -X POST http://localhost:8080/orders \
  -H "X-User-Id: 1"
```

---

### 2. Get Order by ID
```
GET /orders/{orderId}
```
**Path Parameters**: `orderId` - Order ID  
**Description**: Retrieve specific order with all order items

**Response**: 200 OK (Order object) or 404 Not Found

**cURL**:
```bash
curl -X GET http://localhost:8080/orders/1
```

---

### 3. Get User's Orders
```
GET /orders/user/orders
Headers: X-User-Id: {userId}
```
**Headers**: `X-User-Id` - User ID  
**Description**: Retrieve all orders for the user

**Response**: 200 OK  
**Response Body**: Array of Order objects

**cURL**:
```bash
curl -X GET http://localhost:8080/orders/user/orders \
  -H "X-User-Id: 1"
```

---

## Response Format

### Success Response (Product)
```json
{
  "id": 1,
  "name": "Wireless Headphones",
  "price": 79.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/image.jpg",
  "active": true,
  "createdAt": "2026-06-14T12:30:00",
  "updatedAt": "2026-06-14T12:30:00"
}
```

### Success Response (Order)
```json
{
  "id": 1,
  "userId": 1,
  "totalAmount": 159.98,
  "status": "PENDING",
  "orderItems": [
    {
      "id": 1,
      "product": {
        "id": 5,
        "name": "Wireless Headphones",
        "price": 79.99,
        "stockQuantity": 48,
        "category": "Electronics",
        "imageUrl": "https://example.com/image.jpg",
        "active": true,
        "createdAt": "2026-06-14T12:00:00",
        "updatedAt": "2026-06-14T12:00:00"
      },
      "quantity": 2,
      "price": 159.98,
      "createdAt": "2026-06-14T13:00:00",
      "updatedAt": "2026-06-14T13:00:00"
    }
  ],
  "createdAt": "2026-06-14T13:00:00",
  "updatedAt": "2026-06-14T13:00:00"
}
```

### Error Response
```json
{
  "message": "user not found"
}
```

---

## HTTP Status Codes

| Code | Meaning | Common Causes |
|------|---------|---------------|
| 200 | OK | Successful GET/PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid input, quantity ≤ 0, empty cart |
| 404 | Not Found | User/Product/Order not found |
| 500 | Internal Error | Server error |

---

## Validation Rules

| Endpoint | Field | Rule |
|----------|-------|------|
| POST /cart | quantity | Must be > 0 |
| POST /orders | cart | Must not be empty |
| POST /orders | user | Must exist |
| POST /products | name | Required |
| POST /products | price | Required, must be > 0 |
| POST /users | email | Required, must be unique |
| GET /products | active | Always TRUE (hardcoded filter) |
| GET /products | stockQuantity | Always > 0 (hardcoded filter) |

---

## Complete Workflow Example

### 1. Create User
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com"}'
# Response: { "id": 1, ... }
```

### 2. Create Product
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":999.99,"stockQuantity":10,"active":true}'
# Response: { "id": 1, ... }
```

### 3. Add to Cart
```bash
curl -X POST "http://localhost:8080/cart/1?quantity=1" \
  -H "X-User-Id: 1"
# Response: { "id": 1, "userId": 1, "product": {...}, "quantity": 1, ... }
```

### 4. View Cart
```bash
curl -X GET http://localhost:8080/cart \
  -H "X-User-Id: 1"
# Response: [ { "id": 1, "userId": 1, ... } ]
```

### 5. Create Order
```bash
curl -X POST http://localhost:8080/orders \
  -H "X-User-Id: 1"
# Response: { "id": 1, "userId": 1, "status": "PENDING", "totalAmount": 999.99, ... }
```

### 6. Get Order
```bash
curl -X GET http://localhost:8080/orders/1
# Response: { "id": 1, "userId": 1, "status": "PENDING", ... }
```

---

## Future Endpoints (To Be Implemented)

### Order Management
- `PATCH /orders/{orderId}/status` - Update order status
- `GET /orders?status=PENDING` - Filter orders by status
- `GET /orders?from=2026-01-01&to=2026-12-31` - Filter orders by date range

### Advanced Product Search
- `GET /products/category/{category}` - Get products by category
- `GET /products?minPrice=10&maxPrice=100` - Price range filter
- `GET /products?sortBy=price&order=asc` - Sorting

### Cart Advanced
- `PUT /cart/{productId}?quantity={newQuantity}` - Update quantity directly
- `DELETE /cart` - Clear entire cart

### User Management
- `GET /users/{id}/orders` - User order history (alternative to `/orders/user/orders`)
- `GET /users/search?email={email}` - Search users

### Admin Operations
- `GET /orders?userId={userId}` - Admin view of user orders
- `GET /products?active=false` - View inactive products
- `PATCH /products/{id}/stock` - Update stock quantity

