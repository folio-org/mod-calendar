# Pre-Commit Hooks

The usage of pre-commit hooks is extremely helpful in order to ensure every commit works as expected
and meets guidelines.

This is touched on in the [style doc](styling.md).

The current script is in [scripts/pre-commit.sh](/scripts/pre-commit.sh) and may be installed with:

```sh
ln -s scripts/pre-commit.sh .git/hooks/pre-commit
```

This method of installation will keep your hook up to date with any changes from this repo.

In order to use this, all the NPM modules in [styling.md](styling.md) must be installed as well as
`swagger-cli` (to test the schema). Additionally, `liquibase` will need to be installed.

This script consists of a few main parts:

## Prettier

The code formatter [Prettier](https://prettier.io/) is ran on changed code files using the main
`.prettierrc`.  More information is in [styling.md](styling.md)

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
