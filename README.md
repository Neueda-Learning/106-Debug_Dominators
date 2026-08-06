# FasterPay - Payment Processing System

FasterPay is a full-stack payment processing application with a Spring Boot backend and a modern frontend. It supports payment lifecycle operations, refunds, notifications, audit logs, campaigns/contributions, crypto payments, retry flow, and statement generation.

## Repository Structure

```text
payment-processing-system/
|- Backend/
|  |- README.md
|  `- payment-processing-system/
|     |- pom.xml
|     |- mvnw
|     |- mvnw.cmd
|     |- .mvn/
|     `- src/
|- Frontend/
|  |- README.md
|  |- package.json
|  `- src/
|- Dockerfile
|- docker-compose.yml
|- Jenkinsfile
`- start.sh
```

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Data JDBC, Spring Security, MySQL
- Docs and PDF: springdoc-openapi, OpenPDF
- Frontend: React + TypeScript + Vite
- DevOps: Docker, Docker Compose, Jenkins

## Quick Start

### 1) Backend (local)

From repository root:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Backend default port in config: `8082`

### 2) Frontend (local)

```powershell
Set-Location .\Frontend
npm install
npm run dev
```

### 3) Docker (full stack)

```powershell
docker compose up --build
```

## Configuration

Backend properties are in:

- `src/main/resources/application.properties`
- `Backend/payment-processing-system/src/main/resources/application.properties`

Main keys you will likely adjust:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `server.port`
- `app.cors.allowed-origins`

## Testing

Run all backend tests:

```powershell
.\mvnw.cmd test
```

Surefire reports are generated under `target/surefire-reports`.

## API Documentation

Swagger/OpenAPI UI is enabled via springdoc. URL depends on your active backend config and server port.

## Module Docs

- Backend details: `Backend/README.md`
- Frontend details: `Frontend/README.md`
