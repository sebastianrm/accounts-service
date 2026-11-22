# AGENTS.md — accounts-service

> **🤖 FUENTE CANÓNICA PARA AGENTES DE IA — Lógica de negocio de accounts-service**
>
> Este `AGENTS.md` es el documento de referencia **definitivo para agentes de IA** sobre la lógica de negocio, las reglas, la arquitectura, el estilo de código y las instrucciones de desarrollo del microservicio `accounts-service`.
>
> Para **humanos**, consultar [`README.md`](./README.md) en la misma carpeta.
>
> Los archivos de la raíz del repositorio (`AGENTS.md`, `README.md`, `Proyecto General vf.md`) contienen reglas de oro de plataforma, índice del monorepo y requisitos generales compartidos — **no** la lógica de negocio específica de este servicio.
>
> **Toda la lógica de negocio de accounts-service vive aquí (para agentes) y en `README.md` (para humanos).**

---

## Mission

accounts-service manages passive bank products (savings, checking, fixed-term accounts) for the Yanki banking platform. It handles account CRUD, movements (deposits, withdrawals, transfers, debit payments), commissions, local CQRS views fed by Kafka events, and balance caching via Redis. The service is **event-driven** (Parte III): no REST calls between microservices, only Kafka topics.

**Core constraint:** Database-per-service (MongoDB `yanki_accounts`), reactive non-blocking (RxJava 3 + WebFlux), configuration externalized via Config Server.

---

## Setup commands

- **Install deps:** `mvn clean install -DskipTests`
- **Start dev server:** `mvn spring-boot:run` (port 8083)
- **Run tests:** `mvn test`
- **Full build with quality gate:** `mvn clean verify`
- **Checkstyle only:** `mvn checkstyle:check`
- **JaCoCo report:** `mvn jacoco:prepare-agent jacoco:report` → `target/site/jacoco/index.html`
- **Docker build:** `docker build -t yanki/accounts-service:1.0.0 .`

### Required infrastructure

MongoDB, Redis, Kafka, Eureka, Config Server must be running. Use `docker-compose.full.yml` from the monorepo root.

### Environment variables

```powershell
$env:SERVER_PORT=8083
$env:MONGODB_URI="mongodb://localhost:27017/yanki_accounts"
$env:REDIS_HOST=localhost
$env:REDIS_PORT=6379
$env:KAFKA_BOOTSTRAP_SERVERS=localhost:9092
$env:KAFKA_GROUP_ID=accounts-service
$env:EUREKA_URL=http://localhost:8761/eureka/
$env:CONFIG_SERVER_URL=http://localhost:8888
```

---

## Repository layout

```
accounts-service/
├── src/main/java/com/yanki/accounts/
│   ├── domain/                    # THE HEXAGON — pure business logic
│   │   ├── model/                 # Pure entities: Account, Movement, enums, views
│   │   ├── port/
│   │   │   ├── in/                # 6 input ports (use cases)
│   │   │   └── out/               # 8 output ports (persistence, cache, events, views)
│   │   ├── service/               # AccountService (RxJava 3) + AccountSchedulerService + commands + results + strategy
│   │   ├── event/                 # Kafka event payloads (Java records)
│   │   └── exception/             # BusinessException, ErrorCodes
│   ├── infrastructure/            # ADAPTERS — external technology
│   │   ├── config/                # OpenAPI, Redis, Resilience4j beans
│   │   ├── persistence/
│   │   │   ├── document/          # MongoDB documents with @Document/@Id
│   │   │   ├── mapper/            # Document ↔ Entity mappers
│   │   │   ├── adapter/           # MongoDB adapters implementing persistence ports
│   │   │   ├── repository/        # Spring Data reactive repositories
│   │   │   └── ProcessedEvent     # Kafka idempotency tracking
│   │   ├── cache/
│   │   │   └── adapter/           # RedisCacheAdapter implementing CachePort
│   │   └── messaging/
│   │       ├── adapter/           # KafkaEventAdapter implementing EventPublisherPort
│   │       ├── AccountEventPublisher # Outbound Kafka publisher (5 topics)
│   │       └── AccountEventConsumer # 8 inbound Kafka listeners with idempotency
│   └── interfaces/rest/           # DRIVING ADAPTERS — HTTP entry points
│       ├── controller/            # AccountController (11 endpoints)
│       ├── dto/                   # Request/Response DTOs
│       └── GlobalExceptionHandler.java
├── src/test/java/                 # 132 unit tests (domain + infrastructure + interfaces)
├── pom.xml                        # Maven: Spring Boot 3.3.5, RxJava 3, Kafka, Redis
├── Dockerfile                     # Multi-stage build
├── README.md                      # Human-facing docs, hexagonal audit report
└── AGENTS.md                      # This file
```

---

## Code style

- **Language:** English for class/method/package/URL names. Spanish for Javadoc.
- **Indentation:** 4 spaces, no tabs, max 160 chars/line.
- **Imports:** Explicit only, no wildcards, no unused imports.
- **Money:** `java.math.BigDecimal`, scale 2, `RoundingMode.HALF_UP`, default currency `PEN`.
- **Dates:** `java.time.LocalDateTime` / `LocalDate`. Never `java.util.Date`.
- **IDs:** `String` (UUID).
- **Lombok:** `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`.
- **Injection:** Constructor only (`@RequiredArgsConstructor`).
- **Collections:** Lambdas and Streams mandatory.
- **Documentation:** Every public class and method must have Javadoc.

---

## Testing

- **Framework:** JUnit 5 + Mockito + StepVerifier (RxJava).
- **Rule:** Mock repositories — never use real MongoDB or Kafka in unit tests.
- **Coverage target:** ≥ 80% on `domain.service` package.
- **Integration tests:** `@SpringBootTest` + `@AutoConfigureWebTestClient` for controllers.
- **Example pattern:**

```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock AccountRepository accountRepository;
    @InjectMocks AccountService accountService;

    @Test
    void shouldOpenSavingsAccountWhenValid() {
        // Given — mock repositories
        // When + Then — StepVerifier.create(accountService.openAccount(cmd).toFlowable())
        //     .expectNextMatches(...)
        //     .verifyComplete();
    }
}
```

---

## Architecture boundaries

### Hexagonal Architecture — Current Status: ✅ No Violations

The service fully complies with the hexagonal architecture standard defined in `../system-prompt-auditoria-hexagonal-yanki.md`. All previous violations have been refactored and resolved.

**What agents MUST enforce when modifying code:**

1. **Domain model must NOT contain infrastructure annotations.** No `@Document`, `@Id`, `@Indexed`, `@JsonProperty` in `domain/model/`. These belong in `infrastructure/persistence/document/`.
2. **Domain must NOT import Spring Data types.** Repositories in `domain/` should be pure interfaces with RxJava 3 return types (`Single`, `Maybe`, `Completable`, `Flowable`), not `ReactiveMongoRepository`.
3. **Domain services must depend on ports, not adapters.** Inject `AccountPersistencePort`, `CachePort`, `EventPublisherPort` — never `ReactiveMongoRepository`, `ReactiveStringRedisTemplate`, or `KafkaTemplate` directly.
4. **All dependencies point INWARD toward `domain`.** The domain layer never imports from `infrastructure` or `interfaces`.

**Current architecture status:**

| Verification | Result |
|-------------|--------|
| `domain/model` without `@Document`/`@Id` | ✅ Pure entities |
| `domain/service` without Spring Data/Kafka/Web imports | ✅ Zero violations |
| Output ports (`domain/port/out`) | ✅ 8 ports implemented |
| Adapters (`infrastructure/adapter`) | ✅ All implemented |
| Input ports (`domain/port/in`) | ✅ 6 use cases implemented |
| No `.block()`, `.blockingGet()`, `.blockingFirst()` | ✅ Non-blocking reactive |
| Kafka consumer idempotency | ✅ `processed_events` with unique key |

### Reactive model

- Use **RxJava 3** (`Single`, `Maybe`, `Completable`, `Flowable`) in domain services.
- Controllers convert between RxJava and Reactor (`Mono`/`Flux`) only at the boundary.
- **Never** call `.block()`, `.subscribe()`, or `Thread.sleep()` in domain code.

### Event-driven communication (Parte III)

- **No REST calls between microservices.** All inter-service communication is via Kafka.
- **Outbound topics (8):** `account.opened.v1`, `account.closed.v1`, `account.movement.registered.v1`, `debit.payment.settled.v1`, `transfer.settled.v1`, `account.maintenance.applied.v1`, `account.maintenance.failed.v1`, `vip.average.breached.v1`.
- **Inbound topics (8):** `customer.registered/updated/deleted.v1`, `card.opened/closed.v1`, `debit.payment.requested.v1`, `transfer.requested.v1`, `credit.overdue.v1`.
- **Idempotency:** All consumers use `processed_events` collection with key `<topic>:<entityId>:<occurredAt>`.

### Redis cache keys

| Key Pattern | Content | TTL | Invalidated On |
|---|---|---|---|
| `yanki:accounts:balance:{accountId}` | Current balance | 5m | Every movement |
| `yanki:accounts:movement-count:{accountId}:{YYYY-MM}` | Monthly movement counter | End of month | Every movement, cleanup job day 1 |
| `yanki:accounts:commission-config:{accountId}` | Commission params | 1h | Account update |

### Scheduled Jobs (Scheduler)

| Job | Cron | Description |
|---|---|---|
| `vip-daily-average-check` | `0 0 2 1 * ?` | Calculate VIP daily average; apply penalty if below minimum |
| `maintenance-commission` | `0 0 3 1 * ?` | Apply monthly maintenance commission to CHECKING STANDARD accounts |
| `cleanup-movement-counters` | `0 0 4 1 * ?` | Clear previous month movement counters from Redis |

### Commission Configuration (Config Server defaults)

| Parameter | SAVINGS STANDARD | SAVINGS VIP | CHECKING STANDARD | CHECKING PYME | TERM_FIXED |
|---|---|---|---|---|---|
| `maxFreeMovementsMonth` | 10 | 20 | -1 (unlimited) | -1 (unlimited) | 1 |
| `commissionPerExcessMovement` | 2.50 | 2.50 | N/A | N/A | 5.00 |
| `maintenanceCommission` | 0 | 0 | 15.00 | **0** | 0 |
| `vipMinDailyAverage` | N/A | 5000.00 | N/A | N/A | N/A |
| `vipPenaltyAmount` | N/A | 50.00 | N/A | N/A | N/A |
| `allowedMovementDay` | N/A | N/A | N/A | N/A | 15 (1-28) |

---

## Security

- JWT validated at API Gateway, not in this service.
- Authorization via header `X-User-Roles` (ADMIN, OPERATOR, ACCOUNT_HOLDER).
- Gateway propagates: `X-User-Id`, `X-User-Roles`, `X-Customer-Id`, `X-Request-Id`.
- Never log PAN data or credentials.

---

## Business rules (critical — validate in tests)

| Rule | Validation |
|------|------------|
| PERSONAL: max 1 savings, 1 checking | Count by customerId+type |
| ENTERPRISE: no savings, no fixed-term | Reject on type check |
| ENTERPRISE: multiple checking allowed | No limit |
| VIP requires active credit card | Check `customer_card_view.hasCreditCard = true` |
| PYME requires active credit card | Check `customer_card_view.hasCreditCard = true` |
| Overdue debt blocks all operations | Check `customer_risk_view.hasOverdueDebt = false` |
| Fixed-term: 1 movement on allowed day | Check `allowedMovementDay` + count today |
| Savings: free movement limit per month | Redis counter `yanki:accounts:movement-count:{accountId}:{YYYY-MM}` |
| Checking: unlimited, no excess commission | `maxFreeMovementsMonth = -1` |
| Account close: balance must be 0 | Reject if balance ≠ 0 |
| VIP daily average check | Job: if AVG(balanceAfter) < vipMinDailyAverage → penalty commission |
| PYME: zero maintenance commission | `maintenanceCommission = 0` for subType=PYME |

---

## Tracing and observability

- Headers propagated: `traceparent`, `tracestate` (W3C TraceContext).
- Actuator endpoints: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`.
- Structured logging with traceId, spanId, service name.

---

## References

- `README.md` — Full API docs, hexagonal audit, business rules, data model.
- `../AGENTS.md` — Root-level traceability golden rule.
- `../CONVENTIONS.md` — Global technical contract for all microservices.
- `../docs/CASOS_USO` — Main use case with business rules.
- `../system-prompt-auditoria-hexagonal-yanki.md` — Hexagonal architecture standard.
- `../docs/CASOS_PRUEBA` — Traceability matrix (CU ↔ SEC ↔ CP).
