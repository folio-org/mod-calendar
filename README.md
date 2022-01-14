# mod-calendar

[Kanban](https://github.com/orgs/ualibweb/projects/1)

## Environment variables

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

### Integration Tests

Integration tests have special environment variables that control whether or not API requests are
routed through a proxy:

| Name         | Default Value | Description                                            |
| ------------ | ------------- | ------------------------------------------------------ |
| PROXY_ENABLE | `false`       | If requests should be proxied (`true` or `false` only) |
| PROXY_SCHEME | `http`        | The protocol to use for a proxy                        |
| PROXY_HOST   | `localhost`   | The host to proxy through                              |
| PROXY_PORT   | `8888`        | The port on PROXY_HOST to proxy through                |

Additionally, if `PROXY_ENABLE` is `true`, requests will be sent to `/_/database/truncating` every
time the database is truncated (after most integration tests methods/classes). This endpoint does
not exist (and will correspondingly generate `404` errors), however, will appear in any proxy logs,
making it easy to isolate each test/action.
