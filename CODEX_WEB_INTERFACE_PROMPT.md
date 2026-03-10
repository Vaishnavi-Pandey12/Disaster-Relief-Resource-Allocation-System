# Codex Prompt — Build a Website Interface for DDR-SAS (Replace Console UI)

You are a senior full-stack engineer. Build a **web-based interface** for this project:

**Dynamic Disaster Relief Simulation & Allocation System (DDR-SAS)**

## Objective
Replace/augment the current console interaction with a browser UI while preserving existing backend allocation concepts:
- Priority Queue request handling by urgency
- Graph routing + Dijkstra shortest path
- Resource allocation (food, water, medicine)
- Event logging
- Optional threaded simulation of incoming requests

---

## Current Java Package Structure (must keep backend modular)
```
com.ddrsas
 ├── app
 │    └── DisasterReliefApp.java
 ├── model
 │    ├── Request.java
 │    ├── ReliefCenter.java
 │    └── Edge.java
 ├── util
 │    ├── Dijkstra.java
 │    ├── Allocator.java
 │    └── Logger.java
 └── data
      └── Graph.java
```

---

## Required Web Architecture
Use **Java 17 + Spring Boot** and implement a clean layered structure:

- `controller` layer: REST endpoints
- `service` layer: orchestration of allocation, queue handling, simulation
- `model` layer: reuse existing domain entities (or adapt minimally)
- `repository/store` layer: in-memory state (PriorityQueue, centers, logs, graph)
- `web` layer: static frontend (HTML/CSS/JS) served by Spring Boot

If needed, create new packages:
- `com.ddrsas.api`
- `com.ddrsas.service`
- `com.ddrsas.store`
- `src/main/resources/static`

---

## Website Features (must implement)

### 1) Dashboard Layout
Create a modern single-page interface with cards/sections:
- Header: “DDR-SAS Disaster Relief System”
- Quick stats:
  - Pending Requests count
  - Total Allocations
  - Active Relief Centers
  - Simulation status (ON/OFF)

### 2) Add Relief Request Form
Fields:
- Area Name (text)
- Food Required (number)
- Water Required (number)
- Medicine Required (number)
- Urgency (1–5)

Behavior:
- Client + server validation
- Submit via `POST /api/requests`
- Success toast/message
- Auto-refresh request table

### 3) Pending Requests Table
- Show sorted by urgency (desc), then creation time
- Columns: ID, Area, Food, Water, Medicine, Urgency, Created At
- Urgency color badges:
  - 4–5 = red
  - 3 = yellow
  - 1–2 = green

Endpoint example: `GET /api/requests/pending`

### 4) Allocate Resources Action
- “Allocate Next Request” button
- Call `POST /api/allocate/next`
- Show result panel:
  - Request ID
  - Center name
  - Route distance
  - Status (success/failure)
- Include loading spinner animation during request

### 5) Relief Centers Table
- Columns: Center, Location, Food Stock, Water Stock, Medicine Stock
- Endpoint: `GET /api/centers`

### 6) Event Logs Panel
- Scrollable log view with timestamps
- Endpoint: `GET /api/logs`
- Optional auto-refresh every 3–5s

### 7) Simulation Controls (Threaded)
- Start Simulation button → `POST /api/simulation/start`
- Stop Simulation button → `POST /api/simulation/stop`
- Status badge from `GET /api/simulation/status`
- Simulation should generate random requests every few seconds using a scheduler/thread

---

## REST API Contract (implement)
Use JSON request/response and proper status codes.

- `POST /api/requests`
- `GET /api/requests/pending`
- `POST /api/allocate/next`
- `GET /api/centers`
- `GET /api/logs`
- `POST /api/simulation/start`
- `POST /api/simulation/stop`
- `GET /api/simulation/status`
- `GET /api/health`

Return clear DTOs; do not expose mutable internals directly.

---

## Technical Constraints
1. Java 17+
2. Keep algorithms modular and reusable
3. Preserve separation of concerns
4. Do not embed business logic in controllers
5. Thread safety for shared state (queue/logs/simulation)
6. Use DTO classes for API payloads
7. Add global exception handling with meaningful messages

---

## Frontend Requirements
- Plain HTML/CSS/Vanilla JS (no heavy frameworks required)
- Responsive layout using CSS grid/flex
- Clean theme with colored badges and status chips
- Reusable helper functions for API calls and table rendering
- `setInterval` refresh for dynamic sections (pending/logs/status)

Files expected:
- `src/main/resources/static/index.html`
- `src/main/resources/static/styles.css`
- `src/main/resources/static/app.js`

---

## Optional Enhancements
- Route path visualization text (Center -> ... -> Area)
- Filter logs by type (INFO/SUCCESS/ERROR/SIM)
- Allocation strategy selector (nearest, max-stock, hybrid)
- Persist data using H2 in-memory DB

---

## Deliverables
1. Spring Boot project files (`pom.xml`, main app class, packages)
2. REST controllers + services + DTOs
3. Static web UI (index.html/css/js)
4. Updated README with:
   - build/run instructions
   - API endpoint list
   - screenshots section placeholders
5. Ensure app runs with:

```bash
mvn spring-boot:run
```

and web UI is accessible at:

`http://localhost:8080`

---

## Acceptance Criteria
- User can perform all 7 core actions through the website
- Allocation uses existing DSA concepts correctly
- UI updates correctly after operations
- Simulation can be started/stopped safely
- Build passes with no compile errors

Now implement the full solution with production-quality, well-commented code.
