#!/bin/bash
set -x

# We cannot pretty Swing Boot's application files as the parser pukes on @
files_to_pretty=$(git diff --cached --diff-filter=d --name-only | egrep '\.(java|md|xml|sql|json|yaml|yml)$' | grep -v 'application.yaml')

if [ -n "$files_to_pretty" ]; then
  set -euo pipefail # if this returns failure, stop
  npx prettier --config .prettierrc --write $files_to_pretty
  set +euo pipefail
fi

api_changes=$(git diff --cached --diff-filter=d --name-only | grep 'src/main/resources/api')

if [ -n "$api_changes" ]; then
  set -euo pipefail # if this returns failure, stop
  # verify the API
  npx swagger-cli validate src/main/resources/api/mod-calendar.yaml
  set +euo pipefail
fi

# Only change staging once everything succeeds
git add -f $files_to_pretty
