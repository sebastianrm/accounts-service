# CU-02: Gestión de Cuentas — Casos de Prueba

**Microservicio:** accounts-service  
**CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md, SEC003_apertura_cuenta_corriente.md, SEC004_apertura_cuenta_plazo_fijo.md, SEC007_cierre_cuenta.md  
**Fuente:** `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md`

---

## Happy Path (Casos de Éxito)

### CP-020 — Apertura de cuenta ahorro exitosa (STANDARD)
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Happy Path
- **Precondiciones:**
  - Cliente PERSONAL ACTIVE, profile=STANDARD.
  - `customer_profile_view` poblada con datos del cliente.
  - Accounts Service, MongoDB, Redis, Kafka operativos.
- **Pasos:**
  1. Enviar `POST /api/accounts` con `{type:SAVINGS, subType:STANDARD, currency:PEN, initialAmount:1000, customerId}`.
  2. Validar `201 Created` con `accountId`, `accountNumber`, `status:OPEN`, `balance:1000`.
  3. Verificar MongoDB: cuenta creada con `status:OPEN`, `balance:1000`, `maxFreeMovementsMonth:10`, `commissionPerExcessMovement:2.50`, `maintenanceCommission:0`.
  4. Verificar movimiento `OPENING_DEPOSIT` registrado.
  5. Verificar evento `yanki.account.opened.v1` publicado en Kafka.
  6. Verificar Redis: balance cacheado `yanki:accounts:balance:{accountId}`.
- **Postcondiciones:** Cuenta ahorro STANDARD creada y abierta.

### CP-021 — Apertura de cuenta ahorro VIP exitosa
- **CU:** CU-02, CU-14 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente PERSONAL, profile=VIP, `hasCreditCard=true`.
- **Pasos:**
  1. Enviar `POST /api/accounts` con `{type:SAVINGS, subType:VIP, currency:PEN, initialAmount:5000, customerId}`.
  2. Validar `201 Created` con `subType:VIP`, `vipMinDailyAverage:5000`.
  3. Verificar validación `customer_card_view.hasCreditCard=true`.
  4. Verificar evento `yanki.account.opened.v1` incluye `subType:VIP`, `vipMinDailyAverage:5000`.
- **Postcondiciones:** Cuenta Ahorro VIP creada con parámetros VIP.

### CP-025 — Apertura de cuenta corriente exitosa (ENTERPRISE STANDARD — múltiples permitidas)
- **CU:** CU-02 | **SEC:** SEC003_apertura_cuenta_corriente.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente ENTERPRISE, profile=STANDARD o PYME.
- **Pasos:**
  1. Enviar `POST /api/accounts` con `{type:CHECKING, subType:STANDARD, currency:PEN, initialAmount:5000, holders: array, signers: array opcional}`.
  2. Validar `201 Created` con `maintenanceCommission:15.00`, `maxFreeMovementsMonth:-1` (ilimitado).
  3. Verificar múltiples cuentas corrientes permitidas para mismo cliente ENTERPRISE.
- **Postcondiciones:** Cuenta corriente creada.

### CP-026 — Apertura de cuenta corriente PYME exitosa
- **CU:** CU-02 | **SEC:** SEC003_apertura_cuenta_corriente.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente ENTERPRISE, profile=PYME, `hasCreditCard=true`.
- **Pasos:**
  1. Enviar `POST /api/accounts` con `{type:CHECKING, subType:PYME, holders, signers}`.
  2. Validar `201 Created` con `maintenanceCommission:0` (diferencia clave vs STANDARD), `maxFreeMovementsMonth:-1`.
  3. Verificar validación `hasCreditCard=true`.
- **Postcondiciones:** Cuenta corriente PYME sin comisión mantenimiento.

### CP-029 — Apertura de cuenta plazo fijo exitosa (PERSONAL)
- **CU:** CU-02 | **SEC:** SEC004_apertura_cuenta_plazo_fijo.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente PERSONAL, no tiene cuenta plazo fijo (o múltiples permitidas según regla).
- **Pasos:**
  1. Enviar `POST /api/accounts` con `{type:FIXED_TERM, subType:STANDARD, currency:PEN, initialAmount:10000, maturityDate, allowedMovementDay:15}`.
  2. Validar `201 Created` con `allowedMovementDay:15`, `maxFreeMovementsMonth:1`, `commissionPerExcessMovement:5.00`.
  3. Verificar movimiento `OPENING_DEPOSIT` y evento `account.opened.v1`.
- **Postcondiciones:** Cuenta plazo fijo creada con día permitido configurado.

### CP-033 — Consulta de cuenta por ID exitosa
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta existe.
- **Pasos:** `GET /api/accounts/{id}` → `200 OK`.

### CP-034 — Listado de cuentas con filtros
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Happy Path
- **Precondiciones:** Existen cuentas.
- **Pasos:** `GET /api/accounts?customerId={id}&type=SAVINGS&status=OPEN&page=0&size=10` → `200 OK` con PageResponse.

### CP-035 — Cierre de cuenta exitoso (balance=0)
- **CU:** CU-02 | **SEC:** SEC007_cierre_cuenta.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta existe, status=OPEN, balance=0, operador autenticado.
- **Pasos:**
  1. Enviar `DELETE /api/accounts/{id}`.
  2. Validar `204 No Content`.
  3. Verificar MongoDB: `status:CLOSED`, `closedAt:now`, `balance:0`.
  4. Verificar evento `yanki.account.closed.v1`.
- **Postcondiciones:** Cuenta cerrada.

---

## Error / Casos Negativos

### CP-022 — Apertura cuenta ahorro rechazada: cliente ya tiene cuenta ahorro (PERSONAL máx 1)
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cliente PERSONAL ya tiene cuenta tipo SAVINGS (cualquier subType).
- **Pasos:** Enviar `POST /api/accounts` con `type:SAVINGS`.
- **Errores esperados:** HTTP `409`, código `ACCOUNT_ALREADY_EXISTS` / `MAX_SAVINGS_ACCOUNT_REACHED`.

### CP-023 — Apertura cuenta ahorro rechazada: ENTERPRISE no permitido
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cliente ENTERPRISE (cualquier perfil).
- **Pasos:** Enviar `POST /api/accounts` con `type:SAVINGS`.
- **Errores esperados:** HTTP `422`, código `INVALID_ACCOUNT_TYPE_FOR_CUSTOMER`.

### CP-024 — Apertura cuenta ahorro VIP rechazada: sin tarjeta crédito activa
- **CU:** CU-02, CU-14 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Error / Regla de negocio (CU-14 validación transversal)
- **Precondiciones:** Cliente PERSONAL VIP, PERO `customer_card_view.hasCreditCard=false`.
- **Pasos:** Enviar `POST /api/accounts` con `subType:VIP`.
- **Errores esperados:** HTTP `422`, código `INVALID_VIP_PROFILE`, mensaje "Cliente VIP requiere tarjeta de crédito activa".

### CP-027 — Apertura cuenta corriente PYME rechazada: sin tarjeta crédito
- **CU:** CU-02 | **SEC:** SEC003_apertura_cuenta_corriente.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cliente ENTERPRISE PYME, `hasCreditCard=false`.
- **Pasos:** Enviar `POST /api/accounts` con `subType:PYME`.
- **Errores esperados:** HTTP `422`, código `INVALID_PYME_PROFILE`.

### CP-028 — Apertura cuenta corriente rechazada: PERSONAL ya tiene una (máx 1)
- **CU:** CU-02 | **SEC:** SEC003_apertura_cuenta_corriente.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cliente PERSONAL ya tiene cuenta CHECKING.
- **Pasos:** Enviar `POST /api/accounts` con `type:CHECKING`.
- **Errores esperados:** HTTP `409`, código `MAX_CHECKING_ACCOUNT_REACHED`.

### CP-030 — Apertura plazo fijo rechazada: ENTERPRISE no permitido
- **CU:** CU-02 | **SEC:** SEC004_apertura_cuenta_plazo_fijo.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cliente ENTERPRISE.
- **Pasos:** Enviar `POST /api/accounts` con `type:FIXED_TERM`.
- **Errores esperados:** HTTP `422`, código `INVALID_ACCOUNT_TYPE_FOR_CUSTOMER`.

### CP-031 — Apertura cuenta rechazada: cliente inexistente o no ACTIVE
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Error
- **Precondiciones:** customerId no existe en `customer_profile_view` o status ≠ ACTIVE.
- **Pasos:** Enviar `POST /api/accounts` con customerId inválido.
- **Errores esperados:** HTTP `404` o `422`, código `CUSTOMER_NOT_FOUND` o `CUSTOMER_NOT_ACTIVE`.

### CP-032 — Apertura cuenta rechazada: monto inicial < mínimo
- **CU:** CU-02 | **SEC:** SEC002_apertura_cuenta_ahorro.md
- **Tipo:** Error / Validación
- **Precondiciones:** Cuenta requiere `minOpeningAmount > 0` (configurable).
- **Pasos:** Enviar `POST /api/accounts` con `initialAmount` menor al mínimo.
- **Errores esperados:** HTTP `422`, código `INSUFFICIENT_OPENING_AMOUNT`.

### CP-036 — Cierre de cuenta rechazado: balance > 0
- **CU:** CU-02 | **SEC:** SEC007_cierre_cuenta.md
- **Tipo:** Error / Regla de negocio
- **Precondiciones:** Cuenta con balance > 0.
- **Pasos:** Enviar `DELETE /api/accounts/{id}`.
- **Errores esperados:** HTTP `409`, código `ACCOUNT_BALANCE_NOT_ZERO`.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CU-02 | Casos de prueba originales CP-020 a CP-036 |
| `../../CASOS_PRUEBA/RTM.md` | Fila CU-02 | Trazabilidad CU-02 |
| `../../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | Sección 2 | accounts-service detalle |
| `../../DIAGRAMAS_SECUENCIAS/SEC002_apertura_cuenta_ahorro.md` | Diagrama completo | Apertura cuenta ahorro |
| `../../DIAGRAMAS_SECUENCIAS/SEC003_apertura_cuenta_corriente.md` | Diagrama completo | Apertura cuenta corriente |
| `../../DIAGRAMAS_SECUENCIAS/SEC004_apertura_cuenta_plazo_fijo.md` | Diagrama completo | Apertura cuenta plazo fijo |
| `../../DIAGRAMAS_SECUENCIAS/SEC007_cierre_cuenta.md` | Diagrama completo | Cierre de cuenta |
