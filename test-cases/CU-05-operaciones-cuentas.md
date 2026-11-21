# CU-05: Operaciones de Cuentas (Depósitos/Retiros) — Casos de Prueba

**Microservicio:** accounts-service  
**CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md, SEC006_retiro_cuenta.md  
**Fuente:** `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md`

---

## Happy Path (Casos de Éxito)

### CP-037 — Depósito en cuenta ahorro exitoso (sin comisión)
- **CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta ahorro STANDARD OPEN, movimientos mes actual < 10 (maxFree).
- **Pasos:**
  1. Enviar `POST /api/accounts/{id}/deposits` con `{amount:500, description:"Depósito móvil", channel:"MOBILE"}`.
  2. Validar `201 Created` con `movementId`, `newBalance`, `commissionApplied:0`.
  3. Verificar movimiento tipo `DEPOSIT` con `commissionApplied:0`.
  4. Verificar Redis balance invalidado, contador movimientos incrementado en Redis.
  5. Verificar evento `yanki.account.movement.registered.v1` con `commissionApplied:0`.
- **Postcondiciones:** Saldo incrementado, movimiento registrado.

### CP-038 — Depósito en cuenta ahorro CON comisión por exceso
- **CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md
- **Tipo:** Happy Path (regla de negocio)
- **Precondiciones:** Cuenta ahorro STANDARD, movimientos mes actual = 10 (llegó al límite), `commissionPerExcessMovement:2.50`.
- **Pasos:**
  1. Enviar depósito `amount:200`.
  2. Validar `201` con `commissionApplied:2.50`, `newBalance = anterior + 200 - 2.50`.
  3. Verificar movimiento `DEPOSIT` con `commissionApplied:2.50`, `commissionType:EXCESS_MOVEMENT`.
  4. Verificar contador Redis incrementado.
- **Postcondiciones:** Comisión aplicada correctamente.

### CP-040 — Retiro de cuenta ahorro exitoso (sin comisión)
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta ahorro OPEN, balance ≥ amount, movimientos mes < maxFree.
- **Pasos:** `POST /api/accounts/{id}/withdrawals {amount:200, channel:"ATM"}` → `201`, `commissionApplied:0`.

### CP-041 — Retiro de cuenta ahorro CON comisión por exceso
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta ahorro, movimientos mes ≥ maxFree (10), balance ≥ amount + 2.50.
- **Pasos:** Retiro → `201` con `commissionApplied:2.50`, `newBalance = balance - amount - 2.50`.

### CP-043 — Depósito/Retiro en cuenta corriente (ilimitado, sin comisión exceso)
- **CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md, SEC006_retiro_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta corriente STANDARD OPEN.
- **Pasos:** Operación → `201` con `commissionApplied:0` siempre (maxFree=-1).
- **Postcondiciones:** Sin comisión por exceso nunca.

### CP-044 — Depósito/Retiro en cuenta corriente PYME (sin comisión mantenimiento ni exceso)
- **CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md, SEC006_retiro_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta corriente PYME OPEN.
- **Pasos:** Operación → `201` con `commissionApplied:0`.

### CP-045 — Operación en cuenta plazo fijo: día permitido, primer movimiento
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta plazo fijo OPEN, hoy == `allowedMovementDay` (ej. 15), no hay movimientos hoy.
- **Pasos:** Depósito/Retiro → `201` con `commissionApplied:0` (primer movimiento gratis).

### CP-048 — Operación en plazo fijo CON comisión por exceso (2do movimiento en mes)
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Happy Path (regla)
- **Precondiciones:** Día permitido, ya hizo 1 movimiento gratis este mes, `maxFreeMovementsMonth:1`.
- **Pasos:** Segundo movimiento → `201` con `commissionApplied:5.00`.

---

## Error / Casos Negativos

### CP-039 — Depósito rechazado: fondos insuficientes para cubrir comisión
- **CU:** CU-05 | **SEC:** SEC005_deposito_cuenta.md
- **Tipo:** Error
- **Precondiciones:** Cuenta ahorro, balance=10, movimientos mes=10 (exceso), comisión=2.50, depósito=5.
- **Pasos:** Enviar depósito 5 (balance final sería 10+5-2.50=12.50 ≥ 0, OK). Cambiar a balance=1, depósito=1 → 1+1-2.50 < 0.
- **Errores esperados:** HTTP `409`, código `INSUFFICIENT_FUNDS_WITH_COMMISSION`.

### CP-042 — Retiro rechazado: saldo insuficiente (incluye comisión)
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Error
- **Precondiciones:** Balance < amount + comisión (si aplica exceso).
- **Pasos:** Retiro monto mayor a balance disponible.
- **Errores esperados:** HTTP `409`, código `INSUFFICIENT_FUNDS` o `INSUFFICIENT_FUNDS_WITH_COMMISSION`.

### CP-046 — Operación en plazo fijo rechazada: día no permitido
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Hoy ≠ `allowedMovementDay`.
- **Pasos:** Cualquier operación.
- **Errores esperados:** HTTP `422`, código `SOLO_DIA_PERMITIDO`, mensaje "Solo día {allowedMovementDay} permitido".

### CP-047 — Operación en plazo fijo rechazada: ya realizó movimiento hoy
- **CU:** CU-05 | **SEC:** SEC006_retiro_cuenta.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Hoy == día permitido, PERO ya hay movimiento hoy en esta cuenta.
- **Pasos:** Segunda operación en el día.
- **Errores esperados:** HTTP `409`, código `YA_REALIZO_MOVIMIENTO_HOY`.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CU-05 | Casos de prueba originales CP-037 a CP-048 |
| `../../CASOS_PRUEBA/RTM.md` | Fila CU-05 | Trazabilidad CU-05 |
| `../../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | Sección 2 | accounts-service detalle |
| `../../DIAGRAMAS_SECUENCIAS/SEC005_deposito_cuenta.md` | Diagrama completo | Depósito en cuenta |
| `../../DIAGRAMAS_SECUENCIAS/SEC006_retiro_cuenta.md` | Diagrama completo | Retiro de cuenta |
