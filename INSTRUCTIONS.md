# PA03: Spring Boot (Part 1)
## The Yard API — HBCU Organizations & Opportunities Directory

---

### Overview

**Goal:** Build the first slice of a read-only REST API that helps HBCU students discover campus organizations, scholarship opportunities, professional development programs, and community events — all in one place.

Every HBCU student knows how hard it can be to find out about the scholarships, internships, and student organizations that are actually available to them. Information gets buried in emails, outdated flyers, and word of mouth. *The Yard API* solves that by providing a clean, searchable data service that a front-end application — a mobile app, a campus portal, a Discord bot — could call to surface the right opportunities to the right students.

In **Part 1**, you will build the read-only foundation: a Spring Boot application that stores opportunity listings in memory, exposes GET endpoints for browsing and filtering, produces structured log output, and is tested using MockMvc. In **Part 2**, you will extend the API with write operations so that advisors and student leaders can add and update listings programmatically.

---

### Learning Objectives

By completing this assignment, you will be able to:

- Create REST controller endpoints in Spring Boot that respond to HTTP GET requests
- Wire application components together using **constructor-based dependency injection**
- Store and retrieve structured data using in-memory Java data structures
- Produce meaningful **log output** at application startup and on each incoming request using SLF4J
- Write **MockMvc tests** that verify your controller's behavior without starting a real web server

---

### The Domain: Opportunities

Each item your API manages represents an **Opportunity** — something a student at an HBCU might genuinely want to know about. Every opportunity has the following fields:

| Field         | Description                                                                  |
|---------------|------------------------------------------------------------------------------|
| `id`          | Unique identifier (e.g., `"opp-001"`)                                        |
| `title`       | Name of the opportunity (e.g., "UNCF STEM Scholarship")                      |
| `type`        | Category: `Scholarship`, `Internship`, `Organization`, `Event`, `Fellowship` |
| `sponsor`     | Who is offering it (e.g., "NSBE", "Google", "Student Government")            |
| `deadline`    | Application or RSVP deadline (e.g., `"2025-04-15"`)                         |
| `description` | A brief summary of what the opportunity offers                               |
| `tags`        | Keywords for searching (e.g., `["STEM", "freshmen", "paid", "virtual"]`)    |
| `url`         | Link to learn more or apply                                                  |

The `type` field enables category-level filtering (e.g., "show me only scholarships"). The `tags` field supports keyword search (e.g., "show me anything tagged with `engineering` or `freshmen`").

---

### Project Setup (Maven Only)

Generate your project at [https://start.spring.io](https://start.spring.io) with the following configuration:

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 3.x
- **Java:** 17
- **Dependencies:** Spring Web, Spring Boot Test

**Common Maven commands:**

```bash
mvn spring-boot:run     # Start the application
mvn test                # Run all tests
mvn clean package       # Build a JAR
```

Your `README.md` must include: prerequisites (JDK 17+), instructions for running the app and the tests, and sample Postman request details for each endpoint.

---

### Application Architecture

Your Spring Boot application must follow a three-layer architecture. Each layer has a single, distinct responsibility, and dependencies between layers must flow strictly downward — each layer only knows about the layer directly beneath it.

**Layer 1 — Controller (`OpportunitiesController`)**
Handles all incoming HTTP requests. Maps URL paths and query parameters to the appropriate service method. Returns the correct HTTP status codes (`200`, `404`). Does not contain any business logic or data access code.

**Layer 2 — Service (`OpportunitiesService`)**
Contains the application's business logic. Delegates data retrieval to the store layer and applies any filtering the controller requests — by `type`, by keyword, or both. Does not know anything about HTTP or how requests arrive.

**Layer 3 — In-Memory Store (`InMemoryOpportunityStore`)**
Owns all of the application's data. Seeds a realistic set of opportunity records at startup and exposes read methods that the service can call. Contains no business logic and no filtering — it simply returns data.

All three layers must be wired together using **constructor-based dependency injection**. Field injection (placing `@Autowired` directly on a field) is not permitted anywhere in this assignment.

---

### The In-Memory Store

The store is a Spring-managed component (annotated with `@Component`) that initializes your opportunity data in its constructor and exposes three read methods.

**Data structure:** Use a `Map<String, OpportunityDTO>` keyed by ID for efficient lookup, and a `List<OpportunityDTO>` for efficient listing. Both structures must be populated in the constructor from the same set of seed data.

**Seed data requirements:** Populate **at least 8 realistic entries** with a meaningful mix of types and tags. Your data should reflect real-world opportunities that HBCU students would genuinely find useful. Some examples to get you started:

- UNCF STEM Scholarship — type: `Scholarship`, tags: `["STEM", "undergrad", "paid"]`
- Google HBCU Career Residency — type: `Fellowship`, tags: `["tech", "paid", "summer"]`
- National Society of Black Engineers Chapter — type: `Organization`, tags: `["engineering", "networking", "STEM"]`
- Homecoming Step Show Registration — type: `Event`, tags: `["culture", "performing arts", "campus"]`
- Goldman Sachs Summer Analyst Program — type: `Internship`, tags: `["finance", "paid", "junior", "senior"]`

**Required methods the store must expose:**

- A method that returns all opportunities as an unmodifiable list — this prevents other layers from accidentally mutating the store's data.
- A method that accepts an ID string and returns the matching opportunity wrapped in an `Optional` — returning an empty `Optional` when no match is found.
- A method that accepts optional `type` and `q` filter parameters and returns a filtered list — this is where you apply the stream-based filtering logic using Java's `Stream` API.

For the filtering method, filtering rules are as follows: if a `type` value is provided, keep only opportunities whose type matches (case-insensitive). If a `q` value is provided, keep only opportunities where the `title` contains the keyword (case-insensitive) or where any entry in `tags` contains the keyword (case-insensitive). Both filters may be active simultaneously.

**At startup**, the store must log an `INFO`-level message reporting how many opportunities were seeded. This confirms that initialization succeeded and gives you a quick sanity check whenever you restart the application.

---

### The Data Transfer Object (DTO)

All data passing between layers and out through the API must be represented as an `OpportunityDTO`. This class contains the same fields listed in the domain table above: `id`, `title`, `type`, `sponsor`, `deadline`, `description`, `tags`, and `url`.

A **DTO (Data Transfer Object)** acts as the contract between your application and its consumers. By routing all data through the DTO, you ensure that internal implementation details — such as how data is stored — never leak into the public API. This is a standard professional practice that becomes especially important when your API has external consumers.

In Part 2, you will introduce additional DTOs for creating and updating opportunities. For now, `OpportunityDTO` is the only one you need.

---

### Required Endpoints

**`GET /api/opportunities`**
Returns a JSON array of all opportunities. Supports two optional query parameters:

- `type` — filters results to only opportunities matching the specified type, case-insensitively. Example: `?type=Scholarship`
- `q` — performs a case-insensitive keyword search across the `title` field and each entry in `tags`. Example: `?q=STEM`

Both filters may be combined in the same request. If neither is provided, all opportunities are returned. This endpoint always returns HTTP `200 OK`.

**`GET /api/opportunities/{id}`**
Returns a single opportunity matching the given ID. Returns `200 OK` with the opportunity object if found. Returns `404 Not Found` if no opportunity with that ID exists.

---

### Logging Requirements

Logging is a professional expectation in any production system — it is how engineers understand what an application is doing at runtime without attaching a debugger. Your application must use **SLF4J** for all log output. Do not use `System.out.println()` anywhere.

Acquire a logger in each class that logs by declaring a private static final `Logger` field using `LoggerFactory.getLogger(YourClass.class)`. Then call it using parameterized message formatting (e.g., passing values as arguments rather than concatenating strings), which is both more performant and more readable.

**Startup log — in the store's constructor:**
After seeding your data, emit an `INFO`-level message that reports the number of opportunities loaded. Example format: `"Seeded X opportunities into the in-memory store."` This should appear in the console every time the application starts.

**Per-request log — in the list endpoint handler:**
Emit an `INFO`-level message that includes the request path and the values of the `type` and `q` parameters (even when they are absent). Example format: `"GET /api/opportunities — type=Scholarship, q=<empty>"`. This allows you to trace which filters are being applied on every call.

**Per-request log — in the detail endpoint handler:**
Emit an `INFO`-level message that includes the request path and the ID being requested. Example format: `"GET /api/opportunities/opp-003"`.

All required log statements must be at the `INFO` level. You are encouraged to add `DEBUG`-level logs inside your service's filtering logic, but these are not required for full credit.

---

### Testing with MockMvc

**MockMvc** is Spring's built-in framework for testing the web layer of your application. It simulates HTTP requests and evaluates responses without starting a real web server, which makes tests fast and focused. This is the standard approach for controller-level testing in Spring Boot applications.

Your test class should use `@WebMvcTest` scoped to your controller. This loads only the web layer — not the full application context — which keeps tests lean. Because the service is not loaded as a real bean under `@WebMvcTest`, you must substitute it with a mock using `@MockBean`. You then configure that mock to return specific data for each test scenario using Mockito's `when(...).thenReturn(...)` pattern.

You are required to write at least **two passing MockMvc tests**. A bonus third test is described below.

**Required Test 1 — List endpoint returns `200 OK` and a JSON array.**
Configure the mock service to return a list of at least two `OpportunityDTO` objects when called with no filters. Perform a GET request to `/api/opportunities`. Assert that the HTTP status is `200` and that the response body is a JSON array containing the expected number of elements. You may also assert on the value of a specific field (e.g., the title of the first element).

**Required Test 2 — Detail endpoint returns `200 OK` and the correct title for a known ID.**
Configure the mock service to return a specific `OpportunityDTO` when queried with a known ID. Perform a GET request to `/api/opportunities/{id}`. Assert that the HTTP status is `200` and that the `title` field in the JSON response matches the expected value.

**Bonus Test — Detail endpoint returns `404` for an unknown ID.**
Configure the mock service to return an empty `Optional` for an ID that does not exist in the store. Perform a GET request to `/api/opportunities/{id}` using that nonexistent ID. Assert that the HTTP status is `404`.

---

### Requirements (Mastery Targets)

| Criterion | Mastered | Not Yet Mastered |
|-----------|----------|------------------|
| **Build & Run** | Application starts with `mvn spring-boot:run`; README includes setup, run, and test instructions with sample Postman request details | Build fails, or README is absent or incomplete |
| **In-Memory Store** | Store is seeded with ≥ 8 realistic entries in the constructor; both `Map` and `List` structures are used; an unmodifiable copy is returned from `findAll` | Fewer than 8 entries, data hardcoded in the controller or service, or mutable internal data exposed directly |
| **Constructor DI** | All three layers wired via constructor injection; no `@Autowired` on any field | Field injection used anywhere in the codebase |
| **Endpoints** | Both endpoints return correct HTTP status codes and correctly shaped JSON; type and q filters work; 404 returned for unknown IDs | Wrong status codes, missing endpoints, or incorrect response structure |
| **DTO Use** | All API responses expose only `OpportunityDTO` fields; no internal types or raw structures appear in responses | Internal types or unintended fields appear in API output |
| **Logging** | Startup count log appears in the store; each controller endpoint logs path and parameters; SLF4J used throughout; no `System.out.println()` | Logs absent, at wrong level, or using `System.out.println()` |
| **Tests** | ≥ 2 passing MockMvc tests (list + detail) using `@WebMvcTest` and `@MockBean`; tests make assertions on status codes and JSON content | Tests missing, not using MockMvc, using wrong annotations, or failing |
| **Repo Hygiene** | `.gitignore` excludes build artifacts; packages logically organized; README is clear and complete | Messy package structure, missing `.gitignore`, or insufficient documentation |

All criteria must be **Mastered** to receive full credit. Any unmet criterion results in a **Not Yet Mastered** mark, requiring a resubmission token.

---

### Suggested Package Layout

```
src/main/java/edu/famu/cop3060/yard/
├── YardApplication.java
├── controller/
│   └── OpportunitiesController.java
├── service/
│   └── OpportunitiesService.java
├── store/
│   └── InMemoryOpportunityStore.java
└── dto/
    └── OpportunityDTO.java

src/test/java/edu/famu/cop3060/yard/
└── controller/
    └── OpportunitiesControllerTest.java
```

---

### Submission Checklist

Submit the following via Canvas:

1. Your **GitHub repository URL**
2. Your **final commit hash** (copy from GitHub after your last push)
3. One **sample request URL** that demonstrates filtering working correctly (e.g., `/api/opportunities?type=Scholarship&q=STEM`)
4. A **README.md** file in the root of your repository that includes:
    - Prerequisites (JDK 17+)
    - Step-by-step instructions for running the application
    - Step-by-step instructions for running the tests
    - Sample Postman request details for each endpoint (method, URL, and any query parameters)
    - A link to your screen recording (uploaded to YouTube, Google Drive, or similar)
    - An AI Disclosure section (if applicable)

**Screen Recording Requirements:**
Record a walkthrough of your completed project. The recording must show your **entire screen** for the duration of the video — do not crop, zoom in, or use a window-only capture. Your recording must demonstrate each of the following in order:

- Starting the application with `mvn spring-boot:run` and the terminal output that follows
- The startup log message confirming how many opportunities were seeded
- A Postman request to `GET /api/opportunities` and its JSON response
- A Postman request using the `type` query parameter filter and its filtered response
- A Postman request using the `q` query parameter filter and its filtered response
- A Postman request to `GET /api/opportunities/{id}` for a valid ID and its response
- A Postman request to `GET /api/opportunities/{id}` for an invalid ID showing a `404` response
- The per-request log messages visible in the terminal after each request
- Running `mvn test` and the terminal output confirming all tests pass

---

### Testing Your Endpoints with Postman

**Postman** is a graphical tool for sending HTTP requests to APIs and inspecting their responses. If you do not already have it installed, download it for free at [https://www.postman.com/downloads](https://www.postman.com/downloads).

Once your application is running on port `8080`, use the following requests to manually verify each endpoint before recording your walkthrough.

**Retrieve all opportunities**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`

**Filter by type**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `type` = `Scholarship`

**Search by keyword**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `q` = `STEM`

**Combine type and keyword filters**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `type` = `Internship`, `q` = `paid`

**Retrieve a single opportunity by ID**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities/opp-001`

**Verify 404 behavior for a non-existent ID**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities/opp-999`
- Expected response: `404 Not Found`

---

### Academic Integrity & AI Use

AI tools may be used for planning purposes only — for example, brainstorming opportunity titles, outlining your approach, or understanding a concept. All code must be written by you. Any use of AI tools must be disclosed in your README under a clearly labeled **"AI Disclosure"** section. Undisclosed AI-generated code is an academic integrity violation.