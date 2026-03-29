# CrimeWatch — Manual Setup Guide

Step-by-step manual actions required to get this project running on a fresh machine. Everything listed here must be done by hand — the repo alone is not sufficient to boot the app.

---

## 1. Prerequisites

Install these before anything else:

| Tool      | Version       | Check                    |
|-----------|---------------|--------------------------|
| JDK       | 17 (exactly)  | `java -version`          |
| Maven     | 3.9.x         | `mvn -v`                 |
| Git       | any           | `git --version`          |

**If Maven is not installed** (Debian/Kali/Ubuntu):

```bash
# Option A — system package (needs sudo)
sudo apt install maven

# Option B — user-local, no sudo
mkdir -p ~/.local/opt && cd ~/.local/opt
curl -sSL -o maven.tar.gz \
  https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
tar xzf maven.tar.gz && rm maven.tar.gz
export PATH="$HOME/.local/opt/apache-maven-3.9.9/bin:$PATH"   # add to ~/.zshrc / ~/.bashrc
```

**Set `JAVA_HOME`** (needed by Maven):

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64    # adjust to your JDK path
```

Confirm with `mvn -v` — it should print `Java version: 17.x`.

---

## 2. Create the Firebase project

The backend will not boot without valid Firebase credentials.

1. Go to <https://console.firebase.google.com> → **Add project** → name it `crimewatch-<yourname>` → disable Analytics (not needed).
2. In the new project: **Build → Firestore Database → Create database → Start in production mode** → choose the nearest region.
3. **Build → Realtime Database → Create database → Start in locked mode**.
4. **Project settings (gear icon) → Service accounts → Generate new private key** → download the JSON file.
5. Rename the downloaded file to `firebase-service-account.json` and drop it in the **project root** (same folder as `pom.xml`).
6. Note three values from the Firebase console — you need them in the next step:
   - **Project ID** — *Project settings → General*.
   - **Realtime Database URL** — *Build → Realtime Database* (looks like `https://crimewatch-xxxx-default-rtdb.firebaseio.com/`).
   - **Web API key** — *Project settings → General → Your apps → Web app* (only needed if you register a web app; see step 5 below).

---

## 3. Wire the backend to your Firebase project

Edit `src/main/resources/application.properties`:

```properties
firebase.credentials.path=firebase-service-account.json
firebase.database.url=https://crimewatch-xxxx-default-rtdb.firebaseio.com/
firebase.project.id=crimewatch-xxxx
```

Replace the placeholders with the values from step 2.6. The credentials path is relative to the working directory where you run the app.

---

## 4. Firestore / Realtime Database rules (optional but recommended)

For local development the default "locked" rules are fine — the server uses the admin SDK and bypasses them. If you later deploy the static JS dashboard, relax the Realtime Database rules to allow read of the `alerts/` path for authenticated users.

---

## 5. (Optional) Web Firebase config for the Police dashboard

The police dashboard (`/WEB-INF/views/police/dashboard.jsp`) injects `window.FIREBASE_CONFIG` with `databaseURL` and `projectId`. If you need full client-side Firebase features (auth, Firestore from the browser), register a **Web app** in the Firebase console and extend the injected config. No change is required just to watch the live alert feed.

---

## 6. Ports

| Port | Purpose                                   |
|------|-------------------------------------------|
| 8080 | HTTP (Spring MVC + JSP)                   |
| 9090 | Raw Java `ServerSocket` broadcaster       |

Make sure neither is in use: `ss -lntp | grep -E '8080|9090'`. Change them in `application.properties` (`server.port`, `socket.server.port`) if needed.

---

## 7. Build and run

From the project root:

```bash
# build (produces target/crimewatch-1.0.0.jar)
mvn -DskipTests package

# run
mvn spring-boot:run
# — or —
java -jar target/crimewatch-1.0.0.jar
```

On first boot, `DemoDataSeeder` populates Firestore with sample zones, users, and reports. Watch the log for `FirebaseApp initialised for project …` — if that line is missing the credentials are wrong.

Open <http://localhost:8080>.

---

## 8. Demo logins

Created by the seeder on first boot:

| Role    | Username   | Password     |
|---------|------------|--------------|
| Admin   | `admin`    | `admin123`   |
| Officer | `officer1` | `officer123` |
| Officer | `officer2` | `officer123` |
| Citizen | `citizen`  | `citizen123` |

---

## 9. Common failure modes

| Symptom                                                                                              | Fix                                                                                       |
|------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `FileNotFoundException: firebase-service-account.json`                                               | File missing or run from wrong directory. Put the JSON in the project root.               |
| `PERMISSION_DENIED` from Firestore                                                                   | Service account lacks Firestore access — regenerate the key, make sure the project matches. |
| `The JAVA_HOME environment variable is not defined correctly`                                        | `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`                                     |
| `Address already in use` on 8080 or 9090                                                             | Change `server.port` / `socket.server.port` in `application.properties`.                  |
| Dashboard map loads but no live alerts appear                                                        | `firebase.database.url` wrong, or Realtime Database rules block reads.                    |

---

## 10. Files you must not commit

Add these to `.gitignore` if not already present:

```
firebase-service-account.json
target/
*.log
```

The service account JSON grants full admin access to your Firebase project — treat it like a password.
