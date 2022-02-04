# Test Calendars

This loads a series of four calendars into the application.

You may manually specify an `OKAPI_HOST` and `OKAPI_TOKEN` through environment variables, if not
testing locally (defaults to `http://localhost:8080`). For example:

```sh
OKAPI_HOST=https://folio-testing-okapi.dev.folio.org \
OKAPI_TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6Ijk1NTM2ODc1LWNmNGYtNWZjMC04NjNjLTVkYTAwYWIzMzBkNCIsImlhdCI6MTY0MDkyMjEyMywidGVuYW50IjoiZGlrdSJ9.8AAqx6kHVL777wJTGBUyUGLGShLfX6QMnE_SAMPLE \
./install.sh
```

With PowerShell, the process is a little more verbose: (note that this will set the variables for
the remainder of your terminal session)

```posh
${env:OKAPI_HOST}='http://localhost:9130';
${env:OKAPI_TOKEN}='eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6IjVlYjI2ZmI2LTE4NTYtNWIxNy1hM2ZhLWM4ZGI2OGIzNWZmYiIsImlhdCI6MTY0MjcxNzQxMiwidGVuYW50IjoiZGlrdSJ9.SAMPLE-b9S8';
sh ./install.sh
```
