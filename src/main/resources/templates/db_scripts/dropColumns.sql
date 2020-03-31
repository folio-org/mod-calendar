ALTER TABLEaudit_actual_opening_hours DROP COLUMN IF EXISTS created_date;
ALTER TABLE audit_actual_opening_hours DROP COLUMN IF EXISTS operation;
ALTER TABLE audit_actual_opening_hours DROP COLUMN IF EXISTS orig_id;

ALTER TABLE audit_openings DROP COLUMN IF EXISTS created_date;
ALTER TABLE audit_openings DROP COLUMN IF EXISTS operation;
ALTER TABLE audit_openings DROP COLUMN IF EXISTS orig_id;

ALTER TABLE audit_regular_hours	DROP COLUMN IF EXISTS created_date;
ALTER TABLE audit_regular_hours DROP COLUMN IF EXISTS operation;
ALTER TABLE audit_regular_hours DROP COLUMN IF EXISTS orig_id;

ALTER TABLE audit_exceptions DROP COLUMN IF EXISTS created_date;
ALTER TABLE audit_exceptions DROP COLUMN IF EXISTS operation;
ALTER TABLE audit_exceptions DROP COLUMN IF EXISTS orig_id;

ALTER TABLE audit_exceptional_hours DROP COLUMN IF EXISTS created_date;
ALTER TABLE audit_exceptional_hours DROP COLUMN IF EXISTS operation;
ALTER TABLE audit_exceptional_hours DROP COLUMN IF EXISTS orig_id;
