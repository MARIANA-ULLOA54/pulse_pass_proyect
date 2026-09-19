CREATE TABLE venues (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_venue_code UNIQUE (code),
    CONSTRAINT chk_venue_capacity CHECK (capacity > 0)
);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    event_date DATE NOT NULL,
    minimum_age INTEGER,
    venue_id BIGINT NOT NULL,
    CONSTRAINT uq_event_code UNIQUE (event_code),
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venues (id),
    CONSTRAINT chk_event_category CHECK (category IN ('MUSIC','SPORTS','TECHNOLOGY','EDUCATION','CULTURE','ENTERTAINMENT')),
    CONSTRAINT chk_event_status CHECK (status IN ('DRAFT','PUBLISHED','SOLD_OUT','CANCELLED','FINISHED'))
);
CREATE INDEX idx_events_venue_id ON events (venue_id);
CREATE INDEX idx_events_status ON events (status);

CREATE TABLE artists (
    id BIGSERIAL PRIMARY KEY,
    stage_name VARCHAR(150) NOT NULL,
    country VARCHAR(100),
    genre VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_artist_stage_name UNIQUE (stage_name)
);

CREATE TABLE event_artists (
    event_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    CONSTRAINT pk_event_artists PRIMARY KEY (event_id, artist_id),
    CONSTRAINT fk_ea_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_ea_artist FOREIGN KEY (artist_id) REFERENCES artists (id)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_user_username UNIQUE (username),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(30),
    city VARCHAR(100),
    birth_date DATE,
    user_id BIGINT NOT NULL,
    CONSTRAINT uq_profile_user UNIQUE (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_code VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT uq_ticket_code UNIQUE (ticket_code),
    CONSTRAINT chk_ticket_price CHECK (price >= 0),
    CONSTRAINT chk_ticket_type CHECK (type IN ('GENERAL','VIP','BACKSTAGE','STUDENT')),
    CONSTRAINT chk_ticket_status CHECK (status IN ('RESERVED','PAID','CANCELLED','USED')),
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES events (id)
);
CREATE INDEX idx_tickets_user_id ON tickets (user_id);
CREATE INDEX idx_tickets_event_id ON tickets (event_id);