# Rathinam Toastmasters Digital Platform - Backend API

The production-grade backend server for **Rathinam Toastmasters Club Digital Platform**, built as a modular monolith in **Java 21 LTS** with **Spring Boot 3.4.x**.

---

## 🛠 Tech Stack

- **Language:** Java 21 LTS
- **Framework:** Spring Boot 3.4.3
- **Build Tool:** Apache Maven
- **Database:** PostgreSQL (Used in both Development and Production)
- **ORM & Data Access:** Spring Data JPA / Hibernate
- **Database Migrations:** Flyway (Single source of truth for database schema changes)
- **Security:** Spring Security & JWT (Stateless authentication architecture)
- **Validation:** Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Monitoring:** Spring Boot Actuator
- **Testing:** JUnit 5, Mockito, Spring Boot Test, Spring Security Test

---

## 👤 Domain Modules Overview

### 1. Member Domain Module (`com.rathinam.toastmasters.modules.member`)
Represents Toastmasters club member profile data (`firstName`, `lastName`, `displayName`, `email`, `phoneNumber`, `profilePictureUrl`, `joinDate`, `status`, `bio`).

### 2. Account & Auth Module (`com.rathinam.toastmasters.modules.account`, `com.rathinam.toastmasters.modules.auth`)
Manages login credentials, password hashes (BCrypt), JWT token issuance/validation, and roles (`ADMIN`, `PRESIDENT`, `OFFICER`, `MEMBER`).

### 3. Meeting Domain Module (`com.rathinam.toastmasters.modules.meeting`)
Manages scheduled club meetings (`meetingNumber`, `meetingStart`, `meetingEnd`, `theme`, `meetingType`, `status`, `location`, `meetingUrl`, `description`).

### 4. Attendance Domain Module (`com.rathinam.toastmasters.modules.attendance`)
Connects a `Member` to a `Meeting` with attendance status (`PRESENT`, `ABSENT`, `EXCUSED`) and an optional check-in timestamp.

### 6. Points Engine Domain Module (`com.rathinam.toastmasters.modules.points`)
Provides a flexible, database-backed scoring system that decouples **Point Rules** (`PointRuleEntity`) from **Point Events** (`PointEventEntity`).

#### Core Architectural Concepts
1. **Point Rule vs Point Event**:
   - **Point Rule**: Defines scoring criteria (e.g., `ATTENDANCE_PRESENT` = +5, `ROLE_TOASTMASTER` = +10, `MANUAL_BONUS` = +1). Rule configurations are database-driven and editable by ExCom officers without code changes.
   - **Point Event**: Records an actual point award to a member. Stores a **snapshot of the actual points awarded** at event creation time.
2. **Historical Immutability**:
   - If ExCom changes a Point Rule's value in the future (e.g., Speaker points updated from 10 to 15), historical `PointEvent` records **retain their original awarded value (10 points)**.
   - Generic `PATCH` updates on `PointEventEntity` are prohibited to prevent silent point tampering. Point corrections are recorded as compensating events (positive or negative).
3. **Idempotency Mechanism**:
   - Enforced by a PostgreSQL partial unique index `idx_point_events_source_unique` on `(source_type, source_id) WHERE source_id IS NOT NULL`.
   - Prevents duplicate automatic point awards when reprocessing attendance or meeting role assignments.
4. **Dynamic Aggregation (No `totalPoints` on Member)**:
   - Member point totals, meeting totals, and monthly/date-range leaderboards are computed dynamically from `PointEventEntity` via SQL aggregation queries. `MemberEntity` remains unpolluted.

---

## 🗄 Database Tables Summary

- **`accounts`** (Flyway `V3`): `id` (UUID), `email` (VARCHAR, UNIQUE), `password_hash`, `role`, `is_enabled`, auditing columns.
- **`members`** (Flyway `V2`): `id` (UUID), `account_id` (FK -> accounts), `first_name`, `last_name`, `email` (UNIQUE INDEX LOWER(email)), `status`, `join_date`, auditing columns.
- **`meetings`** (Flyway `V4`): `id` (UUID), `meeting_number` (INTEGER, UNIQUE), `meeting_start` (TIMESTAMPTZ), `meeting_end`, `theme`, `meeting_type`, `status`, `location`, `meeting_url`, `description`, auditing columns.
- **`attendance`** (Flyway `V5`): `id` (UUID), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `member_id` (FK -> members ON DELETE RESTRICT), `status` (VARCHAR(20)), `check_in_time` (TIMESTAMPTZ), UNIQUE INDEX `(meeting_id, member_id)`, auditing columns.
- **`role_definitions`** (Flyway `V6`, `V7`): `id` (UUID), `name` (VARCHAR(100), UNIQUE INDEX LOWER(name)), `description` (TEXT), `is_active` (BOOLEAN), auditing columns.
- **`meeting_role_assignments`** (Flyway `V6`): `id` (UUID), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `role_definition_id` (FK -> role_definitions ON DELETE RESTRICT), `member_id` (FK -> members ON DELETE RESTRICT), UNIQUE INDEX `(meeting_id, member_id)`, UNIQUE INDEX `(meeting_id, role_definition_id)`, auditing columns.
- **`point_rules`** (Flyway `V8`, `V9`): `id` (UUID), `code` (VARCHAR(50), UNIQUE INDEX LOWER(code)), `name`, `description`, `points`, `is_active`, `category`, `role_definition_id` (FK -> role_definitions), auditing columns.
- **`point_events`** (Flyway `V8`): `id` (UUID), `member_id` (FK -> members ON DELETE RESTRICT), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `point_rule_id` (FK -> point_rules ON DELETE RESTRICT), `points`, `reason`, `source_type`, `source_id`, UNIQUE INDEX `(source_type, source_id) WHERE source_id IS NOT NULL`, auditing columns.

---

## 🔍 Currently Implemented Endpoints

### 1. Application & Actuator Health Checks
- **Public Endpoints:** `GET /api/v1/health`, `GET /actuator/health`

### 2. Authentication API (`/api/v1/auth`)
- `POST /api/v1/auth/login` (Public) - Authenticates user and returns JWT token
- `GET /api/v1/auth/me` (Authenticated) - Returns current user details

### 3. Member Domain API (`/api/v1/members`)
- `POST /api/v1/members` (Authenticated) - Create new member profile
- `GET /api/v1/members/{id}` (Authenticated) - Get member profile by ID
- `PATCH /api/v1/members/{id}` (Authenticated) - Update member profile

### 4. Meeting Domain API (`/api/v1/meetings`)
- `POST /api/v1/meetings` (Authenticated) - Create a new scheduled meeting
- `GET /api/v1/meetings/{id}` (Authenticated) - Retrieve meeting by UUID
- `GET /api/v1/meetings` (Authenticated) - Retrieve paginated meetings list
- `PATCH /api/v1/meetings/{id}` (Authenticated) - Partially update meeting details

### 5. Attendance Domain API (`/api/v1`)
- Base Path: `/api/v1`
- Authentication: Authenticated

| Method | Endpoint | Description | Request Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/meetings/{meetingId}/attendance` | Record attendance for a member at a meeting | `CreateAttendanceRequest` | `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `GET` | `/api/v1/meetings/{meetingId}/attendance` | Get all attendance records for a meeting | None | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/attendance/{attendanceId}` | Get single attendance record by ID | None | `200 OK`, `404 Not Found` |
| `PATCH` | `/api/v1/attendance/{attendanceId}` | Update attendance record status/time | `UpdateAttendanceRequest` | `200 OK`, `400 Bad Request`, `404 Not Found` |

### 6. Meeting Roles Domain API (`/api/v1`)
- Restricted Write Operations: `ADMIN`, `PRESIDENT`, `OFFICER`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/roles` | Create new customizable role definition | `201 Created`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/roles` | List all active/inactive role definitions | `200 OK` |
| `PATCH` | `/api/v1/roles/{id}` | Update role definition name or active status | `200 OK`, `403 Forbidden`, `404 Not Found` |
| `POST` | `/api/v1/meetings/{meetingId}/roles` | Assign role to member in a meeting | `201 Created`, `400 Bad Request`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/meetings/{meetingId}/roles` | Get role assignments for a meeting | `200 OK`, `404 Not Found` |

### 13. Notifications & Reminders Module (`com.rathinam.toastmasters.modules.notification`)
Provides in-app notification storage, tracking, and reminder abstractions for members across meeting events, role assignments, point awards, and achievements.

#### Core Architectural Concepts
1. **Delivery Channel Abstraction (`NotificationChannel`)**:
   - Primary interface (`void send(NotificationEntity notification)`) decoupled from specific delivery mechanisms.
   - Initial implementation (`InAppNotificationChannel`) persists notifications to PostgreSQL database via `NotificationRepository`.
   - Extensible for future `EmailNotificationChannel` or `PushNotificationChannel` implementations without modifying business services.
2. **Strict Member Isolation**:
   - Member notification retrieval, unread counting, and mark-as-read operations strictly filter by the authenticated user's `member_id` (`@AuthenticationPrincipal CustomUserDetails` / `MemberRepository.findByEmailIgnoreCase`).
   - Prevents members from viewing or altering other members' notification records.
3. **Idempotency & Duplicate Notification Protection**:
   - `NotificationService.notifyMember(...)` checks `source_type` and `source_id` before dispatching. If a notification for the same source event already exists for a member, creation is skipped idempotently.
4. **Historical Immutability**:
   - Notifications are immutable records. Content modification APIs are omitted. Only `readAt` timestamp is updated when marked read.

---

## 🗄 Database Tables Summary

- **`accounts`** (Flyway `V3`): `id` (UUID), `email` (VARCHAR, UNIQUE), `password_hash`, `role`, `is_enabled`, auditing columns.
- **`members`** (Flyway `V2`): `id` (UUID), `account_id` (FK -> accounts), `first_name`, `last_name`, `email` (UNIQUE INDEX LOWER(email)), `status`, `join_date`, auditing columns.
- **`meetings`** (Flyway `V4`): `id` (UUID), `meeting_number` (INTEGER, UNIQUE), `meeting_start` (TIMESTAMPTZ), `meeting_end`, `theme`, `meeting_type`, `status`, `location`, `meeting_url`, `description`, auditing columns.
- **`attendance`** (Flyway `V5`): `id` (UUID), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `member_id` (FK -> members ON DELETE RESTRICT), `status` (VARCHAR(20)), `check_in_time` (TIMESTAMPTZ), UNIQUE INDEX `(meeting_id, member_id)`, auditing columns.
- **`role_definitions`** (Flyway `V6`, `V7`): `id` (UUID), `name` (VARCHAR(100), UNIQUE INDEX LOWER(name)), `description` (TEXT), `is_active` (BOOLEAN), auditing columns.
- **`meeting_role_assignments`** (Flyway `V6`): `id` (UUID), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `role_definition_id` (FK -> role_definitions ON DELETE RESTRICT), `member_id` (FK -> members ON DELETE RESTRICT), UNIQUE INDEX `(meeting_id, member_id)`, UNIQUE INDEX `(meeting_id, role_definition_id)`, auditing columns.
- **`point_rules`** (Flyway `V8`, `V9`): `id` (UUID), `code` (VARCHAR(50), UNIQUE INDEX LOWER(code)), `name`, `description`, `points`, `is_active`, `category`, `role_definition_id` (FK -> role_definitions), auditing columns.
- **`point_events`** (Flyway `V8`): `id` (UUID), `member_id` (FK -> members ON DELETE RESTRICT), `meeting_id` (FK -> meetings ON DELETE RESTRICT), `point_rule_id` (FK -> point_rules ON DELETE RESTRICT), `points`, `reason`, `source_type`, `source_id`, UNIQUE INDEX `(source_type, source_id) WHERE source_id IS NOT NULL`, auditing columns.
- **`achievement_definitions`** (Flyway `V10`, `V11`): `id` (UUID), `code` (VARCHAR(50), UNIQUE INDEX LOWER(code)), `name`, `description`, `icon`, `category`, `criteria_type`, `criteria_threshold`, `is_repeatable`, `is_active`, auditing columns.
- **`member_achievements`** (Flyway `V10`): `id` (UUID), `member_id` (FK -> members ON DELETE RESTRICT), `achievement_definition_id` (FK -> achievement_definitions ON DELETE RESTRICT), `earned_at`, `meeting_id` (FK -> meetings), `reason`, partial UNIQUE INDEX `idx_member_achievements_unique_non_repeatable`, auditing columns.
- **`certificates`** (Flyway `V10`): `id` (UUID), `certificate_number` (VARCHAR(100), UNIQUE INDEX LOWER(certificate_number)), `member_id` (FK -> members ON DELETE RESTRICT), `certificate_type`, `title`, `description`, `issued_date`, `achievement_id` (FK -> member_achievements), `status`, auditing columns.
- **`notifications`** (Flyway `V12`): `id` (UUID), `member_id` (FK -> members ON DELETE RESTRICT), `type`, `title`, `message`, `meeting_id` (FK -> meetings), `source_type`, `source_id`, `read_at`, auditing columns.

---

## 🔍 Currently Implemented Endpoints

### 1. Application & Actuator Health Checks
- **Public Endpoints:** `GET /api/v1/health`, `GET /actuator/health`

### 2. Authentication API (`/api/v1/auth`)
- `POST /api/v1/auth/login` (Public) - Authenticates user and returns JWT token
- `GET /api/v1/auth/me` (Authenticated) - Returns current user details

### 3. Member Domain API (`/api/v1/members`)
- `POST /api/v1/members` (Authenticated) - Create new member profile
- `GET /api/v1/members/{id}` (Authenticated) - Get member profile by ID
- `PATCH /api/v1/members/{id}` (Authenticated) - Update member profile

### 4. Meeting Domain & Workflow API (`/api/v1/meetings`)
- `POST /api/v1/meetings` (Authenticated) - Create a new scheduled meeting
- `GET /api/v1/meetings/{id}` (Authenticated) - Retrieve meeting by UUID
- `GET /api/v1/meetings` (Authenticated) - Retrieve paginated meetings list
- `PATCH /api/v1/meetings/{id}` (Authenticated) - Partially update meeting details
- `POST /api/v1/meetings/{id}/start` (Officers only) - Transition meeting from `SCHEDULED` to `IN_PROGRESS`
- `POST /api/v1/meetings/{id}/complete` (Officers only) - Transition meeting from `IN_PROGRESS` to `COMPLETED`
- `GET /api/v1/meetings/{id}/workflow` (Authenticated) - Retrieve unified meeting workflow status, warnings, and metrics summary

### 5. Attendance Domain API (`/api/v1`)
- Base Path: `/api/v1`
- Authentication: Authenticated

| Method | Endpoint | Description | Request Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/meetings/{meetingId}/attendance` | Record attendance for a member at a meeting | `CreateAttendanceRequest` | `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `GET` | `/api/v1/meetings/{meetingId}/attendance` | Get all attendance records for a meeting | None | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/attendance/{attendanceId}` | Get single attendance record by ID | None | `200 OK`, `404 Not Found` |
| `PATCH` | `/api/v1/attendance/{attendanceId}` | Update attendance record status/time | `UpdateAttendanceRequest` | `200 OK`, `400 Bad Request`, `404 Not Found` |

### 6. Meeting Roles Domain API (`/api/v1`)
- Restricted Write Operations: `ADMIN`, `PRESIDENT`, `OFFICER`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/roles` | Create new customizable role definition | `201 Created`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/roles` | List all active/inactive role definitions | `200 OK` |
| `PATCH` | `/api/v1/roles/{id}` | Update role definition name or active status | `200 OK`, `403 Forbidden`, `404 Not Found` |
| `POST` | `/api/v1/meetings/{meetingId}/roles` | Assign role to member in a meeting | `201 Created`, `400 Bad Request`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/meetings/{meetingId}/roles` | Get role assignments for a meeting | `200 OK`, `404 Not Found` |

### 7. Points Engine Domain API (`/api/v1`)
- Restricted Write Operations: `ADMIN`, `PRESIDENT`, `OFFICER`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/point-rules` | Create new point rule (Officers only) | `201 Created`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/point-rules` | List all active/inactive point rules | `200 OK` |
| `PATCH` | `/api/v1/point-rules/{id}` | Update point rule points or active status | `200 OK`, `403 Forbidden`, `404 Not Found` |
| `POST` | `/api/v1/points/manual` | Award manual point adjustment (Officers only) | `201 Created`, `400 Bad Request`, `403 Forbidden`, `404 Not Found` |
| `GET` | `/api/v1/points/{eventId}` | Get single point event details | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/members/{memberId}/points` | Get member total points & point events history | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/meetings/{meetingId}/points` | Get meeting points summary breakdown | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/points/leaderboard` | Get member points leaderboard ranking | `200 OK` |

### 8. Rankings & Monthly Championship Domain API (`/api/v1`)
- Authentication: Authenticated (`MEMBER`, `OFFICER`, `PRESIDENT`, `ADMIN`)

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/rankings/leaderboard` | Get paginated date-range leaderboard (`?from=&to=&page=&size=`) | `200 OK`, `400 Bad Request` |
| `GET` | `/api/v1/rankings/monthly/{year}/{month}` | Get monthly member rankings (`?page=&size=`) | `200 OK`, `400 Bad Request` |
| `GET` | `/api/v1/rankings/member/{memberId}` | Get individual member rank & points for a specific month (`?year=&month=`) | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/rankings/meetings/{meetingId}` | Get member rankings for a specific meeting | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/rankings/member/{memberId}/trends` | Get historical monthly performance point trends for a member | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/championships/monthly/{year}/{month}` | Get monthly championship leaderboard and winner | `200 OK`, `400 Bad Request` |
| `GET` | `/api/v1/championships/monthly/current` | Get current month's championship leaderboard and winner | `200 OK` |

### 9. Badges, Achievements & Certificates Domain API (`/api/v1`)
- Restricted Write Operations: `ADMIN`, `PRESIDENT`, `OFFICER`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/achievements` | Create new achievement definition (Officers only) | `201 Created`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/achievements` | List all active/inactive achievement definitions | `200 OK` |
| `GET` | `/api/v1/achievements/{id}` | Get achievement definition by ID | `200 OK`, `404 Not Found` |
| `PATCH` | `/api/v1/achievements/{id}` | Update achievement definition (Officers only) | `200 OK`, `403 Forbidden`, `404 Not Found` |
| `GET` | `/api/v1/members/{memberId}/achievements` | Get all achievements earned by a member | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/members/{memberId}/badges` | Get visual badges representation for a member | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/members/{memberId}/achievements/evaluate` | Trigger achievement evaluation for a member | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/certificates` | Issue a new certificate to a member (Officers only) | `201 Created`, `403 Forbidden`, `409 Conflict` |
| `GET` | `/api/v1/certificates/{id}` | Get certificate by ID | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/members/{memberId}/certificates` | Get all certificates issued to a member | `200 OK`, `404 Not Found` |

### 10. Analytics & Reports Domain API (`/api/v1/analytics`)
- Authentication: Authenticated (`MEMBER`, `OFFICER`, `PRESIDENT`, `ADMIN`)

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/analytics/members/{memberId}` | Get member analytics metrics card summary | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/analytics/meetings/{meetingId}` | Get meeting performance & attendance analytics | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/analytics/overview` | Get high-level club overview statistics | `200 OK` |
| `GET` | `/api/v1/analytics/monthly/{year}/{month}` | Get monthly metrics & top performers breakdown | `200 OK`, `400 Bad Request` |
| `GET` | `/api/v1/analytics/members/{memberId}/performance` | Get chronological monthly performance trend for charts (`?months=6`) | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/analytics/reports/monthly/{year}/{month}` | Get structured monthly report for executive viewing & future AI integration | `200 OK`, `400 Bad Request` |

### 11. AI Meeting Summary & Insights Domain API (`/api/v1/ai`)
- Authentication: Authenticated (`MEMBER`, `OFFICER`, `PRESIDENT`, `ADMIN`)

| Method | Endpoint | Description | Request Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/ai/meetings/{meetingId}/summary` | Generate AI meeting summary & insights | `GenerateMeetingSummaryRequest` | `200 OK`, `404 Not Found`, `429 Too Many Requests`, `503 Service Unavailable` |

### 12. Notifications & Reminders Domain API (`/api/v1/notifications`)
- Authentication: Authenticated (`MEMBER`, `OFFICER`, `PRESIDENT`, `ADMIN`)

| Method | Endpoint | Description | Request Body | Status Codes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/notifications` | Get paginated notification history for current logged-in member | None | `200 OK`, `403 Forbidden` |
| `GET` | `/api/v1/notifications/unread` | Get paginated unread notifications for current logged-in member | None | `200 OK`, `403 Forbidden` |
| `GET` | `/api/v1/notifications/unread/count` | Get count of unread notifications for current logged-in member | None | `200 OK`, `403 Forbidden` |
| `PATCH` | `/api/v1/notifications/{id}/read` | Mark single notification as read | None | `200 OK`, `404 Not Found`, `403 Forbidden` |
| `PATCH` | `/api/v1/notifications/read-all` | Mark all unread notifications as read for current member | None | `200 OK`, `403 Forbidden` |

---

## 🔒 Production Readiness & Environment Configuration

### Backend Environment Variables (`rathinam-backend`)
All sensitive production configurations are read from environment variables:

| Environment Variable | Description | Required in Production | Default / Fallback |
| :--- | :--- | :---: | :--- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL | **Yes** | `jdbc:postgresql://localhost:5432/rathinam_dev_db` (Dev) |
| `SPRING_DATASOURCE_USERNAME` | Database username | **Yes** | `postgres` (Dev) |
| `SPRING_DATASOURCE_PASSWORD` | Database password | **Yes** | `postgres` (Dev) |
| `JWT_SECRET` | 256-bit HMAC signing key for JWT tokens | **Yes** | Externalized |
| `JWT_EXPIRATION_MS` | JWT validity duration in milliseconds | Optional | `86400000` (24 Hours) |
| `GEMINI_API_KEY` | Google Gemini API key for meeting AI insights | **Yes** | Disabled gracefully if blank |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | **Yes** | `http://localhost:3000,http://localhost:5173` (Dev) |
| `PORT` | Spring Boot server port | Optional | `8080` |

### Frontend Environment Variables (`rathinam-frontend`)

| Environment Variable | Description | Example Value |
| :--- | :--- | :--- |
| `VITE_API_BASE_URL` | Deployed backend REST API base URL | `https://rathinam-backend.onrender.com/api/v1` |

> [!CAUTION]
> Never expose `JWT_SECRET`, `GEMINI_API_KEY`, or `SPRING_DATASOURCE_PASSWORD` in frontend environment variables. Frontend environment variables prefixed with `VITE_` are bundled into client JavaScript and visible to web browsers.
