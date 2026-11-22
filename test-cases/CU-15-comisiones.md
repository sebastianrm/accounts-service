# CU-15: Comisiones (Jobs + Tiempo Real) — Casos de Prueba

**Microservicio:** accounts-service  
**CU:** CU-15 | **SEC:** SEC013_comisiones.md, SEC005_deposito_cuenta.md, SEC006_retiro_cuenta.md  
**Fuente:** `../../docs/CASOS_PRUEBA`

---

## Happy Path (Casos de Éxito)

### CP-138 — Comisión exceso tiempo real: depósito/retiro/transferencia
- **CU:** CU-15, CU-05, CU-12 | **SEC:** SEC013_comisiones.md, SEC005_deposito_cuenta.md, SEC006_retiro_cuenta.md, SEC010_transferencias.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta ahorro STANDARD, movimientos mes actual ≥ maxFree (10).
- **Pasos:**
  1. Realizar depósito/retiro/transferencia cuando contador Redis ≥ maxFree.
  2. Validar `commissionApplied` = `commissionPerExcessMovement`.
  3. Verificar contador Redis `yanki:accounts:movement-count:{accountId}:{YYYY-MM}` INCR.
  4. Verificar `commissionType:EXCESS_MOVEMENT` en movimiento registrado.
- **Postcondiciones:** Comisión aplicada en tiempo real.

### CP-139 — Job mensual comisión mantenimiento: cuenta corriente STANDARD éxito
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Happy Path (Job día 1, 03:00)
- **Precondiciones:** Cuentas CHECKING STANDARD OPEN, `maintenanceCommission:15.00`, balance ≥ 15.
- **Pasos:**
  1. Trigger job `maintenance-commission`.
  2. Verificar movimiento `COMMISSION` type=MAINTENANCE, amount=15, `channel:SCHEDULER`.
  3. Verificar evento `account.maintenance.applied.v1`.
  4. Verificar Redis balance invalidado.
- **Postcondiciones:** Comisión mantenimiento debitada.

### CP-141 — Job mensual comisión mantenimiento: cuenta PYME (commission=0) → sin acción
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta CHECKING PYME (`maintenanceCommission:0`).
- **Pasos:** Trigger job → NO se procesa cuenta PYME (filtro query: maintenanceCommission > 0).

### CP-142 — Job limpieza contadores movimientos mes anterior (día 1, 04:00)
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Happy Path
- **Precondiciones:** Contadores Redis `yanki:accounts:movement-count:*:{YYYY-MM-1}` existen.
- **Pasos:** Trigger job `cleanup-movement-counters` → keys eliminadas.

### CP-143 — Configuración comisiones externalizada Config Server → BD cuenta → aplicación real
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Integración
- **Precondiciones:** Config Server con defaults por tipo/subtipo.
- **Pasos:**
  1. Verificar cuenta creada hereda parámetros de Config Server.
  2. `PUT /api/accounts/{id}/commission-config` actualiza parámetros en BD.
  3. Verificar aplicación en siguiente operación.

### CP-144 — Auditoría movimiento: commissionApplied > 0, commissionType correcto, channel=SCHEDULER para jobs
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Validación datos
- **Precondiciones:** Movimiento con comisión generado.
- **Validación:** Campo `commissionApplied` = amount para COMMISSION, `commissionType` ∈ {EXCESS_MOVEMENT, MAINTENANCE, VIP_AVERAGE_PENALTY}, `channel` = SCHEDULER para jobs, MOBILE/ATM/WEB para usuario.

---

## Error / Casos Negativos

### CP-140 — Job mensual comisión mantenimiento: saldo insuficiente → REJECTED
- **CU:** CU-15 | **SEC:** SEC013_comisiones.md
- **Tipo:** Error (Caso borde job)
- **Precondiciones:** Cuenta corriente STANDARD, balance < maintenanceCommission.
- **Pasos:** Trigger job.
- **Validación:** Movimiento `COMMISSION` status=REJECTED, evento `account.maintenance.failed.v1` reason=INSUFFICIENT_BALANCE.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../docs/CASOS_PRUEBA` | CU-15 | Casos de prueba originales CP-138 a CP-144 |
| `../../docs/CASOS_PRUEBA` | Fila CU-15 | Trazabilidad CU-15 |
| `../../docs/CASOS_PRUEBA` | Sección 2 | accounts-service como owner de comisiones |
| `../../DIAGRAMAS_SECUENCIAS/SEC013_comisiones.md` | Diagrama completo | Comisiones y jobs |
