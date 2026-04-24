# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures (2025/26)  
**University:** University of Westminster  
**Stack:** Java 11 · JAX-RS (Jersey 2.41) · Apache Tomcat 9 · Maven

---

## API Overview

This RESTful API manages the Smart Campus infrastructure — Rooms and Sensors deployed across the university. It is built exclusively with JAX-RS (Jersey 2) and uses in-memory `ConcurrentHashMap` data structures as the persistence layer.

### Base URL

```
http://localhost:8080/sensor-api/api/v1
```

### Resource Hierarchy

```
/api/v1
├── /rooms
│   ├── GET     /           — List all rooms
│   ├── POST    /           — Create a new room
│   ├── GET     /{roomId}   — Get room details
│   └── DELETE  /{roomId}   — Delete a room (blocked if sensors assigned)
├── /sensors
│   ├── GET     /           — List all sensors (optional ?type= filter)
│   ├── POST    /           — Register a new sensor
│   ├── GET     /{sensorId} — Get sensor details
│   └── /{sensorId}/readings
│       ├── GET  /          — Get reading history for a sensor
│       └── POST /          — Add a new reading (updates sensor's currentValue)
└── /test                   — Health check endpoint
```

---

## How to Build & Run

### Prerequisites

- Java JDK 11 or later
- Apache Maven 3.6+
- Apache Tomcat 9.x
- Apache NetBeans IDE (recommended)

### Build

```bash
mvn clean package
```

The WAR file is created at `target/sensor-api.war`.

### Deploy via Apache NetBeans

1. Open NetBeans → **File → Open Project** → select this folder
2. Go to **Tools → Servers → Add Server → Apache Tomcat or TomEE**
3. Browse to your Tomcat 9 directory, set username/password, click Finish
4. Right-click project → **Properties → Run** → select Apache Tomcat 9
5. Press **F6** (Run) — NetBeans builds and deploys automatically

### Deploy manually to Tomcat

```bash
mvn clean package
cp target/sensor-api.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh   # or startup.bat on Windows
```

---

## Sample curl Commands

### 1. Discovery — GET /api/v1

```bash
curl -X GET http://localhost:8080/sensor-api/api/v1
```

### 2. Create a Room — POST /api/v1/rooms

```bash
curl -X POST http://localhost:8080/sensor-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

### 3. Register a Sensor — POST /api/v1/sensors

```bash
curl -X POST http://localhost:8080/sensor-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

### 4. Filter Sensors by Type — GET /api/v1/sensors?type=CO2

```bash
curl -X GET "http://localhost:8080/sensor-api/api/v1/sensors?type=CO2"
```

### 5. Post a Sensor Reading — POST /api/v1/sensors/{sensorId}/readings

```bash
curl -X POST http://localhost:8080/sensor-api/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":412.5}'
```

### 6. Get Reading History — GET /api/v1/sensors/{sensorId}/readings

```bash
curl -X GET http://localhost:8080/sensor-api/api/v1/sensors/CO2-001/readings
```

### 7. Delete a Room with Sensors (409 Conflict)

```bash
curl -X DELETE http://localhost:8080/sensor-api/api/v1/rooms/LIB-301
```

### 8. Delete an Empty Room (204 Success)

```bash
# First create a room with no sensors
curl -X POST http://localhost:8080/sensor-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"EMPTY-001","name":"Empty Room","capacity":10}'

curl -X DELETE http://localhost:8080/sensor-api/api/v1/rooms/EMPTY-001
```

### 9. Trigger 422 — Sensor with non-existent roomId

```bash
curl -X POST http://localhost:8080/sensor-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"CO2","status":"ACTIVE","roomId":"DOES-NOT-EXIST"}'
```

### 10. Trigger 403 — Post to a MAINTENANCE sensor

```bash
curl -X POST http://localhost:8080/sensor-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"MAINT-001","type":"Temperature","status":"MAINTENANCE","roomId":"LIB-301"}'

curl -X POST http://localhost:8080/sensor-api/api/v1/sensors/MAINT-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":22.1}'
```

---

## Conceptual Report — Answers to Coursework Questions

### Part 1: Service Architecture & Setup

**Question 1.1: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.**

**Answer:** JAX-RS creates an instance from related class for each incoming request. Once it send the respond to the client it goes to garbage collector because standard instance variables inside the resource class are reset with every request. This is how JAX-RS default runtime works. This design improves thread safety because each requests operates on it is own object instance. However it creates a trade off for In memory data structures. Since a new instance is created per request in memory data structures like hashmap , arralists are reinitialized every time. Data isn’t persisted across requests. So Synchronization should be managed explicitly , for that we can use Concurrent Hash map for this task. It ensures synchronized read and write operation for each simultaneous API calls.

---

**Question 1.2: Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**

**Answer:** HATEIS transforms a static API into a dynamic self describing system by combining navigation links directly within JSON responses. Instead of hardcoding URLs and replying on external documentation the client can navigate the application state by following these links.it acts as a form of live , self updating documentation. It eliminates the risk of outdated or mismatching API documentation and enabling client to adapt pragmatically to new features.

### Part 2: Room Management

**Question 2.1: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**

**Answer:** Returning Only IDs provides a highly efficient network payload. It is ideal for low bandwidth usage , improves response time Especially for large datasets or low bandwidth environment but client needs to request N+1 times to process. Client must make N additional separate requests to fetch details. Returning full room objects requires only single request. It simplifies client side processing and reducing the number of requests, but for large payload sizes it might slow the responses. Therefore returning IDs is good for lightweight communication but increases the client workload. Returning full objects optimizes client convenience and reduces unnecessary requests but may impact on performance due to heavier data transfer.

---

**Question 2.2: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**

**Answer:** Yes, the DELETE operation is strictly idempotent. Performing the same DELETE request multiple times results in the same final server state. On the first request that mentioned room will be deleted from the memory and update the state across the server then responds with a 204 No content. If the client mistakenly sends the exact same DELETE request multiple times the server simply returns 404 Not Found. So any additional DELETE requests on that room ID won’t have any effect on the state of Concurrent Hashmap.

### Part 3: Sensor Operations & Linking

**Question 3.1: We explicitly use the @Consumes (MediaType. APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**

**Answer:** if a client ignores @Consumes constraint and sends data in a format such as text/plain to an endpoint the JAX-RS runtime intercepts the requests before it reaches the controller method. Because jersey can’t map plain text to the expected data model using Jackson. It automatically abort the request and returns an HTTP 415 Unsupported Media Type error to the client, enforcing strict data contract boundaries.

---

**Question 3.2: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/C02). Why is the query parameter approach generally considered superior for filtering and searching collections?**

**Answer:** Query Parameters are designed specifically for searching because they allow the URL to remain focused on the primary resource collection (/sensors) while providing optional "modifiers" (?type=CO2) to the view. This makes it far easier to combine multiple search filters in the future without breaking the core URL structure. This reduces complexity and improves maintainability, while giving client developers a consistent and predictable way to perform searches without relying heavily on external documentation or custom conventions.

### Part 4: Deep Nesting with Sub-Resources

**Question 4.1: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**

**Answer:** The Sub-Resource Locator pattern prevents parent controllers from becoming unmaintainable. Instead of handling deeply nested paths in one file, the parent SensorResource acts as a router, extracting the ID and delegating the HTTP processing to a dedicated SensorReadingResource. This isolation improves code readability, enforces the Single Responsibility Principle, and makes unit testing much more efficient. This improves separation of concerns, as each resource class is responsible only for its own domain logic, leading to better maintainability and readability in large applications. It also enhances reusability and scalability. Sub resources can be developed, tested and extended independently without affecting the parent resource.

### Part 5: Advanced Error Handling, Exception Mapping & Logging

**Question 5.2 (Dependency Validation): Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**

**Answer:** A 404 Not Found indicates the requested URI routing path does not exist. If the client hits a valid POST endpoint with perfectly formatted JSON, returning a 404 is technically inaccurate. 422 Unprocessable Entity correctly signals that the server understood the request and the JSON syntax was valid but inside the given data there are some mismatches or violated business logic.

---

**Question 5.4 (The Global Safety Net): From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**

**Answer:** Exposing stack traces is a critical "Information Disclosure" vulnerability. An attacker can analyze the trace back to determine the specific frameworks the system uses, their library versions, internal server file paths, and class names that should be hidden from the users. This allows them to understand how the system is built internally and identify potential weaknesses. As an example, they can search for Common Vulnerabilities and Exposures for that tech stack which can lead them to target more attacks into the system. So stack trace should be hidden, and system should return generic error messages.

---

**Question 5.5 (API Request & Response Logging Filters): Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**

**Answer:** Logging is a cross-cutting concern that applies universally across the API. Manually inserting Logger.info() inside every method severely clutters business logic and violates the DRY principle. By utilizing ContainerRequestFilter and ContainerResponseFilter , the logging mechanism wraps around the application transparently, centralizing the logic in one file and ensuring complete observability without polluting individual controllers.
