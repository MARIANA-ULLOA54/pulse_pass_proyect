# Checklist de ejecución manual

Un 2xx no basta: revisa el body y reconsulta el estado. Marca cada paso al terminarlo.

## 0. Preflight (si algo falla → BLOCKED)
- [ ] Base `pulsepass_e2e` creada, app arrancada con perfil `e2e` (Flyway V1–V4 aplicado)
- [ ] `seed-e2e.sql` cargado; `PULSEPASS_JWT_SECRET_BASE64` definido solo en runtime
- [ ] Ambiente `PulsePass-local` seleccionado; `runId`, correos/usuarios, contraseñas (12+ caracteres) completados
- [ ] `E2E-PREP01` → 200

## Flujo A — Descubrimiento público (carpeta 09, Flujo A)
- [ ] Venues (2) · Artists (3) · `/events/published` sin borradores · borrador sin token → 404 · by-artist público

## Flujo B — Registro y JWT
- [ ] `TC-AUTH-001`, `TC-BIZ-006` · `TC-SEC-016` (rol inyectado) · logins A, B, STAFF, ADMIN → copiar tokens
- [ ] `/me` por actor · `TC-AUTH-004/005/007/008/012/015`
- [ ] Preparar y ejecutar `TC-AUTH-009/010/011/013a/013b` (README §6)

## Flujo C — Catálogo
- [ ] Crear → leer como ADMIN → asociar artista → publicar → leer sin token → published / by-artist
- [ ] USER y STAFF denegados: `TC-SEC-001b/001c/002/002b/003`

## Flujo D — Tickets
- [ ] T1 y T2 (compra A) → copiar códigos · compra ajena denegada (`TC-SEC-008` y `TC-SEC-010b`)
- [ ] B denegado en GET/by-user/cancel/use (`TC-SEC-009/010/011/012`) · reconsultar T1 y T2: sin cambios
- [ ] STAFF lista pagados · A cancela T1 · repetir cancelar → 409 · STAFF usa T2 · repetir → 409 · USER no usa

## Flujo E — Errores
- [ ] Carpeta 08: 400, 404, 409, compras inválidas (`TC-BIZ-014a-d`) · cada ErrorResponse con 5 campos, sin stack trace
- [ ] `TC-SEC-017` (rol revocado)

## Cierre
- [ ] 22/22 rutas con un caso nominal (ver `matriz-cobertura.md`) · registro completo · defectos listados · sin tokens ni contraseñas en evidencias
