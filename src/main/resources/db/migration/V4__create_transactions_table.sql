CREATE TABLE transactions(
    id UUID PRIMARY KEY NOT NULL ,
    source_user_id UUID NOT NULL,
    source_account_number VARCHAR NOT NULL,
    source_agency VARCHAR NOT NULL,

    destination_user_id UUID NOT NULL,
    destination_account_number VARCHAR NOT NULL ,
    destination_agency VARCHAR NOT NULL ,

    amount NUMERIC(19, 2) NOT NULL ,

    status VARCHAR NOT NULL DEFAULT 'PENDING',
    type VARCHAR NOT NULL ,

    description VARCHAR,

    created_at TIMESTAMP NOT NULL ,

    completed_at TIMESTAMP ,
    CONSTRAINT chk_type CHECK (type IN ('TRANSFER', 'DEPOSIT', 'WITHDRAWAL')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    CONSTRAINT fk_source_user_id FOREIGN KEY (source_user_id) REFERENCES users(id),
    CONSTRAINT  fk_destination_user_id FOREIGN KEY (destination_user_id) references users(id)

);
CREATE INDEX idx_transactions_source_user_id ON transactions(source_user_id);

CREATE INDEX idx_transactions_destination_user_id ON transactions(destination_user_id);

CREATE INDEX idx_transactions_status ON transactions(status);

CREATE INDEX idx_transactions_type ON transactions(type);

CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);


