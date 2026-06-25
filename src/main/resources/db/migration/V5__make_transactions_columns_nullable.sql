ALTER TABLE transactions ALTER COLUMN source_user_id DROP NOT NULL;
ALTER TABLE transactions ALTER COLUMN destination_user_id DROP NOT NULL;
ALTER TABLE transactions ALTER COLUMN source_account_number DROP NOT NULL;
ALTER TABLE transactions ALTER COLUMN source_agency DROP NOT NULL;
ALTER TABLE transactions ALTER COLUMN destination_account_number DROP NOT NULL;
ALTER TABLE transactions ALTER COLUMN destination_agency DROP NOT NULL;