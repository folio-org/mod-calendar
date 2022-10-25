#!/bin/bash

# See README.md for documentation

if [ -z "$OKAPI_HOST" ]; then
  OKAPI_HOST="http://localhost:8081"
fi

for file in *.json; do
  echo "Adding calendar: $file"
  curl -L --fail --silent --show-error -o /dev/null -X POST \
    -H "X-Okapi-Tenant: diku" \
    -H "X-Okapi-Token: $OKAPI_TOKEN" \
    -H "Content-type: application/json" \
    -d @$file \
    $OKAPI_HOST/calendar/periods/3a40852d-49fd-4df2-a1f9-6e2641a6e91f/period
done

echo "Finished"
