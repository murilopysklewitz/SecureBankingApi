CREATE TABLE refresh_tokens(
    id UUID PRIMARY KEY,
    user_id NOT NULL,
    revoked boolean NOT NULL,
    token VARCHAR NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_user_id ON refresh_tokens(user_id);
