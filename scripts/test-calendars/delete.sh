#!/bin/bash

# You may manually specify an OKAPI_HOST and OKAPI_TOKEN through environment variables, if not testing locally.
# For example:
#   OKAPI_HOST=https://folio-testing-okapi.dev.folio.org \
#   OKAPI_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6Ijk1NTM2ODc1LWNmNGYtNWZjMC04NjNjLTVkYTAwYWIzMzBkNCIsImlhdCI6MTY0MDkyMjEyMywidGVuYW50IjoiZGlrdSJ9.8AAqx6kHVL777wJTGBUyUGLGShLfX6QMnE_fX0kY5h0 \
#   ./install.sh

if [ -z "$OKAPI_HOST" ]; then
  OKAPI_HOST="http://localhost:8081"
fi

for uuid in 00000000-0000-0000-0000-000000000000 11111111-1111-1111-1111-111111111111 22222222-2222-2222-2222-222222222222 33333333-3333-3333-3333-333333333333
do
  echo "Deleting UUID "$uuid
  curl -L --fail --show-error -X DELETE \
    -H "X-Okapi-Tenant: diku" \
    -H "X-Okapi-Token: $OKAPI_TOKEN" \
    $OKAPI_HOST/calendar/periods/3a40852d-49fd-4df2-a1f9-6e2641a6e91f/period/$uuid
done

echo "Finished"
