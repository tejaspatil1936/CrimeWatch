# CrimeWatch — Crowdsourced Crime Reporting & Live Heatmap
## Complete System Design & Implementation Specification for Claude Code

> **Document purpose:** This is a full build specification. Claude Code should treat every section below as authoritative. Implement in the order given. Do not improvise technology choices — they are rubric-constrained.

---

**Course:** Advanced Java / Enterprise Application Development (EAD)
**Course Code:** 2304269
**Program:** SY B.Tech | AY 2025–26
**Team Members:** `[Member 1 Name — Roll No.]`, `[Member 2 Name — Roll No.]`
**Mini Project Title:** CrimeWatch — Crowdsourced Crime Reporting & Live Heatmap

---

## Table of Contents

0. [Directive to Claude Code](#0-directive-to-claude-code)
1. [Project Overview](#1-project-overview)
2. [SDG Alignment](#2-sdg-alignment)
3. [CO (Course Outcome) Mapping](#3-co-mapping)
4. [Rubric Mapping & Honest Tradeoffs](#4-rubric-mapping--honest-tradeoffs)
5. [Technology Stack](#5-technology-stack)
6. [System Architecture](#6-system-architecture)
7. [Firebase Data Model](#7-firebase-data-model)
8. [Entity Relationship Diagram (Logical)](#8-entity-relationship-diagram-logical)
9. [Module Breakdown](#9-module-breakdown)
10. [Folder / Package Structure](#10-folder--package-structure)
11. [Maven Dependencies (`pom.xml`)](#11-maven-dependencies-pomxml)
12. [Configuration Files](#12-configuration-files)
13. [Firebase Setup Steps](#13-firebase-setup-steps)
14. [Entity (POJO) Classes](#14-entity-pojo-classes)
15. [Repository Layer (Firestore-backed)](#15-repository-layer)
16. [Service Layer](#16-service-layer)
17. [Controller Layer & REST/MVC Endpoints](#17-controller-layer)
18. [Spring Security & Role-Based Access](#18-spring-security--role-based-access)
19. [Servlet Filter & Spring Interceptor (CO2)](#19-servlet-filter--spring-interceptor-co2)
20. [Multithreading Module (CO1)](#20-multithreading-module-co1)
21. [Socket Programming Module (CO4)](#21-socket-programming-module-co4)
22. [JSP Views & JSTL Pages](#22-jsp-views--jstl-pages)
23. [UI Design System — Editorial Charcoal + Amber](#23-ui-design-system)
24. [Seed Data](#24-seed-data)
25. [Build & Run Instructions](#25-build--run-instructions)
26. [Testing Plan](#26-testing-plan)
27. [Deliverables Checklist](#27-deliverables-checklist)
28. [Future Scope](#28-future-scope)

---

## 0. Directive to Claude Code

You are building a complete Spring Boot 3.x web application with **Firebase Firestore** (primary DB) and **Firebase Realtime Database** (live alert stream) as the data layer, **no MySQL**. Use **JSP + JSTL** for the view layer. Apply **Spring Security** with three roles. Implement a **Java `ServerSocket` broadcaster**, a **`@Async` + `@Scheduled` escalation engine**, a **Servlet filter** for audit logging, and a **Spring Interceptor** for authentication logging.

**Implementation order (strict):**

1. Scaffold the Maven project with exact dependencies from §11.
2. Create all config classes (§12) — Firebase, Async, Security, Web.
3. Create entity POJOs (§14).
4. Create repository interfaces and Firestore implementations (§15).
5. Create services (§16).
6. Create controllers (§17).
7. Create JSP views (§22) with the exact design tokens from §23.
8. Implement the Socket server (§21) and start it with `@PostConstruct`.
9. Implement the escalation scheduler (§20).
10. Implement seed data runner (§24).
11. Verify against the rubric scorecard (§4).

**Rules:**

- Do not swap Firebase for MySQL. The user has made this choice explicitly.
- Do not introduce additional third-party UI libraries beyond what is listed (Bootstrap 5, Leaflet.js, Chart.js).
- Do not use neon, gradient-heavy, or "AI-aesthetic" styling. The UI is editorial: deep charcoal surfaces, warm paper backgrounds, amber accents, hairline rules. See §23.
- Every `@Service` method that modifies multiple documents must use `firestore.runTransaction(...)` — this is our `@Transactional` substitute.
- Every repository must implement a named interface (e.g., `CrimeReportRepository`) with a Firestore implementation (`FirestoreCrimeReportRepository`). This preserves the rubric's Repository pattern expectation.
- Include a `README.md` at the repository root with build/run steps, Firebase setup, and a one-page project summary.

---

## 1. Project Overview

**CrimeWatch** is a crowdsourced civic safety platform. Citizens anonymously report crimes or suspicious activity with geo-coordinates. The system aggregates reports into a live heatmap and pushes real-time alerts to a Police Dashboard via Java Sockets *and* Firebase Realtime Database. An automated multithreaded escalation engine watches each city zone for report spikes and triggers alerts when thresholds are breached.

### Problem Statement

Urban areas lack a unified, accessible platform for citizens to report safety incidents quickly and anonymously. Traditional reporting channels (phone, in-person) are slow, intimidating, and hard to aggregate. Police departments therefore receive delayed, fragmented information — limiting proactive response. CrimeWatch bridges this gap with real-time, geo-tagged, crowdsourced crime intelligence and a dedicated officer dashboard.

### Key Features

| Feature | Description |
|---|---|
| Anonymous reporting | Citizens can report without creating an account; optionally log in via Firebase Auth |
| Live crime heatmap | Leaflet.js + Leaflet.heat plugin visualises report density |
| Police dashboard | Real-time incoming alerts via Firebase listener + Java Socket client |
| Zone escalation | `@Scheduled` + `@Async` job auto-detects report spikes per zone |
| Role-based access | Citizen, Officer, Admin — enforced by Spring Security |
| Audit trail | Every state-changing action logged to `audit_log` collection via Servlet filter |
| Batch export | Admin exports reports to CSV using Firestore batch read + `OpenCSV` |
| Multithreaded dispatch | Alerts fanned out via `ThreadPoolTaskExecutor` |

---

## 2. SDG Alignment

| SDG | Goal | How CrimeWatch Addresses It |
|---|---|---|
| **SDG 16** | Peace, Justice and Strong Institutions | Empowers citizens to participate in community safety; creates a transparent, auditable crime-reporting record; strengthens police response times |
| **SDG 11** | Sustainable Cities and Communities | Enables data-driven policing and civic engagement for safer urban environments |

### Measurable SDG Impact Metric

**Primary:** Average Police Response Time (minutes)
Calculated as `resolvedAt − reportedAt` averaged across resolved reports per week. Displayed on the Admin dashboard as a Chart.js line chart with weekly buckets.

**Secondary metrics:**

- Reports per zone (bar chart)
- Resolution rate (`RESOLVED / total`)
- Escalation frequency (count per zone per week)
- Anonymous vs registered report ratio

### SDG Justification (≤200 words — for project report)

CrimeWatch directly advances **SDG 16 (Peace, Justice and Strong Institutions)** by democratising crime reporting and creating a transparent, accountable incident-tracking system. Anonymous submissions remove the fear-of-retaliation barrier that suppresses reporting today. Each report is timestamped, geo-tagged, and traceable through resolution, producing an auditable institutional record. The Police Dashboard converts raw reports into actionable intelligence — officers see incoming alerts in under a second, enabling faster triage and data-driven resource allocation.

The platform also supports **SDG 11 (Sustainable Cities and Communities)** by transforming civic reports into a visual heatmap that city planners and law enforcement can use to identify high-risk zones, allocate patrols efficiently, and measure the effectiveness of safety interventions over time. Our measurable impact indicator, **Average Police Response Time Reduction**, provides a quantifiable before/after metric. As reports accumulate and assignment workflows mature, response times should trend downward — giving municipalities a concrete way to demonstrate institutional progress under SDG 16.3 (access to justice) and SDG 11.7 (safe public spaces).

---

## 3. CO Mapping

| CO | Bloom's Level | Concept Area | How CrimeWatch Fulfills It |
|---|---|---|---|
| **CO1** | L3 | OOP, JDBC, Multithreading | POJO entity classes with encapsulation + validation; `@Async` + `ThreadPoolTaskExecutor` for escalation dispatch; `@Scheduled` periodic zone sweep; Firestore batch writes as batch-operation analogue |
| **CO2** | L3 | Servlet & JSP API | JSP + JSTL for all views; `AuditLoggingFilter` (Servlet filter); `AuthInterceptor` (Spring Interceptor); session management; form-based login; JSTL `c:forEach`, `c:if`, `fmt:formatDate` |
| **CO3** | L3 | Spring + ORM fullstack | Spring Boot 3.x MVC with `@Controller` → `@Service` → `@Repository` layering; custom Repository pattern over Firebase Admin SDK; Firestore transactions (`firestore.runTransaction`) as `@Transactional` substitute |
| **CO4** | L3 | Socket Programming | Java `ServerSocket` on port 9090; `CopyOnWriteArrayList<PrintWriter>` for multi-client fan-out; browser connects via WebSocket bridge to same message bus |

---

## 4. Rubric Mapping & Honest Tradeoffs

### Rubric scorecard under Firebase-only architecture

| Rubric Component | Max | Projected | How CrimeWatch Covers It |
|---|---|---|---|
| **Requirement & Design** | 5 | **4** | Problem clarity ✅, SDG linkage ✅, use-case diagram ✅, class diagram ✅, logical ER diagram ✅, Firebase data model ✅. **−1** — no Hibernate entity mapping with relational FK constraints (Firebase is NoSQL). |
| **Back-end Development** | 5 | **3** | `@Controller` / `@Service` / `@Repository` layers ✅, Firestore transactions as `@Transactional` ✅, batch operations ✅, Firebase query API used extensively ✅. **−2** — no Hibernate CRUD with HQL/Criteria API, no JDBC `PreparedStatement` (no relational DB exists to run them against). |
| **Front-end / Web Layer** | 5 | **5** | JSP + JSTL views ✅, server-side form validation ✅, `AuditLoggingFilter` Servlet filter ✅, `AuthInterceptor` Spring Interceptor ✅, Spring Security with 3 roles ✅, session management ✅, MVC + REST endpoints ✅. |
| **Multithreading / Socket** | 3 | **3** | `@Async` + `ThreadPoolTaskExecutor` escalation dispatch ✅, `@Scheduled` zone sweep ✅, Java `ServerSocket` real-time broadcaster ✅. |
| **SDG Integration** | 2 | **2** | SDG 16/11 clearly mapped ✅, measurable metric (Avg Response Time) ✅, dashboard chart ✅, ≤200-word justification ✅. |
| **Total** | **20** | **17** | |

### What's gained by going Firebase-only

- Realtime UI updates are trivial (Firebase JS SDK `onValue`)
- No schema migrations, no DDL
- Offline-first capability available via Firebase JS SDK
- Free tier is generous for a student project
- Cleaner demo (no local MySQL install required)

### What's lost

- ~3 rubric marks (Hibernate/JPA/HQL/JDBC sections)
- Declarative JPA relationships (`@OneToMany`, `@ManyToMany`)
- Viva examiners may ask "why not MySQL" — prepare a one-paragraph justification emphasising realtime requirements

### Mitigations (already baked into this design)

- Custom `Repository` pattern interfaces preserve the layering the rubric cares about
- `firestore.runTransaction(...)` wrapping in every multi-document write preserves the transactional semantics
- Firestore batch writes demonstrate batch-operation discipline (conceptual JDBC batch analogue)
- Explicit viva preparation notes in §27

---

## 5. Technology Stack

### Backend

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.x |
| Web | Spring MVC | bundled |
| Security | Spring Security | 6.x |
| View | JSP + JSTL | Jakarta JSTL 3.0 |
| Database (primary) | Firebase Firestore | via Admin SDK 9.2.x |
| Database (realtime) | Firebase Realtime Database | via Admin SDK 9.2.x |
| Auth (citizen) | Firebase Authentication | via Admin SDK |
| Async | `@Async` + `ThreadPoolTaskExecutor` | Spring |
| Scheduling | `@Scheduled` | Spring |
| Socket | Java `ServerSocket` / `Socket` | java.net |
| CSV Export | OpenCSV | 5.9 |
| Validation | Jakarta Bean Validation | 3.0 |
| Build | Maven | 3.9+ |
| Servlet Container | Embedded Tomcat | bundled |

### Frontend

| Layer | Technology | Version |
|---|---|---|
| View templates | JSP + JSTL | 3.0 |
| CSS framework | Bootstrap | 5.3 (via CDN) |
| Maps | Leaflet.js + Leaflet.heat | 1.9.4 / 0.2.0 |
| Charts | Chart.js | 4.4 |
| Realtime client | Firebase JS SDK | 10.x (modular) |
| Fonts | Inter + Source Serif 4 | Google Fonts |

### Deployment

| Component | Choice |
|---|---|
| Packaging | Embedded Spring Boot JAR (`./mvnw spring-boot:run`) |
| Server Port | 8080 (web), 9090 (socket) |
| Version Control | Git + GitHub (public repo with `README.md`) |

---

## 6. System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                              │
│   ┌───────────────┐   ┌──────────────────┐   ┌──────────────────┐  │
│   │Citizen Portal │   │ Police Dashboard │   │   Admin Panel    │  │
│   │   (JSP+JSTL)  │   │    (JSP+JSTL)    │   │    (JSP+JSTL)    │  │
│   └───────┬───────┘   └────────┬─────────┘   └────────┬─────────┘  │
└───────────┼────────────────────┼──────────────────────┼─────────────┘
            │ HTTP               │ HTTP + Firebase JS   │ HTTP
            │                    │   + WS Socket bridge │
┌───────────▼────────────────────▼──────────────────────▼─────────────┐
│                       SPRING BOOT APPLICATION                       │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ Servlet Filter Chain                                        │   │
│  │   → AuditLoggingFilter (CO2)  → Spring Security Chain       │   │
│  │   → AuthInterceptor (HandlerInterceptor, CO2)               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────────┐   │
│  │ @Controller  │─▶│  @Service    │─▶│  @Repository            │   │
│  │ (MVC + REST) │  │ (Business)   │  │ (Firestore impls)       │   │
│  └──────────────┘  └──────────────┘  └─────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────┐   ┌──────────────────────────────────┐    │
│  │ Async / Scheduled   │   │ Socket Server                    │    │
│  │ EscalationEngine    │   │ (ServerSocket :9090, daemon)     │    │
│  │ ThreadPoolExecutor  │   │ CopyOnWriteArrayList<PrintWriter>│    │
│  └─────────────────────┘   └──────────────────────────────────┘    │
│                                                                     │
└──────────────────┬──────────────────────┬───────────────────────────┘
                   │                      │
                   │  Firebase Admin SDK  │
                   │                      │
      ┌────────────▼───────┐    ┌─────────▼──────────────┐
      │  Firestore         │    │  Realtime Database     │
      │  (structured docs) │    │  (live alert stream)   │
      │  ─ users           │    │  /alerts/{pushId}      │
      │  ─ zones           │    │  /escalations/{zoneId} │
      │  ─ crime_reports   │    │                        │
      │  ─ assignments     │    │                        │
      │  ─ escalations     │    │                        │
      │  ─ audit_log       │    │                        │
      └────────────────────┘    └────────────────────────┘
```

---

## 7. Firebase Data Model

Firebase is NoSQL, but we enforce relational discipline by using document IDs as references (pseudo-foreign keys) and validating relationships in the service layer.

### Collection: `users`

```json
{
  "userId": "usr_a7F9...",
  "username": "ravi.k",
  "email": "ravi@example.com",
  "passwordHash": "$2a$10$...",
  "role": "OFFICER",
  "zoneId": "zone_001",
  "enabled": true,
  "createdAt": 1745000000000
}
```

**Indexes:** `email` (unique, enforced in service), `role`, `zoneId`.

### Collection: `zones`

```json
{
  "zoneId": "zone_001",
  "zoneName": "Central District",
  "city": "Pune",
  "latCenter": 18.5204,
  "lngCenter": 73.8567,
  "escalationThreshold": 5,
  "createdAt": 1745000000000
}
```

### Collection: `crime_reports`

```json
{
  "reportId": "rpt_2bX8...",
  "title": "Break-in near metro",
  "description": "Two individuals attempting to force open...",
  "crimeType": "THEFT",
  "severity": "HIGH",
  "latitude": 18.5211,
  "longitude": 73.8571,
  "zoneId": "zone_001",
  "reporterId": null,
  "status": "PENDING",
  "reportedAt": 1745001234567,
  "resolvedAt": null
}
```

**Enums (validated in POJO):**

- `crimeType`: `THEFT | ASSAULT | VANDALISM | SUSPICIOUS | OTHER`
- `severity`: `LOW | MEDIUM | HIGH | CRITICAL`
- `status`: `PENDING | ASSIGNED | RESOLVED | DISMISSED`

### Collection: `assignments`

```json
{
  "assignmentId": "asn_9kL...",
  "reportId": "rpt_2bX8...",
  "officerId": "usr_officer_01",
  "assignedAt": 1745001500000,
  "notes": "Dispatched unit from Station 4"
}
```

### Collection: `escalations`

```json
{
  "escalationId": "esc_x7...",
  "zoneId": "zone_001",
  "triggeredAt": 1745001800000,
  "reportCount": 8,
  "escalationLevel": "CRITICAL",
  "resolved": false
}
```

### Collection: `audit_log`

```json
{
  "logId": "log_a1...",
  "userId": "usr_a7F9...",
  "action": "REPORT_SUBMITTED",
  "entityType": "CrimeReport",
  "entityId": "rpt_2bX8...",
  "timestamp": 1745001234999,
  "ipAddress": "10.0.0.7"
}
```

### Realtime Database — live alert path

```
/alerts/{pushId}
  ├─ reportId: "rpt_2bX8..."
  ├─ title: "Break-in near metro"
  ├─ crimeType: "THEFT"
  ├─ severity: "HIGH"
  ├─ latitude: 18.5211
  ├─ longitude: 73.8571
  ├─ zoneId: "zone_001"
  ├─ zoneName: "Central District"
  ├─ timestamp: 1745001234567
  └─ status: "PENDING"

/escalations/{zoneId}
  ├─ level: "CRITICAL"
  ├─ reportCount: 8
  └─ triggeredAt: 1745001800000
```

Realtime Database is used **only** for the sub-second live stream. All canonical data lives in Firestore.

---

## 8. Entity Relationship Diagram (Logical)

Firebase has no enforced foreign keys; relationships are logical and maintained by the service layer.

```
┌──────────┐       ┌──────────────┐       ┌──────────┐
│  zones   │◀──────│ crime_reports│──────▶│  users   │
│──────────│  1:N  │──────────────│  N:1  │──────────│
│ zoneId   │       │ reportId     │       │ userId   │
│ zoneName │       │ zoneId (FK*) │       │ username │
│ threshold│       │ reporterId   │       │ role     │
└──────────┘       │   (FK*,null) │       │ zoneId   │
      │            │ status       │       └──────────┘
      │ 1:N        │ severity     │             │
      │            └───────┬──────┘             │ 1:N
      │                    │ 1:N                │
      ▼                    ▼                    ▼
┌─────────────┐      ┌─────────────┐
│ escalations │      │ assignments │
│─────────────│      │─────────────│
│ escId       │      │ asnId       │
│ zoneId (FK*)│      │ reportId(FK*)│
│ level       │      │ officerId(FK*)
│ resolved    │      │ assignedAt   │
└─────────────┘      └─────────────┘

(FK*) = logical reference, enforced in service layer

Cross-cutting:
┌────────────┐
│ audit_log  │   ← written by AuditLoggingFilter for every
│────────────│      authenticated state-changing request
│ logId      │
│ userId     │
│ action     │
│ entityType │
│ entityId   │
│ timestamp  │
└────────────┘
```


---

## 9. Module Breakdown

### M1 — Citizen Report Portal

Anonymous or logged-in report submission with browser-captured geolocation.

- JSP form `citizen/report-form.jsp` with `@Valid` + `BindingResult` server-side validation
- On submit: `CrimeReportService.submitReport()`
  1. Persist to Firestore `crime_reports`
  2. Push to Realtime DB `/alerts/{pushId}` (Firebase Admin SDK)
  3. Broadcast via Java Socket server
  4. Write audit log entry (filter handles this transparently)
- Public heatmap page `citizen/map.jsp` rendering Leaflet.heat from Firestore `crime_reports`

### M2 — Police Dashboard

Live incoming alerts + interactive heatmap + report management.

- JSP `police/dashboard.jsp` loads Firebase JS SDK client-side
- `onValue(ref(db, 'alerts'), ...)` listener renders alerts in-pane without refresh
- Officers assign reports to themselves, update status (`ASSIGNED` → `RESOLVED` / `DISMISSED`)
- Zone-level statistics page with Chart.js (`police/zone-stats.jsp`)

### M3 — Admin Panel

Full CRUD over zones and users, audit log, CSV export, escalation threshold config.

- JSP pages under `admin/` — `zones.jsp`, `users.jsp`, `audit-log.jsp`, `escalations.jsp`
- CSV export endpoint uses Firestore batch read + OpenCSV writer
- Configurable escalation thresholds per zone

### M4 — Real-Time Alert Engine (Socket, CO4)

Java `ServerSocket` bound to port 9090. Starts via `@PostConstruct` on application boot as a daemon thread. Maintains `CopyOnWriteArrayList<PrintWriter>` of connected clients.

- `AlertSocketBroadcaster.broadcast(alertJson)` iterates writers
- Browser clients connect via a lightweight WebSocket bridge (Spring's built-in WebSocket relay) — the raw Socket is also usable by a Java desktop/demo client for viva

### M5 — Zone Escalation Engine (Multithreading, CO1)

- `@Scheduled(fixedDelay = 300000)` — runs every 5 minutes
- Iterates all zones, counts unresolved reports in the last 60 minutes per zone
- If count ≥ zone's `escalationThreshold` → `@Async` method dispatches escalation:
  - Insert into `escalations` collection
  - Push to Realtime DB `/escalations/{zoneId}`
  - Socket broadcast with `level: "CRITICAL"`
- Backed by `ThreadPoolTaskExecutor` (core 4, max 8, queue 100)

### M6 — Batch CSV Export (CO1 — JDBC analogue)

- Admin triggers `/admin/export` → `ExportService.exportAllReports()`
- Uses Firestore batch read (paginated, 500 docs/page)
- Writes to CSV via OpenCSV `CSVWriter`
- Returns as `application/octet-stream` download
- **Note for viva:** this is the Firebase equivalent of JDBC batch; we iterate pages and stream writes just as `PreparedStatement.addBatch()` / `executeBatch()` would do against MySQL.

---

## 10. Folder / Package Structure

```
crimewatch/
├── pom.xml
├── README.md
├── .gitignore
├── mvnw, mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── firebase-service-account.json            (gitignored — user provides)
└── src/
    └── main/
        ├── java/
        │   └── com/crimewatch/
        │       ├── CrimeWatchApplication.java
        │       ├── config/
        │       │   ├── FirebaseConfig.java
        │       │   ├── AsyncConfig.java
        │       │   ├── SecurityConfig.java
        │       │   ├── WebMvcConfig.java
        │       │   └── ViewResolverConfig.java
        │       ├── controller/
        │       │   ├── HomeController.java
        │       │   ├── AuthController.java
        │       │   ├── ReportController.java
        │       │   ├── DashboardController.java
        │       │   ├── AdminController.java
        │       │   ├── MapController.java
        │       │   └── api/
        │       │       ├── ReportApiController.java
        │       │       └── StatsApiController.java
        │       ├── service/
        │       │   ├── CrimeReportService.java
        │       │   ├── ZoneService.java
        │       │   ├── UserService.java
        │       │   ├── AssignmentService.java
        │       │   ├── EscalationService.java
        │       │   ├── FirebaseAlertService.java
        │       │   ├── AuditService.java
        │       │   ├── ExportService.java
        │       │   └── StatsService.java
        │       ├── repository/
        │       │   ├── CrimeReportRepository.java
        │       │   ├── ZoneRepository.java
        │       │   ├── UserRepository.java
        │       │   ├── AssignmentRepository.java
        │       │   ├── EscalationRepository.java
        │       │   ├── AuditLogRepository.java
        │       │   └── impl/
        │       │       ├── FirestoreCrimeReportRepository.java
        │       │       ├── FirestoreZoneRepository.java
        │       │       ├── FirestoreUserRepository.java
        │       │       ├── FirestoreAssignmentRepository.java
        │       │       ├── FirestoreEscalationRepository.java
        │       │       └── FirestoreAuditLogRepository.java
        │       ├── entity/
        │       │   ├── User.java
        │       │   ├── Zone.java
        │       │   ├── CrimeReport.java
        │       │   ├── Assignment.java
        │       │   ├── Escalation.java
        │       │   └── AuditLog.java
        │       ├── enums/
        │       │   ├── Role.java
        │       │   ├── CrimeType.java
        │       │   ├── ReportStatus.java
        │       │   ├── Severity.java
        │       │   └── EscalationLevel.java
        │       ├── dto/
        │       │   ├── ReportSubmissionDto.java
        │       │   ├── AlertDto.java
        │       │   └── ZoneStatsDto.java
        │       ├── filter/
        │       │   └── AuditLoggingFilter.java
        │       ├── interceptor/
        │       │   └── AuthInterceptor.java
        │       ├── security/
        │       │   ├── FirestoreUserDetailsService.java
        │       │   └── CustomAuthenticationProvider.java
        │       ├── socket/
        │       │   ├── SocketServer.java
        │       │   └── AlertSocketBroadcaster.java
        │       ├── scheduler/
        │       │   └── EscalationScheduler.java
        │       ├── seed/
        │       │   └── DemoDataSeeder.java
        │       └── util/
        │           ├── FirestoreUtils.java
        │           └── IdGenerator.java
        ├── resources/
        │   ├── application.properties
        │   ├── logback-spring.xml
        │   └── static/
        │       ├── css/
        │       │   ├── tokens.css              ← design tokens (§23)
        │       │   ├── base.css
        │       │   ├── components.css
        │       │   └── pages.css
        │       ├── js/
        │       │   ├── firebase-config.js
        │       │   ├── dashboard.js           ← Firebase listener + alerts
        │       │   ├── heatmap.js             ← Leaflet.heat
        │       │   ├── report-form.js         ← geolocation capture
        │       │   ├── stats-charts.js        ← Chart.js
        │       │   └── socket-client.js      ← WS bridge client
        │       └── img/
        │           └── logo.svg
        └── webapp/
            └── WEB-INF/
                └── views/
                    ├── layout/
                    │   ├── header.jsp
                    │   ├── footer.jsp
                    │   └── nav.jsp
                    ├── auth/
                    │   ├── login.jsp
                    │   └── register.jsp
                    ├── citizen/
                    │   ├── home.jsp
                    │   ├── report-form.jsp
                    │   ├── my-reports.jsp
                    │   └── map.jsp
                    ├── police/
                    │   ├── dashboard.jsp
                    │   ├── reports-pending.jsp
                    │   └── zone-stats.jsp
                    ├── admin/
                    │   ├── home.jsp
                    │   ├── zones.jsp
                    │   ├── users.jsp
                    │   ├── escalations.jsp
                    │   └── audit-log.jsp
                    └── error/
                        ├── 403.jsp
                        ├── 404.jsp
                        └── 500.jsp
```

---

## 11. Maven Dependencies (`pom.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.crimewatch</groupId>
    <artifactId>crimewatch</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>CrimeWatch</name>
    <description>Crowdsourced Crime Reporting &amp; Live Heatmap</description>

    <properties>
        <java.version>17</java.version>
        <firebase-admin.version>9.2.0</firebase-admin.version>
        <opencsv.version>5.9</opencsv.version>
        <jstl.version>3.0.0</jstl.version>
    </properties>

    <dependencies>
        <!-- Spring Boot core -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <!-- JSP + JSTL (rubric CO2) -->
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.servlet.jsp.jstl</groupId>
            <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
            <version>${jstl.version}</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.web</groupId>
            <artifactId>jakarta.servlet.jsp.jstl</artifactId>
            <version>${jstl.version}</version>
        </dependency>

        <!-- Firebase Admin SDK -->
        <dependency>
            <groupId>com.google.firebase</groupId>
            <artifactId>firebase-admin</artifactId>
            <version>${firebase-admin.version}</version>
        </dependency>

        <!-- OpenCSV for batch export -->
        <dependency>
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>${opencsv.version}</version>
        </dependency>

        <!-- Spring Security Taglibs (for JSP <sec:authorize>) -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-taglibs</artifactId>
        </dependency>

        <!-- Dev tools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 12. Configuration Files

### `application.properties`

```properties
# Application
spring.application.name=CrimeWatch
server.port=8080

# JSP + JSTL view resolver (required for src/main/webapp/WEB-INF/views layout)
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp

# Encoding
spring.mvc.converters.preferred-json-mapper=jackson
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# Session
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=lax

# Firebase
firebase.credentials.path=firebase-service-account.json
firebase.database.url=https://YOUR-PROJECT-default-rtdb.firebaseio.com/
firebase.project.id=YOUR-PROJECT-ID

# Socket server
socket.server.port=9090

# Async executor
crimewatch.async.core-pool-size=4
crimewatch.async.max-pool-size=8
crimewatch.async.queue-capacity=100

# Scheduler
crimewatch.escalation.fixed-delay-ms=300000
crimewatch.escalation.lookback-minutes=60

# Upload (for future media feature)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.crimewatch=INFO
logging.level.org.springframework.security=INFO
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Jackson
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=true
```

### `logback-spring.xml` (minimal, production-ready)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    <logger name="com.crimewatch" level="DEBUG"/>
</configuration>
```

### `.gitignore`

```
target/
.idea/
*.iml
*.iws
.vscode/
.DS_Store
*.log
firebase-service-account.json
!firebase-service-account.example.json
application-local.properties
```

---

## 13. Firebase Setup Steps

These go into `README.md` verbatim.

1. Visit <https://console.firebase.google.com> → **Add Project** → name it `crimewatch-<yourname>`.
2. **Build → Firestore Database** → Create database → Start in **production mode** → pick nearest region (e.g., `asia-south1` for India).
3. **Build → Realtime Database** → Create database → Start in **locked mode**.
4. **Project Settings (⚙️) → Service accounts** → **Generate new private key** → download JSON.
5. Rename to `firebase-service-account.json` and place in the project root (same directory as `pom.xml`).
6. Copy the **Realtime Database URL** (e.g., `https://crimewatch-xxxx-default-rtdb.asia-southeast1.firebasedatabase.app/`) into `application.properties` → `firebase.database.url`.
7. Copy the **Project ID** into `firebase.project.id`.
8. In the Firestore console, under **Rules** tab, temporarily set (for development only):
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if true;   // dev only — tighten for prod
       }
     }
   }
   ```
   Do the same for Realtime Database rules.
9. Run `./mvnw spring-boot:run` — `DemoDataSeeder` will populate sample zones, users, and reports on first boot.

---

## 14. Entity (POJO) Classes

All entities are plain POJOs with validation annotations. They are **not** JPA entities, but they mirror the structure a JPA entity would have. Every entity has a no-args constructor (required for Firestore deserialization), all-args constructor, getters, setters, `toString()`, `equals()` / `hashCode()` based on the ID field.

### `enums/Role.java`

```java
package com.crimewatch.enums;

public enum Role {
    CITIZEN,
    OFFICER,
    ADMIN
}
```

### `enums/CrimeType.java`

```java
package com.crimewatch.enums;

public enum CrimeType {
    THEFT,
    ASSAULT,
    VANDALISM,
    SUSPICIOUS,
    OTHER
}
```

### `enums/ReportStatus.java`

```java
package com.crimewatch.enums;

public enum ReportStatus {
    PENDING,
    ASSIGNED,
    RESOLVED,
    DISMISSED
}
```

### `enums/Severity.java`

```java
package com.crimewatch.enums;

public enum Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
```

### `enums/EscalationLevel.java`

```java
package com.crimewatch.enums;

public enum EscalationLevel {
    WARNING,
    ALERT,
    CRITICAL
}
```

### `entity/User.java`

```java
package com.crimewatch.entity;

import com.crimewatch.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class User {

    private String userId;

    @NotBlank @Size(min = 3, max = 60)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, max = 255)
    private String passwordHash;

    private Role role;

    private String zoneId;   // logical FK

    private boolean enabled = true;

    private long createdAt;

    public User() {}

    // all-args constructor, getters, setters, toString, equals, hashCode
    // (Claude Code: generate these completely)
}
```

### `entity/Zone.java`

```java
package com.crimewatch.entity;

import jakarta.validation.constraints.*;

public class Zone {

    private String zoneId;

    @NotBlank @Size(max = 100)
    private String zoneName;

    @NotBlank @Size(max = 100)
    private String city;

    @NotNull
    private Double latCenter;

    @NotNull
    private Double lngCenter;

    @Min(1) @Max(100)
    private int escalationThreshold = 5;

    private long createdAt;

    public Zone() {}
    // + all-args, getters, setters, toString, equals, hashCode
}
```

### `entity/CrimeReport.java`

```java
package com.crimewatch.entity;

import com.crimewatch.enums.CrimeType;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.enums.Severity;
import jakarta.validation.constraints.*;

public class CrimeReport {

    private String reportId;

    @NotBlank @Size(min = 5, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private CrimeType crimeType;

    @NotNull
    private Severity severity = Severity.MEDIUM;

    @NotNull @DecimalMin("-90") @DecimalMax("90")
    private Double latitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180")
    private Double longitude;

    @NotBlank
    private String zoneId;                    // logical FK → zones

    private String reporterId;                // logical FK → users (nullable = anonymous)

    private ReportStatus status = ReportStatus.PENDING;

    private long reportedAt;

    private Long resolvedAt;                  // null until resolved

    public CrimeReport() {}
    // + all-args, getters, setters, toString, equals, hashCode
}
```

### `entity/Assignment.java`

```java
package com.crimewatch.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Assignment {

    private String assignmentId;

    @NotBlank
    private String reportId;       // logical FK → crime_reports

    @NotBlank
    private String officerId;      // logical FK → users (role=OFFICER)

    private long assignedAt;

    @Size(max = 1000)
    private String notes;

    public Assignment() {}
    // + all-args, getters, setters, toString, equals, hashCode
}
```

### `entity/Escalation.java`

```java
package com.crimewatch.entity;

import com.crimewatch.enums.EscalationLevel;
import jakarta.validation.constraints.*;

public class Escalation {

    private String escalationId;

    @NotBlank
    private String zoneId;         // logical FK → zones

    private long triggeredAt;

    @Min(1)
    private int reportCount;

    @NotNull
    private EscalationLevel escalationLevel;

    private boolean resolved = false;

    public Escalation() {}
    // + all-args, getters, setters, toString, equals, hashCode
}
```

### `entity/AuditLog.java`

```java
package com.crimewatch.entity;

public class AuditLog {

    private String logId;
    private String userId;         // nullable for anonymous
    private String action;
    private String entityType;
    private String entityId;
    private long timestamp;
    private String ipAddress;

    public AuditLog() {}
    // + all-args, getters, setters, toString, equals, hashCode
}
```

### DTOs

```java
// dto/ReportSubmissionDto.java
public class ReportSubmissionDto {
    @NotBlank @Size(min = 5, max = 200) private String title;
    @Size(max = 2000) private String description;
    @NotNull private CrimeType crimeType;
    @NotNull private Severity severity;
    @NotNull private Double latitude;
    @NotNull private Double longitude;
    // getters/setters
}

// dto/AlertDto.java  (serialised to Realtime DB & socket stream)
public class AlertDto {
    private String reportId;
    private String title;
    private String crimeType;
    private String severity;
    private double latitude;
    private double longitude;
    private String zoneId;
    private String zoneName;
    private long timestamp;
    private String status;
    // getters/setters, static factory from(CrimeReport)
}

// dto/ZoneStatsDto.java
public class ZoneStatsDto {
    private String zoneId;
    private String zoneName;
    private long totalReports;
    private long pending;
    private long resolved;
    private double avgResponseMinutes;
    // getters/setters
}
```


---

## 15. Repository Layer

Each repository is an interface (preserves Spring Data JPA-style pattern for the rubric) with a Firestore implementation.

### `repository/CrimeReportRepository.java` (interface)

```java
package com.crimewatch.repository;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.enums.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface CrimeReportRepository {

    CrimeReport save(CrimeReport report);

    Optional<CrimeReport> findById(String reportId);

    List<CrimeReport> findAll();

    List<CrimeReport> findByZoneId(String zoneId);

    List<CrimeReport> findByStatus(ReportStatus status);

    List<CrimeReport> findByReporterId(String reporterId);

    long countByZoneIdAndStatusIn(String zoneId, List<ReportStatus> statuses, long sinceTimestamp);

    void update(CrimeReport report);

    void deleteById(String reportId);

    long count();

    /** Batch fetch for CSV export — paginated. */
    List<CrimeReport> findAllPaged(int pageSize, String lastDocId);
}
```

### `repository/impl/FirestoreCrimeReportRepository.java`

```java
package com.crimewatch.repository.impl;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreCrimeReportRepository implements CrimeReportRepository {

    private static final String COLLECTION = "crime_reports";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public CrimeReport save(CrimeReport report) {
        if (report.getReportId() == null) {
            report.setReportId(IdGenerator.reportId());
        }
        if (report.getReportedAt() == 0) {
            report.setReportedAt(System.currentTimeMillis());
        }
        try {
            col().document(report.getReportId()).set(report).get();
            return report;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    @Override
    public Optional<CrimeReport> findById(String reportId) {
        try {
            DocumentSnapshot snap = col().document(reportId).get().get();
            return snap.exists()
                    ? Optional.ofNullable(snap.toObject(CrimeReport.class))
                    : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore findById failed", e);
        }
    }

    @Override
    public List<CrimeReport> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore findAll failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByZoneId(String zoneId) {
        try {
            return col().whereEqualTo("zoneId", zoneId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByZoneId failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByStatus(ReportStatus status) {
        try {
            return col().whereEqualTo("status", status.name()).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByStatus failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByReporterId(String reporterId) {
        try {
            return col().whereEqualTo("reporterId", reporterId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByReporterId failed", e);
        }
    }

    @Override
    public long countByZoneIdAndStatusIn(String zoneId, List<ReportStatus> statuses, long sinceTimestamp) {
        try {
            List<String> statusNames = statuses.stream().map(Enum::name).toList();
            return col()
                    .whereEqualTo("zoneId", zoneId)
                    .whereIn("status", statusNames)
                    .whereGreaterThanOrEqualTo("reportedAt", sinceTimestamp)
                    .get().get()
                    .size();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("countByZoneIdAndStatusIn failed", e);
        }
    }

    @Override
    public void update(CrimeReport report) {
        save(report);   // Firestore `set` with full doc == upsert
    }

    @Override
    public void deleteById(String reportId) {
        try {
            col().document(reportId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("deleteById failed", e);
        }
    }

    @Override
    public long count() {
        try {
            return col().count().get().get().getCount();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("count failed", e);
        }
    }

    @Override
    public List<CrimeReport> findAllPaged(int pageSize, String lastDocId) {
        try {
            Query q = col().orderBy("reportedAt").limit(pageSize);
            if (lastDocId != null) {
                DocumentSnapshot cursor = col().document(lastDocId).get().get();
                q = q.startAfter(cursor);
            }
            return q.get().get().getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAllPaged failed", e);
        }
    }
}
```

**Note for Claude Code:** Generate the other five repositories (`ZoneRepository`, `UserRepository`, `AssignmentRepository`, `EscalationRepository`, `AuditLogRepository`) following the exact same pattern. Each has an interface with CRUD + domain-specific finders, and a Firestore implementation in `repository/impl/`.

#### Minimum methods for each repository

| Repository | Required Methods |
|---|---|
| `ZoneRepository` | `save`, `findById`, `findAll`, `findByCity`, `deleteById`, `count` |
| `UserRepository` | `save`, `findById`, `findByEmail`, `findByUsername`, `findByRole`, `findAll`, `deleteById`, `count` |
| `AssignmentRepository` | `save`, `findById`, `findByReportId`, `findByOfficerId`, `findAll`, `deleteById` |
| `EscalationRepository` | `save`, `findById`, `findByZoneId`, `findByResolvedFalse`, `findAll`, `update` |
| `AuditLogRepository` | `save`, `findByUserId`, `findAll`, `findAllPaged`, `findByEntityTypeAndEntityId` |

---

## 16. Service Layer

All services are in `com.crimewatch.service`. Business logic lives here. **Multi-document writes use `firestore.runTransaction(...)`** — this is our `@Transactional` substitute and must be highlighted in the viva.

### `service/CrimeReportService.java`

```java
package com.crimewatch.service;

import com.crimewatch.dto.AlertDto;
import com.crimewatch.dto.ReportSubmissionDto;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.entity.Zone;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.socket.AlertSocketBroadcaster;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrimeReportService {

    private static final Logger log = LoggerFactory.getLogger(CrimeReportService.class);

    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private ZoneRepository zoneRepo;
    @Autowired private FirebaseAlertService firebaseAlertService;
    @Autowired private AlertSocketBroadcaster socketBroadcaster;
    @Autowired private AuditService auditService;

    /**
     * Submits a new report. Writes to Firestore inside a transaction, then
     * fans out the alert through Firebase Realtime DB and the Socket server.
     */
    public CrimeReport submitReport(ReportSubmissionDto dto, String reporterId, String ipAddress) {
        Zone zone = resolveZoneByProximity(dto.getLatitude(), dto.getLongitude());

        CrimeReport report = new CrimeReport();
        report.setTitle(dto.getTitle());
        report.setDescription(dto.getDescription());
        report.setCrimeType(dto.getCrimeType());
        report.setSeverity(dto.getSeverity());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setZoneId(zone.getZoneId());
        report.setReporterId(reporterId);                    // nullable
        report.setStatus(ReportStatus.PENDING);
        report.setReportedAt(System.currentTimeMillis());

        Firestore db = FirestoreClient.getFirestore();

        // TRANSACTIONAL write — our @Transactional substitute.
        // If any step inside fails, the transaction rolls back.
        try {
            db.runTransaction(txn -> {
                txn.set(db.collection("crime_reports").document(generateId(report)), report);
                return null;
            }).get();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transactional report submission failed", e);
        }

        // Post-commit fan-out
        AlertDto alert = AlertDto.from(report, zone.getZoneName());
        firebaseAlertService.pushAlert(alert);
        socketBroadcaster.broadcastJson(alert);
        auditService.log(reporterId, "REPORT_SUBMITTED", "CrimeReport",
                         report.getReportId(), ipAddress);

        log.info("Report submitted: id={}, zone={}, severity={}",
                 report.getReportId(), zone.getZoneId(), report.getSeverity());
        return report;
    }

    private Zone resolveZoneByProximity(double lat, double lng) {
        // Simple nearest-centre lookup. Replace with geo-hash for scale.
        List<Zone> zones = zoneRepo.findAll();
        return zones.stream()
                .min((a, b) -> Double.compare(
                        haversine(lat, lng, a.getLatCenter(), a.getLngCenter()),
                        haversine(lat, lng, b.getLatCenter(), b.getLngCenter())))
                .orElseThrow(() -> new IllegalStateException("No zones configured"));
    }

    private String generateId(CrimeReport r) {
        if (r.getReportId() == null) {
            r.setReportId(com.crimewatch.util.IdGenerator.reportId());
        }
        return r.getReportId();
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371e3;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1);
        double dl = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2)
                + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // findById, findAll, findPending, findByZone, assignToOfficer, updateStatus...
    // Every multi-doc write MUST use db.runTransaction.
}
```

### `service/FirebaseAlertService.java`

```java
package com.crimewatch.service;

import com.crimewatch.dto.AlertDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAlertService {

    public void pushAlert(AlertDto alert) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("alerts")
                .push();

        Map<String, Object> payload = new HashMap<>();
        payload.put("reportId", alert.getReportId());
        payload.put("title", alert.getTitle());
        payload.put("crimeType", alert.getCrimeType());
        payload.put("severity", alert.getSeverity());
        payload.put("latitude", alert.getLatitude());
        payload.put("longitude", alert.getLongitude());
        payload.put("zoneId", alert.getZoneId());
        payload.put("zoneName", alert.getZoneName());
        payload.put("timestamp", alert.getTimestamp());
        payload.put("status", alert.getStatus());

        ref.setValueAsync(payload);
    }

    public void pushEscalation(String zoneId, String level, int reportCount) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("escalations")
                .child(zoneId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("level", level);
        payload.put("reportCount", reportCount);
        payload.put("triggeredAt", System.currentTimeMillis());

        ref.setValueAsync(payload);
    }
}
```

### `service/EscalationService.java`

```java
package com.crimewatch.service;

import com.crimewatch.entity.Escalation;
import com.crimewatch.entity.Zone;
import com.crimewatch.enums.EscalationLevel;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.repository.EscalationRepository;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.socket.AlertSocketBroadcaster;
import com.crimewatch.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EscalationService {

    private static final Logger log = LoggerFactory.getLogger(EscalationService.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private EscalationRepository escRepo;
    @Autowired private FirebaseAlertService firebaseAlertService;
    @Autowired private AlertSocketBroadcaster socketBroadcaster;

    @Value("${crimewatch.escalation.lookback-minutes}")
    private int lookbackMinutes;

    @Async("crimewatchTaskExecutor")
    public void evaluateZone(Zone zone) {
        long since = System.currentTimeMillis() - (lookbackMinutes * 60_000L);
        long count = reportRepo.countByZoneIdAndStatusIn(
                zone.getZoneId(),
                List.of(ReportStatus.PENDING, ReportStatus.ASSIGNED),
                since);

        if (count >= zone.getEscalationThreshold()) {
            EscalationLevel level = count >= zone.getEscalationThreshold() * 2
                    ? EscalationLevel.CRITICAL
                    : EscalationLevel.ALERT;

            Escalation esc = new Escalation();
            esc.setEscalationId(IdGenerator.escalationId());
            esc.setZoneId(zone.getZoneId());
            esc.setTriggeredAt(System.currentTimeMillis());
            esc.setReportCount((int) count);
            esc.setEscalationLevel(level);
            esc.setResolved(false);
            escRepo.save(esc);

            firebaseAlertService.pushEscalation(zone.getZoneId(), level.name(), (int) count);
            socketBroadcaster.broadcastJson(esc);

            log.warn("ESCALATION TRIGGERED zone={} level={} count={}",
                     zone.getZoneId(), level, count);
        }
    }
}
```

### Other services — minimum responsibilities

| Service | Responsibility |
|---|---|
| `ZoneService` | CRUD on zones, list by city, update threshold |
| `UserService` | Registration (BCrypt hash), role changes, login lookup |
| `AssignmentService` | Assign report to officer (transactional: create assignment + update report status) |
| `AuditService` | Write audit entries (called by filter + services) |
| `ExportService` | Paginated Firestore read → OpenCSV writer → return `Resource` |
| `StatsService` | Compute zone stats, avg response time, resolution rate |

---

## 17. Controller Layer

MVC controllers return JSP view names; REST controllers under `controller/api/` return JSON for async client calls (dashboard live data).

### `controller/ReportController.java`

```java
package com.crimewatch.controller;

import com.crimewatch.dto.ReportSubmissionDto;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.service.CrimeReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired private CrimeReportService service;

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("reportDto", new ReportSubmissionDto());
        return "citizen/report-form";
    }

    @PostMapping("/submit")
    public String submit(@Valid @ModelAttribute("reportDto") ReportSubmissionDto dto,
                         BindingResult br,
                         Authentication auth,
                         HttpServletRequest req,
                         Model model) {
        if (br.hasErrors()) {
            return "citizen/report-form";
        }
        String reporterId = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        CrimeReport saved = service.submitReport(dto, reporterId, req.getRemoteAddr());
        model.addAttribute("report", saved);
        return "redirect:/report/success?id=" + saved.getReportId();
    }

    @GetMapping("/success")
    public String success(@RequestParam String id, Model model) {
        model.addAttribute("reportId", id);
        return "citizen/report-success";
    }

    @GetMapping("/my")
    public String myReports(Authentication auth, Model model) {
        List<CrimeReport> reports = service.findByReporterId(auth.getName());
        model.addAttribute("reports", reports);
        return "citizen/my-reports";
    }
}
```

### `controller/DashboardController.java` (Police)

```java
@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('OFFICER')")
public class DashboardController {

    @Autowired private CrimeReportService reportService;
    @Autowired private StatsService statsService;

    @GetMapping
    public String home(Authentication auth, Model model) {
        model.addAttribute("pending", reportService.findPending());
        model.addAttribute("officerId", auth.getName());
        return "police/dashboard";
    }

    @PostMapping("/reports/{id}/assign")
    public String assign(@PathVariable String id, Authentication auth) {
        reportService.assignToOfficer(id, auth.getName());
        return "redirect:/dashboard";
    }

    @PostMapping("/reports/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam String status) {
        reportService.updateStatus(id, status);
        return "redirect:/dashboard";
    }

    @GetMapping("/zone/{zoneId}/stats")
    public String zoneStats(@PathVariable String zoneId, Model model) {
        model.addAttribute("stats", statsService.computeZoneStats(zoneId));
        return "police/zone-stats";
    }
}
```

### `controller/AdminController.java`

```java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private ZoneService zoneService;
    @Autowired private UserService userService;
    @Autowired private ExportService exportService;
    @Autowired private AuditService auditService;
    @Autowired private EscalationRepository escRepo;

    @GetMapping
    public String home() { return "admin/home"; }

    @GetMapping("/zones")
    public String zones(Model model) {
        model.addAttribute("zones", zoneService.findAll());
        return "admin/zones";
    }

    @PostMapping("/zones/add")
    public String addZone(@Valid Zone zone, BindingResult br) {
        if (br.hasErrors()) return "admin/zones";
        zoneService.save(zone);
        return "redirect:/admin/zones";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable String id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/escalations")
    public String escalations(Model model) {
        model.addAttribute("escalations", escRepo.findAll());
        return "admin/escalations";
    }

    @GetMapping("/audit")
    public String audit(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("logs", auditService.findPaged(page, 50));
        return "admin/audit-log";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> export() {
        return exportService.exportAllReportsAsCsv();
    }
}
```

### `controller/api/StatsApiController.java`

```java
@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    @Autowired private StatsService statsService;

    @GetMapping("/weekly-response-time")
    public List<WeeklyResponseDto> weeklyResponseTime() {
        return statsService.computeWeeklyAvgResponseTime();
    }

    @GetMapping("/reports-by-zone")
    public Map<String, Long> reportsByZone() {
        return statsService.reportsByZone();
    }

    @GetMapping("/crime-type-distribution")
    public Map<String, Long> crimeTypeDistribution() {
        return statsService.crimeTypeDistribution();
    }
}
```

---

## 18. Spring Security & Role-Based Access

### `config/SecurityConfig.java`

```java
package com.crimewatch.config;

import com.crimewatch.security.FirestoreUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired private FirestoreUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register",
                                 "/css/**", "/js/**", "/img/**",
                                 "/report/new", "/report/submit",
                                 "/map", "/api/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/dashboard/**").hasRole("OFFICER")
                .requestMatchers("/report/my").authenticated()
                .anyRequest().authenticated())
            .formLogin(f -> f
                .loginPage("/login")
                .defaultSuccessUrl("/post-login", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(l -> l.logoutSuccessUrl("/").permitAll())
            .exceptionHandling(e -> e.accessDeniedPage("/error/403"))
            .sessionManagement(s -> s
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false));
        return http.build();
    }
}
```

### `security/FirestoreUserDetailsService.java`

```java
@Service
public class FirestoreUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        com.crimewatch.entity.User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRole().name())
                .disabled(!u.isEnabled())
                .build();
    }
}
```

### Three roles — access matrix

| URL pattern | Citizen | Officer | Admin | Anonymous |
|---|---|---|---|---|
| `/` (landing) | ✅ | ✅ | ✅ | ✅ |
| `/login`, `/register` | ✅ | ✅ | ✅ | ✅ |
| `/report/new`, `/report/submit` | ✅ | ✅ | ✅ | ✅ |
| `/map` | ✅ | ✅ | ✅ | ✅ |
| `/report/my` | ✅ | ✅ | ✅ | ❌ |
| `/dashboard/**` | ❌ | ✅ | ✅ (inherits) | ❌ |
| `/admin/**` | ❌ | ❌ | ✅ | ❌ |

---

## 19. Servlet Filter & Spring Interceptor (CO2)

### `filter/AuditLoggingFilter.java` (Servlet filter — CO2)

Registered as a Spring bean and attached via `FilterRegistrationBean`. Runs for every request.

```java
package com.crimewatch.filter;

import com.crimewatch.entity.AuditLog;
import com.crimewatch.repository.AuditLogRepository;
import com.crimewatch.util.IdGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class AuditLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);
    private static final Set<String> AUDIT_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Autowired private AuditLogRepository auditRepo;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            if (AUDIT_METHODS.contains(httpReq.getMethod())) {
                recordAudit(httpReq);
            }
            log.debug("{} {} took {}ms",
                      httpReq.getMethod(), httpReq.getRequestURI(),
                      System.currentTimeMillis() - start);
        }
    }

    private void recordAudit(HttpServletRequest req) {
        try {
            var authObj = SecurityContextHolder.getContext().getAuthentication();
            String userId = (authObj != null && authObj.isAuthenticated()) ? authObj.getName() : null;

            AuditLog entry = new AuditLog();
            entry.setLogId(IdGenerator.auditId());
            entry.setUserId(userId);
            entry.setAction(req.getMethod() + " " + req.getRequestURI());
            entry.setEntityType(extractEntityType(req.getRequestURI()));
            entry.setEntityId(req.getParameter("id"));
            entry.setTimestamp(System.currentTimeMillis());
            entry.setIpAddress(req.getRemoteAddr());
            auditRepo.save(entry);
        } catch (Exception e) {
            log.error("Audit log write failed", e);  // never fail the request due to audit
        }
    }

    private String extractEntityType(String uri) {
        if (uri.contains("/report")) return "CrimeReport";
        if (uri.contains("/zone")) return "Zone";
        if (uri.contains("/user")) return "User";
        if (uri.contains("/assign")) return "Assignment";
        return "Other";
    }
}
```

### `interceptor/AuthInterceptor.java` (Spring Interceptor — CO2)

```java
package com.crimewatch.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        log.info("AUTH-INT | {} | {} {} | from {}",
                 user, req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
        return true;
    }
}
```

### `config/WebMvcConfig.java` — register interceptor + filter

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/img/**");
    }

    @Bean
    public FilterRegistrationBean<AuditLoggingFilter> auditFilter(AuditLoggingFilter f) {
        FilterRegistrationBean<AuditLoggingFilter> reg = new FilterRegistrationBean<>(f);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
```

---

## 20. Multithreading Module (CO1)

### `config/AsyncConfig.java`

```java
package com.crimewatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Value("${crimewatch.async.core-pool-size}") private int core;
    @Value("${crimewatch.async.max-pool-size}")  private int max;
    @Value("${crimewatch.async.queue-capacity}") private int queue;

    @Bean("crimewatchTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(core);
        ex.setMaxPoolSize(max);
        ex.setQueueCapacity(queue);
        ex.setThreadNamePrefix("cw-async-");
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(30);
        ex.initialize();
        return ex;
    }
}
```

### `scheduler/EscalationScheduler.java`

```java
package com.crimewatch.scheduler;

import com.crimewatch.entity.Zone;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.service.EscalationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EscalationScheduler.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private EscalationService escalationService;

    /**
     * Every 5 minutes, sweep all zones. Each zone is evaluated in its own
     * async thread via @Async → ThreadPoolTaskExecutor.
     */
    @Scheduled(fixedDelayString = "${crimewatch.escalation.fixed-delay-ms}")
    public void sweepZones() {
        List<Zone> zones = zoneRepo.findAll();
        log.info("Escalation sweep starting — {} zones", zones.size());
        for (Zone z : zones) {
            escalationService.evaluateZone(z);   // @Async fans out
        }
    }
}
```

**Rubric-facing explanation (for viva):** Three multithreading pieces are in play: `@Scheduled` drives the sweep, `@Async` offloads each zone's evaluation to a separate thread, and `ThreadPoolTaskExecutor` (core 4 / max 8 / queue 100) bounds concurrency. The pool name prefix `cw-async-` shows up in logs — easy to demonstrate live.

---

## 21. Socket Programming Module (CO4)

### `socket/SocketServer.java`

```java
package com.crimewatch.socket;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketServer {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    @Value("${socket.server.port}")
    private int port;

    private final List<PrintWriter> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    @PostConstruct
    public void start() {
        acceptThread = new Thread(this::runAcceptLoop, "cw-socket-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        log.info("Socket server started on port {}", port);
    }

    private void runAcceptLoop() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket client = serverSocket.accept();
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                clients.add(writer);
                log.info("Socket client connected from {}. Total clients: {}",
                         client.getInetAddress(), clients.size());
            }
        } catch (IOException e) {
            if (running) log.error("Socket accept loop error", e);
        }
    }

    List<PrintWriter> getClients() {
        return clients;
    }

    @PreDestroy
    public void stop() {
        running = false;
        clients.forEach(PrintWriter::close);
        clients.clear();
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
        log.info("Socket server stopped");
    }
}
```

### `socket/AlertSocketBroadcaster.java`

```java
package com.crimewatch.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class AlertSocketBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(AlertSocketBroadcaster.class);

    @Autowired private SocketServer socketServer;
    private final ObjectMapper mapper = new ObjectMapper();

    public void broadcastJson(Object payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            for (PrintWriter client : socketServer.getClients()) {
                try {
                    client.println(json);
                } catch (Exception e) {
                    log.warn("Failed to write to a client; removing", e);
                    socketServer.getClients().remove(client);
                }
            }
            log.debug("Broadcasted to {} client(s)", socketServer.getClients().size());
        } catch (Exception e) {
            log.error("Broadcast serialisation failed", e);
        }
    }
}
```

### Browser-side bridge

Because raw TCP sockets aren't reachable from a browser, we expose a WebSocket endpoint (`/ws/alerts`) that internally subscribes to the same broadcaster. Connect from JSP with `new WebSocket("ws://localhost:8080/ws/alerts")`. For the viva demo, you can also run a small Java console client (`SocketClientDemo.java`) connecting directly to `localhost:9090` to show raw-socket functionality.

### `socket/SocketClientDemo.java` (standalone viva demo)

```java
public class SocketClientDemo {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("localhost", 9090);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            System.out.println("Connected. Listening for alerts...");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("ALERT >> " + line);
            }
        }
    }
}
```


---

## 22. JSP Views & JSTL Pages

All JSPs live under `src/main/webapp/WEB-INF/views/`. Layout uses a header/nav/footer include pattern. JSTL taglibs declared at the top of every page.

### Standard JSP header (every page starts with this)

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
```

### `layout/header.jsp`

```jsp
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'CrimeWatch'}"/></title>

    <!-- Design tokens + base styles (loaded in order) -->
    <link rel="stylesheet" href="<c:url value='/css/tokens.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/pages.css'/>">

    <!-- Fonts: Inter for UI, Source Serif 4 for display -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Source+Serif+4:wght@400;600;700&display=swap" rel="stylesheet">

    <!-- Leaflet (only loaded on map pages) -->
    <c:if test="${requestScope['javax.servlet.forward.request_uri'].contains('map') || requestScope['javax.servlet.forward.request_uri'].contains('dashboard')}">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
    </c:if>
</head>
<body>
<jsp:include page="nav.jsp"/>
<main class="container">
```

### `layout/nav.jsp`

```jsp
<nav class="site-nav">
    <div class="nav-inner">
        <a class="brand" href="<c:url value='/'/>">
            <span class="brand-mark"></span>
            <span class="brand-text">CrimeWatch</span>
        </a>
        <ul class="nav-links">
            <li><a href="<c:url value='/map'/>">Heatmap</a></li>
            <li><a href="<c:url value='/report/new'/>">Report</a></li>
            <sec:authorize access="hasRole('OFFICER')">
                <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            </sec:authorize>
            <sec:authorize access="hasRole('ADMIN')">
                <li><a href="<c:url value='/admin'/>">Admin</a></li>
            </sec:authorize>
        </ul>
        <div class="nav-actions">
            <sec:authorize access="!isAuthenticated()">
                <a class="btn btn-ghost" href="<c:url value='/login'/>">Sign in</a>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <span class="nav-user"><sec:authentication property="name"/></span>
                <form action="<c:url value='/logout'/>" method="post" class="inline-form">
                    <sec:csrfInput/>
                    <button class="btn btn-ghost" type="submit">Sign out</button>
                </form>
            </sec:authorize>
        </div>
    </div>
</nav>
```

### `layout/footer.jsp`

```jsp
</main>
<footer class="site-footer">
    <div class="footer-inner">
        <span>CrimeWatch · SY B.Tech Mini Project · AY 2025–26</span>
        <span>SDG 16 · SDG 11</span>
    </div>
</footer>
</body>
</html>
```

### `citizen/report-form.jsp` (key form)

```jsp
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Report an incident</p>
    <h1>File a crime report</h1>
    <p class="lede">Your report is anonymous by default. Geo-coordinates are captured from your browser and stored alongside the report to place it on the public heatmap.</p>
</section>

<form:form method="post" action="/report/submit" modelAttribute="reportDto" cssClass="form-stack">
    <sec:csrfInput/>

    <div class="field">
        <form:label path="title">Title</form:label>
        <form:input path="title" placeholder="e.g., Suspicious figure near school gate"/>
        <form:errors path="title" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="crimeType">Type</form:label>
        <form:select path="crimeType">
            <form:option value="" label="— select —"/>
            <form:option value="THEFT">Theft</form:option>
            <form:option value="ASSAULT">Assault</form:option>
            <form:option value="VANDALISM">Vandalism</form:option>
            <form:option value="SUSPICIOUS">Suspicious Activity</form:option>
            <form:option value="OTHER">Other</form:option>
        </form:select>
        <form:errors path="crimeType" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="severity">Severity</form:label>
        <form:select path="severity">
            <form:option value="LOW">Low</form:option>
            <form:option value="MEDIUM">Medium</form:option>
            <form:option value="HIGH">High</form:option>
            <form:option value="CRITICAL">Critical</form:option>
        </form:select>
    </div>

    <div class="field">
        <form:label path="description">What happened?</form:label>
        <form:textarea path="description" rows="5" placeholder="Describe the incident in your own words."/>
        <form:errors path="description" cssClass="field-error"/>
    </div>

    <div class="field-row">
        <div class="field">
            <form:label path="latitude">Latitude</form:label>
            <form:input path="latitude" type="number" step="0.000001" readonly="true"/>
        </div>
        <div class="field">
            <form:label path="longitude">Longitude</form:label>
            <form:input path="longitude" type="number" step="0.000001" readonly="true"/>
        </div>
        <button type="button" id="capture-loc" class="btn btn-secondary">Capture my location</button>
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Submit report</button>
        <a href="<c:url value='/'/>" class="btn btn-ghost">Cancel</a>
    </div>
</form:form>

<script src="<c:url value='/js/report-form.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
```

### `police/dashboard.jsp` (realtime with Firebase listener)

```jsp
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="dashboard-head">
    <p class="eyebrow">Officer dashboard</p>
    <h1>Live incident feed</h1>
    <p class="lede">New reports stream here in real time. Click any alert to review details and assign yourself.</p>
</section>

<div class="dashboard-grid">
    <div class="card card-stream">
        <header class="card-head">
            <h2>Incoming alerts</h2>
            <span class="badge badge-live">LIVE</span>
        </header>
        <ul id="alert-list" class="alert-list"></ul>
    </div>

    <div class="card card-map">
        <header class="card-head"><h2>Heatmap</h2></header>
        <div id="map" class="map-surface"></div>
    </div>

    <div class="card card-pending">
        <header class="card-head">
            <h2>Pending reports in your zone</h2>
            <span class="count-pill"><c:out value="${pending.size()}"/></span>
        </header>
        <table class="data-table">
            <thead>
                <tr><th>Title</th><th>Severity</th><th>Time</th><th>Action</th></tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${pending}">
                    <tr>
                        <td><c:out value="${r.title}"/></td>
                        <td><span class="sev sev-${r.severity}">${r.severity}</span></td>
                        <td><fmt:formatDate value="${r.reportedAtDate}" pattern="HH:mm · dd MMM"/></td>
                        <td>
                            <form method="post" action="<c:url value='/dashboard/reports/${r.reportId}/assign'/>">
                                <sec:csrfInput/>
                                <button class="btn btn-sm">Assign to me</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Firebase config injected server-side -->
<script>
    window.FIREBASE_CONFIG = {
        apiKey: "<c:out value='${firebaseApiKey}'/>",
        databaseURL: "<c:out value='${firebaseDatabaseUrl}'/>",
        projectId: "<c:out value='${firebaseProjectId}'/>"
    };
</script>

<script type="module" src="<c:url value='/js/firebase-config.js'/>"></script>
<script type="module" src="<c:url value='/js/dashboard.js'/>"></script>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet.heat@0.2.0/dist/leaflet-heat.js"></script>
<script src="<c:url value='/js/heatmap.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
```

### `admin/audit-log.jsp` (JSTL iteration example)

```jsp
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Audit log</h1>
</section>

<table class="data-table">
    <thead>
        <tr>
            <th>Time</th>
            <th>User</th>
            <th>Action</th>
            <th>Entity</th>
            <th>IP</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty logs}">
                <tr><td colspan="5" class="empty">No audit entries yet.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="l" items="${logs}">
                    <tr>
                        <td><fmt:formatDate value="${l.timestampDate}" pattern="dd MMM yyyy · HH:mm:ss"/></td>
                        <td><c:out value="${l.userId != null ? l.userId : 'anonymous'}"/></td>
                        <td><c:out value="${l.action}"/></td>
                        <td><c:out value="${l.entityType}"/> · <c:out value="${l.entityId}"/></td>
                        <td><c:out value="${l.ipAddress}"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
```

### Other JSPs to generate (follow the same pattern)

| Path | Role | Purpose |
|---|---|---|
| `citizen/home.jsp` | public | Landing page — hero, CTA "Report incident" / "View map" |
| `citizen/report-success.jsp` | public | Confirmation screen with report ID |
| `citizen/my-reports.jsp` | citizen | Logged-in citizen's own reports |
| `citizen/map.jsp` | public | Public heatmap |
| `police/reports-pending.jsp` | officer | List view of pending reports in zone |
| `police/zone-stats.jsp` | officer | Chart.js stats for officer's zone |
| `admin/home.jsp` | admin | Admin landing with metric tiles |
| `admin/zones.jsp` | admin | Zone CRUD |
| `admin/users.jsp` | admin | User listing + role editor |
| `admin/escalations.jsp` | admin | All escalations, resolve toggle |
| `auth/login.jsp` | public | Spring Security form |
| `auth/register.jsp` | public | New citizen registration |
| `error/403.jsp`, `404.jsp`, `500.jsp` | all | Friendly error pages |

---

## 23. UI Design System

**Aesthetic:** Editorial. Authoritative. Restrained. Think institutional publication meets civic-tech dashboard. Deep charcoal as the dominant tone, warm off-white paper background, amber as the single accent that signals action or attention, hairline rules dividing content. Serif for display headings to carry editorial weight; neutral sans for body and UI. No gradients, no glow, no neon, no glass, no emoji in chrome, no rounded-pill buttons with saturated primaries.

### Design tokens — `src/main/resources/static/css/tokens.css`

```css
/* ==================================================================
   CrimeWatch — Design Tokens
   Editorial Charcoal + Amber. Do not add neon, gradients, or glow.
   ================================================================== */

:root {
    /* --- Surfaces ------------------------------------------------ */
    --bg-paper:        #f7f5f0;   /* warm off-white, body background */
    --bg-surface:      #ffffff;   /* cards, tables */
    --bg-inset:        #efece5;   /* subtle inset panels */
    --bg-charcoal:     #1a1a1a;   /* hero / dark sections */
    --bg-charcoal-2:   #242424;   /* nav bg */

    /* --- Ink (text) --------------------------------------------- */
    --ink-primary:     #1a1a1a;   /* headings, body */
    --ink-secondary:   #4a4a4a;   /* muted body */
    --ink-tertiary:    #78756f;   /* timestamps, meta */
    --ink-inverse:     #f7f5f0;   /* text on dark bg */

    /* --- Accent (amber, used sparingly) ------------------------- */
    --amber-50:        #fdf7ed;
    --amber-100:       #fbecd1;
    --amber-400:       #d99a3b;
    --amber-600:       #b47527;   /* primary accent */
    --amber-700:       #8a5918;   /* hover/pressed */
    --amber-on-dark:   #e9a74a;   /* amber that reads on charcoal */

    /* --- Severity / Semantic ------------------------------------ */
    --sev-low:         #6b7c5b;   /* muted sage */
    --sev-medium:      #8a7730;   /* muted mustard */
    --sev-high:        #a85a2b;   /* burnt orange */
    --sev-critical:    #8a2b2b;   /* deep brick */
    --status-success:  #506b4a;
    --status-error:    #8a2b2b;

    /* --- Lines -------------------------------------------------- */
    --rule-hair:       #d9d4ca;   /* primary hairline */
    --rule-soft:       #e8e3d9;
    --rule-strong:     #1a1a1a;

    /* --- Type --------------------------------------------------- */
    --font-sans:       'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
    --font-serif:      'Source Serif 4', Georgia, 'Times New Roman', serif;
    --font-mono:       ui-monospace, 'SF Mono', 'Cascadia Mono', Consolas, monospace;

    --fs-display-1:    clamp(2.4rem, 4.2vw, 3.6rem);
    --fs-display-2:    clamp(1.8rem, 2.8vw, 2.4rem);
    --fs-h1:           1.75rem;
    --fs-h2:           1.35rem;
    --fs-h3:           1.1rem;
    --fs-body:         1rem;
    --fs-small:        0.875rem;
    --fs-xs:           0.75rem;

    --lh-tight:        1.2;
    --lh-normal:       1.55;
    --lh-loose:        1.7;

    --fw-regular:      400;
    --fw-medium:       500;
    --fw-semibold:     600;
    --fw-bold:         700;

    /* --- Spacing (4-based) -------------------------------------- */
    --s-1:  0.25rem;
    --s-2:  0.5rem;
    --s-3:  0.75rem;
    --s-4:  1rem;
    --s-5:  1.5rem;
    --s-6:  2rem;
    --s-7:  3rem;
    --s-8:  4rem;
    --s-9:  6rem;

    /* --- Geometry ----------------------------------------------- */
    --radius-0:  0;
    --radius-1:  2px;
    --radius-2:  4px;     /* default — intentionally square-ish */
    --radius-3:  6px;

    /* --- Elevation (very subtle — editorial doesn't float) ------ */
    --shadow-0:  none;
    --shadow-1:  0 1px 0 rgba(26,26,26,0.04);
    --shadow-2:  0 1px 2px rgba(26,26,26,0.06), 0 0 0 1px var(--rule-hair);

    /* --- Focus -------------------------------------------------- */
    --focus-ring:  0 0 0 2px var(--bg-paper), 0 0 0 4px var(--amber-600);

    /* --- Layout ------------------------------------------------- */
    --container-width: 1180px;
    --nav-height:      64px;
}
```

### Base styles — `base.css`

```css
*, *::before, *::after { box-sizing: border-box; }

html, body {
    margin: 0;
    padding: 0;
    background: var(--bg-paper);
    color: var(--ink-primary);
    font-family: var(--font-sans);
    font-size: var(--fs-body);
    line-height: var(--lh-normal);
    -webkit-font-smoothing: antialiased;
}

h1, h2, h3, .display-1, .display-2 {
    font-family: var(--font-serif);
    font-weight: var(--fw-semibold);
    letter-spacing: -0.01em;
    line-height: var(--lh-tight);
    color: var(--ink-primary);
    margin: 0 0 var(--s-4);
}

h1 { font-size: var(--fs-h1); }
h2 { font-size: var(--fs-h2); }
h3 { font-size: var(--fs-h3); font-family: var(--font-sans); font-weight: var(--fw-semibold); }

p { margin: 0 0 var(--s-4); color: var(--ink-secondary); }

a {
    color: var(--amber-700);
    text-decoration: underline;
    text-underline-offset: 3px;
    text-decoration-thickness: 1px;
}
a:hover { color: var(--ink-primary); }

.container {
    max-width: var(--container-width);
    margin: 0 auto;
    padding: var(--s-6) var(--s-5);
}

.eyebrow {
    font-family: var(--font-sans);
    font-size: var(--fs-xs);
    font-weight: var(--fw-semibold);
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: var(--amber-700);
    margin: 0 0 var(--s-2);
}

.lede {
    font-size: 1.1rem;
    line-height: var(--lh-loose);
    color: var(--ink-secondary);
    max-width: 62ch;
}

hr, .rule {
    border: 0;
    height: 1px;
    background: var(--rule-hair);
    margin: var(--s-6) 0;
}
```

### Components — `components.css`

```css
/* ---------------- Navigation ---------------- */
.site-nav {
    background: var(--bg-charcoal-2);
    color: var(--ink-inverse);
    border-bottom: 1px solid #000;
}
.nav-inner {
    max-width: var(--container-width);
    margin: 0 auto;
    padding: 0 var(--s-5);
    height: var(--nav-height);
    display: flex;
    align-items: center;
    gap: var(--s-6);
}
.brand {
    display: flex;
    align-items: center;
    gap: var(--s-2);
    color: var(--ink-inverse);
    text-decoration: none;
    font-family: var(--font-serif);
    font-weight: var(--fw-bold);
    font-size: 1.15rem;
}
.brand-mark {
    width: 12px; height: 12px;
    background: var(--amber-on-dark);
    display: inline-block;
}
.nav-links { list-style: none; display: flex; gap: var(--s-5); margin: 0; padding: 0; }
.nav-links a {
    color: var(--ink-inverse);
    text-decoration: none;
    font-size: var(--fs-small);
    font-weight: var(--fw-medium);
    opacity: 0.8;
}
.nav-links a:hover { opacity: 1; color: var(--amber-on-dark); }
.nav-actions { margin-left: auto; display: flex; align-items: center; gap: var(--s-3); }
.nav-user { font-size: var(--fs-small); color: var(--amber-on-dark); }

/* ---------------- Buttons ---------------- */
.btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    padding: 0 var(--s-5);
    font-family: var(--font-sans);
    font-size: var(--fs-small);
    font-weight: var(--fw-semibold);
    letter-spacing: 0.01em;
    border: 1px solid transparent;
    border-radius: var(--radius-2);
    cursor: pointer;
    text-decoration: none;
    transition: background-color 120ms ease, border-color 120ms ease, color 120ms ease;
}
.btn:focus-visible { outline: none; box-shadow: var(--focus-ring); }
.btn-primary {
    background: var(--amber-600);
    color: #fff;
}
.btn-primary:hover { background: var(--amber-700); }
.btn-secondary {
    background: var(--bg-surface);
    color: var(--ink-primary);
    border-color: var(--ink-primary);
}
.btn-secondary:hover { background: var(--ink-primary); color: var(--bg-paper); }
.btn-ghost {
    background: transparent;
    color: inherit;
    border-color: currentColor;
    opacity: 0.85;
}
.btn-ghost:hover { opacity: 1; }
.btn-sm { height: 32px; padding: 0 var(--s-3); font-size: var(--fs-xs); }

/* ---------------- Forms ---------------- */
.form-stack { max-width: 640px; display: grid; gap: var(--s-5); }
.field { display: grid; gap: var(--s-2); }
.field label {
    font-size: var(--fs-small);
    font-weight: var(--fw-semibold);
    color: var(--ink-primary);
}
.field input, .field select, .field textarea {
    width: 100%;
    font-family: var(--font-sans);
    font-size: var(--fs-body);
    padding: 10px 12px;
    border: 1px solid var(--rule-hair);
    border-radius: var(--radius-2);
    background: var(--bg-surface);
    color: var(--ink-primary);
}
.field input:focus, .field select:focus, .field textarea:focus {
    outline: none;
    border-color: var(--ink-primary);
    box-shadow: 0 0 0 3px var(--amber-100);
}
.field-error {
    color: var(--status-error);
    font-size: var(--fs-xs);
    margin-top: var(--s-1);
}
.field-row { display: grid; grid-template-columns: 1fr 1fr auto; gap: var(--s-3); align-items: end; }

/* ---------------- Cards ---------------- */
.card {
    background: var(--bg-surface);
    border: 1px solid var(--rule-hair);
    border-radius: var(--radius-2);
    padding: var(--s-5);
    box-shadow: var(--shadow-1);
}
.card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid var(--rule-hair);
    padding-bottom: var(--s-3);
    margin-bottom: var(--s-4);
}
.card-head h2 { margin: 0; font-size: var(--fs-h3); font-family: var(--font-sans); }

/* ---------------- Tables ---------------- */
.data-table {
    width: 100%;
    border-collapse: collapse;
    font-size: var(--fs-small);
}
.data-table th {
    text-align: left;
    font-weight: var(--fw-semibold);
    color: var(--ink-tertiary);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    font-size: var(--fs-xs);
    padding: var(--s-2) var(--s-3);
    border-bottom: 1px solid var(--rule-strong);
}
.data-table td {
    padding: var(--s-3);
    border-bottom: 1px solid var(--rule-hair);
    color: var(--ink-primary);
}
.data-table tr:hover td { background: var(--bg-inset); }
.data-table .empty { text-align: center; color: var(--ink-tertiary); padding: var(--s-6); }

/* ---------------- Alerts & Badges ---------------- */
.alert-list {
    list-style: none;
    padding: 0;
    margin: 0;
    max-height: 480px;
    overflow-y: auto;
}
.alert-item {
    padding: var(--s-3) var(--s-4);
    border-bottom: 1px solid var(--rule-hair);
    display: grid;
    grid-template-columns: auto 1fr auto;
    gap: var(--s-3);
    align-items: center;
    animation: slide-in 320ms ease;
}
@keyframes slide-in {
    from { background: var(--amber-50); transform: translateX(-8px); opacity: 0; }
    to   { background: transparent;     transform: translateX(0);    opacity: 1; }
}
.alert-sev {
    width: 4px;
    align-self: stretch;
    background: var(--sev-medium);
}
.alert-sev.sev-HIGH     { background: var(--sev-high); }
.alert-sev.sev-CRITICAL { background: var(--sev-critical); }

.badge {
    display: inline-block;
    padding: 2px var(--s-2);
    font-size: var(--fs-xs);
    font-weight: var(--fw-semibold);
    letter-spacing: 0.08em;
    text-transform: uppercase;
    border-radius: var(--radius-1);
}
.badge-live {
    background: var(--ink-primary);
    color: var(--amber-on-dark);
}
.count-pill {
    display: inline-block;
    min-width: 28px;
    padding: 2px 8px;
    text-align: center;
    font-size: var(--fs-xs);
    font-weight: var(--fw-bold);
    background: var(--bg-inset);
    border: 1px solid var(--rule-hair);
    border-radius: 999px;
}

.sev {
    font-weight: var(--fw-semibold);
    font-size: var(--fs-xs);
    text-transform: uppercase;
    letter-spacing: 0.08em;
}
.sev-LOW      { color: var(--sev-low); }
.sev-MEDIUM   { color: var(--sev-medium); }
.sev-HIGH     { color: var(--sev-high); }
.sev-CRITICAL { color: var(--sev-critical); }

/* ---------------- Dashboard grid ---------------- */
.dashboard-grid {
    display: grid;
    grid-template-columns: 1fr 2fr;
    grid-template-rows: auto auto;
    gap: var(--s-5);
    margin-top: var(--s-5);
}
.card-stream   { grid-column: 1; grid-row: 1 / span 2; }
.card-map      { grid-column: 2; grid-row: 1; }
.card-pending  { grid-column: 2; grid-row: 2; }
.map-surface   { width: 100%; height: 440px; border: 1px solid var(--rule-hair); }

/* ---------------- Footer ---------------- */
.site-footer {
    border-top: 1px solid var(--rule-hair);
    margin-top: var(--s-8);
    padding: var(--s-5) 0;
    color: var(--ink-tertiary);
    font-size: var(--fs-small);
}
.footer-inner {
    max-width: var(--container-width);
    margin: 0 auto;
    padding: 0 var(--s-5);
    display: flex;
    justify-content: space-between;
}
```

### Design principles for Claude Code (do / don't)

| Do | Don't |
|---|---|
| Use hairline rules (1px `--rule-hair`) to separate content | Use drop shadows for separation |
| Use amber-600 exactly once per screen (for primary CTA) | Use amber on every clickable element |
| Pair Source Serif for display, Inter for UI | Use decorative/script fonts anywhere |
| Use generous whitespace (`s-6`/`s-7` between sections) | Cram multiple cards without breathing room |
| Use 2–4px `border-radius` or 0 | Use pill/rounded-full buttons or cards |
| Use muted semantic colours for severity | Use saturated red/green/blue for status |
| Render tables with zebra hover, not fills | Add gradient backgrounds to rows |
| Keep animations to fades and short slides (≤320ms) | Use bouncy/springy easing or scale transforms |
| Respect `prefers-reduced-motion` | Autoplay or loop background animations |

### JavaScript — `dashboard.js` (Firebase listener, vanilla)

```javascript
// src/main/resources/static/js/dashboard.js
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
import { getDatabase, ref, onChildAdded, query, limitToLast }
    from "https://www.gstatic.com/firebasejs/10.12.0/firebase-database.js";

const app = initializeApp(window.FIREBASE_CONFIG);
const db  = getDatabase(app);

const alertList = document.getElementById("alert-list");
const alertsRef = query(ref(db, "alerts"), limitToLast(50));

onChildAdded(alertsRef, (snap) => {
    const a = snap.val();
    const li = document.createElement("li");
    li.className = "alert-item";
    li.innerHTML = `
        <span class="alert-sev sev-${a.severity}"></span>
        <div>
            <strong>${escapeHtml(a.title)}</strong>
            <div class="alert-meta">${a.crimeType} · ${a.zoneName} · ${formatTime(a.timestamp)}</div>
        </div>
        <span class="sev sev-${a.severity}">${a.severity}</span>`;
    alertList.prepend(li);

    // Also drop a point onto the heatmap if present
    if (window.heatLayer) {
        window.heatLayer.addLatLng([a.latitude, a.longitude, 0.6]);
    }
});

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g,
        c => ({ "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;" }[c]));
}
function formatTime(ts) {
    const d = new Date(ts);
    return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
```

### `heatmap.js`

```javascript
// src/main/resources/static/js/heatmap.js
const map = L.map('map').setView([18.5204, 73.8567], 12);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap',
    maxZoom: 19
}).addTo(map);

// Heat layer, starts empty; dashboard.js adds points as alerts arrive
window.heatLayer = L.heatLayer([], {
    radius: 25,
    blur: 15,
    maxZoom: 17,
    gradient: {
        0.2: '#6b7c5b',
        0.4: '#8a7730',
        0.6: '#a85a2b',
        0.9: '#8a2b2b'
    }
}).addTo(map);

// Fetch existing points from server on page load
fetch('/api/public/reports/points')
    .then(r => r.json())
    .then(points => points.forEach(p => window.heatLayer.addLatLng([p.lat, p.lng, 0.5])));
```

### `report-form.js` (geolocation capture)

```javascript
document.getElementById('capture-loc').addEventListener('click', () => {
    if (!navigator.geolocation) {
        alert('Geolocation is not available in this browser.');
        return;
    }
    navigator.geolocation.getCurrentPosition(
        pos => {
            document.querySelector('input[name="latitude"]').value  = pos.coords.latitude.toFixed(6);
            document.querySelector('input[name="longitude"]').value = pos.coords.longitude.toFixed(6);
        },
        err => alert('Unable to capture location: ' + err.message),
        { enableHighAccuracy: true, timeout: 10_000 }
    );
});
```


---

## 24. Seed Data

`seed/DemoDataSeeder.java` runs on application startup (`ApplicationRunner`). It checks if the `zones` collection is empty; if so, it seeds a baseline dataset so the demo is immediately usable.

### `seed/DemoDataSeeder.java`

```java
package com.crimewatch.seed;

import com.crimewatch.entity.*;
import com.crimewatch.enums.*;
import com.crimewatch.repository.*;
import com.crimewatch.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        if (zoneRepo.count() > 0) {
            log.info("Seed skipped — data already present.");
            return;
        }
        log.info("Seeding demo data...");

        // --- Zones ---
        Zone z1 = new Zone(null, "Central District",  "Pune", 18.5204, 73.8567, 5, System.currentTimeMillis());
        Zone z2 = new Zone(null, "Kothrud",           "Pune", 18.5074, 73.8077, 4, System.currentTimeMillis());
        Zone z3 = new Zone(null, "Hinjawadi IT Park", "Pune", 18.5912, 73.7389, 6, System.currentTimeMillis());
        List<Zone> zones = List.of(z1, z2, z3);
        zones.forEach(z -> { z.setZoneId(IdGenerator.zoneId()); zoneRepo.save(z); });

        // --- Users (one per role) ---
        userRepo.save(buildUser("admin",   "admin@crimewatch.local",   Role.ADMIN,   null,             "admin123"));
        userRepo.save(buildUser("officer1","officer1@crimewatch.local",Role.OFFICER, z1.getZoneId(),   "officer123"));
        userRepo.save(buildUser("officer2","officer2@crimewatch.local",Role.OFFICER, z2.getZoneId(),   "officer123"));
        userRepo.save(buildUser("citizen", "citizen@crimewatch.local", Role.CITIZEN, null,             "citizen123"));

        // --- Sample reports (some anonymous) ---
        reportRepo.save(buildReport("Unattended bag at station", CrimeType.SUSPICIOUS, Severity.MEDIUM,
                                    18.5211, 73.8571, z1.getZoneId(), null));
        reportRepo.save(buildReport("Phone snatching incident",  CrimeType.THEFT,      Severity.HIGH,
                                    18.5072, 73.8080, z2.getZoneId(), null));
        reportRepo.save(buildReport("Vandalism at bus stop",     CrimeType.VANDALISM,  Severity.LOW,
                                    18.5910, 73.7391, z3.getZoneId(), null));
        reportRepo.save(buildReport("Loud altercation on street", CrimeType.ASSAULT,   Severity.CRITICAL,
                                    18.5205, 73.8570, z1.getZoneId(), null));

        log.info("Demo data seeded: {} zones, 4 users, 4 reports.", zones.size());
    }

    private User buildUser(String username, String email, Role role, String zoneId, String password) {
        User u = new User();
        u.setUserId(IdGenerator.userId());
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role);
        u.setZoneId(zoneId);
        u.setEnabled(true);
        u.setCreatedAt(System.currentTimeMillis());
        return u;
    }

    private CrimeReport buildReport(String title, CrimeType type, Severity sev,
                                    double lat, double lng, String zoneId, String reporterId) {
        CrimeReport r = new CrimeReport();
        r.setReportId(IdGenerator.reportId());
        r.setTitle(title);
        r.setDescription(title + " — seeded demo entry.");
        r.setCrimeType(type);
        r.setSeverity(sev);
        r.setLatitude(lat);
        r.setLongitude(lng);
        r.setZoneId(zoneId);
        r.setReporterId(reporterId);
        r.setStatus(ReportStatus.PENDING);
        r.setReportedAt(System.currentTimeMillis());
        return r;
    }
}
```

### Demo credentials (document in README + viva slides)

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Officer | `officer1` | `officer123` |
| Officer | `officer2` | `officer123` |
| Citizen | `citizen` | `citizen123` |

---

## 25. Build & Run Instructions

### Prerequisites

- Java 17 (`java --version`)
- Maven 3.9+ (`mvn --version`) — or use the `./mvnw` wrapper
- Firebase project with Firestore + Realtime Database enabled
- `firebase-service-account.json` placed in project root

### Steps

```bash
# 1. Clone
git clone https://github.com/<your-username>/crimewatch.git
cd crimewatch

# 2. Place firebase-service-account.json in project root
#    (see §13 for how to obtain it)

# 3. Update application.properties with your Firebase URL + project ID

# 4. Build
./mvnw clean package

# 5. Run
./mvnw spring-boot:run
#    Web app:        http://localhost:8080
#    Socket server:  localhost:9090
```

### Smoke test

1. Open `http://localhost:8080/` → landing page loads with editorial styling
2. Click "Report incident" → fill form → "Capture my location" → submit
3. In another tab, sign in as `officer1 / officer123` → dashboard → new report should appear in the live alert stream within 1–2 seconds
4. Sign in as `admin / admin123` → `/admin/audit` should show the submission event

---

## 26. Testing Plan

### Unit tests (`src/test/java`)

| Target | Test class | What to cover |
|---|---|---|
| `CrimeReportService` | `CrimeReportServiceTest` | Submit happy path (mock repos + Firebase), nearest-zone resolution, transaction call |
| `EscalationService` | `EscalationServiceTest` | Threshold boundary, level escalation WARNING→ALERT→CRITICAL |
| `AuditLoggingFilter` | `AuditLoggingFilterTest` | POST triggers audit write, GET does not, audit failure does not break request |
| `FirestoreUserDetailsService` | `UserDetailsServiceTest` | Maps entity to Spring UserDetails correctly |
| `IdGenerator` | `IdGeneratorTest` | Prefix correctness, uniqueness under concurrent calls |

### Integration tests

- `@SpringBootTest` with an embedded Firestore emulator (`firebase emulators:start --only firestore`) for the repository layer
- `WebMvcTest` for controllers using `MockMvc`
- Spring Security tests using `@WithMockUser(roles = ...)`

### Manual test checklist (attach screenshots to final report)

- [ ] Anonymous report submission succeeds and places point on heatmap
- [ ] Officer dashboard receives the new alert within 2s
- [ ] Escalation fires after N reports in a zone within the lookback window
- [ ] Socket demo client (`SocketClientDemo`) prints broadcast JSON
- [ ] Admin CSV export downloads a valid file with all reports
- [ ] Audit log entry exists for every POST/PUT/DELETE request
- [ ] 403 page shown when citizen tries `/dashboard` or `/admin`
- [ ] Session times out after 30 minutes of inactivity

---

## 27. Deliverables Checklist

Per the rubric:

### Moodle submission

- [ ] Zipped project source (exclude `target/`, `node_modules/`, `.idea/`, `*.class`, `firebase-service-account.json`)
- [ ] Project report PDF containing:
  - [ ] Title page: title, team members, roll numbers, CO mapping, SDG addressed
  - [ ] Abstract (≤200 words)
  - [ ] Problem statement + SDG justification (≤200 words)
  - [ ] Data model diagram (ER + Firebase collection tree)
  - [ ] Class diagram (services + repositories)
  - [ ] Use case diagram
  - [ ] Implementation — key code snippets:
    - Firestore repository impl
    - `@Async` + `@Scheduled` escalation flow
    - `ServerSocket` broadcaster
    - `AuditLoggingFilter`
    - Spring Security configuration
  - [ ] Screenshots of every module (citizen form, heatmap, dashboard live alerts, admin panel, audit log, CSV export)
  - [ ] SDG impact dashboard screenshot with "Avg Response Time" metric
  - [ ] Viva preparation notes (below)
  - [ ] Conclusion & future scope
  - [ ] References

### GitHub repository

- [ ] Public repo (or shared with evaluator)
- [ ] `README.md` with setup + demo credentials + screenshots
- [ ] `.gitignore` excludes `firebase-service-account.json`
- [ ] Clean commit history (no single 50-file "initial commit")
- [ ] `docs/` folder with diagrams (`er.png`, `class.png`, `usecase.png`)

### Viva preparation notes (include in report)

**Q: Why Firebase instead of MySQL?**
> Firebase Realtime Database delivers sub-second push updates to the Police Dashboard without any polling logic, which directly supports the real-time CO4 requirement. Firestore provides structured document storage with its own transaction API (`runTransaction`) that serves as our `@Transactional` equivalent. We acknowledge this trades off some Hibernate/JPA rubric marks in exchange for a simpler deployment (no MySQL install) and much stronger realtime UX.

**Q: Where is the `@Transactional` semantics?**
> Every multi-document write in the service layer is wrapped in `firestore.runTransaction(txn -> ...)`. Firestore guarantees atomicity across all reads and writes inside the transaction block — if any step fails, the entire transaction rolls back, same as Hibernate's `@Transactional`.

**Q: Where is the JDBC batch concept demonstrated?**
> `ExportService.exportAllReportsAsCsv()` performs a paginated Firestore batch read (500 docs per page using `Query.startAfter`) and streams CSV rows to the response, which is the conceptual equivalent of `PreparedStatement.executeBatch()` — both avoid single-row round-trips.

**Q: Explain the multithreading design.**
> `@Scheduled(fixedDelay = 5min)` triggers `EscalationScheduler.sweepZones()`, which iterates zones and calls `EscalationService.evaluateZone(zone)`. That method is `@Async` and runs on `crimewatchTaskExecutor` (`ThreadPoolTaskExecutor`, core 4 / max 8 / queue 100). So the scheduler doesn't block — zones are evaluated concurrently up to the pool size.

**Q: Walk me through the socket flow.**
> `SocketServer` binds port 9090 in a `@PostConstruct` daemon thread and holds a `CopyOnWriteArrayList<PrintWriter>` of connected clients. When a report is submitted, `CrimeReportService` calls `AlertSocketBroadcaster.broadcastJson(alert)`, which serialises the alert as JSON and writes to every connected client. `SocketClientDemo.java` lets me demonstrate a raw Java TCP client receiving alerts live.

**Q: Where's the Servlet filter and how does it differ from the Interceptor?**
> `AuditLoggingFilter` implements `jakarta.servlet.Filter` — it runs before Spring MVC even sees the request, captures every POST/PUT/DELETE, and writes an audit entry. `AuthInterceptor` is a Spring `HandlerInterceptor` — it runs after Spring resolves the handler and has access to the `Authentication` object. The filter is Servlet-level (CO2), the interceptor is Spring-level.

---

## 28. Future Scope

- **Mobile app** — React Native / Flutter client with camera attach and push notifications
- **AI crime prediction** — trained on historical reports, flags probable hotspots (Python microservice via REST)
- **Media evidence** — photo/video uploads via Firebase Storage
- **SMS dispatch** — Twilio integration to page officers on critical alerts
- **Multi-city / multi-tenant** — city-scoped data partitions so several municipalities share infrastructure
- **NLP auto-classification** — derive `crimeType` and severity from natural-language descriptions
- **Offline PWA** — queue reports from poor-connectivity areas and sync when online
- **Immutable audit trail** — publish audit log hashes to a public chain for tamper-evidence

---

## Appendix A — `util/IdGenerator.java`

```java
package com.crimewatch.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {}
    private static String shortUuid() { return UUID.randomUUID().toString().replace("-", "").substring(0, 10); }
    public static String reportId()      { return "rpt_" + shortUuid(); }
    public static String zoneId()        { return "zone_" + shortUuid(); }
    public static String userId()        { return "usr_" + shortUuid(); }
    public static String assignmentId()  { return "asn_" + shortUuid(); }
    public static String escalationId()  { return "esc_" + shortUuid(); }
    public static String auditId()       { return "log_" + shortUuid(); }
    public static String alertId()       { return "alt_" + shortUuid(); }
}
```

---

## Appendix B — `config/FirebaseConfig.java`

```java
package com.crimewatch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @Value("${firebase.project.id}")
    private String projectId;

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp already initialised");
            return;
        }
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                .setDatabaseUrl(databaseUrl)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options);
        log.info("FirebaseApp initialised for project {}", projectId);
    }
}
```

---

## Appendix C — `CrimeWatchApplication.java`

```java
package com.crimewatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrimeWatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrimeWatchApplication.class, args);
    }
}
```

---

## Appendix D — `README.md` (generate at repo root)

```markdown
# CrimeWatch

Crowdsourced crime reporting with a live heatmap and real-time Police Dashboard.

**Stack:** Spring Boot 3.x · Firebase (Firestore + Realtime Database) · JSP + JSTL · Spring Security · Java Sockets

## Quick start

1. Clone the repo
2. Create a Firebase project, enable Firestore + Realtime Database, download service account JSON
3. Place the JSON as `firebase-service-account.json` in the project root
4. Update `application.properties` with your `firebase.database.url` and `firebase.project.id`
5. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
6. Visit `http://localhost:8080`

## Demo credentials

| Role    | Username   | Password     |
|---------|------------|--------------|
| Admin   | `admin`    | `admin123`   |
| Officer | `officer1` | `officer123` |
| Citizen | `citizen`  | `citizen123` |

## Project report

See `docs/CrimeWatch_Report.pdf`.

## Team

- `[Member 1 Name — Roll No.]`
- `[Member 2 Name — Roll No.]`

SY B.Tech · EAD Mini Project · AY 2025–26
```

---

*End of specification. Claude Code: implement strictly in the order listed in §0. Do not introduce new technologies or swap Firebase for MySQL.*
