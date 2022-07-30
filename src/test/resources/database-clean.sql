-- This is used to speed up database refreshes, as opposed to a total recreation
TRUNCATE
  TABLE test_mod_calendar.calendars CASCADE;
