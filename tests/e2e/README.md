# PulsePass — Fase 5: pruebas E2E manuales con Postman

Sin scripts, sin Runner, sin Newman. Cada request se envía con **Send** y se compara a mano.

```
tests/e2e/
├── README.md · checklist-e2e-manual.md · registro-ejecucion-manual.md · matriz-cobertura.md
├── postman/  PulsePass-Manual-E2E.postman_collection.json · PulsePass-local.postman_environment.json
├── fixtures/ README.md · seed-e2e.sql
└── .gitignore
```
Perfil de la app: `src/main/resources/application-e2e.yml`.

## 1. Preparar la base y la app
```bash
psql -U postgres -c "CREATE DATABASE pulsepass_e2e;"
```
Secreto JWT exclusivo E2E (mín. 32 bytes, solo en runtime):
```bash
# Linux / macOS / Git Bash
export PULSEPASS_JWT_SECRET_BASE64=$(openssl rand -base64 32)
export DB_USER=postgres DB_PASSWORD=<tu-clave>
./mvnw spring-boot:run -Dspring-boot.run.profiles=e2e
```
```powershell
# Windows PowerShell
$b = New-Object byte[] 32; [Security.Cryptography.RandomNumberGenerator]::Fill($b)
$env:PULSEPASS_JWT_SECRET_BASE64 = [Convert]::ToBase64String($b)
$env:DB_USER = "postgres"; $env:DB_PASSWORD = "<tu-clave>"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=e2e"
```
La primera ejecución aplica Flyway V1–V4. Detén la app o déjala corriendo y carga el seed:
```bash
psql -U postgres -d pulsepass_e2e -v admin_pw='AdminE2E-clave-123' -v staff_pw='StaffE2E-clave-123' -v aux_pw='AuxE2E-clave-1234' -f tests/e2e/fixtures/seed-e2e.sql
```
(Sustituye las contraseñas por las tuyas; 12+ caracteres.)

## 2. Importar y configurar Postman
1. Importa la colección y el ambiente; selecciona **PulsePass-local**.
2. Rellena en **Current value**: `runId`, `userA*`/`userB*` (usuario, correo, contraseña de 12+ caracteres), `adminPassword`, `staffPassword`, `auxPassword` (las mismas del seed).
3. Los valores de fixtures ya vienen alineados con el seed; el enum y el tipo reales son `MUSIC` y `GENERAL`.
4. `newEventCode` debe ser único por corrida (p. ej. `E2E-<runId>-01`).
5. Ejecuta `E2E-PREP01` → 200.

## 3. Orden de ejecución
Sigue `checklist-e2e-manual.md`. La carpeta **09** contiene la secuencia recomendada (Flujos A–E); las carpetas 00–08 son el catálogo completo de casos. Cada request lleva ID, actor, precondiciones, HTTP esperado y qué revisar.

## 4. Qué copiar y dónde
| Origen (respuesta) | Variable |
|---|---|
| `accessToken` de `TC-AUTH-002` | `userAToken` |
| `accessToken` de `E2E-AUTH01-B` / `-STAFF` / `-ADMIN` | `userBToken` / `staffToken` / `adminToken` |
| `ticketCode` de `E2E-TKT01-T1` | `ticketCancelableCode` |
| `ticketCode` de `E2E-TKT01-T2` | `ticketUsableCode` |

El token dura 900 s; si aparece un 401 inesperado, repite el login. Antes de cada request protegida revisa la pestaña **Authorization**.

## 5. Criterios y evidencia
PASS: HTTP, JSON y efecto de negocio coinciden. FAIL: algo no coincide o hay un 500. BLOCKED: falta un prerrequisito (p. ej. `/api/auth/me`). Para cada denegación sobre un recurso mutable, reconsulta con un actor autorizado. Anota en `registro-ejecucion-manual.md`; no pegues JWT ni contraseñas.

Política observada en el código: recurso ajeno → **403**; borrador sin privilegios → **404**; sin token → **401**.

## 6. Casos con preparación especial (sin tocar el código de la app)
- **TC-AUTH-009 (alterado):** copia `userAToken` en `tamperedToken` y cambia un carácter de la **firma** (la tercera parte, tras el último punto), no el último carácter.
- **TC-AUTH-010 (expirado):** reinicia la app con `PULSEPASS_SECURITY_JWT_ACCESS_TOKEN_TTL=1` (1 segundo), haz login de A, copia el token en `expiredToken`, espera 2 s y ejecuta el caso. Vuelve a arrancar con el TTL normal.
- **TC-AUTH-011 (otra clave):** arranca una segunda instancia (otro puerto, `-Dserver.port=8081`, otro `PULSEPASS_JWT_SECRET_BASE64` generado al azar), haz login de A allí y copia el token en `foreignKeyToken`. Ejecuta el caso contra la instancia principal.
- **TC-AUTH-013b (desactivado):** login con `deact_e2e` (contraseña `auxPassword`) → `deactToken`. Luego:
  ```sql
  UPDATE users SET active = false WHERE username = 'deact_e2e';
  ```
  y envía `/api/auth/me` con ese token → 401.
- **TC-SEC-017 (rol revocado):** login con `revoke_e2e` → `revokedStaffToken`. Luego:
  ```sql
  DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'revoke_e2e');
  ```
  y envía `PATCH /api/tickets/{{ticketUsableCode}}/use` → 403. Ejecuta este caso **antes** de `E2E-TKT06`, o usa un ticket distinto, para que el 403 no se confunda con un 409.
- **TC-SEC-016:** tras el 201, haz login con el usuario creado y consulta `/me`: `roles` debe ser `["USER"]`.

## 7. Repetir desde cero
```bash
psql -U postgres -c "DROP DATABASE pulsepass_e2e;" -c "CREATE DATABASE pulsepass_e2e;"
```
Reinicia la app (Flyway recrea el esquema), vuelve a cargar el seed y repite. Alternativa: cambia `runId`, usuarios y `newEventCode`. Verifica siempre el nombre de la base antes de borrar; nunca lo hagas sobre bases de desarrollo, QA o producción.

## 8. Limitaciones conocidas
- STAFF es global para tickets (no por evento): limitación del MVP, no un diseño para producción.
- `eventDate` del ambiente (`2030-01-15`) debe seguir en el futuro al ejecutar.
