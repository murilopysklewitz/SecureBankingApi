CREATE TABLE refresh_tokens(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    revoked boolean NOT NULL,
    token VARCHAR NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);