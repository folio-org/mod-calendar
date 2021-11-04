CREATE TYPE "weekday" AS (
  'sunday',
  'monday',
  'tuesday',
  'wednesday',
  'thursday',
  'friday',
  'saturday'
);

CREATE TABLE "calendar_v2"."calendars" (
  "id" uuid NOT NULL,
  "name" text NOT NULL,
  "start_date" date NOT NULL,
  "end_date" date NOT NULL,
  CONSTRAINT "calendar_id" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE INDEX "calendar_start_end_date" ON "calendar_v2"."calendars" USING btree ("start_date", "end_date");

COMMENT ON TABLE "calendar_v2"."calendars" IS 'Stores calendar metadata';
COMMENT ON COLUMN "calendar_v2"."calendars"."start_date" IS 'no TZ';
COMMENT ON COLUMN "calendar_v2"."calendars"."end_date" IS 'no TZ';

CREATE TABLE "calendar_v2"."exception_hours" (
  "id" uuid NOT NULL,
  "exception_id" uuid NOT NULL,
  "start_date" date NOT NULL,
  "end_date" date NOT NULL,
  "open_start_time" time without time zone,
  "open_end_time" time without time zone,
  CONSTRAINT "exception_hours_id" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE INDEX "exception_hours_exception_id" ON "calendar_v2"."exception_hours" USING btree ("exception_id");

COMMENT ON TABLE "calendar_v2"."exception_hours" IS 'Absolute hours for each exception.  Nullity of open_?_time controls if this designates closure or opening.';
COMMENT ON COLUMN "calendar_v2"."exception_hours"."start_date" IS 'No TZ';
COMMENT ON COLUMN "calendar_v2"."exception_hours"."end_date" IS 'No TZ';
COMMENT ON COLUMN "calendar_v2"."exception_hours"."open_start_time" IS 'No TZ';
COMMENT ON COLUMN "calendar_v2"."exception_hours"."open_end_time" IS 'No TZ';


CREATE TABLE "calendar_v2"."exceptions" (
  "id" uuid NOT NULL,
  "calendar_id" uuid NOT NULL,
  "start_date" date NOT NULL,
  "end_date" date NOT NULL,
  CONSTRAINT "exceptions_id" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE INDEX "exceptions_calendar_id" ON "calendar_v2"."exceptions" USING btree ("calendar_id");
CREATE INDEX "exceptions_start_date_end_date" ON "calendar_v2"."exceptions" USING btree ("start_date", "end_date");

COMMENT ON TABLE "calendar_v2"."exceptions" IS 'Metadata for each exception';

CREATE TABLE "calendar_v2"."normal_hours" (
  "id" uuid NOT NULL,
  "calendar_id" uuid NOT NULL,
  "start_day" weekday NOT NULL,
  "start_time" time without time zone NOT NULL,
  "end_day" weekday NOT NULL,
  "end_time" time without time zone NOT NULL,
  CONSTRAINT "normal_hours_id" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE INDEX "normal_hours_calendar_id" ON "calendar_v2"."normal_hours" USING btree ("calendar_id");

COMMENT ON TABLE "calendar_v2"."normal_hours" IS 'Holds relative opening information for each calendar';
COMMENT ON COLUMN "calendar_v2"."normal_hours"."start_day" IS 'no TZ';
COMMENT ON COLUMN "calendar_v2"."normal_hours"."end_day" IS 'no TZ';

CREATE TABLE "calendar_v2"."service_point_calendars" (
  "service_point_id" uuid NOT NULL,
  "calendar_id" uuid NOT NULL
) WITH (oids = false);

CREATE INDEX "service_point_calendars_calendar_id" ON "calendar_v2"."service_point_calendars" USING btree ("calendar_id");
CREATE INDEX "service_point_calendars_service_point_id" ON "calendar_v2"."service_point_calendars" USING btree ("service_point_id");

COMMENT ON TABLE "calendar_v2"."service_point_calendars" IS 'Relates service points to calendars';

-- Foreign Keys
ALTER TABLE ONLY "calendar_v2"."exception_hours" ADD CONSTRAINT "exception_hours_exception_id_fkey" FOREIGN KEY (exception_id) REFERENCES exceptions(id) ON UPDATE CASCADE ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "calendar_v2"."exceptions" ADD CONSTRAINT "exceptions_calendar_id_fkey" FOREIGN KEY (calendar_id) REFERENCES calendars(id) ON UPDATE CASCADE ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "calendar_v2"."normal_hours" ADD CONSTRAINT "normal_hours_calendar_id_fkey" FOREIGN KEY (calendar_id) REFERENCES calendars(id) ON UPDATE CASCADE ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "calendar_v2"."service_point_calendars" ADD CONSTRAINT "service_point_calendars_calendar_id_fkey" FOREIGN KEY (calendar_id) REFERENCES calendars(id) NOT DEFERRABLE;
