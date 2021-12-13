# mod-calendar

## Environment variables

See `.env.sample` for sample values.

| Name                                   | Default Value | Description                                                 |                                                                                                                                               | ----------------       |
| -------------------------------------- | ------------- | ----------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- | ----------- | --------------- | -------- | ------------- |
|                                        | JAVA_OPTIONS  | `XX:MaxRAMPercentage=66.0 -Dlog4j2.formatMsgNoLookups=true` | Sets Java options. Default is to set the maximum ram and mitigate [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228) in Log4j. |
| percentage to 66%                      |               | DB_HOST                                                     | `localhost`                                                                                                                                   | Postgres hostname      |             | DB_PORT         | `5432`   | Postgres port |
|                                        | DB_USERNAME   | `folio_admin`                                               | Postgres username                                                                                                                             |                        | DB_PASSWORD | `folio_admin`   | Postgres |
| password                               |               | DB_DATABASE                                                 | `okapi_modules`                                                                                                                               | Postgres database name |             | DB_QUERYTIMEOUT | `60000`  |
| Postgres query time out. Default is 6s |               | DB_CHARSET                                                  | `UTF-8`                                                                                                                                       | Postgres charset       |             |
| DB_MAXPOOLSIZE                         | `5`           | Postgres max pool size                                      |
