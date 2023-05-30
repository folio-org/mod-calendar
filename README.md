# mod-calendar

Copyright (C) 2017-2023 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0. See the file
"[LICENSE](LICENSE)" for more information.

- [Introduction](#introduction)
- [Compiling](#compiling)
- [Running the module locally](#running-the-module-locally)
- [Testing the module](#testing-the-module)
- [Docker/Deploying the module](#dockerdeploying-the-module)
  - [Module descriptor](#module-descriptor)
  - [Tenant parameters](#tenant-parameters)
  - [Environment variables](#environment-variables)
    - [Integration Tests](#integration-tests)
- [API documentation](#api-documentation)
- [Internationalization](#internationalization)
- [Code analysis](#code-analysis)
- [Issue tracking](#issue-tracking)
- [Download and configuration](#download-and-configuration)

## Introduction

This module provides calendar functionalities for FOLIO systems, allowing service points to maintain
hours of operation.

## Compiling

To compile, simply run:

```sh
mvn install
```

Note: _you will need Docker installed and running, for the integration tests that Maven runs as part
of the installation. If you wish to skip this, add `-DskipTests` to the command._

## Running the module locally

To run the module locally, you can create a JAR with:

```sh
mvn package
```

Once the module has been packaged into a JAR, you can run it on the command line (with the
appropriate [environment variables](#environment-variables)):

```sh
java -jar target/mod-calendar-*.jar
```

For developers with VS Code, a [`launch.json`](.vscode/launch.json) is provided which allows the
built-in run features to launch and debug the module within the IDE.

## Testing the module

To test the module locally, you can run the `test` Maven command:

```sh
mvn clean test jacoco:report
```

To do this, you must be running Docker. For more information, see the [test docs](docs/testing.md).

## Docker/Deploying the module

Please see the [install](docs/install.md) docs for information on building a docker container and
registering it with/deploying it to Okapi.

### Module descriptor

See the built `target/ModuleDescriptor.json` or the template
`descriptors/ModuleDescriptor-template.json` for the interfaces that this module requires and
provides, the permissions, and the additional module metadata. An explanation of the contents of
this file may be found in the [build process](docs/build-process.md) document.

### Tenant parameters

When deploying to a module through Okapi, the following parameters are available:

| Name          | Default Value | Description                                                                                                                                                                                                                                                                                                                   |
| ------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| loadReference | `"false"`     | Does not do anything, however, is supported per the [Okapi specification](https://github.com/folio-org/okapi/blob/master/doc/guide.md)                                                                                                                                                                                        |
| loadSample    | `"false"`     | If `"true"`, a series of sample calendars will be added representing examples of exceptions and openings across multiple calendars and service points. This will use the same service points loaded by [mod-inventory-storage](https://github.com/folio-org/mod-inventory-storage/tree/master/reference-data/service-points). |

### Environment variables

See `.env.sample` for example values.

| Name         | Default Value              | Description                                                                           |
| ------------ | -------------------------- | ------------------------------------------------------------------------------------- |
| JAVA_OPTIONS | `XX:MaxRAMPercentage=66.0` | Sets `java` command-line options. Default is to set the maximum ram percentage to 66% |
| DB_HOST      | `localhost`                | Postgres hostname                                                                     |
| DB_PORT      | `5432`                     | Postgres port                                                                         |
| DB_USERNAME  | `folio_admin`              | Postgres username                                                                     |
| DB_PASSWORD  | `folio_admin`              | Postgres password                                                                     |
| DB_DATABASE  | `okapi_modules`            | Postgres database name                                                                |

#### Integration Tests

Integration tests have special environment variables that control whether or not API requests are
routed through a proxy. A proxy can be helpful for debugging all of the API requests associated with
integration tests, providing a separate GUI interface that may provide more details.

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

## Internationalization

This module is locale-aware, based on the `Accept-Language` header. Translations are stored in the
[translations](translations/mod-calendar/) folder.

## Code analysis

[SonarQube analysis](https://sonarcloud.io/dashboard?id=org.folio%3Amod-calendar).

## Issue tracking

See [MODCAL](https://issues.folio.org/projects/MODCAL) in the
[FOLIO issue tracker](https://issues.folio.org/).

## Download and configuration

The built artifacts for this module are available. See
[configuration](https://dev.folio.org/download/artifacts) for repository access, and the
[Docker image](https://hub.docker.com/r/folioorg/mod-calendar/).
