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

## Firebase Setup

1. Visit https://console.firebase.google.com → **Add Project** → name it `crimewatch-<yourname>`
2. **Build → Firestore Database** → Create database → Start in **production mode** → pick nearest region
3. **Build → Realtime Database** → Create database → Start in **locked mode**
4. **Project Settings → Service accounts** → **Generate new private key** → download JSON
5. Rename to `firebase-service-account.json` and place in the project root
6. Copy the **Realtime Database URL** into `application.properties` → `firebase.database.url`
7. Copy the **Project ID** into `firebase.project.id`
8. Run `./mvnw spring-boot:run` — `DemoDataSeeder` will populate sample data on first boot

## Demo credentials

| Role    | Username   | Password     |
|---------|------------|--------------|
| Admin   | `admin`    | `admin123`   |
| Officer | `officer1` | `officer123` |
| Officer | `officer2` | `officer123` |
| Citizen | `citizen`  | `citizen123` |

## Architecture

- **Spring Boot 3.2.x** with Spring MVC, Spring Security (3 roles), JSP + JSTL views
- **Firebase Firestore** — primary database (structured documents)
- **Firebase Realtime Database** — live alert stream for sub-second push updates
- **Java ServerSocket** on port 9090 — multi-client fan-out broadcaster
- **@Async + @Scheduled + ThreadPoolTaskExecutor** — zone escalation engine
- **Servlet Filter** (`AuditLoggingFilter`) — audit trail for all state-changing requests
- **Spring Interceptor** (`AuthInterceptor`) — authentication logging

## SDG Alignment

- **SDG 16** — Peace, Justice and Strong Institutions
- **SDG 11** — Sustainable Cities and Communities

## Team

- `[Member 1 Name — Roll No.]`
- `[Member 2 Name — Roll No.]`

SY B.Tech · EAD Mini Project · AY 2025-26
