CREATE TABLE accounts(
    id UUID PRIMARY KEY NOT NULL,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    agency VARCHAR(10) NOT NULL,
    user_id UUID NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    account_type VARCHAR(20) NOT NULL,
    account_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_user_id ON accounts(user_id);
CREATE INDEX idx_account_status ON accounts(account_status);