# The Yard API

**The Yard API** is a Spring Boot REST API that helps HBCU students discover campus organizations, scholarship opportunities, professional development programs, and community events — all in one place.

---

## Prerequisites

- **JDK 17 or higher** — verify with `java -version`
- **Maven 3.8+** — verify with `mvn -version`
- **Postman** (optional, for manual testing) — download at https://www.postman.com/downloads

---

## Running the Application

```bash
# Clone the repository
git clone <your-repo-url>
cd yard

# Start the application
mvn spring-boot:run
```

The API will start on **http://localhost:8080**. You should see a startup log message like:

```
INFO  InMemoryOpportunityStore - Seeded 10 opportunities into the in-memory store.
```

---

## Running the Tests

```bash
mvn test
```

All tests use MockMvc and run without starting a real server. You should see output confirming all tests pass.

---

## Building a JAR

```bash
mvn clean package
java -jar target/yard-0.0.1-SNAPSHOT.jar
```

---

## API Endpoints

### GET /api/opportunities

Returns all opportunities. Supports optional query parameters for filtering.

| Parameter | Description | Example |
|-----------|-------------|---------|
| `type`    | Filter by opportunity type (case-insensitive) | `?type=Scholarship` |
| `q`       | Keyword search across title and tags (case-insensitive) | `?q=STEM` |

**Sample Postman Requests:**

**All opportunities:**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`

**Filter by type:**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `type = Scholarship`

**Keyword search:**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `q = STEM`

**Combine filters:**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities`
- Query Params: `type = Internship`, `q = paid`

---

### GET /api/opportunities/{id}

Returns a single opportunity by ID.

- Returns `200 OK` with the opportunity if found.
- Returns `404 Not Found` if the ID does not exist.

**Sample Postman Requests:**

**Valid ID:**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities/opp-001`

**Invalid ID (404):**
- Method: `GET`
- URL: `http://localhost:8080/api/opportunities/opp-999`

---

### POST /api/opportunities

Creates a new opportunity listing. The server generates the ID automatically.

- Returns `201 Created` with the new record (including generated ID) and a `Location` header.
- Returns `400 Bad Request` with field-level error details if any required field is missing or invalid.

**Sample Postman Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/opportunities`
- Header: `Content-Type: application/json`
- Body (raw JSON):

```json
{
  "title": "Howard University Research Fellowship",
  "type": "Fellowship",
  "sponsor": "Howard University",
  "deadline": "2025-06-30",
  "description": "Summer research fellowship for undergraduate students at HBCUs. Includes a stipend and mentorship from faculty researchers.",
  "tags": ["research", "paid", "summer", "undergrad"],
  "url": "https://howard.edu/research-fellowship"
}
```

**400 example (missing title):**

```json
{
  "type": "Scholarship",
  "sponsor": "Test Sponsor",
  "deadline": "2025-12-01",
  "description": "A test scholarship.",
  "tags": ["STEM"],
  "url": "https://example.com"
}
```

---

### PUT /api/opportunities/{id}

Fully replaces an existing opportunity. All fields are required.

- Returns `200 OK` with the updated record if the ID exists.
- Returns `404 Not Found` if the ID does not exist.
- Returns `400 Bad Request` with field-level errors if validation fails.

**Sample Postman Request:**
- Method: `PUT`
- URL: `http://localhost:8080/api/opportunities/opp-001`
- Header: `Content-Type: application/json`
- Body (raw JSON):

```json
{
  "title": "UNCF STEM Scholars Program — Updated",
  "type": "Scholarship",
  "sponsor": "UNCF",
  "deadline": "2025-05-01",
  "description": "Updated description for the UNCF STEM Scholars Program with new award amounts.",
  "tags": ["STEM", "undergrad", "paid", "updated"],
  "url": "https://uncf.org/programs/uncf-stem-scholars-program"
}
```

---

### DELETE /api/opportunities/{id}

Permanently removes an opportunity listing.

- Returns `204 No Content` if the record was found and deleted.
- Returns `404 Not Found` if the ID does not exist.

**Sample Postman Requests:**

**Valid ID:**
- Method: `DELETE`
- URL: `http://localhost:8080/api/opportunities/opp-001`

**Invalid ID (404):**
- Method: `DELETE`
- URL: `http://localhost:8080/api/opportunities/opp-999`

---

## Sample Filtering Request

Demonstrates combined type + keyword filtering:

```
GET http://localhost:8080/api/opportunities?type=Scholarship&q=STEM
```

---

## Project Structure

```
src/main/java/edu/famu/cop3060/yard/
├── YardApplication.java
├── controller/
│   ├── OpportunitiesController.java
│   └── ValidationExceptionHandler.java
├── service/
│   └── OpportunitiesService.java
├── store/
│   └── InMemoryOpportunityStore.java
└── dto/
    ├── OpportunityDTO.java
    ├── CreateOpportunityDTO.java
    └── UpdateOpportunityDTO.java

src/test/java/edu/famu/cop3060/yard/
└── controller/
    └── OpportunitiesControllerTest.java
```

---

## AI Disclosure

This project was developed with the assistance of Claude (claude.ai), an AI assistant by Anthropic, as permitted by the course instructor. Claude was used to generate the Java source files, test class, and this README based on the assignment specification. All AI usage has been disclosed per course requirements.
