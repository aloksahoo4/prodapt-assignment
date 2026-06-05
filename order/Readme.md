# Order API -- Spring Boot Assignment

A RESTful API that manages orders.

---

## Tech Stack

| Layer | Choice |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Build | Maven |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Storage | In-memory (`ConcurrentHashMap`) |
| Testing | JUnit 5 + Mockito + AssertJ |

---

## Prerequisites

- **Java 17+** -- verify with `java -version`
- **Maven 3.6+** -- verify with `mvn -version`
    - Or use the included `./mvnw` wrapper (downloads Maven automatically)

---

## How to Run

### 1. Clone the project

### 2. Build

```bash
mvn clean package -DskipTests
# or with the wrapper:
./mvnw clean package -DskipTests
```

### 3. Run

```bash
java -jar target/order-1.0.0.jar
```

Server starts on **http://localhost:9099**

### 4. Run Tests

Run the full unit test suite:

```bash
mvn test
# or with the wrapper:
./mvnw test
```

**Run a single test class:**

```bash
mvn test -Dtest=OrderServiceTest
```

**Run a single test method:**

```bash
mvn test -Dtest=OrderServiceTest#methodName
```

**Run tests as part of a full build:**

```bash
mvn clean verify
```

---

## API Endpoints

### POST /orders -- Create an order

**Request body:**
```json
{
  "customerName": "Alice",
  "customerType": "PREMIUM",
  "amount": 1000.00,
  "orderDate": "2024-01-15"
}
```

**Response -- 201 Created:**
```json
{
  "id": "3f2504e0-4f89-11d3-9a0c-0305e82c3301",
  "customerName": "Alice",
  "customerType": "PREMIUM",
  "amount": 1000.00,
  "orderDate": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### GET /orders/{id} -- Fetch a single order

```
GET /orders/3f2504e0-4f89-11d3-9a0c-0305e82c3301
```

**Response -- 200 OK:** same shape as above  
**Response -- 404 Not Found:** if ID doesn't exist

---

### GET /orders?month=YYYY-MM -- Monthly revenue report

```
GET /orders?month=2024-01
```

**Response -- 200 OK:**
```json
{
  "month": "2024-01",
  "orderCount": 2,
  "totalRevenue": 1400.00,
  "orders": [ ... ]
}
```

---

## Error Response Shape

All errors return a consistent JSON envelope:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    { "field": "amount", "message": "amount must be greater than 0" }
  ]
}
```

| Scenario | HTTP Status |
|---|---|
| Validation failure | 400 Bad Request |
| Malformed JSON / bad enum | 400 Bad Request |
| Invalid `?month` format | 400 Bad Request |
| Missing `?month` param | 400 Bad Request |
| Order not found by ID | 404 Not Found |
| Unexpected error | 500 Internal Server Error |

---

## Sample cURL Commands

```bash
# Create a STANDARD order
curl -X POST http://localhost:9099/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alice","customerType":"STANDARD","amount":500.00,"orderDate":"2024-01-10"}'

# Create a PREMIUM order
curl -X POST http://localhost:9099/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Bob","customerType":"PREMIUM","amount":1000.00,"orderDate":"2024-01-20"}'

# Get by ID (replace with actual ID from above)
curl http://localhost:9099/orders/3f2504e0-4f89-11d3-9a0c-0305e82c3301

# Monthly revenue
curl "http://localhost:9099/orders?month=2024-01"
```

---

## Assumptions

1. **`amount` is the face value;**  
  stored and returned in the response so the caller can see the original price.

2. **`orderDate` accepts past or present dates only.**  
   Future dates are rejected (`@PastOrPresent`). The assignment did not specify this, but it's a sensible business rule for an order management system.

3. **`amount` must be > 0 with at most 2 decimal places.**  
   Zero amounts are excluded (`@DecimalMin("0.01")`). More than 2 decimal places are rejected (`@Digits(fraction=2)`).

4. **`month` query param is required for GET /orders.**  
   There is no "list all orders" endpoint without a month filter. The assignment defined only `GET /orders?month=YYYY-MM`.

5. **IDs are UUID v4 strings.**  
   The assignment did not specify ID format. UUID avoids collisions without a sequence generator and is idiomatic for REST APIs.

6. **Data is lost on restart.**  
   Storage is in-memory by design (as specified). A real system would use a database.
