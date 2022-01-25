# Test Calendars

## Dynamic Sample Data

The recommended way to get sample data is to install this module on your Okapi tenant using the
`loadSample` parameter.

For example:

```sh
curl -d'[{"id":"mod-foo-1.0.0", "action":"enable"}]' \
  http://localhost:9130/_/proxy/tenants/testlib/install?tenantParameters=loadReference%3Dtrue
```

Or, if running locally outside of Okapi (note that `true` is a string, not a normal boolean):

```sh
curl -d '{"module_to": "mod-foo-1.0.0", "parameters":["loadSample": "true"]}'
```

## Static Sample Data

To load the hardcoded calendars in this directly, follow these instructions:

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
