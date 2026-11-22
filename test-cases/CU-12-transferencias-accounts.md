# CU-12: Transferencias — Casos de Prueba (Perspectiva accounts-service)

**Microservicio:** accounts-service  
**CU:** CU-12 | **SEC:** SEC010_transferencias.md  
**Fuente:** `../../docs/CASOS_PRUEBA`  
**Nota:** accounts-service actúa como **consumer/liquidador** de transferencias. Consume `transfer.requested.v1` de Kafka, liquida en MongoDB (debita origen, acredita destino) y publica `transfer.settled.v1`.

---

## Happy Path (Casos de Éxito)

### CP-067 — Transferencia: comisión por exceso en cuenta ahorro origen
- **CU:** CU-12 | **SEC:** SEC010_transferencias.md
- **Tipo:** Happy Path (Regla comisión)
- **Precondiciones:** Cuenta origen = Ahorro STANDARD, movimientos mes actual = 10 (maxFree=10), `commissionPerExcessMovement:2.50`.
- **Pasos:** Transferencia amount=500 → comisión=2.50 aplicada, total debitado=502.50.
- **Validación:** Verificar movement origen `TRANSFER_OUT` con `commissionApplied:2.50`, `commissionType:EXCESS_MOVEMENT`.

### CP-068 — Transferencia: cuenta corriente origen (sin comisión exceso)
- **CU:** CU-12 | **SEC:** SEC010_transferencias.md
- **Tipo:** Happy Path
- **Precondiciones:** Cuenta origen = Corriente (maxFree=-1).
- **Pasos:** Transferencia → comisión=0 siempre.

### CP-069 — Transferencia: cuenta plazo fijo origen (día permitido + comisión si exceso)
- **CU:** CU-12 | **SEC:** SEC010_transferencias.md
- **Tipo:** Happy Path / Error según día
- **Precondiciones:** Cuenta plazo fijo, hoy = allowedMovementDay, movimientos mes=1 (maxFree=1).
- **Pasos:** Transferencia → comisión=5.00 (exceso), validar día permitido.

---

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../docs/CASOS_PRUEBA` | CU-12 | Casos de prueba originales CP-058 a CP-074 (CP-067 a CP-069 son los que aplican a accounts-service como liquidador) |
| `../../docs/CASOS_PRUEBA` | Fila CU-12 | Trazabilidad CU-12 |
| `../../docs/CASOS_PRUEBA` | Sección 2 | accounts-service como consumer de transfer.requested |
| `../../DIAGRAMAS_SECUENCIAS/SEC010_transferencias.md` | Diagrama completo | Transferencias event-driven |
