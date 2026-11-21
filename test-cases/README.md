# Test Cases — accounts-service

**Microservicio:** accounts-service (puerto 8083)  
**Fuente:** `CASOS_PRUEBA/CASOS_PRUEBA_QA.md`  
**Última actualización:** 2026-11-14

## Descripción

Este directorio contiene los casos de prueba específicos para el microservicio **accounts-service**, extraídos y organizados desde la documentación central de QA del proyecto Yanki Platform.

## Casos de Uso Cubiertos

| Caso de Uso | Descripción | Casos de Prueba |
|---|---|---|
| **CU-02** | Gestión de Cuentas (apertura/cierre 3 tipos) | CP-020 a CP-036 |
| **CU-05** | Operaciones: Depósitos/Retiros + Comisiones | CP-037 a CP-048 |
| **CU-08** | Consulta Saldos (Redis Cache) | CP-049 a CP-053 |
| **CU-09** | Consulta Movimientos (paginados con filtros) | CP-054 a CP-057 |
| **CU-12** | Transferencias (event-driven, idempotente) — rol consumer/liquidador | CP-067 a CP-069 |
| **CU-14** | Perfiles VIP/PYME — validador en apertura de cuenta | CP-131 a CP-133 |
| **CU-15** | Comisiones (Jobs + Tiempo Real) | CP-138 a CP-144 |

## Resumen de Casos de Prueba

| Categoría | Cantidad |
|---|---|
| **Happy Path** | ~25 |
| **Error / Negativo** | ~20 |
| **Integración / Jobs** | ~5 |
| **Total** | ~50 |

## Estructura de Archivos

| Archivo | Contenido |
|---|---|
| `README.md` | Este archivo — índice y descripción |
| `CU-02-gestion-cuentas.md` | CP-020 a CP-036 — Apertura y cierre de cuentas |
| `CU-05-operaciones-cuentas.md` | CP-037 a CP-048 — Depósitos, retiros y comisiones |
| `CU-08-consulta-saldos.md` | CP-049 a CP-053 — Consulta de saldos con Redis Cache |
| `CU-09-consulta-movimientos.md` | CP-054 a CP-057 — Consulta de movimientos paginados |
| `CU-12-transferencias-accounts.md` | CP-067 a CP-069 — Transferencias desde perspectiva de accounts-service |
| `CU-14-perfiles-vip-pyme.md` | CP-131 a CP-133 — Validación VIP/PYME en apertura de cuenta |
| `CU-15-comisiones.md` | CP-138 a CP-144 — Comisiones tiempo real y jobs programados |

## Referencias

| Archivo | Sección | Descripción |
|---|---|---|
| `../../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CU-02 a CU-15 | Fuente original de todos los casos de prueba |
| `../../CASOS_PRUEBA/RTM.md` | Fila accounts-service | Matriz de trazabilidad completa |
| `../../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | Sección 2 | Trazabilidad detallada por servicio |
| `../../DIAGRAMAS_SECUENCIAS/` | SEC002-SEC013, SEC027-SEC028 | Diagramas de secuencia relacionados |

## Criterios Generales de Prueba

### Precondiciones generales para Happy Path
- Usuario autenticado con token JWT válido.
- Accounts Service, MongoDB (`yanki_accounts`), Redis y Kafka operativos.
- Cuando aplique, `customer_profile_view` y `customer_card_view` pobladas.
- Eureka Server tiene registrado accounts-service.

### Criterios de validación comunes
- Validar código HTTP esperado.
- Validar estructura y contenido de la respuesta (JSON schema).
- Validar persistencia en MongoDB.
- Validar invalidación/uso de caché Redis cuando aplique.
- Validar publicación de eventos Kafka cuando aplique.
- Validar mensajes o códigos de error funcional esperados.
- Validar comportamiento de Circuit Breaker cuando aplique.
