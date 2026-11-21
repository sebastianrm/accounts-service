# Yanki Accounts Service — Contrato OpenAPI 3.0

## Descripción General

Este directorio contiene el contrato OpenAPI 3.0 completo del **Yanki Accounts Service**, el microservicio responsable de la gestión de cuentas bancarias dentro del ecosistema Yanki Platform.

### Archivos

| Archivo | Descripción |
|---------|-------------|
| [`accounts-service.yml`](accounts-service.yml) | Especificación OpenAPI 3.0.3 completa con 11 endpoints, 14 schemas y ejemplos detallados |

## Endpoints (11)

### CU-02: Gestión de Cuentas

| Método | Endpoint | Descripción | HTTP Status |
|--------|----------|-------------|-------------|
| `POST` | `/api/accounts` | Abrir cuenta bancaria | 201, 400, 409 |
| `GET` | `/api/accounts` | Listar cuentas con filtros | 200 |
| `GET` | `/api/accounts/{id}` | Obtener cuenta por ID | 200, 404 |
| `DELETE` | `/api/accounts/{id}` | Cerrar cuenta (saldo = 0) | 204, 404, 409 |

### CU-05: Operaciones

| Método | Endpoint | Descripción | HTTP Status |
|--------|----------|-------------|-------------|
| `POST` | `/api/accounts/{id}/deposits` | Depósito | 201, 404, 409 |
| `POST` | `/api/accounts/{id}/withdrawals` | Retiro | 201, 404, 409 |

### CU-08: Consulta de Saldos

| Método | Endpoint | Descripción | HTTP Status |
|--------|----------|-------------|-------------|
| `GET` | `/api/accounts/{id}/balance` | Consultar saldo | 200, 404 |

### CU-09: Consulta de Movimientos

| Método | Endpoint | Descripción | HTTP Status |
|--------|----------|-------------|-------------|
| `GET` | `/api/accounts/{id}/movements` | Listar movimientos | 200, 404 |

### CU-15: Comisiones y Admin

| Método | Endpoint | Descripción | HTTP Status |
|--------|----------|-------------|-------------|
| `POST` | `/api/accounts/jobs/maintenance-commission` | Job comisiones mantenimiento | 204 |
| `POST` | `/api/accounts/jobs/vip-average-check` | Job verificación VIP | 204 |
| `GET` | `/api/accounts/{id}/movement-count` | Contador de movimientos | 200, 404 |

## Schemas (14)

| Schema | Descripción |
|--------|-------------|
| `AccountRequest` | Solicitud para abrir cuenta (con `AccountHolderRequest`, `AccountSignerRequest`) |
| `AccountResponse` | Respuesta con datos completos de cuenta (con `AccountHolderResponse`, `AccountSignerResponse`) |
| `DepositRequest` | Solicitud de depósito |
| `WithdrawalRequest` | Solicitud de retiro |
| `MovementResponse` | Detalle de un movimiento bancario |
| `BalanceResponse` | Saldo disponible con indicador de caché |
| `MovementCountResponse` | Contador de movimientos del mes |
| `PageResponseOfAccountResponse` | Respuesta paginada de cuentas |
| `PageResponseOfMovementResponse` | Respuesta paginada de movimientos |
| `ApiError` | Estructura unificada de error |

## Códigos de Error de Negocio

| Código | HTTP Status | Descripción |
|--------|-------------|-------------|
| `ACCOUNT_NOT_FOUND` | 404 | Cuenta no encontrada |
| `ACCOUNT_ALREADY_CLOSED` | 409 | Cuenta ya cerrada |
| `ACCOUNT_BLOCKED` | 409 | Cuenta bloqueada |
| `ACCOUNT_INSUFFICIENT_BALANCE` | 409 | Saldo insuficiente |
| `ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO` | 409 | Saldo ≠ 0 al cerrar |
| `SAVINGS_ACCOUNT_ALREADY_EXISTS` | 409 | Ya existe cuenta de ahorro |
| `CURRENT_ACCOUNT_PERSONAL_LIMIT` | 409 | Límite de corriente para PERSONAL |
| `TERM_FIXED_ACCOUNT_ALREADY_EXISTS` | 409 | Ya existe cuenta plazo fijo |
| `ENTERPRISE_CANNOT_HAVE_SAVINGS` | 409 | Enterprise no puede tener SAVINGS |
| `ENTERPRISE_CANNOT_HAVE_TERM_FIXED` | 409 | Enterprise no puede tener TERM_FIXED |
| `CREDIT_CARD_REQUIRED_FOR_VIP` | 409 | VIP requiere tarjeta crédito |
| `CREDIT_CARD_REQUIRED_FOR_PYME` | 409 | PYME requiere tarjeta crédito |
| `CUSTOMER_HAS_OVERDUE_DEBT` | 409 | Cliente con deuda vencida |
| `CUSTOMER_NOT_FOUND` | 404 | Cliente no encontrado |
| `CUSTOMER_INACTIVE` | 409 | Cliente inactivo |
| `MOVEMENT_DAY_NOT_ALLOWED` | 409 | Día no permitido (plazo fijo) |
| `MOVEMENT_ALREADY_DONE_TODAY` | 409 | Movimiento ya realizado hoy |
| `EXCEEDED_FREE_MOVEMENTS` | 409 | Exceso de movimientos gratuitos |
| `INVALID_MOVEMENT_AMOUNT` | 409 | Monto de movimiento inválido |
| `VALIDATION_ERROR` | 400 | Error de validación de entrada |

## Uso

### Visualización con Swagger UI

Al iniciar el servicio, la documentación interactiva está disponible en:

```
http://localhost:8081/swagger-ui.html
```

### Importar en Postman

1. Abra Postman
2. Seleccione **Import** → **Upload Files**
3. Seleccione `accounts-service.yml`
4. La colección se importará con todos los endpoints y ejemplos

### Validación del esquema

```bash
# Con openapi-cli
npx @apidevtools/swagger-cli validate accounts-service.yml

# Con Redocly
npx @redocly/cli lint accounts-service.yml
```

## Referencias

| Recurso | Ubicación |
|---------|-----------|
| Código fuente del controller | [`AccountController.java`](../src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java) |
| DTOs | [`interfaces/rest/dto/`](../src/main/java/com/yanki/accounts/interfaces/rest/dto/) |
| Códigos de error | [`ErrorCodes.java`](../src/main/java/com/yanki/accounts/domain/exception/ErrorCodes.java) |
| Manejador de excepciones | [`GlobalExceptionHandler.java`](../src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java) |
| Colección Postman | [`postman/`](../postman/) |
| Casos de prueba | [`test-cases/`](../test-cases/) |
| Tests JUnit | [`src/test/java/`](../src/test/java/) |

## Versiones

| Versión | Fecha | Descripción |
|---------|-------|-------------|
| 1.0.0 | 2026-11-15 | Contrato OpenAPI inicial basado en código y pruebas existentes |
