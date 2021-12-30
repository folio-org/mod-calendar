#!/bin/bash

for file in *.json; do
  echo "Adding calendar: "$file
  curl --fail --silent --show-error -o /dev/null -X POST \
    -H "X-Okapi-Tenant: diku" \
    -H "Content-type: application/json" \
    -d @$file \
    http://localhost:8080/calendar/periods/00000000-0000-0000-0000-000000000000/period
done

echo "Finished"
