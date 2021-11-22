# API and Schema Management

The API routing, parameters, etc., are all managed through Swagger. This is done through the main
`mod-calendar.yaml` in `main/resources/swagger.api` folder. This should be entirely up to the
OpenAPI spec.

Other FOLIO projects seem to aggressively prefer abstracting every parameter, response, etc., into
separate schemas. For the provided `examples`, it is a fair bit looser -- it seems to be an example
response for each endpoint, but I have yet to find anywhere that these are actually referenced?

It seems that the error

```
[INFO] 'host' (OAS 2.0) or 'servers' (OAS 3.0) not defined in the spec. Default to [http://localhost] for server URL [http://localhost/{endpoint}/]
```

cannot be fixed.

## Api Documentation Generation

The `api-doc` in [`folio-tools`](https://github.com/folio-org/folio-tools) is used to generate the
docs.

When in a Vagrant VM, the following will build the docs (assuming you are working in the
`mod-calendar` directory):

```sh
~/folio-tools/api-doc/api_doc.py -o /vagrant/folio-api-docs -t OAS -d src/main/resources/swagger.api
```

If you have not used `api-doc` before, the command `yarn install` must be ran in its directory in
order to install all of its dependencies.
