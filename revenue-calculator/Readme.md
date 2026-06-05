# Monthly Revenue Calculator -- Java 8 Assignment

## Overview

Calculates monthly revenue from a list of orders, grouped by `YearMonth`.  
Applies a **10% discount** for `PREMIUM` customers and ignores invalid entries (null or negative amounts).

## Requirements

Java 8 or above, 
Build tool - None required -- plain `javac`


## How to Run

### 1. Clone / download the files

### 2. Compile

```bash
javac -d out src/main/java/org/example/enums/CustomerType.java src/main/java/org/example/model/Order.java src/main/java/org/example/revenuecalculator/MonthlyRevenueCalculator.java src/main/java/org/example/revenuecalculator/MonthlyRevenueCalculatorMain.java
```

### 3. Run

```bash
java -cp out org.example.revenuecalculator.MonthlyRevenueCalculatorMain
```
### 3. Use Shell Script

```Alternatively run the script file after downloading the project.
./run.sh
```

## How to Run the Unit Tests

The project includes JUnit 5 tests (`MonthlyRevenueCalculatorTest`) run via Maven Surefire.

### Requirements

- **JDK 21**
- **Maven 3.6+**

JUnit 5 (`junit-jupiter` 5.10.2) is declared in `pom.xml` and downloaded automatically by Maven.

### 1. Run all tests

From the project root (the directory containing `pom.xml`):

```bash
mvn test
```

This compiles the source and test classes and executes every test. A passing run ends with:

```
[INFO] BUILD SUCCESS
```

Test results are also written to `target/surefire-reports/`.

### 2. Run a single test class

```bash
mvn test -Dtest=MonthlyRevenueCalculatorTest
```

### 3. Run a single test method

```bash
mvn test -Dtest=MonthlyRevenueCalculatorTest#returnsEmptyMapForNullList
```

### 4. Clean build then test

```bash
mvn clean test
```
---

## Assumptions

### 1. Order date field
`Order.getOrderDate()` returns a `java.time.LocalDate`.  
The assignment did not specify the date type; `LocalDate` was chosen as the standard Java 8 date type for a calendar date without time or timezone.

### 2. Amount field type
`Order.getAmount()` returns `java.math.BigDecimal`.  
`double` / `Double` was deliberately avoided -- revenue is financial data and floating-point arithmetic causes rounding errors (e.g. `0.1 + 0.2 != 0.3`). `BigDecimal` with `HALF_UP` rounding is the industry standard for money.

### 3. "Ignore null or negative amounts" -- zero included in ignored set
An order with `amount = 0` contributes nothing to revenue and is also filtered out.  
The filter is `amount > 0` (strictly positive).

### 4. Discount applies to the unit price, not a separate field
The 10% PREMIUM discount is applied to `order.getAmount()` directly.  
No separate `basePrice` or `discountedPrice` field is assumed to exist on the model.

### 5. Null safety scope
The following are all treated as invalid and silently skipped:
- The `Order` object itself being `null`
- `order.getAmount()` being `null`
- `order.getOrderDate()` being `null`
- `order.getCustomerType()` being `null`

No exception is thrown for these -- they are filtered out upstream before any computation.

### 6. Return type -- `Map<YearMonth, BigDecimal>`
The method returns a `TreeMap` (chronologically sorted) wrapped as `Map`.  
The caller receives months in natural order (Jan -> Feb -> ... -> Dec) without needing to sort separately.

### 7. Rounding
All amounts are rounded to **2 decimal places** using `RoundingMode.HALF_UP` -- standard for retail/financial rounding (e.g. Rs.180.005 -> Rs.180.01).