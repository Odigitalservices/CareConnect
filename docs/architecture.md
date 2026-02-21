# Architecture

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.4.2 / Java 21 |
| Database | PostgreSQL (Hibernate / Spring Data JPA) |
| Auth | JWT (JJWT library) — access token 24h, refresh token 7d |
| Mobile | Flutter 3.27.4 — flutter_bloc, go_router, dio, shared_preferences |
| Admin Web | Angular (standalone components, ReactiveFormsModule) |

## Backend Modules

```
com.careconnect
├── modules/
│   ├── auth/               # JWT filter, login, register, User entity
│   ├── booking/            # Bookings CRUD + cancel
│   └── professionals/      # Public browse, self-register profile, admin CRUD
└── shared/                 # ApiResponse, AppException, BaseEntity, PagedResponse
```

## API Endpoints

### Auth — `/api/auth` (public)
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register (PATIENT, PROFESSIONAL, PLATFORM_ADMIN, CLINIC_ADMIN) |
| POST | `/api/auth/login` | Login → returns accessToken + refreshToken |

### Professionals — `/api/professionals`
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/professionals` | Public | List verified professionals (filter: city, specialization, minRate, maxRate) |
| GET | `/api/professionals/{id}` | Public | Get professional detail + available slots |
| POST | `/api/professionals/profile` | PROFESSIONAL | Self-register professional profile |

### Admin Professionals — `/api/admin/professionals`
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/admin/professionals` | PLATFORM_ADMIN | List all professionals |
| POST | `/api/admin/professionals` | PLATFORM_ADMIN | Create professional (admin-side) |
| PUT | `/api/admin/professionals/{id}` | PLATFORM_ADMIN | Update professional |
| DELETE | `/api/admin/professionals/{id}` | PLATFORM_ADMIN | Delete professional |
| PATCH | `/api/admin/professionals/{id}/verify` | PLATFORM_ADMIN | Toggle verification |

### Bookings — `/api/bookings`
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/bookings` | PATIENT | Create booking (slotId + visitType) |
| GET | `/api/bookings` | PATIENT | List my bookings |
| PATCH | `/api/bookings/{id}/cancel` | PATIENT | Cancel a booking |

## Mobile App — Routes

| Route | Screen | Role |
|---|---|---|
| `/login` | LoginScreen | Public |
| `/register` | RegisterScreen | Public |
| `/home` | HomeScreen | PATIENT |
| `/professionals` | ProfessionalsListScreen | PATIENT |
| `/professionals/:id` | ProfessionalDetailScreen | PATIENT |
| `/bookings` | MyBookingsScreen | PATIENT |
| `/professional/home` | ProfessionalHomeScreen | PROFESSIONAL |
| `/professional/complete-profile` | ProfessionalProfileScreen | PROFESSIONAL |

## Role-Based Routing (after login/register)
- **PATIENT** → `/home`
- **PROFESSIONAL** → `/professional/home`
- **PLATFORM_ADMIN / CLINIC_ADMIN** → snackbar: "Please use the web admin portal"

## Security
- JWT secret in `application.yml` (`app.jwt.secret`)
- `JwtAuthFilter` extracts Bearer token, looks up user by UUID, sets `SecurityContextHolder`
- `@AuthenticationPrincipal User user` used in controllers (NOT `UserDetails`)
- CORS enabled for all origins (dev only — restrict in production)
- Method-level security via `@PreAuthorize` + `@EnableMethodSecurity`

## Storage
- Tokens stored in `SharedPreferences` on mobile (works over HTTP; `flutter_secure_storage` requires HTTPS)
- Admin token stored in `localStorage`

## Running Locally

```bash
# Backend (port 8080)
cd backend && ./mvnw spring-boot:run

# Angular admin (port 4200)
cd web-admin && npm start

# Flutter web (port 3000, accessible on LAN)
cd mobile && flutter run -d web-server --web-port 3000 --web-hostname 0.0.0.0
```

## Test Accounts

| Email | Password | Role |
|---|---|---|
| `superadmin@careconnect.ma` | `Admin123` | PLATFORM_ADMIN |
| `dr.hassan@careconnect.ma` | `Password123` | PROFESSIONAL (pending verification) |
| `test@test.com` | `Password123` | PATIENT |
