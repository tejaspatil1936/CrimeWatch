# CrimeWatch

A crowdsourced crime reporting platform with a live incident heatmap and a real-time police dashboard.

## Overview

CrimeWatch lets citizens report incidents with a location, category, and severity. Each report is automatically routed to the nearest patrol zone (by geographic distance), plotted on a public live heatmap, and streamed to a police dashboard. A background scheduler continuously watches report volume per zone and raises escalations when a configurable threshold is crossed within a lookback window. Administrators manage zones, users, and escalation thresholds, and can export all report data to CSV.

The backend is a server-rendered Spring Boot MVC application (JSP + JSTL views) with Firebase as the persistence layer — Firestore for structured data, and the Firebase Realtime Database as a push channel for live alerts.

## Features

- **Citizen incident reporting** — a public form (`/report/new`) that geocodes each submission to the nearest configured zone using a haversine distance calculation, works for both anonymous and logged-in users.
- **Live public heatmap** (`/map`) — Leaflet.js map with a `leaflet.heat` density layer plus individual severity-colored markers, backed by a public read-only JSON API.
- **Police dashboard** — live incoming-alert feed (polled every 5 seconds), a pending-reports queue with one-click "assign to me", a separate active (assigned / in-progress) reports view, and per-zone stats.
- **Admin console** — zone CRUD with an interactive Leaflet map picker for setting zone coordinates, inline escalation-threshold editing, user role management, a paginated audit-log viewer, and full CSV export of all reports (via OpenCSV).
- **Automatic zone escalation** — a scheduled sweep evaluates every zone on a fixed interval, counting pending/assigned reports in a lookback window; crossing the configured threshold raises a `WARNING`/`ALERT`/`CRITICAL` escalation, pushed to Firebase and broadcast over a raw socket.
- **Role-based access control** — three roles (`CITIZEN`, `OFFICER`, `ADMIN`) enforced through Spring Security with per-path authorization rules and method-level `@PreAuthorize`.
- **Audit trail** — every state-changing request (`POST`/`PUT`/`DELETE`/`PATCH`) is recorded to Firestore automatically by a servlet filter, independent of individual controller logic.
- **Analytics charts** — Chart.js visualizations on the admin home page: weekly average response time, reports by zone, and crime-type distribution.
- **Demo data seeding** — on first boot, if Firestore has no zones yet, `DemoDataSeeder` creates 3 zones, 4 users (one per role plus a second officer), and 4 sample reports so the app is usable immediately.

## Tech Stack

**Backend**
- Java 17, Spring Boot 3.2.5 (Web, Security, Validation, WebSocket starters)
- JSP + JSTL views rendered via `tomcat-embed-jasper` and a custom `InternalResourceViewResolver`
- Spring Security — form login, session cap of 1 per user, CSRF protection, role-based `HttpSecurity` rules, and `@PreAuthorize` method security
- A raw `java.net.ServerSocket` broadcaster (port 9090) that fans out new-report and escalation events as JSON to any connected TCP client
- `@Async` + `@Scheduled` escalation engine backed by a dedicated `ThreadPoolTaskExecutor`
- OpenCSV for the admin CSV export endpoint

**Database**
- Firebase Firestore (via the `firebase-admin` SDK) — primary store for crime reports, zones, users, escalations, and audit logs, accessed through a repository-interface / Firestore-implementation split
- Firebase Realtime Database — write-through channel for the `alerts/` and `escalations/` paths, intended for low-latency push updates

**Frontend**
- Server-rendered JSP/JSTL pages, no client-side framework
- Leaflet.js 1.9.4 + `leaflet.heat` for the public heatmap and the admin zone-picker map
- Chart.js 4.4.0 for admin analytics
- Vanilla JavaScript for polling, DOM rendering, and the socket demo client
- Lucide icons on the citizen landing page

**DevOps**
- Docker multi-stage build: Maven/Temurin 17 build stage producing a WAR, run on an `eclipse-temurin:17-jre-jammy` runtime image
- Tuned for Render's free tier (`-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError`), reads `PORT` and `FIREBASE_CREDENTIALS_JSON` from the environment
- `/healthz` endpoint for platform health checks
- Firebase CLI configuration (`firebase.json`, `database.rules.json`) for Realtime Database security rules

## Architecture

The application follows a conventional layered MVC structure:

```
Controller (MVC + REST)  →  Service  →  Repository (interface)  →  Firestore implementation
```

- **MVC controllers** (`AdminController`, `DashboardController`, `ReportController`, `AuthController`, `HomeController`, `MapController`) return JSP view names.
- **REST controllers** under `controller/api` (`ReportApiController`, `StatsApiController`) expose read-only JSON used by the map, dashboard polling, and admin charts.
- **Services** hold business logic: `CrimeReportService` (submission, zone assignment, status transitions, all inside Firestore transactions where consistency matters), `EscalationService` (async threshold evaluation), `ZoneService`, `UserService`, `AuditService`, `ExportService`, `StatsService`, `FirebaseAlertService`.
- **Repositories** are defined as interfaces (`CrimeReportRepository`, `ZoneRepository`, `UserRepository`, `AssignmentRepository`, `EscalationRepository`, `AuditLogRepository`) with a single Firestore-backed implementation each in `repository/impl`.
- **Cross-cutting concerns** are handled outside the controller layer: `AuditLoggingFilter` (a `jakarta.servlet.Filter`) records every mutating request to Firestore, and `AuthInterceptor` (a Spring `HandlerInterceptor`) logs the authenticated user on every request.
- **EscalationScheduler** runs on a fixed delay, iterating all zones and delegating each zone's evaluation to an `@Async` method so zones are checked concurrently.
- **SocketServer** accepts TCP connections on a background daemon thread and holds the client list; `AlertSocketBroadcaster` serializes report/escalation events and writes them to every connected client. `SocketClientDemo` is a minimal standalone client (`main()` method) that connects and prints incoming alerts, useful for demonstrating the socket channel outside the web app.

## Project Structure

```
src/main/java/com/crimewatch/
├── config/          Spring configuration (Security, Firebase, Async, MVC, JSP view resolver)
├── controller/       MVC controllers (admin, dashboard, report, auth, home, map)
│   └── api/          REST controllers (public reports, stats)
├── dto/              Request/response DTOs (report submission, alert payload, zone stats)
├── entity/            Domain entities (CrimeReport, Zone, User, Assignment, Escalation, AuditLog)
├── enums/             CrimeType, Severity, ReportStatus, Role, EscalationLevel
├── filter/            AuditLoggingFilter (servlet filter)
├── interceptor/       AuthInterceptor (Spring HandlerInterceptor)
├── repository/        Repository interfaces
│   └── impl/          Firestore-backed repository implementations
├── scheduler/         EscalationScheduler
├── security/          FirestoreUserDetailsService, CustomAuthenticationProvider
├── seed/              DemoDataSeeder (first-boot sample data)
├── service/           Business logic layer
├── socket/            Raw ServerSocket broadcaster + standalone demo client
└── util/              IdGenerator, FirestoreUtils

src/main/resources/
├── application.properties
└── static/{css,js}/    Stylesheets and client-side JS (heatmap, dashboard polling, charts)

src/main/webapp/WEB-INF/views/   JSP templates (citizen, police, admin, auth, layout, error)
```

## Getting Started

### Prerequisites

- JDK 17
- Maven 3.9.x
- A Firebase project with Firestore and Realtime Database enabled, plus a downloaded service-account JSON key

### Environment variables

`FirebaseConfig` and `application.properties` read the following at startup:

| Variable | Purpose | Default / fallback |
|---|---|---|
| `FIREBASE_CREDENTIALS_JSON` | Firebase service-account JSON, passed inline (used in cloud deployments) | falls back to reading the file at `firebase.credentials.path` |
| `firebase.credentials.path` | Local path to the service-account JSON file | `firebase-service-account.json` |
| `FIREBASE_DATABASE_URL` | Realtime Database URL | `firebase.database.url` in `application.properties` |
| `FIREBASE_PROJECT_ID` | Firebase project ID | `firebase.project.id` in `application.properties` |
| `PORT` / `SERVER_PORT` | HTTP port | `8081` |
| `socket.server.port` | Raw TCP broadcaster port | `9090` |
| `socket.server.enabled` | Toggles the socket server (disabled on some cloud hosts) | `true` |

The service-account file must never be committed — it's already excluded via `.gitignore`.

### Run locally

```bash
# 1. Place your Firebase service-account key at the project root
cp /path/to/your-key.json firebase-service-account.json

# 2. Point application.properties (or env vars) at your Firebase project
#    firebase.database.url, firebase.project.id

# 3. Run
mvn spring-boot:run
```

On first boot, `DemoDataSeeder` populates Firestore with sample zones, users, and reports if none exist. The app listens on `http://localhost:8081` by default.

### Build

```bash
mvn -DskipTests package
java -jar target/crimewatch-1.0.0.war
```

Or with Docker:

```bash
docker build -t crimewatch .
docker run -p 10000:10000 -e FIREBASE_CREDENTIALS_JSON="$(cat firebase-service-account.json)" crimewatch
```

## Usage

Demo accounts created by `DemoDataSeeder` on first boot:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Officer | `officer1` | `officer123` |
| Officer | `officer2` | `officer123` |
| Citizen | `citizen` | `citizen123` |

Key routes:

| Route | Access | Purpose |
|---|---|---|
| `/` | Public | Citizen landing page |
| `/report/new`, `/report/submit` | Public | Submit an incident |
| `/map` | Public | Live heatmap |
| `/report/my` | Authenticated | A citizen's own submitted reports |
| `/dashboard` | Role `OFFICER` | Live alert feed, pending/active reports, zone stats |
| `/admin` | Role `ADMIN` | Zones, users, escalations, audit log, CSV export |

## API Documentation

All API endpoints return JSON and are read-only.

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/api/public/reports` | Public | Full list of all crime reports |
| `GET` | `/api/public/reports/points` | Public | Report coordinates only (`{lat, lng}`), used for lightweight map rendering |
| `GET` | `/api/stats/weekly-response-time` | Authenticated | Average response time per week |
| `GET` | `/api/stats/reports-by-zone` | Authenticated | Report count grouped by zone |
| `GET` | `/api/stats/crime-type-distribution` | Authenticated | Report count grouped by crime type |
| `GET` | `/healthz` | Public | Plaintext health check (`ok`) |

`/api/public/**` is explicitly permitted for anonymous access in `SecurityConfig`; `/api/stats/**` isn't matched by any specific rule and so falls under `anyRequest().authenticated()` — any logged-in user, regardless of role, can read it.

## Design Decisions

- **Polling instead of the push channels it already writes to.** The backend pushes every new report and escalation to both the Firebase Realtime Database and the raw socket broadcaster, but the dashboard's own JavaScript (`dashboard.js`) polls `/api/public/reports` every 5 seconds instead of subscribing to either channel — the code comments this as intentional, since it avoids relaxing Realtime Database security rules for client-side reads.
- **WAR packaging for cloud JSP compilation.** The project is built and packaged as a WAR (rather than the Spring Boot default of an executable JAR with embedded classes), and `server.tomcat.basedir` is pointed at a writable `/tmp` directory — both changes exist specifically so JSPs compile correctly inside the embedded Tomcat container when deployed to Render.
- **Geographic auto-assignment of reports.** New reports aren't manually filed into a zone; `CrimeReportService` computes the nearest zone by haversine distance between the report's coordinates and each zone's center point.
- **Escalation evaluation is decoupled and concurrent.** `EscalationScheduler` runs one fixed-delay sweep across all zones, but delegates the actual per-zone check to an `@Async` method on a dedicated thread pool (`crimewatchTaskExecutor`), so zone evaluations run in parallel rather than sequentially blocking the scheduler thread.
- **Dual Firebase credential loading.** `FirebaseConfig` checks for a `FIREBASE_CREDENTIALS_JSON` environment variable first (for platforms where mounting a file is inconvenient) and only falls back to reading a local JSON file, letting the same code run unmodified in local development and in cloud deployment.
- **Cross-cutting audit logging via a servlet filter.** Rather than each controller calling an audit service explicitly, `AuditLoggingFilter` intercepts every mutating HTTP method at the servlet layer and writes a Firestore audit entry automatically, so new endpoints get audited without extra code.

## Future Improvements

- Wire the dashboard's live feed to the Firebase Realtime Database or the socket broadcaster it already receives events on, instead of polling `/api/public/reports` every 5 seconds.
- `CrimeReportRepository.findAll()` and `findByZoneId()` load entire Firestore collections into memory; only the CSV export path (`findAllPaged`) uses cursor-based pagination — extending that pattern to the report listing and dashboard endpoints would avoid unbounded reads as report volume grows.
- `database.rules.json` currently denies all reads and writes at the Realtime Database, which works today because the backend uses the Admin SDK — but any future client-side Firebase usage (e.g. a browser subscribing directly to `alerts/`) would need scoped rules first.
- `SocketClientDemo` demonstrates the raw TCP alert channel as a standalone `main()` program but isn't otherwise integrated into the app or documented as a supported integration point.
