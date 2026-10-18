# Fixtures E2E

| Fixture | Valor | Variable de Postman |
|---|---|---|
| Venue activo | `VEN-E2E-01` (capacidad 500) | `venueActiveCode` |
| Artista activo | id `1`, `Solar Beat` (migración V2) | `artistId`, `artistStageName` |
| Evento publicado | `EVT-PUB-E2E` | `publishedEventCode` |
| Evento borrador | `EVT-DRAFT-E2E` | `draftEventCode` |
| Evento por publicar | `EVT-TOPUB-E2E` | `eventToPublishCode` |
| Evento agotado | `EVT-SOLDOUT-E2E` | `soldOutEventCode` |
| Evento solo mayores | `EVT-AGE-E2E` (edad mínima 99) | `underageEventCode` |
| ADMIN / STAFF | `admin_e2e` / `staff_e2e` | `adminIdentifier`, `staffIdentifier` |
| Cuenta inactiva | `inactive_e2e` | `inactiveIdentifier` |
| Cuenta a desactivar | `deact_e2e` | (README §6) |
| STAFF a revocar | `revoke_e2e` | (README §6) |

- `seed-e2e.sql` verifica que la base actual sea `pulsepass_e2e` y aborta si no lo es.
- Reinicio entre sesiones: recrear la base, arrancar la app (Flyway) y volver a cargar el seed (README §7).
- Correos con dominio `example.test`; sin datos personales reales.
