# Pre-Commit Hooks

The usage of pre-commit hooks is extremely helpful in order to ensure every commit works as expected
and meets guidelines.

This is touched on in the [style doc](styling.md).

Here is the current script, to be placed in `.git/hooks/pre-commit`.

```bash
#!/bin/bash
set -x

files_to_pretty=$(git diff --cached --diff-filter=d --name-only | egrep '\.(java|md|xml|sql|json|yaml|yml)$')

if [ -n "$files_to_pretty" ]; then
  set -euo pipefail # if this returns failure, stop
  prettier --config .prettierrc --write $files_to_pretty
  set +euo pipefail
fi

liquibase_changes=$(git diff --cached --diff-filter=d --name-only | grep 'src/main/resources/db')

if [ -n "$liquibase_changes" ]; then
  set -euo pipefail # if this returns failure, stop
  liquibase validate \
    --driver=org.postgresql.Driver \
    --url="jdbc:postgresql://localhost:5432/okapi_modules" \
    --username=okapi --password=okapi25 \
    --changelog-file src/main/resources/db/changelog-master.yaml
  set +euo pipefail
fi

api_changes=$(git diff --cached --diff-filter=d --name-only | grep 'src/main/resources/swagger.api')

if [ -n "$api" ]; then
  set -euo pipefail # if this returns failure, stop
  # verify the API
  swagger-cli validate src/main/resources/swagger.api/mod-calendar.yaml
  set +euo pipefail
fi

# Only change staging once everything succeeds
git add -f $files_to_pretty
```

In order to use this, all the NPM modules in [styling.md](styling.md) must be installed as well as
`swagger-cli` (to test the schema). Additionally, `liquibase` will need to be installed.

This script consists of a few main parts:

## Prettier

The code formatter [Prettier](https://prettier.io/) is ran on changed code files using the main
`.prettierrc`.

## Liquibase

The `liquibase validate` command is used to ensure that any applied changes can actually be
performed against the database (assuming Okapi's PostgreSQL database is on port `5432` with default
credentials). It will also verify rollbacks work as expected, making this idempotent.

## Swagger Validation

This uses [swagger-cli](https://apitools.dev/swagger-cli/) to verify that the OpenAPI schema is up
to specifications.

## Notes

`prettier` and `swagger-cli` are used directly as `npx` is extremely slow on Windows systems.

The bash options `e`,`u`, and `pipefail` are toggled on and off each time one of the actual commands
are ran. This is because, if no matching files are found in the `grep` statements, the script will
prematurely terminate (as the options will cause the script to terminate on any error).
