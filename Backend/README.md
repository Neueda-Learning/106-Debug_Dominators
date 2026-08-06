# Backend - FasterPay

This module contains the Spring Boot backend for FasterPay.

## Path

Backend module root:

- `Backend/payment-processing-system`

## Package Layout

Main package: `com.paymentprocessing.payment_processing_system`

- `controller` - REST endpoints
- `service` and `service/impl` - business logic
- `repository` - Spring Data JDBC repositories
- `model` - domain entities
- `dto` - API contracts
- `exception` - custom exceptions and global handler
- `security` - security configuration
- `util` - helper utilities

## Prerequisites

- Java 17+
- MySQL 8+

## Run Locally

```powershell
Set-Location .\Backend\payment-processing-system
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## Configuration

Backend config file:

- `Backend/payment-processing-system/src/main/resources/application.properties`

Important values:

- datasource URL, username, password
- server port (default `8082`)
- CORS allowed origins

## Testing

```powershell
Set-Location .\Backend\payment-processing-system
.\mvnw.cmd test
```

Reports:

- `Backend/payment-processing-system/target/surefire-reports`
