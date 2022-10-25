# API and Schema Management

The API routing, parameters, etc., are all managed through Swagger/OpenAPI. This is done through the
main `calendar.yaml` in `main/resources/api` folder.

It seems that the error

```
[INFO] 'host' (OAS 2.0) or 'servers' (OAS 3.0) not defined in the spec. Default to [http://localhost] for server URL [http://localhost/{endpoint}/]
```

cannot be fixed due to the dynamic nature of Okapi deployments and environments.

## Api Documentation Generation

The `api-doc` in [`folio-tools`](https://github.com/folio-org/folio-tools) is used to generate the
docs.

When in a Vagrant VM, the following will build the docs (assuming you are working in the
`mod-calendar` directory):

```sh
~/folio-tools/api-doc/api_doc.py -o /vagrant/folio-api-docs -t OAS -d src/main/resources/api
```

If you have not used `api-doc` before, the commands `yarn install` and
`pip3 install -r requirements.txt` must be ran in its directory in order to install all of its
dependencies, per the
[`api-doc` documentation](https://github.com/folio-org/folio-tools/tree/master/api-doc).
