# Order Service

The `order-service` manages the lifecycle of customer orders in the online store microservices ecosystem. It communicates synchronously with `inventory-service` via Spring Cloud OpenFeign to verify stock availability and persists orders in Oracle Database.

---

## 🛠️ Architecture & Tech Stack

* **Java 21 & Spring Boot 3.3.4**
* **Spring Cloud 2023.0.3**:
  * Netflix Eureka Client (Service Registration & Discovery)
  * Spring Cloud OpenFeign (Declarative REST client for inter-service calls)
  * Spring Cloud Config Client
* **Spring Data JPA & Hibernate 6**: ORM mapping to Oracle Database
* **Oracle Database (FREEPDB1)**:
  * `ORDERS` table (Parent order record)
  * `ORDER_ITEMS` table (Child purchased item lines)
* **Jakarta Bean Validation**: Input validation on order creation payloads

---

## 🚀 Prerequisites

Ensure the following infrastructure and services are running:

1. **Oracle Database (Docker):**
   ```bash
   docker ps --filter "name=oracle-free"
   ```
2. **Registry Service (Eureka):** Port `8761`
3. **Config Server:** Port `8888`
4. **Inventory Service:** Port `8083` (needed for stock availability checks)
5. **Order Service:** Port `8084`

---

## 🧪 Testing the APIs with Postman / cURL

### 1. Place a New Order (Success)

Valid product ID from Oracle `INVENTORY`: `b6c0b6bea69c722939585baeac73c13d`

* **Method:** `POST`
* **URL:** `http://localhost:8084/api/orders`
* **Headers:** `Content-Type: application/json`

#### cURL:
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-101",
    "shippingAddress": "Flat 402, Green Valley Apartments, Mumbai",
    "items": [
      {
        "productId": "b6c0b6bea69c722939585baeac73c13d",
        "sku": "pp5006380337",
        "quantity": 2,
        "price": 24.16
      }
    ]
  }'
```

#### Expected Response (`201 CREATED`):
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "customerId": "CUST-101",
    "status": "CONFIRMED",
    "totalAmount": 48.32,
    "shippingAddress": "Flat 402, Green Valley Apartments, Mumbai",
    "items": [
      {
        "id": 1,
        "productId": "b6c0b6bea69c722939585baeac73c13d",
        "sku": "pp5006380337",
        "quantity": 2,
        "price": 24.16
      }
    ],
    "createdAt": "2026-11-06T14:15:00Z"
  },
  "timestamp": "2026-11-06T14:15:01Z"
}
```

---

### 2. Fetch Order by Order ID

* **Method:** `GET`
* **URL:** `http://localhost:8084/api/orders/{orderId}`

#### cURL:
```bash
curl -X GET http://localhost:8084/api/orders/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

#### Expected Response (`200 OK`):
```json
{
  "success": true,
  "message": "Order fetched successfully",
  "data": {
    "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "customerId": "CUST-101",
    "status": "CONFIRMED",
    "totalAmount": 48.32,
    "shippingAddress": "Flat 402, Green Valley Apartments, Mumbai",
    "items": [
      {
        "id": 1,
        "productId": "b6c0b6bea69c722939585baeac73c13d",
        "sku": "pp5006380337",
        "quantity": 2,
        "price": 24.16
      }
    ],
    "createdAt": "2026-11-06T14:15:00Z"
  },
  "timestamp": "2026-11-06T14:16:00Z"
}
```

---

### 3. Fetch Order History by Customer ID

* **Method:** `GET`
* **URL:** `http://localhost:8084/api/orders/customer/{customerId}`

#### cURL:
```bash
curl -X GET http://localhost:8084/api/orders/customer/CUST-101
```

#### Expected Response (`200 OK`):
```json
{
  "success": true,
  "message": "Customer orders fetched successfully",
  "data": [
    {
      "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "customerId": "CUST-101",
      "status": "CONFIRMED",
      "totalAmount": 48.32,
      "shippingAddress": "Flat 402, Green Valley Apartments, Mumbai",
      "items": [
        {
          "id": 1,
          "productId": "b6c0b6bea69c722939585baeac73c13d",
          "sku": "pp5006380337",
          "quantity": 2,
          "price": 24.16
        }
      ],
      "createdAt": "2026-11-06T14:15:00Z"
    }
  ],
  "timestamp": "2026-11-06T14:17:00Z"
}
```

---

### 4. Out-of-Stock Error (Exception Handling)

* **Method:** `POST`
* **URL:** `http://localhost:8084/api/orders`
* **Headers:** `Content-Type: application/json`

#### cURL:
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-102",
    "shippingAddress": "123 Street",
    "items": [
      {
        "productId": "non-existent-product-id",
        "sku": "FAKE-SKU",
        "quantity": 1,
        "price": 10.00
      }
    ]
  }'
```

#### Expected Response (`400 BAD REQUEST`):
```json
{
  "success": false,
  "message": " these products are out of stock: [non-existent-product-id]",
  "data": null,
  "timestamp": "2026-11-06T14:18:00Z"
}
```

---

### 5. Validation Error (Bean Validation)

When required fields (like `customerId` or `items`) are blank:

#### cURL:
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "",
    "shippingAddress": "",
    "items": []
  }'
```

#### Expected Response (`400 BAD REQUEST`):
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "customerId": "Customer ID is required",
    "shippingAddress": "Shipping address is required",
    "items": "Order must contain at least one item"
  },
  "timestamp": "2026-11-06T14:19:00Z"
}
```

---

## 🗄️ Verify in Oracle Database Directly

Run this command to inspect saved orders inside the Oracle Docker container:

```bash
docker exec -i oracle-free sqlplus -s SYSTEM/Password123@FREEPDB1 << 'EOF'
SET LINESIZE 120
COL ORDER_ID FORMAT A36
COL CUSTOMER_ID FORMAT A12
COL STATUS FORMAT A10
COL SHIPPING_ADDRESS FORMAT A30
SELECT order_id, customer_id, status, total_amount, shipping_address FROM orders;
SELECT id, order_id, product_id, quantity, price FROM order_items;
EXIT;
EOF
```
