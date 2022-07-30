-- Harvested from a mod-calendar 1.12.0
-- C is a correct/normal calendar
-- B is a calendar with completely invalid regular_hours JSON
-- 0 is a calendar with improper start/end dates
INSERT INTO
  "test_mod_calendar"."openings" ("id", "jsonb", "creation_date", "created_by")
VALUES
  (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '{"id": "cccccccc-cccc-cccc-cccc-cccccccccccc", "name": "replay quake aloft routine", "endDate": "2021-01-04T00:00:00.000+00:00", "startDate": "2021-01-01T00:00:00.000+00:00", "exceptional": true, "servicePointId": "55555555-5555-5555-5555-555555555555"}',
    NULL,
    NULL
  ),
  (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '{"id": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "name": "comic sublime upscale utilize", "endDate": "2021-09-22T00:00:00.000+00:00", "startDate": "2021-05-01T00:00:00.000+00:00", "exceptional": false, "servicePointId": "00000000-0000-0000-0000-000000000000"}',
    NULL,
    NULL
  ),
  (
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    '{"id": "ffffffff-ffff-ffff-ffff-ffffffffffff", "name": "comic sublime upscale utilize", "endDate": "invalid", "startDate": "2021-03-16T00:00:00.000+00:00", "exceptional": true, "servicePointId": "00000000-0000-0000-0000-000000000000"}',
    NULL,
    NULL
  ),
  (
    '00000000-0000-0000-0000-000000000000',
    '{"id": "00000000-0000-0000-0000-000000000000", "name": "replay quake aloft routine", "endDate": "2021-01-22T00:00:00.000+00:00", "startDate": "2021-07-04T00:00:00.000+00:00", "exceptional": true, "servicePointId": "00000000-0000-0000-0000-000000000000"}',
    NULL,
    NULL
  );

INSERT INTO
  "test_mod_calendar"."regular_hours" ("id", "jsonb", "creation_date", "created_by")
VALUES
  (
    '59a398b0-ea5c-4c44-9d5e-ccaa5f3d914e',
    '{"id": "59a398b0-ea5c-4c44-9d5e-ccaa5f3d914e", "openingId": "cccccccc-cccc-cccc-cccc-cccccccccccc", "openingDays": [{"openingDay": {"open": true, "allDay": false, "exceptional": true, "openingHour": [{"endTime": "14:59", "startTime": "04:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    'f3ed575b-3cae-45d7-b31e-b5d766a5dabf',
    '{"id": "f3ed575b-3cae-45d7-b31e-b5d766a5dabf", "openingId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "openingDays": "invalid"}',
    NULL,
    NULL
  ),
  (
    '8df90aa8-a0de-4022-b353-bb69f80ac80d',
    '{"id": "8df90aa8-a0de-4022-b353-bb69f80ac80d", "openingId": "ffffffff-ffff-ffff-ffff-ffffffffffff", "openingDays": [{"openingDay": {"open": true, "allDay": true, "exceptional": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    '71f194d5-98ee-417c-95f4-39ccb99c33f9',
    '{"id": "71f194d5-98ee-417c-95f4-39ccb99c33f9", "openingId": "00000000-0000-0000-0000-000000000000", "openingDays": [{"openingDay": {"open": false, "allDay": true, "exceptional": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  );
