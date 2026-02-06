CREATE TABLE accounts(
    id UUID PRIMARY KEY NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    agency VARCHAR(10) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_accounts_user FOREIGN KEY (userId) REFERENCES users(id) NOT NULL,
    balance INTEGER NOT NULL DEFAULT 0,
    account_type VARCHAR(20) NOT NULL,
    account_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_account_type ON accounts(account_type);
CREATE INDEX idx_account_status ON accounts(account_status);