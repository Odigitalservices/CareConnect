# Professionals Frontend & Mobile Design

**Date:** 2026-02-21
**Feature:** Professional listing/search for Flutter mobile + Angular admin CRUD + backend admin endpoints

---

## Goal

Deliver the professional listing & search experience on both client surfaces, backed by a complete admin CRUD API.

---

## Backend: Admin Endpoints

### New route prefix: `/api/admin/professionals`

All routes require `PLATFORM_ADMIN` role (enforced in SecurityConfig + service layer).

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/admin/professionals` | List ALL professionals (verified + unverified) with email/phone |
| POST | `/api/admin/professionals` | Create user + professional profile in one step |
| PUT | `/api/admin/professionals/{id}` | Update specialization, bio, city, hourlyRate |
| DELETE | `/api/admin/professionals/{id}` | Hard delete professional + cascade |
| PATCH | `/api/admin/professionals/{id}/verify` | Toggle `verified` flag |

### New DTOs

**`CreateProfessionalRequest`** (Java record, validated):
- email, password, firstName, lastName, phone (nullable)
- specialization, licenseNumber, city, hourlyRate (nullable), bio (nullable)

**`UpdateProfessionalRequest`** (Java record):
- specialization, bio, city, hourlyRate — all nullable for partial update

**`ProfessionalAdminResponse`** (Java record):
- All fields of `ProfessionalDetailResponse` + email, phone, verified, licenseNumber

### Security

SecurityConfig updated: `/api/admin/**` requires authentication. Role check (`PLATFORM_ADMIN`) enforced in `AdminProfessionalsService` via `SecurityContextHolder`.

---

## Flutter Mobile

### Architecture

```
mobile/lib/features/professionals/
├── bloc/
│   ├── professionals_bloc.dart        # List: loading, filtering, pagination
│   ├── professionals_event.dart
│   ├── professionals_state.dart
│   └── professional_detail_cubit.dart # Detail: simple cubit
├── data/
│   ├── professional_repository.dart   # Interface
│   └── professional_repository_impl.dart
├── models/
│   ├── professional_summary.dart      # fromJson for list items
│   └── professional_detail.dart       # fromJson for detail + slots
└── screens/
    ├── professionals_list_screen.dart
    ├── professional_detail_screen.dart
    └── widgets/
        └── professional_card.dart
```

### BLoC: `ProfessionalsBloc`

**Events:** `ProfessionalsLoaded` (initial load), `ProfessionalsFiltered` (city/spec/rate changed), `ProfessionalsNextPage` (scroll to end)

**States:** `ProfessionalsInitial`, `ProfessionalsLoading`, `ProfessionalsLoaded` (list + hasMore bool + filters), `ProfessionalsError`

### Cubit: `ProfessionalDetailCubit`

**States:** `ProfessionalDetailInitial`, `ProfessionalDetailLoading`, `ProfessionalDetailLoaded`, `ProfessionalDetailError`

### Models

Plain Dart classes with `fromJson` factories — no code generation needed at this scale.

```dart
class ProfessionalSummary {
  final String id, firstName, lastName, specialization, city;
  final double? hourlyRate;
  // fromJson factory
}

class ProfessionalDetail extends ProfessionalSummary {
  final String? bio;
  final List<AvailabilitySlot> availableSlots;
}

class AvailabilitySlot {
  final String id;
  final DateTime startTime, endTime;
}
```

### Screens

**`ProfessionalsListScreen`:**
- AppBar: "Find a Professional"
- Search field: specialization (text)
- Filter chips row: city (dropdown), min/max rate (optional)
- `BlocBuilder<ProfessionalsBloc, ProfessionalsState>` → `ListView.builder` with `ProfessionalCard`
- `ScrollController` triggers `ProfessionalsNextPage` near end
- Error state shows retry button

**`ProfessionalDetailScreen`:**
- Receives `id` via GoRouter path parameter
- `BlocProvider<ProfessionalDetailCubit>` wraps the screen
- Shows: name, specialization, bio, city, rate
- Available slots section: list of time range chips
- Empty state: "No available slots at the moment"

### Routing

`app_router.dart` additions:
```dart
GoRoute(path: '/professionals', builder: ... ProfessionalsListScreen),
GoRoute(path: '/professionals/:id', builder: (ctx, state) =>
    ProfessionalDetailScreen(id: state.pathParameters['id']!)),
```

Home screen gets: `ElevatedButton("Find a Professional", onPressed: () => context.push('/professionals'))`

---

## Angular Admin

### Architecture

```
web-admin/src/app/features/professionals/
├── professionals.routes.ts
├── professionals-list.component.ts    # Table + filters + actions
├── professional-form-dialog.component.ts  # Create/Edit MatDialog
└── professional.service.ts            # HTTP calls via ApiService
```

### `ProfessionalService`

```typescript
@Injectable({ providedIn: 'root' })
export class ProfessionalService {
  list(filters: ProfessionalFilters): Observable<ApiResponse<PagedResponse<ProfessionalAdminSummary>>>
  create(data: CreateProfessionalDto): Observable<ApiResponse<ProfessionalAdminDetail>>
  update(id: string, data: UpdateProfessionalDto): Observable<ApiResponse<ProfessionalAdminDetail>>
  delete(id: string): Observable<ApiResponse<void>>
  toggleVerify(id: string): Observable<ApiResponse<ProfessionalAdminDetail>>
}
```

TypeScript interfaces mirror the backend DTOs.

### `ProfessionalsListComponent`

- `MatTable` with columns: Name, Specialization, City, Rate, Verified, Actions
- Filter form above table: search by name/spec, city input
- `MatPaginator` connected to service calls
- Verified column: `MatSlideToggle` calling `toggleVerify()`
- Actions column: Edit button (opens dialog) + Delete button (confirm via `MatDialog`)
- "Add Professional" button in toolbar

### `ProfessionalFormDialogComponent`

- `MAT_DIALOG_DATA` injection: `{ professional?: ProfessionalAdminDetail }` (null = create)
- Reactive form with `FormBuilder`:
  - Create mode: email, password, firstName, lastName, phone, specialization, licenseNumber, city, hourlyRate, bio
  - Edit mode: specialization, city, hourlyRate, bio (identity fields hidden)
- `MatFormField` + `MatInput` + `MatError` for validation messages
- Save button calls `create()` or `update()` then closes dialog with result

### Routing

`app.routes.ts` addition:
```typescript
{ path: 'professionals', loadComponent: () => import('./features/professionals/professionals-list.component') }
```

---

## What Is NOT in This Design (YAGNI)

- No booking flow from the mobile detail screen (future feature)
- No professional self-registration/profile management UI (future feature)
- No image upload for professional profiles
- No real-time slot availability (WebSocket — future feature)
- No Angular auth guard (login flow not yet implemented)
