-- liquibase formatted sql
-- changeset university-of-alabama/ncovercash:0010-initial-01-create-weekday-type
CREATE TYPE "weekday" AS ENUM (
  'sunday',
  'monday',
  'tuesday',
  'wednesday',
  'thursday',
  'friday',
  'saturday'
);

-- rollback DROP TYPE "weekday";