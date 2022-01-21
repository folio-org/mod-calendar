INSERT INTO "regular_hours" (
  "id", "jsonb", "creation_date", "created_by"
)
VALUES
  (
    'd6f6d367-9bc2-4339-8eee-9ab12956be7e',
    '{"id": "d6f6d367-9bc2-4339-8eee-9ab12956be7e", "openingId": "00000000-0000-0000-0000-000000000000", "openingDays": [{"openingDay": {"open": false, "allDay": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}]}',
    NULL, NULL
  ),
  (
    '8522afa4-3ecd-4ebc-ae19-7ce950ec45b3',
    '{"id": "8522afa4-3ecd-4ebc-ae19-7ce950ec45b3", "openingId": "11111111-1111-1111-1111-111111111111", "openingDays": [{"openingDay": {"open": true, "openingHour": [{"endTime": "23:00", "startTime": "02:00"}]}}]}',
    NULL, NULL
  ),
  (
    '8d25d5d1-bc78-4876-a44d-7e901c49f579',
    '{"id": "8d25d5d1-bc78-4876-a44d-7e901c49f579", "openingId": "22222222-2222-2222-2222-222222222222", "openingDays": [{"weekdays": {"day": "MONDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "TUESDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "WEDNESDAY"}, "openingDay": {"open": true, "allDay": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "THURSDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "FRIDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "SATURDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "12:00", "startTime": "07:00"}, {"endTime": "20:00", "startTime": "13:30"}]}}]}',
    NULL, NULL
  ),
  (
    '4a22c6e4-bd2c-48f6-8412-197eb38ad2b5',
    '{"id": "4a22c6e4-bd2c-48f6-8412-197eb38ad2b5", "openingId": "33333333-3333-3333-3333-333333333333", "openingDays": [{"weekdays": {"day": "MONDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "TUESDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "WEDNESDAY"}, "openingDay": {"open": true, "allDay": true, "openingHour": [{"endTime": "23:59", "startTime": "00:00"}]}}, {"weekdays": {"day": "THURSDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "FRIDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "17:30", "startTime": "07:30"}]}}, {"weekdays": {"day": "SATURDAY"}, "openingDay": {"open": true, "allDay": false, "openingHour": [{"endTime": "12:00", "startTime": "07:00"}, {"endTime": "20:00", "startTime": "13:30"}]}}]}',
    NULL, NULL
  );
INSERT INTO "openings" (
  "id", "jsonb", "creation_date", "created_by"
)
VALUES
  (
    '00000000-0000-0000-0000-000000000000',
    '{"id": "00000000-0000-0000-0000-000000000000", "name": "Christmas Closure", "endDate": "2021-12-25T00:00:00.000+00:00", "startDate": "2021-12-24T00:00:00.000+00:00", "exceptional": true, "servicePointId": "3a40852d-49fd-4df2-a1f9-6e2641a6e91f"}',
    NULL, NULL
  ),
  (
    '11111111-1111-1111-1111-111111111111',
    '{"id": "11111111-1111-1111-1111-111111111111", "name": "Finals Extra Opening", "endDate": "2021-12-17T00:00:00.000+00:00", "startDate": "2021-12-11T00:00:00.000+00:00", "exceptional": true, "servicePointId": "3a40852d-49fd-4df2-a1f9-6e2641a6e91f"}',
    NULL, NULL
  ),
  (
    '22222222-2222-2222-2222-222222222222',
    '{"id": "22222222-2222-2222-2222-222222222222", "name": "November", "endDate": "2021-11-30T00:00:00.000+00:00", "startDate": "2021-11-01T00:00:00.000+00:00", "exceptional": false, "servicePointId": "3a40852d-49fd-4df2-a1f9-6e2641a6e91f"}',
    NULL, NULL
  ),
  (
    '33333333-3333-3333-3333-333333333333',
    '{"id": "33333333-3333-3333-3333-333333333333", "name": "December 5 through 31", "endDate": "2021-12-31T00:00:00.000+00:00", "startDate": "2021-12-05T00:00:00.000+00:00", "exceptional": false, "servicePointId": "3a40852d-49fd-4df2-a1f9-6e2641a6e91f"}',
    NULL, NULL
  );
