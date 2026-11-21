# CU-08: Consulta de Saldos — Casos de Prueba

**Microservicio:** accounts-service  
**CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md  
**Fuente:** `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md`

---

## Happy Path (Casos de Éxito)

### CP-049 — Consulta de saldo exitosa (Redis Cache HIT)
- **CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta existe, Redis `yanki:accounts:balance:{accountId}` poblado (TTL 5m).
- **Pasos:**
  1. Enviar `GET /api/accounts/{id}/balance` con JWT válido.
  2. Validar `200 OK` con `{accountId, balance, currency, lastUpdated}`.
  3. Verificar respuesta servida desde Redis (cache HIT).
- **Postcondiciones:** Sin cambios.

### CP-050 — Consulta de saldo (Redis Cache MISS → MongoDB)
- **CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta existe, Redis cache expirado/vacío.
- **Pasos:**
  1. `GET /api/accounts/{id}/balance`.
  2. Validar `200 OK` con balance desde MongoDB.
  3. Verificar Redis poblado con TTL 5m.

---

## Error / Casos Negativos

### CP-051 — Consulta de saldo rechazada: token inválido/expirado
- **CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md
- **Tipo:** Error / Seguridad
- **Precondiciones:** JWT inválido, expirado o sin token.
- **Pasos:** Invocar endpoint sin Authorization o con token inválido.
- **Errores esperados:** HTTP `401` (Gateway) o `403`.

### CP-052 — Consulta de saldo rechazada: cuenta no autorizada (otro cliente)
- **CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md
- **Tipo:** Error / Autorización
- **Precondiciones:** JWT válido de cliente A, intenta consultar cuenta de cliente B.
- **Pasos:** `GET /api/accounts/{id}/balance` donde account.customerId ≠ token.customerId.
- **Errores esperados:** HTTP `403` o `404` (según implementación: no encontrado vs no autorizado).

### CP-053 — Consulta de saldo: cuenta no existe
- **CU:** CU-08 | **SEC:** SEC008_consulta_saldo.md
- **Tipo:** Error
- **Precondiciones:** accountId inexistente.
- **Pasos:** `GET /api/accounts/{id}/balance`.
- **Errores esperados:** HTTP `404`.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CU-08 | Casos de prueba originales CP-049 a CP-053 |
| `../../CASOS_PRUEBA/RTM.md` | Fila CU-08 | Trazabilidad CU-08 |
| `../../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | Sección 2 | accounts-service detalle |
| `../../DIAGRAMAS_SECUENCIAS/SEC008_consulta_saldo.md` | Diagrama completo | Consulta de saldo |
