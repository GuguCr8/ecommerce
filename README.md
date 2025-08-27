## Pricing Service (Hexagonal Architecture · Java 17 · Spring Boot 3.5.4)

Returns the applicable (unique) price for a given product and brand at a specific date.
**Stack:** In-memory H2 · Spring Data JPA · Springdoc OpenAPI (Swagger).

## Requeriments
- Java 17
- Maven 3.9+

## Execute maven run
mvn spring-boot:run
Swagger UI: http://localhost:8080/swagger-ui/index.html

## H2 Console: http://localhost:8080/h2-console
JDBC: jdbc:h2:mem:ecommerce
User: sa
Password:

## API-First
The contract was defined before development (API-First) to avoid frontend–backend blocking.
Frontend and backend are built against the same contract from day one.

Endpoint
GET /api/v1/prices

Query Parameters:

Name	    Type	    Required	 Example	           Notes
brandId	    integer 	true	     1	                   Brand ID (1 = ZARA)
productId	integer 	true	     35455	               Product ID
date	    string      true	     2020-06-14T16:00:00No timezone (uses LocalDateTime)

200 OK — Example Response
json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}

Error Codes

400 → Invalid or missing parameters

404 → No applicable price found for given brand/product/date

500 → Unexpected error

## Sample Data (H2)
Loaded on startup from `schema.sql` + `data.sql`:

| BRAND_ID | START_DATE           | END_DATE             | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
|---:|---|---|---:|---:|---:|---:|:---:|
| 1 | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1 | 35455 | 0 | 35.50 | EUR |
| 1 | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2 | 35455 | 1 | 25.45 | EUR |
| 1 | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3 | 35455 | 1 | 30.50 | EUR |
| 1 | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4 | 35455 | 1 | 38.95 | EUR |

---

## Test Cases (Requests & Expected Results)

| Case | Request | priceList | price | startDate | endDate |
|:---:|---|---:|---:|---|---|
| T1 | `/api/v1/prices?brandId=1&productId=35455&date=2020-06-14T10:00:00` | 1 | 35.50 | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 |
| T2 | `/api/v1/prices?brandId=1&productId=35455&date=2020-06-14T16:00:00` | 2 | 25.45 | 2020-06-14T15:00:00 | 2020-06-14T18:30:00 |
| T3 | `/api/v1/prices?brandId=1&productId=35455&date=2020-06-14T21:00:00` | 1 | 35.50 | 2020-06-14T00:00:00 | 2020-12-31T23:59:59 |
| T4 | `/api/v1/prices?brandId=1&productId=35455&date=2020-06-15T10:00:00` | 3 | 30.50 | 2020-06-15T00:00:00 | 2020-06-15T11:00:00 |
| T5 | `/api/v1/prices?brandId=1&productId=35455&date=2020-06-16T21:00:00` | 4 | 38.95 | 2020-06-15T16:00:00 | 2020-12-31T23:59:59 |

**Error Examples**

| Status | Request | Notes |
|:---:|---|---|
| 400 | `/api/v1/prices?brandId=0&productId=35455&date=2020-06-14T10:00:00` | invalid `brandId` |
| 400 | `/api/v1/prices?brandId=1&productId=35455` | missing `date` |
| 404 | `/api/v1/prices?brandId=1&productId=35455&date=1999-01-01T00:00:00` | no applicable price |

---

## Benchmark (with vs without indexes)
*How to measure:* Postman → **Time** / **Timing** tab

| Case | Request ISO | Avg w/o indexes (ms) | Avg with indexes (ms) | Improvement |
|:---:|---|---:|---:|---|
| T1 | `2020-06-14T10:00:00` | 17 | 8  | 9 ms (52.9%) |
| T2 | `2020-06-14T16:00:00` | 22 | 9  | 13 ms (59.1%) |
| T3 | `2020-06-14T21:00:00` | 14 | 7  | 7 ms (50.0%) |
| T4 | `2020-06-15T10:00:00` | 19 | 5  | 14 ms (73.7%) |
| T5 | `2020-06-16T21:00:00` | 33 | 10 | 23 ms (69.7%) |

## Unit test and unit integracion in controller
Unit (Service · JUnit 5 + Mockito): Verifies the logic of PriceServiceImpl and the Prices → PriceResponse mapping, 
using Given–When–Then (AAA) and JPA repository mocks. It also checks that the repository is called with the correct parameters.

Integration (Endpoint · SpringBootTest + MockMvc + H2): Runs the real flow controller → service → repository → JPA → H2,
with data loaded from schema.sql + data.sql. Validates the 5 specified cases (T1–T5) and 400/404 error scenarios.

## Next Versions API
@RestControllerAdvice with Problem Details (RFC 7807) for consistent error responses and client-specific formats (mobile/web).

DTO projections / MapStruct for future DTO growth.

Repository (Optimized data access): Uses JPQL with Pageable to sort by priority and fetch a single result, avoiding duplicate date parameters and improving readability without sacrificing performance.