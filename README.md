# CareConnect

Morocco's healthcare marketplace — connecting patients with verified nurses and physiotherapists.

## Stack
- Backend: Spring Boot 3.4.x / Java 21 LTS
- Mobile: Flutter 3.x (stable)
- Admin: Angular 19 LTS
- Database: PostgreSQL 16 LTS

## Local Development

### Prerequisites
- Java 21 LTS
- Flutter 3.x SDK (stable channel)
- Node.js 22 LTS
- Docker & Docker Compose (Docker Engine 24+)
- Angular CLI: `npm install -g @angular/cli@19`

### Environment setup
```bash
cp .env.example .env
# Edit .env with your local values (defaults work for local dev)
```

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

## Project Structure

```
careconnect/
├── backend/       # Spring Boot modular monolith (Java 21)
├── mobile/        # Flutter app — patients & professionals
├── web-admin/     # Angular admin dashboard
└── docs/          # Product docs and implementation plans
```
