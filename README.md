# mod-calendar

Copyright (C) 2017-2022 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0. See the file
"[LICENSE](LICENSE)" for more information.

[JIRA MOD-CAL](https://issues.folio.org/projects/MODCAL)

## Introduction

Module to provide calendar functionalities for FOLIO systems.

## Module Descriptor

See the built `target/ModuleDescriptor.json` or the template
`descriptors/ModuleDescriptor-template.json` for the interfaces that this module requires and
provides, the permissions, and the additional module metadata.

### Tenant Parameters

When deploying to a module through Okapi, the following parameters are available:

| Name          | Default Value | Description                                                                                                                                                                                                                                                                                                                   |
| ------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| loadReference | `"false"`     | Does not do anything, however, is supported per the [Okapi specification](https://github.com/folio-org/okapi/blob/master/doc/guide.md)                                                                                                                                                                                        |
| loadSample    | `"false"`     | If `"true"`, a series of sample calendars will be added representing examples of exceptions and openings across multiple calendars and service points. This will use the same service points loaded by [mod-inventory-storage](https://github.com/folio-org/mod-inventory-storage/tree/master/reference-data/service-points). |

### Environment variables

See `.env.sample` for example values.

| Name            | Default Value              | Description                                                                           |
| --------------- | -------------------------- | ------------------------------------------------------------------------------------- |
| JAVA_OPTIONS    | `XX:MaxRAMPercentage=66.0` | Sets `java` command-line options. Default is to set the maximum ram percentage to 66% |
| DB_HOST         | `localhost`                | Postgres hostname                                                                     |
| DB_PORT         | `5432`                     | Postgres port                                                                         |
| DB_USERNAME     | `folio_admin`              | Postgres username                                                                     |
| DB_PASSWORD     | `folio_admin`              | Postgres password                                                                     |
| DB_DATABASE     | `okapi_modules`            | Postgres database name                                                                |
| DB_QUERYTIMEOUT | `60000`                    | Postgres query time out. Default is 6s                                                |
| DB_CHARSET      | `UTF-8`                    | Postgres charset                                                                      |
| DB_MAXPOOLSIZE  | `5`                        | Postgres max pool size                                                                |

#### Integration Tests

Integration tests have special environment variables that control whether or not API requests are
routed through a proxy:

| Name         | Default Value | Description                                            |
| ------------ | ------------- | ------------------------------------------------------ |
| PROXY_ENABLE | `false`       | If requests should be proxied (`true` or `false` only) |
| PROXY_SCHEME | `http`        | The protocol to use for a proxy                        |
| PROXY_HOST   | `localhost`   | The host to proxy through                              |
| PROXY_PORT   | `8888`        | The port on PROXY_HOST to proxy through                |

Additionally, if `PROXY_ENABLE` is `true`, additional logging-only requests will be sent to a few
endpoints:

- `GET /_/tests/_/database-truncate` every time the database is truncated (after most modifying
  integration tests methods/classes)
- `GET /_/tests/class/method` before every test begins
- `GET /_/tests/_/finish` after every test finishes (successful or otherwise)

These endpoints do not exist (and will correspondingly generate `404` errors), however, will appear
in any proxy logs, making it easy to isolate each test/action.

## API documentation

This module's [API documentation](https://dev.folio.org/reference/api/#mod-calendar).

## Code analysis

[SonarQube analysis](https://sonarcloud.io/dashboard?id=org.folio%3Amod-calendar).

## Download and configuration

The built artifacts for this module are available. See
[configuration](https://dev.folio.org/download/artifacts) for repository access, and the
[Docker image](https://hub.docker.com/r/folioorg/mod-calendar/).
