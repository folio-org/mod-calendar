# mod-calendar
## Environment variables
See `.env.sample`
| Name             | Default Value              | Description                                                            |
|---------------- |-------------------------- |---------------------------------------------------------------------- |
| JAVA\_OPTIONS    | `XX:MaxRAMPercentage=66.0` | Sets Java options. Default is to set the maximum ram percentage to 66% |
| DB\_HOST         | `localhost`                | Postgres hostname                                                      |
| DB\_PORT         | `5432`                     | Postgres port                                                          |
| DB\_USERNAME     | `folio_admin`              | Postgres username                                                      |
| DB\_PASSWORD     | `folio_admin`              | Postgres password                                                      |
| DB\_DATABASE     | `okapi_modules`            | Postgres database name                                                 |
| DB\_QUERYTIMEOUT | `60000`                    | Postgres query time out. Default is 6s                                 |
| DB\_CHARSET      | `UTF-8`                    | Postgres charset                                                       |
| DB\_MAXPOOLSIZE  | `5`                        | Postgres max pool size                                                 |
