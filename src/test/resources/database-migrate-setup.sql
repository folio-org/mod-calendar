-- Harvested from a mod-calendar 1.12.0 using the contents of /scripts/test-calendars/
-- Indexes and foreign keys removed for simplicity, as well as certan internal/audit tables and data
DROP SCHEMA IF EXISTS "test_mod_calendar" CASCADE;

CREATE SCHEMA "test_mod_calendar";

CREATE TABLE "test_mod_calendar"."actual_opening_hours" (
  "id" uuid NOT NULL,
  "jsonb" jsonb NOT NULL,
  "creation_date" timestamp,
  "created_by" text,
  CONSTRAINT "actual_opening_hours_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "test_mod_calendar"."exceptional_hours" (
  "id" uuid NOT NULL,
  "jsonb" jsonb NOT NULL,
  "creation_date" timestamp,
  "created_by" text,
  CONSTRAINT "exceptional_hours_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "test_mod_calendar"."exceptions" (
  "id" uuid NOT NULL,
  "jsonb" jsonb NOT NULL,
  "creation_date" timestamp,
  "created_by" text,
  CONSTRAINT "exceptions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "test_mod_calendar"."openings" (
  "id" uuid NOT NULL,
  "jsonb" jsonb NOT NULL,
  "creation_date" timestamp,
  "created_by" text,
  CONSTRAINT "openings_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "test_mod_calendar"."regular_hours" (
  "id" uuid NOT NULL,
  "jsonb" jsonb NOT NULL,
  "creation_date" timestamp,
  "created_by" text,
  CONSTRAINT "regular_hours_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "test_mod_calendar"."rmb_internal" (
  "id" integer DEFAULT nextval('rmb_internal_id_seq') NOT NULL,
  "jsonb" jsonb NOT NULL,
  CONSTRAINT "rmb_internal_pkey" PRIMARY KEY ("id")
);