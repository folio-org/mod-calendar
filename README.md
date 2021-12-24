# mod-calendar

Copyright (C) 2017-2021 The Open Library Foundation

This software is distributed under the terms of the Apache License, Version 2.0. See the file
"[LICENSE](LICENSE)" for more information.

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
