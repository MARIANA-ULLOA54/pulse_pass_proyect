# Matriz de cobertura: 22 rutas × roles

| Ruta | Casos | Actores cubiertos |
|---|---|---|
| `GET /api/venues/active` | E2E-PREP01, E2E-VEN02, TC-AUTH-015 | ANONYMOUS |
| `GET /api/venues/{code}` | E2E-VEN01, E2E-VEN01-404 | ANONYMOUS |
| `POST /api/events` | E2E-EVT01, TC-SEC-001a, TC-SEC-001b, TC-SEC-001c, TC-AUTH-014, TC-HTTP-400e, TC-BIZ-015a, TC-BIZ-015b | ADMIN, ANONYMOUS, STAFF, USER_A |
| `GET /api/events/published` | E2E-EVT03 | ANONYMOUS |
| `GET /api/events/by-artist` | E2E-EVT06, TC-SEC-005 | ADMIN, ANONYMOUS |
| `GET /api/events/{eventCode}/tickets/paid` | TC-SEC-013, E2E-TKT04 | STAFF, USER_A |
| `GET /api/events/{eventCode}` | E2E-EVT02-PUB, E2E-EVT02-404, TC-SEC-004, TC-SEC-015, TC-SEC-015b, TC-SEC-004b | ADMIN, ANONYMOUS, STAFF, USER_A |
| `PATCH /api/events/{eventCode}/publish` | TC-SEC-002, TC-SEC-002b, E2E-EVT04, E2E-EVT04-409 | ADMIN, STAFF, USER_A |
| `POST /api/events/{eventCode}/artists/{artistId}` | TC-SEC-003, E2E-EVT05, E2E-EVT05-dup, E2E-EVT05-404 | ADMIN, USER_A |
| `GET /api/artists/active` | E2E-ART03 | ANONYMOUS |
| `GET /api/artists/by-stage-name` | E2E-ART02 | ANONYMOUS |
| `GET /api/artists/{id}` | E2E-ART01, E2E-ART01-404 | ANONYMOUS |
| `POST /api/users` | TC-AUTH-001, TC-BIZ-006, TC-SEC-016, TC-HTTP-400a, TC-HTTP-400b, TC-HTTP-400c, TC-HTTP-400d, TC-HTTP-409 | ANONYMOUS |
| `GET /api/users/by-email` | E2E-USR02, E2E-USR02-ADM, E2E-USR02-404, TC-SEC-006, TC-SEC-006b | ADMIN, ANONYMOUS, USER_A, USER_B |
| `GET /api/users/by-username` | E2E-USR03, TC-SEC-007 | USER_A, USER_B |
| `POST /api/tickets` | E2E-TKT01-T1, E2E-TKT01-T2, TC-SEC-008, TC-SEC-008b, TC-BIZ-014a, TC-BIZ-014b, TC-BIZ-014c, TC-BIZ-014d | ANONYMOUS, USER_A |
| `GET /api/tickets/by-user` | E2E-TKT03, E2E-TKT03-ADM, TC-SEC-010, TC-SEC-010b | ADMIN, USER_A, USER_B |
| `GET /api/tickets/{ticketCode}` | E2E-TKT02, E2E-TKT02-STAFF, E2E-TKT02-404, TC-SEC-009 | STAFF, USER_A, USER_B |
| `PATCH /api/tickets/{ticketCode}/cancel` | TC-SEC-011, E2E-TKT05, TC-BIZ-011 | USER_A, USER_B |
| `PATCH /api/tickets/{ticketCode}/use` | TC-SEC-012, E2E-TKT06-A, E2E-TKT06, TC-BIZ-013, TC-BIZ-010b, TC-SEC-017 | STAFF, STAFF_REVOKED, USER_A, USER_B |
| `POST /api/auth/login` | TC-AUTH-002, TC-AUTH-003, E2E-AUTH01-B, E2E-AUTH01-STAFF, E2E-AUTH01-ADMIN, TC-AUTH-004, TC-AUTH-005, TC-AUTH-013a | ANONYMOUS |
| `GET /api/auth/me` | TC-AUTH-006, E2E-AUTH02-B, E2E-AUTH02-STAFF, E2E-AUTH02-ADMIN, TC-AUTH-007, TC-AUTH-008, TC-AUTH-009, TC-AUTH-010, TC-AUTH-011, TC-AUTH-012, TC-AUTH-013b | ADMIN, ANONYMOUS, DEACT_USER, STAFF, USER_A, USER_B |

Rutas con al menos un caso: 22/22 · Casos únicos: 95

## Trazabilidad requisito → evidencia

| Requisito | Evidencia |
|---|---|
| FR-E2E-001/002 | Colección importable, `PulsePass-local.postman_environment.json`, 22 rutas arriba |
| FR-E2E-003/004 | `TC-AUTH-002`, `E2E-AUTH01-*`, `E2E-AUTH02-*` |
| FR-E2E-005/006 | Descripción de cada request + README §4 (qué copiar) |
| FR-E2E-007/008 | Carpetas 04–07; reconsultas indicadas en cada caso |
| FR-E2E-009 | Todos los casos 4xx (revisar 5 campos) |
| FR-E2E-010/016 | `fixtures/seed-e2e.sql`, README §2 y §7 |
| FR-E2E-011/012 | `checklist-e2e-manual.md`, `registro-ejecucion-manual.md` |
| FR-E2E-013 | `.gitignore`, variables `secret` vacías en el ambiente |
| FR-E2E-014/015 | Sección Defectos del registro; estado BLOCKED |
| FR-E2E-017 | Cuerpos tomados de los `record` reales del proyecto |
