-- seed-e2e.sql — EXCLUSIVO para la base pulsepass_e2e (aplicar DESPUÉS de las migraciones Flyway V1–V4).
-- Uso (las contraseñas se pasan por variables de psql; no se guardan en el repositorio):
--   psql -U postgres -d pulsepass_e2e -v admin_pw='...' -v staff_pw='...' -v aux_pw='...' -f seed-e2e.sql
-- Cada contraseña debe tener 12+ caracteres. Los hashes se generan con BCrypt (pgcrypto, formato $2a$ compatible con Spring).
\set ON_ERROR_STOP on

SELECT current_database() = 'pulsepass_e2e' AS is_e2e \gset
\if :is_e2e
\else
  \echo 'ABORTADO: la base actual no es pulsepass_e2e'
  \quit
\endif

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Usuarios con credenciales y roles (el registro público solo crea USER)
WITH u AS (INSERT INTO users (username, email, active) VALUES ('admin_e2e', 'admin_e2e@example.test', TRUE) RETURNING id),
c AS (INSERT INTO user_credentials (user_id, password_hash, password_changed_at)
      SELECT id, crypt(:'admin_pw', gen_salt('bf', 10)), now() FROM u)
INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM u;

WITH u AS (INSERT INTO users (username, email, active) VALUES ('staff_e2e', 'staff_e2e@example.test', TRUE) RETURNING id),
c AS (INSERT INTO user_credentials (user_id, password_hash, password_changed_at)
      SELECT id, crypt(:'staff_pw', gen_salt('bf', 10)), now() FROM u)
INSERT INTO user_roles (user_id, role) SELECT id, 'STAFF' FROM u;

-- Cuenta inactiva (login debe devolver 401)
WITH u AS (INSERT INTO users (username, email, active) VALUES ('inactive_e2e', 'inactive_e2e@example.test', FALSE) RETURNING id),
c AS (INSERT INTO user_credentials (user_id, password_hash, password_changed_at)
      SELECT id, crypt(:'aux_pw', gen_salt('bf', 10)), now() FROM u)
INSERT INTO user_roles (user_id, role) SELECT id, 'USER' FROM u;

-- Cuenta activa que se desactiva DURANTE la prueba TC-AUTH-013b
WITH u AS (INSERT INTO users (username, email, active) VALUES ('deact_e2e', 'deact_e2e@example.test', TRUE) RETURNING id),
c AS (INSERT INTO user_credentials (user_id, password_hash, password_changed_at)
      SELECT id, crypt(:'aux_pw', gen_salt('bf', 10)), now() FROM u)
INSERT INTO user_roles (user_id, role) SELECT id, 'USER' FROM u;

-- STAFF cuyo rol se revoca DURANTE la prueba TC-SEC-017
WITH u AS (INSERT INTO users (username, email, active) VALUES ('revoke_e2e', 'revoke_e2e@example.test', TRUE) RETURNING id),
c AS (INSERT INTO user_credentials (user_id, password_hash, password_changed_at)
      SELECT id, crypt(:'aux_pw', gen_salt('bf', 10)), now() FROM u)
INSERT INTO user_roles (user_id, role) SELECT id, 'STAFF' FROM u;

-- 2) Venue activo. Artistas: V2 ya inserta 'Solar Beat' (id 1), 'Neon Waves', etc.
INSERT INTO venues (code, name, city, capacity, active) VALUES ('VEN-E2E-01', 'Venue E2E', 'Barranquilla', 500, TRUE);

-- 3) Eventos (fechas relativas a hoy para no caducar)
INSERT INTO events (event_code, name, description, category, status, event_date, minimum_age, venue_id)
SELECT v.code, v.name, 'fixture E2E', 'MUSIC', v.status, CURRENT_DATE + 365, v.min_age, ve.id
FROM (VALUES
  ('EVT-PUB-E2E',     'Evento publicado E2E',   'PUBLISHED', 0),
  ('EVT-DRAFT-E2E',   'Evento borrador E2E',    'DRAFT',     0),
  ('EVT-TOPUB-E2E',   'Evento por publicar E2E','DRAFT',     0),
  ('EVT-SOLDOUT-E2E', 'Evento agotado E2E',     'SOLD_OUT',  0),
  ('EVT-AGE-E2E',     'Evento solo mayores',    'PUBLISHED', 99)
) AS v(code, name, status, min_age)
CROSS JOIN (SELECT id FROM venues WHERE code = 'VEN-E2E-01') AS ve;
