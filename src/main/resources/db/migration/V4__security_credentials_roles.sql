-- V4__security_credentials_roles.sql: Credenciales y roles de usuario para PulsePass

CREATE TABLE user_credentials (
    user_id BIGINT PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    password_changed_at TIMESTAMP,
    CONSTRAINT fk_user_credentials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_valid_role CHECK (role IN ('USER', 'STAFF', 'ADMIN'))
);

-- Índices recomendados para optimizar consultas de seguridad
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);