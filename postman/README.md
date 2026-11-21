# Colección Postman — accounts-service

**Microservicio:** accounts-service (puerto 8083)  
**Última actualización:** 2026-11-14  
**Versión de la API:** 1.0

## Descripción

Colección completa de pruebas REST para el microservicio **accounts-service** de la plataforma bancaria Yanki. Cubre los ~50 casos de prueba documentados en `../test-cases/` y validados en los tests JUnit (`../src/test/java/`).

## Archivos

| Archivo | Descripción |
|---------|-------------|
| `Yanki-Accounts-Service.postman_collection.json` | Colección Postman v2.1 con 37 requests organizadas en 8 folders |

## Estructura de la Colección

### Folders por Caso de Uso

| Folder | CU | Requests | Casos de Prueba |
|--------|----|----------|-----------------|
| **CU-02: Gestión de Cuentas** | CU-02 | 13 | CP-020 a CP-036 (apertura, cierre, filtros) |
| **CU-05: Operaciones (Depósitos/Retiros)** | CU-05 | 6 | CP-037 a CP-043 (depósitos, retiros, comisiones) |
| **CU-08: Consulta de Saldos** | CU-08 | 3 | CP-049 a CP-053 (saldo con Redis cache) |
| **CU-09: Consulta de Movimientos** | CU-09 | 2 | CP-054 a CP-055 (movimientos paginados) |
| **CU-12: Transferencias (Event-Driven)** | CU-12 | 2 | CP-067 a CP-068 (transferencias como liquidador) |
| **CU-14: Perfiles VIP/PYME** | CU-14 | 3 | CP-131 a CP-133 (validación VIP/PYME) |
| **CU-15: Comisiones (Jobs + Utilidades)** | CU-15 | 4 | CP-139 a CP-142 (jobs admin, movimiento count) |
| **Errores y Validaciones** | — | 3 | 400, 404, validaciones DTO |

### Resumen

- **Total de requests:** 37
- **Total de folders:** 8
- **Happy Path:** ~20 requests
- **Error / Negativo:** ~17 requests
- **Respuestas documentadas:** 1 por request (con body de ejemplo)

## Variables de Colección

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `baseUrl` | `http://localhost:8083` | URL base del accounts-service |
| `accountId` | `ACC001` | ID de cuenta de ahorro para pruebas |
| `customerId` | `CUST001` | ID de cliente PERSONAL STANDARD |
| `enterpriseCustomerId` | `ENT001` | ID de cliente ENTERPRISE |
| `vipCustomerId` | `CUST002` | ID de cliente PERSONAL VIP con tarjeta crédito |
| `jwtToken` | (vacío) | Token JWT de autenticación |

## Cómo Usar

### 1. Importar la Colección

1. Abrir Postman
2. Click en **Import** → **File** → Seleccionar `Yanki-Accounts-Service.postman_collection.json`
3. La colección se importará con todas las variables y requests configuradas

### 2. Configurar el Entorno

1. Actualizar la variable `baseUrl` si el servicio corre en otro puerto/host
2. Si usa autenticación JWT, obtener un token del auth-service y establecerlo en `jwtToken`
3. En desarrollo local sin auth, dejar `jwtToken` vacío (el pre-request script deshabilita auth automáticamente)

### 3. Ejecutar las Pruebas

**Orden recomendado:**

1. **CU-02:** CP-020 (crear cuenta ahorro) → CP-033 (consultar por ID) → CP-034 (listado)
2. **CU-05:** CP-037 (depósito) → CP-040 (retiro) → CP-049 (consultar saldo)
3. **CU-09:** CP-054 (consultar movimientos)
4. **CU-15:** CP-142 (contador de movimientos)
5. **CU-02:** CP-035 (cerrar cuenta con balance=0)

**Para pruebas de error:**
- Ejecutar CP-020 primero, luego CP-022 (duplicado)
- Ejecutar CP-036 (cierre con balance > 0) antes de vaciar el saldo

### 4. Colección Runner

Para ejecutar todas las requests automáticamente:
1. Click derecho en la colección → **Run collection**
2. Configurar delays entre requests si es necesario
3. Ejecutar

## Endpoints Cubiertos

| Método | Endpoint | Folder | Requests |
|--------|----------|--------|----------|
| POST | `/api/accounts` | CU-02, CU-14 | 10 |
| GET | `/api/accounts` | CU-02 | 1 |
| GET | `/api/accounts/{id}` | CU-02, Errores | 2 |
| GET | `/api/accounts/{id}/balance` | CU-08 | 3 |
| GET | `/api/accounts/{id}/movements` | CU-09, CU-12 | 4 |
| POST | `/api/accounts/{id}/deposits` | CU-05, Errores | 3 |
| POST | `/api/accounts/{id}/withdrawals` | CU-05 | 3 |
| DELETE | `/api/accounts/{id}` | CU-02 | 2 |
| POST | `/api/accounts/jobs/maintenance-commission` | CU-15 | 2 |
| POST | `/api/accounts/jobs/vip-average-check` | CU-15 | 1 |
| GET | `/api/accounts/{id}/movement-count` | CU-15 | 1 |

## Casos de Prueba Referenciados

Cada request incluye en su descripción:
- **Caso de Prueba:** CP-XXX
- **CU:** Caso de Uso relacionado
- **SEC:** Diagrama de secuencia relacionado
- **Tipo:** Happy Path / Error / Regla de negocio
- **Precondiciones:** Estado requerido antes de ejecutar
- **Pasos:** Instrucciones detalladas
- **Respuesta Esperada:** HTTP status y estructura del body
- **Postcondiciones:** Estado resultante (cuando aplica)

## Notas Importantes

### Autenticación
La autenticación JWT es delegada al API Gateway. En desarrollo local, las requests funcionan sin token. En producción, configure `jwtToken` con un token válido.

### Transferencias (CU-12)
Las transferencias son event-driven: accounts-service consume `transfer.requested.v1` de Kafka. Las requests de CU-12 validan los movimientos resultantes consultando los movimientos de las cuentas, no inician transferencias directamente.

### Comisiones por Exceso
Para probar CP-038/CP-041 (comisión por exceso), primero ejecute 10 operaciones (CP-037/CP-040) para alcanzar el límite `maxFreeMovementsMonth=10`.

### Jobs Admin
Los jobs de mantenimiento (CP-139, CP-140) requieren cuentas corriente con saldo suficiente. Crear una cuenta corriente (CP-025) antes de ejecutar estos jobs.

## Referencias

| Archivo | Descripción |
|---------|-------------|
| `../test-cases/` | Casos de prueba detallados por CU |
| `../src/test/java/` | Tests JUnit con mocks |
| `../README.md` | Documentación del servicio |
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | Fuente original de los CPs |
| `../../CASOS_PRUEBA/RTM.md` | Matriz de trazabilidad |
| `../../DIAGRAMAS_SECUENCIAS/` | Diagramas de secuencia |

---

**Autor:** Sebastián Retamal  
**Contacto:** semorale@emeal.nttdata.com
