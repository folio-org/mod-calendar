#!/bin/bash

# You may manually specify an OKAPI_HOST and OKAPI_TOKEN through environment variables, if not testing locally.
# For example:
#   OKAPI_HOST=folio-testing-okapi.dev.folio.org \
#   OKAPI_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6Ijk1NTM2ODc1LWNmNGYtNWZjMC04NjNjLTVkYTAwYWIzMzBkNCIsImlhdCI6MTY0MDkyMjEyMywidGVuYW50IjoiZGlrdSJ9.8AAqx6kHVL777wJTGBUyUGLGShLfX6QMnE_fX0kY5h0 \
#   ./install.sh

if [ -z "$OKAPI_HOST" ]; then
  OKAPI_HOST="localhost:8080"
fi

for file in *.json; do
  echo "Adding calendar: $file"
  curl --fail --silent --show-error -o /dev/null -X POST \
    -H "X-Okapi-Tenant: diku" \
    -H "X-Okapi-Token: $OKAPI_TOKEN" \
    -H "Content-type: application/json" \
    -d @$file \
    http://$OKAPI_HOST/calendar/periods/3a40852d-49fd-4df2-a1f9-6e2641a6e91f/period
done

echo "Finished"
