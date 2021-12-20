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
