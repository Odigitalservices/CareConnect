# CareConnect

Morocco's healthcare marketplace — connecting patients with verified nurses and physiotherapists.

## Stack
- Backend: Spring Boot 3.4.x / Java 21 LTS
- Mobile: Flutter 3.x (stable)
- Admin: Angular 19 LTS
- Database: PostgreSQL 16 LTS

## Local Development

### Prerequisites
- Java 21
- Flutter 3.x SDK
- Node.js 22 LTS
- Docker & Docker Compose

### Start services
```bash
docker compose up -d
```

### Backend
```bash
cd backend && ./mvnw spring-boot:run
```

### Admin web
```bash
cd web-admin && npm install && ng serve
```

### Mobile
```bash
cd mobile && flutter run
```
