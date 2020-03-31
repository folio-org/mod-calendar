ALTER TABLE ${myuniversity}_${mymodule}.audit_actual_opening_hours
	DROP COLUMN IF EXISTS created_date;
ALTER TABLE ${myuniversity}_${mymodule}.audit_actual_opening_hours
	DROP COLUMN IF EXISTS operation;
ALTER TABLE ${myuniversity}_${mymodule}.audit_actual_opening_hours
	DROP COLUMN IF EXISTS orig_id;

ALTER TABLE ${myuniversity}_${mymodule}.audit_openings
	DROP COLUMN IF EXISTS created_date;
ALTER TABLE ${myuniversity}_${mymodule}.audit_openings
	DROP COLUMN IF EXISTS operation;
ALTER TABLE ${myuniversity}_${mymodule}.audit_openings
	DROP COLUMN IF EXISTS orig_id;

ALTER TABLE ${myuniversity}_${mymodule}.audit_regular_hours
	DROP COLUMN IF EXISTS created_date;
ALTER TABLE ${myuniversity}_${mymodule}.audit_regular_hours
	DROP COLUMN IF EXISTS operation;
ALTER TABLE ${myuniversity}_${mymodule}.audit_regular_hours
	DROP COLUMN IF EXISTS orig_id;

ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptions
	DROP COLUMN IF EXISTS created_date;
ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptions
	DROP COLUMN IF EXISTS operation;
ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptions
	DROP COLUMN IF EXISTS orig_id;

ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptional_hours
	DROP COLUMN IF EXISTS created_date;
ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptional_hours
	DROP COLUMN IF EXISTS operation;
ALTER TABLE ${myuniversity}_${mymodule}.audit_exceptional_hours
	DROP COLUMN IF EXISTS orig_id;
