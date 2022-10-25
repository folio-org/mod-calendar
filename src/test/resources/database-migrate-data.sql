-- Harvested from a mod-calendar 1.12.0 using the contents of AbstractExistingCalendarTest
INSERT INTO
  "test_mod_calendar"."openings" ("id", "jsonb", "creation_date", "created_by")
VALUES
  (
    'cccccccc-cccc-4ccc-8ccc-cccccccccccc',
    '{"id": "cccccccc-cccc-4ccc-8ccc-cccccccccccc", "name": "replay quake aloft routine", "endDate": "2021-01-04T00:00:00.000+00:00", "startDate": "2021-01-01T00:00:00.000+00:00", "exceptional": true, "servicePointId": "55555555-5555-5555-5555-555555555555"}',
    NULL,
    NULL
  ),
  (
    'dddddddd-dddd-4ddd-8ddd-dddddddddddd',
    '{"id": "dddddddd-dddd-4ddd-8ddd-dddddddddddd", "name": "supplier grouped bride lazily", "endDate": "2021-09-22T00:00:00.000+00:00", "startDate": "2021-05-01T00:00:00.000+00:00", "exceptional": false, "servicePointId": "11111111-1111-1111-1111-111111111111"}',
    NULL,
    NULL
  ),
  (
    'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
    '{"id": "bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb", "name": "comic sublime upscale utilize", "endDate": "2021-09-22T00:00:00.000+00:00", "startDate": "2021-05-01T00:00:00.000+00:00", "exceptional": false, "servicePointId": "00000000-0000-4000-8000-000000000000"}',
    NULL,
    NULL
  ),
  (
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
    '{"id": "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa", "name": "sectional proving blanching deputy", "endDate": "2021-04-30T00:00:00.000+00:00", "startDate": "2021-01-01T00:00:00.000+00:00", "exceptional": false, "servicePointId": "00000000-0000-4000-8000-000000000000"}',
    NULL,
    NULL
  ),
  (
    'ffffffff-ffff-4fff-8fff-ffffffffffff',
    '{"id": "ffffffff-ffff-4fff-8fff-ffffffffffff", "name": "comic sublime upscale utilize", "endDate": "2021-04-30T00:00:00.000+00:00", "startDate": "2021-03-16T00:00:00.000+00:00", "exceptional": true, "servicePointId": "00000000-0000-4000-8000-000000000000"}',
    NULL,
    NULL
  ),
  (
    '00000000-0000-4000-8000-000000000000',
    '{"id": "00000000-0000-4000-8000-000000000000", "name": "replay quake aloft routine", "endDate": "2021-09-22T00:00:00.000+00:00", "startDate": "2021-07-04T00:00:00.000+00:00", "exceptional": true, "servicePointId": "00000000-0000-4000-8000-000000000000"}',
    NULL,
    NULL
  );

INSERT INTO
  "test_mod_calendar"."regular_hours" ("id", "jsonb", "creation_date", "created_by")
VALUES
  (
    '59a398b0-ea5c-4c44-9d5e-ccaa5f3d914e',
    '{"id": "59a398b0-ea5c-4c44-9d5e-ccaa5f3d914e", "openingId": "cccccccc-cccc-4ccc-8ccc-cccccccccccc", "openingDays": [{"openingDay": {"open": true, "allDay": false, "exceptional": true, "openingHour": [{"endTime": "14:59", "startTime": "04:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    'f3ed575b-3cae-45d7-b31e-b5d766a5dabf',
    '{"id": "f3ed575b-3cae-45d7-b31e-b5d766a5dabf", "openingId": "bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb", "openingDays": [{"weekdays": {"day": "MONDAY"}, "openingDay": {"open": true, "allDay": false, "exceptional": false, "openingHour": [{"endTime": "12:30", "startTime": "00:00"}, {"endTime": "23:59", "startTime": "23:00"}]}}, {"weekdays": {"day": "THURSDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    '41c3367a-a6a8-42aa-8f59-168e93b923be',
    '{"id": "41c3367a-a6a8-42aa-8f59-168e93b923be", "openingId": "dddddddd-dddd-4ddd-8ddd-dddddddddddd", "openingDays": [{"weekdays": {"day": "MONDAY"}, "openingDay": {"open": true, "allDay": false, "exceptional": false, "openingHour": [{"endTime": "12:30", "startTime": "00:00"}, {"endTime": "23:59", "startTime": "23:00"}]}}, {"weekdays": {"day": "THURSDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    '47f71658-f592-4197-8386-d551a5a5a8aa',
    '{"id": "47f71658-f592-4197-8386-d551a5a5a8aa", "openingId": "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa", "openingDays": [{"weekdays": {"day": "SUNDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "MONDAY"}, "openingDay": {"open": true, "allDay": false, "exceptional": false, "openingHour": [{"endTime": "14:59", "startTime": "04:00"}]}}, {"weekdays": {"day": "TUESDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "WEDNESDAY"}, "openingDay": {"open": true, "allDay": false, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "23:00"}]}}, {"weekdays": {"day": "THURSDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "FRIDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "SATURDAY"}, "openingDay": {"open": true, "allDay": true, "exceptional": false, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    '8df90aa8-a0de-4022-b353-bb69f80ac80d',
    '{"id": "8df90aa8-a0de-4022-b353-bb69f80ac80d", "openingId": "ffffffff-ffff-4fff-8fff-ffffffffffff", "openingDays": [{"openingDay": {"open": true, "allDay": true, "exceptional": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  ),
  (
    '71f194d5-98ee-417c-95f4-39ccb99c33f9',
    '{"id": "71f194d5-98ee-417c-95f4-39ccb99c33f9", "openingId": "00000000-0000-4000-8000-000000000000", "openingDays": [{"openingDay": {"open": false, "allDay": true, "exceptional": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL,
    NULL
  );