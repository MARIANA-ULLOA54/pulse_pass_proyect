# Registro de ejecución manual — PulsePass E2E

- Fecha: ____ · Ejecutor: ____ · runId: ____ · Base: pulsepass_e2e
- Resultado: PASS / FAIL / BLOCKED / NOT RUN. No pegar JWT, contraseñas ni datos personales reales.

| Caso | Actor | Ruta | HTTP esperado | HTTP observado | JSON / estado observado | Resultado | Evidencia / observación |
|---|---|---|---|---|---|---|---|
| E2E-PREP01 | ANONYMOUS | GET /api/venues/active | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-VEN01 | ANONYMOUS | GET /api/venues/{{venueActiveCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-VEN01-404 | ANONYMOUS | GET /api/venues/NO-EXISTE-{{runId}} | 404 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-VEN02 | ANONYMOUS | GET /api/venues/active | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-ART01 | ANONYMOUS | GET /api/artists/{{artistId}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-ART01-404 | ANONYMOUS | GET /api/artists/999999999 | 404 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-ART02 | ANONYMOUS | GET /api/artists/by-stage-name | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-ART03 | ANONYMOUS | GET /api/artists/active | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-001 | ANONYMOUS | POST /api/users | 201 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-006 | ANONYMOUS | POST /api/users | 201 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-002 | ANONYMOUS | POST /api/auth/login | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-003 | ANONYMOUS | POST /api/auth/login | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH01-B | ANONYMOUS | POST /api/auth/login | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH01-STAFF | ANONYMOUS | POST /api/auth/login | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH01-ADMIN | ANONYMOUS | POST /api/auth/login | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-004 | ANONYMOUS | POST /api/auth/login | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-005 | ANONYMOUS | POST /api/auth/login | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-013a | ANONYMOUS | POST /api/auth/login | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-006 | USER_A | GET /api/auth/me | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH02-B | USER_B | GET /api/auth/me | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH02-STAFF | STAFF | GET /api/auth/me | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-AUTH02-ADMIN | ADMIN | GET /api/auth/me | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-007 | ANONYMOUS | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-008 | ANONYMOUS | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-009 | USER_A | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-010 | USER_A | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-011 | USER_A | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-012 | ANONYMOUS | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-015 | ANONYMOUS | GET /api/venues/active | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-USR02 | USER_A | GET /api/users/by-email | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-USR02-ADM | ADMIN | GET /api/users/by-email | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-USR02-404 | ADMIN | GET /api/users/by-email | 404 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-USR03 | USER_A | GET /api/users/by-username | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-006 | USER_B | GET /api/users/by-email | {{expectedForeignResourceStatus}} (403) | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-007 | USER_B | GET /api/users/by-username | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-006b | ANONYMOUS | GET /api/users/by-email | 401 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT01 | ADMIN | POST /api/events | 201 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-001a | ANONYMOUS | POST /api/events | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-001b | USER_A | POST /api/events | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-001c | STAFF | POST /api/events | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT02-PUB | ANONYMOUS | GET /api/events/{{publishedEventCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT02-404 | ANONYMOUS | GET /api/events/NO-EXISTE-{{runId}} | 404 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-004 | ANONYMOUS | GET /api/events/{{draftEventCode}} | 404 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-015 | ADMIN | GET /api/events/{{draftEventCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-015b | STAFF | GET /api/events/{{draftEventCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-004b | USER_A | GET /api/events/{{draftEventCode}} | 404 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT03 | ANONYMOUS | GET /api/events/published | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-003 | USER_A | POST /api/events/{{newEventCode}}/artists/{{artistId}} | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT05 | ADMIN | POST /api/events/{{newEventCode}}/artists/{{artistId}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT05-dup | ADMIN | POST /api/events/{{newEventCode}}/artists/{{artistId}} | 409 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT05-404 | ADMIN | POST /api/events/{{newEventCode}}/artists/999999999 | 404 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-002 | USER_A | PATCH /api/events/{{newEventCode}}/publish | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-002b | STAFF | PATCH /api/events/{{newEventCode}}/publish | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT04 | ADMIN | PATCH /api/events/{{newEventCode}}/publish | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT04-409 | ADMIN | PATCH /api/events/{{newEventCode}}/publish | 409 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-EVT06 | ANONYMOUS | GET /api/events/by-artist | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-005 | ADMIN | GET /api/events/by-artist | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT01-T1 | USER_A | POST /api/tickets | 201 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT01-T2 | USER_A | POST /api/tickets | 201 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-008 | USER_A | POST /api/tickets | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-008b | ANONYMOUS | POST /api/tickets | 401 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT02 | USER_A | GET /api/tickets/{{ticketCancelableCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT02-STAFF | STAFF | GET /api/tickets/{{ticketCancelableCode}} | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT02-404 | STAFF | GET /api/tickets/TCK-NOEXISTE | 404 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-009 | USER_B | GET /api/tickets/{{ticketCancelableCode}} | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT03 | USER_A | GET /api/tickets/by-user | 200 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT03-ADM | ADMIN | GET /api/tickets/by-user | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-010 | USER_B | GET /api/tickets/by-user | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-010b | USER_B | GET /api/tickets/by-user | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-013 | USER_A | GET /api/events/{{publishedEventCode}}/tickets/paid | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT04 | STAFF | GET /api/events/{{publishedEventCode}}/tickets/paid | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-011 | USER_B | PATCH /api/tickets/{{ticketCancelableCode}}/cancel | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT05 | USER_A | PATCH /api/tickets/{{ticketCancelableCode}}/cancel | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-011 | USER_A | PATCH /api/tickets/{{ticketCancelableCode}}/cancel | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-012 | USER_B | PATCH /api/tickets/{{ticketUsableCode}}/use | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT06-A | USER_A | PATCH /api/tickets/{{ticketUsableCode}}/use | 403 | pendiente | pendiente | NOT RUN | pendiente |
| E2E-TKT06 | STAFF | PATCH /api/tickets/{{ticketUsableCode}}/use | 200 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-013 | STAFF | PATCH /api/tickets/{{ticketUsableCode}}/use | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-010b | STAFF | PATCH /api/tickets/{{ticketCancelableCode}}/use | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-016 | ANONYMOUS | POST /api/users | 201 (role ignorado) o 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-014 | USER_A | POST /api/events | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-AUTH-013b | DEACT_USER | GET /api/auth/me | 401 | pendiente | pendiente | NOT RUN | pendiente |
| TC-SEC-017 | STAFF_REVOKED | PATCH /api/tickets/{{ticketUsableCode}}/use | 403 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-400a | ANONYMOUS | POST /api/users | 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-400b | ANONYMOUS | POST /api/users | 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-400c | ANONYMOUS | POST /api/users | 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-400d | ANONYMOUS | POST /api/users | 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-400e | ADMIN | POST /api/events | 400 | pendiente | pendiente | NOT RUN | pendiente |
| TC-HTTP-409 | ANONYMOUS | POST /api/users | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-015a | ADMIN | POST /api/events | 404 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-015b | ADMIN | POST /api/events | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-014a | USER_A | POST /api/tickets | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-014b | USER_A | POST /api/tickets | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-014c | USER_A | POST /api/tickets | 409 | pendiente | pendiente | NOT RUN | pendiente |
| TC-BIZ-014d | USER_A | POST /api/tickets | 404 | pendiente | pendiente | NOT RUN | pendiente |

## Consolidación por módulo

| Módulo | PASS | FAIL | BLOCKED | NOT RUN |
|---|---|---|---|---|
| 00 Preparación / conectividad | | | | 1 |
| 01 Venues (2 rutas) | | | | 3 |
| 02 Artists (3 rutas) | | | | 4 |
| 03 Auth / usuarios (registro, login, me) | | | | 21 |
| 04 Usuarios / consulta y propiedad (2 rutas) | | | | 7 |
| 05 Events / administración y visibilidad (6 rutas) | | | | 21 |
| 06 Tickets / compra, consulta y ciclo de vida (6 rutas) | | | | 22 |
| 07 Seguridad / escenarios negativos | | | | 4 |
| 08 Validaciones y errores HTTP | | | | 12 |

## Defectos

| ID | Precondición | Pasos | Esperado | Observado | Severidad |
|---|---|---|---|---|---|
| | | | | | |

## Política 403/404 adoptada

- Recurso ajeno (tickets, usuarios): **403** (`@PreAuthorize`).
- Borrador sin privilegios: **404** (no revela existencia).
- Sin token en ruta protegida: **401**.
