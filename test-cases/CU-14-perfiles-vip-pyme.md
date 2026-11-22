# CU-14: Perfiles VIP/PYME — Casos de Prueba (Perspectiva accounts-service)

**Microservicio:** accounts-service  
**CU:** CU-14 | **SEC:** SEC012_perfiles_vip_pyme.md  
**Fuente:** `../../docs/CASOS_PRUEBA`  
**Nota:** accounts-service actúa como **validador** al abrir cuentas VIP/PYME. Valida `customer_profile_view` + `customer_card_view` antes de permitir la apertura.

---

## Happy Path (Casos de Éxito)

### CP-131 — Apertura Cuenta Ahorro VIP exitosa (validación completa accounts-service)
- **CU:** CU-14 | **SEC:** SEC012_perfiles_vip_pyme.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente PERSONAL, profile=VIP, `hasCreditCard=true`.
- **Pasos:**
  1. `POST /api/accounts {type:SAVINGS, subType:VIP, customerId, initialAmount:5000}`.
  2. Validar `201 Created` con `subType:VIP`, `vipMinDailyAverage:5000`.
  3. Verificar que accounts-service valida `customer_profile_view.profile=VIP` y `customer_card_view.hasCreditCard=true`.
  4. Verificar evento `yanki.account.opened.v1` con `subType:VIP`.
- **Postcondiciones:** Cuenta VIP creada.

### CP-133 — Apertura Cuenta Corriente PYME exitosa
- **CU:** CU-14 | **SEC:** SEC012_perfiles_vip_pyme.md
- **Tipo:** Happy Path
- **Precondiciones:** Cliente ENTERPRISE PYME, `hasCreditCard=true`.
- **Pasos:** `POST /api/accounts {type:CHECKING, subType:PYME, holders:[...], signers:[...]}` → `201` con `maintenanceCommission:0`.

---

## Error / Casos Negativos

### CP-132 — Apertura Cuenta Ahorro VIP rechazada: sin tarjeta crédito (validación accounts-service)
- **CU:** CU-14 | **SEC:** SEC012_perfiles_vip_pyme.md
- **Tipo:** Error
- **Precondiciones:** Cliente PERSONAL VIP PERO `hasCreditCard=false` (tarjeta cerrada después de asignar perfil).
- **Pasos:** `POST /api/accounts {type:SAVINGS, subType:VIP}`.
- **Errores esperados:** HTTP `422`, código `INVALID_VIP_PROFILE`, mensaje "Cliente VIP requiere tarjeta de crédito activa".

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../docs/CASOS_PRUEBA` | CU-14 | Casos de prueba originales CP-126 a CP-137 (CP-131 a CP-133 aplican a accounts-service) |
| `../../docs/CASOS_PRUEBA` | Fila CU-14 | Trazabilidad CU-14 |
| `../../docs/CASOS_PRUEBA` | Sección 2 | accounts-service como validador VIP/PYME |
| `../../DIAGRAMAS_SECUENCIAS/SEC012_perfiles_vip_pyme.md` | Diagrama completo | Perfiles VIP/PYME |
