# CU-09: Consulta de Movimientos — Casos de Prueba

**Microservicio:** accounts-service  
**CU:** CU-09 | **SEC:** SEC009_consulta_movimientos.md  
**Fuente:** `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md`

---

## Happy Path (Casos de Éxito)

### CP-054 — Consulta de movimientos exitosa (paginación + filtros fecha)
- **CU:** CU-09 | **SEC:** SEC009_consulta_movimientos.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta existe con movimientos, usuario autenticado dueño de la cuenta.
- **Pasos:**
  1. `GET /api/accounts/{id}/movements?page=0&size=20&dateFrom=2026-11-01&dateTo=2026-11-14&type=DEPOSIT`.
  2. Validar `200 OK` con `PageResponse<MovementResponse>` (content, paginación, filtros aplicados).
  3. Verificar orden: `occurredAt` descendente (más reciente primero).
- **Postcondiciones:** Sin cambios.

---

## Error / Casos Negativos

### CP-055 — Consulta de movimientos: cuenta inexistente
- **CU:** CU-09 | **SEC:** SEC009_consulta_movimientos.md
- **Tipo:** Error
- **Precondiciones:** accountId no existe.
- **Pasos:** `GET /api/accounts/{id}/movements`.
- **Errores esperados:** HTTP `404`.

### CP-056 — Consulta de movimientos: sin permisos (cuenta de otro cliente)
- **CU:** CU-09 | **SEC:** SEC009_consulta_movimientos.md
- **Tipo:** Error / Autorización
- **Precondiciones:** JWT válido, cuenta pertenece a otro cliente.
- **Pasos:** Consultar movimientos.
- **Errores esperados:** HTTP `403` o `404`.

### CP-057 — Consulta de movimientos: filtros inválidos (fechaFrom > fechaTo)
- **CU:** CU-09 | **SEC:** SEC009_consulta_movimientos.md
- **Tipo:** Error / Validación
- **Precondiciones:** Usuario autenticado.
- **Pasos:** `GET ...?dateFrom=2026-11-15&dateTo=2026-11-01`.
- **Errores esperados:** HTTP `400` o `422`, código `INVALID_DATE_RANGE`.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CU-09 | Casos de prueba originales CP-054 a CP-057 |
| `../../CASOS_PRUEBA/RTM.md` | Fila CU-09 | Trazabilidad CU-09 |
| `../../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | Sección 2 | accounts-service detalle |
| `../../DIAGRAMAS_SECUENCIAS/SEC009_consulta_movimientos.md` | Diagrama completo | Consulta de movimientos |
