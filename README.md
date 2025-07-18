# Employee Balance Management System 💰

A comprehensive Spring Boot application for managing employee balances with advanced querying, reporting, and bulk operations.

## 📋 Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [API Endpoints](#api-endpoints)
- [Balance Query Methods](#balance-query-methods)
- [Usage Examples](#usage-examples)
- [Comparison Guide](#comparison-guide)
- [Bulk Operations](#bulk-operations)
- [Reporting Features](#reporting-features)
- [Error Handling](#error-handling)
- [Contributing](#contributing)

## ✨ Features

- **Complete Balance CRUD Operations** - Create, read, update, and delete employee balances
- **Advanced Querying** - Find employees by balance ranges, thresholds, and exact amounts
- **Bulk Operations** - Process multiple balance updates in a single request
- **Real-time Statistics** - Get comprehensive balance analytics and reports
- **Validation & Security** - Built-in validation for all balance operations
- **Administrative Controls** - Freeze/unfreeze balances and adjustment tools

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- PostgreSQL 12+
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-repo/balance-management-system.git
   cd balance-management-system
   ```

2. **Configure Database**
   ```yaml
   # application.yml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/balance_db
       username: your_username
       password: your_password
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080/api/balance`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🌐 API Endpoints

### Basic Balance Operations

| HTTP Method | Endpoint | Description | Example |
|-------------|----------|-------------|---------|
| `GET` | `/api/balance/{pin}` | Get employee balance | `GET /api/balance/1234` |
| `PUT` | `/api/balance/update/{pin}?amount={value}` | Update balance (relative) | `PUT /api/balance/update/1234?amount=100` |
| `PUT` | `/api/balance/set/{pin}?newBalance={value}` | Set balance (absolute) | `PUT /api/balance/set/1234?newBalance=500` |

### Balance Query Endpoints

| Endpoint | Description | Example Response |
|----------|-------------|------------------|
| `GET /api/balance/less-than/{threshold}` | Balance < threshold (strict) | Employees with balance < 100 |
| `GET /api/balance/less-than-equal/{maxBalance}` | Balance ≤ maxBalance | Employees with balance ≤ 100 |
| `GET /api/balance/greater-than/{threshold}` | Balance > threshold (strict) | Employees with balance > 500 |
| `GET /api/balance/greater-than-equal/{minBalance}` | Balance ≥ minBalance | Employees with balance ≥ 500 |
| `GET /api/balance/exact/{balance}` | Balance = exact amount | Employees with exactly 250 balance |
| `GET /api/balance/between?minBalance=100&maxBalance=1000` | Balance range | Employees with balance 100-1000 |

### Statistics & Analytics

| Endpoint | Description | Returns |
|----------|-------------|---------|
| `GET /api/balance/statistics` | Comprehensive balance statistics | Total, average, min, max, counts |
| `GET /api/balance/total` | Sum of all active employee balances | BigDecimal value |
| `GET /api/balance/negative/count` | Count of employees with negative balance | Long value |
| `GET /api/balance/zero/count` | Count of employees with zero balance | Long value |

## 🔍 Balance Query Methods

### Repository Level
```java
// Strict comparisons
List<Employee> findByBalanceLessThan(BigDecimal threshold);
List<Employee> findByBalanceGreaterThan(BigDecimal threshold);

// Inclusive comparisons  
List<Employee> findByBalanceLessThanEqual(BigDecimal maxBalance);
List<Employee> findByBalanceGreaterThanEqual(BigDecimal minBalance);

// Range and exact queries
List<Employee> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
List<Employee> findByBalance(BigDecimal exactBalance);
```

### Service Level
```java
// Get employees by balance criteria
List<EmployeeDTO> getEmployeesWithBalanceLessThan(BigDecimal threshold);
List<EmployeeDTO> getEmployeesWithBalanceGreaterThan(BigDecimal threshold);
List<EmployeeDTO> getEmployeesWithBalanceBetween(BigDecimal min, BigDecimal max);
List<EmployeeDTO> getEmployeesWithExactBalance(BigDecimal exactBalance);

// Statistics and analytics
BalanceStatisticsDTO getBalanceStatistics();
BigDecimal getTotalActiveEmployeeBalances();
```

## 💡 Usage Examples

### 1. Get Employee Balance
```bash
curl -X GET "http://localhost:8080/api/balance/1234"
```
```json
{
  "balance": 750.50
}
```

### 2. Update Employee Balance (Add/Subtract)
```bash
# Add 100 to balance
curl -X PUT "http://localhost:8080/api/balance/update/1234?amount=100"

# Subtract 50 from balance  
curl -X PUT "http://localhost:8080/api/balance/update/1234?amount=-50"
```

### 3. Set Absolute Balance
```bash
curl -X PUT "http://localhost:8080/api/balance/set/1234?newBalance=1000"
```

### 4. Query Employees by Balance
```bash
# Get employees with balance less than 100
curl -X GET "http://localhost:8080/api/balance/less-than/100"

# Get employees with balance between 500-2000
curl -X GET "http://localhost:8080/api/balance/between?minBalance=500&maxBalance=2000"
```

### 5. Bulk Balance Updates
```bash
curl -X POST "http://localhost:8080/api/balance/bulk-update" \
  -H "Content-Type: application/json" \
  -d '{
    "updates": [
      {"pin": "1234", "amount": 100, "description": "Monthly bonus"},
      {"pin": "5678", "amount": -25, "description": "Meal deduction"}
    ]
  }'
```

### 6. Get Balance Statistics
```bash
curl -X GET "http://localhost:8080/api/balance/statistics"
```
```json
{
  "totalBalance": 45780.50,
  "averageBalance": 763.01,
  "minimumBalance": -120.00,
  "maximumBalance": 5000.00,
  "employeesWithNegativeBalance": 3,
  "employeesWithZeroBalance": 2,
  "totalActiveEmployees": 60
}
```

## 📊 Comparison Guide

Understanding the difference between balance query methods:

| Comparison | Method | Example (threshold = 100) | Use Case |
|------------|--------|---------------------------|----------|
| **<** | `findByBalanceLessThan` | Returns: 50, 75, 99.99 | Find employees who need urgent top-up |
| **≤** | `findByBalanceLessThanEqual` | Returns: 50, 75, 99.99, **100** | Find employees eligible for bonus |
| **>** | `findByBalanceGreaterThan` | Returns: 100.01, 150, 200 | Find employees with surplus |
| **≥** | `findByBalanceGreaterThanEqual` | Returns: **100**, 100.01, 150, 200 | Find employees meeting minimum threshold |
| **=** | `findByBalance` | Returns: **100** only | Find employees with exact balance |
| **Between** | `findByBalanceBetween(50, 150)` | Returns: 50, 75, 100, 125, 150 | Find employees in target range |

### Practical Examples

```java
// Find employees who urgently need balance top-up
balanceService.getEmployeesWithBalanceLessThan(new BigDecimal("50"));

// Find employees eligible for meal program (balance >= 100)
balanceService.getEmployeesWithBalanceGreaterThanEqual(new BigDecimal("100"));

// Find employees in optimal balance range
balanceService.getEmployeesWithBalanceBetween(
    new BigDecimal("100"), 
    new BigDecimal("1000")
);
```

## 🔄 Bulk Operations

### Bulk Update
```json
POST /api/balance/bulk-update
{
  "updates": [
    {"pin": "1234", "amount": 100, "description": "Salary credit", "reason": "SALARY"},
    {"pin": "5678", "amount": -30, "description": "Meal deduction", "reason": "MEAL"}
  ]
}
```

### Bulk Credit (Positive amounts only)
```json
POST /api/balance/bulk-credit
[
  {"pin": "1234", "amount": 500, "description": "Bonus payment"},
  {"pin": "5678", "amount": 200, "description": "Overtime pay"}
]
```

### Bulk Debit (Negative amounts only)
```json
POST /api/balance/bulk-debit
[
  {"pin": "1234", "amount": 25, "description": "Meal cost"},
  {"pin": "5678", "amount": 15, "description": "Coffee charge"}
]
```

### Response Format
```json
{
  "totalRequested": 2,
  "successful": 2,
  "failed": 0,
  "successfulPins": ["1234", "5678"],
  "failedPins": [],
  "totalAmountProcessed": 485.00,
  "successRate": 100.0,
  "summary": "Bulk update completed: 2/2 successful (100.0%), Amount processed: 485.00"
}
```

## 📈 Reporting Features

### Balance Reports

| Endpoint | Description | Use Case |
|----------|-------------|----------|
| `GET /api/balance/report` | All employees balance report | Monthly balance review |
| `GET /api/balance/report/negative` | Employees with negative balances | Identify accounts needing attention |
| `GET /api/balance/report/range?minBalance=0&maxBalance=100` | Employees in specific range | Target specific balance groups |

### Sample Report Output
```json
[
  {
    "pin": "1234",
    "username": "john.doe",
    "email": "john.doe@company.com",
    "designation": "Software Engineer",
    "balance": 750.50,
    "balanceStatus": "NORMAL"
  }
]
```

### Balance Status Categories
- **NEGATIVE**: Balance < 0
- **ZERO**: Balance = 0  
- **LOW**: 0 < Balance < 100
- **NORMAL**: 100 ≤ Balance ≤ 1000
- **HIGH**: Balance > 1000

## 🔧 Administrative Operations

### Balance Adjustments
```bash
# Set all negative balances to zero
POST /api/balance/adjust-negative

# Adjust specific employee to minimum balance
POST /api/balance/adjust-minimum/1234?minimumBalance=100
```

### Balance Controls
```bash
# Freeze employee balance (prevent operations)
POST /api/balance/freeze/1234

# Unfreeze employee balance
POST /api/balance/unfreeze/1234

# Check if balance is frozen
GET /api/balance/is-frozen/1234
```

### Validation Endpoints
```bash
# Check if employee has negative balance
GET /api/balance/has-negative/1234

# Check if employee has sufficient balance for operation
GET /api/balance/has-sufficient/1234?requiredAmount=100
```

## ⚠️ Error Handling

### Common HTTP Status Codes

| Status Code | Description | Example |
|-------------|-------------|---------|
| `200 OK` | Operation successful | Balance retrieved successfully |
| `400 Bad Request` | Invalid input data | Invalid PIN format or negative threshold |
| `404 Not Found` | Employee not found | PIN does not exist |
| `422 Unprocessable Entity` | Business rule violation | Insufficient balance for operation |
| `500 Internal Server Error` | System error | Database connection issue |

### Error Response Format
```json
{
  "timestamp": "2025-07-18T10:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with PIN: 1234",
  "path": "/api/balance/1234"
}
```

### Validation Rules

- **PIN**: Must be exactly 4 digits
- **Amount**: Cannot be null, supports up to 2 decimal places
- **Balance Operations**: Cannot result in balance below -1000 (configurable)
- **Frozen Accounts**: No balance operations allowed until unfrozen

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn integration-test
```

### API Testing with Postman
Import the Postman collection from `/docs/postman/Balance-API.json`

## 📝 Configuration

### Application Properties
```yaml
# Balance-specific configurations
app:
  balance:
    minimum-threshold: -1000.00
    maximum-single-transaction: 10000.00
    enable-balance-freeze: true
    auto-adjust-negative: false
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding standards
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🆘 Support

- **Documentation**: [Wiki](https://github.com/your-repo/wiki)
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/discussions)
- **Email**: support@yourcompany.com

## 🔄 Changelog

### Version 1.0.0 (2025-07-18)
- ✅ Complete balance CRUD operations
- ✅ Advanced balance querying (6 comparison methods)
- ✅ Bulk operations support
- ✅ Comprehensive reporting system
- ✅ Administrative controls and validation
- ✅ Real-time statistics and analytics

---

**Built with ❤️ using Spring Boot, PostgreSQL, and modern Java practices.**
