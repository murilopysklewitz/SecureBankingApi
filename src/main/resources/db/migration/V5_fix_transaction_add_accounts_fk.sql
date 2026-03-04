ALTER TABLE transactions ADD COLUMN source_account_id UUID;
ALTER TABLE transactions ADD COLUMN destination_account_id UUID;

UPDATE transactions t
SET source_account_id = (
    SELECT a.id
    FROM accounts a
    WHERE a.user_id = t.source_user_id
      AND a.account_number = t.source_account_number
      AND a.agency = t.source_agency
    LIMIT 1
    );

UPDATE transactions t
SET destination_account_id = (
    SELECT a.id
    FROM accounts a
    WHERE a.user_id = t.destination_user_id
      AND a.account_number = t.destination_account_number
      AND a.agency = t.destination_agency
    LIMIT 1
    );

ALTER TABLE transactions ALTER COLUMN source_account_id SET NOT NULL;
ALTER TABLE transactions ALTER COLUMN destination_account_id SET NOT NULL;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_source_account
        FOREIGN KEY (source_account_id)
            REFERENCES accounts(id)
            ON DELETE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_destination_account
        FOREIGN KEY (destination_account_id)
            REFERENCES accounts(id)
            ON DELETE RESTRICT;

CREATE INDEX idx_transactions_source_account_id ON transactions(source_account_id);
CREATE INDEX idx_transactions_destination_account_id ON transactions(destination_account_id);
CREATE INDEX idx_transactions_source_account_created ON transactions(source_account_id, created_at DESC);
CREATE INDEX idx_transactions_destination_account_created ON transactions(destination_account_id, created_at DESC);