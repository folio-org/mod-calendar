# API and Schema Management

The API routing, parameters, etc., are all managed through Swagger. This is done through the main
`mod-calendar.yaml` in `main/resources/swagger.api` folder. This should be entirely up to the
OpenAPI spec.

Other FOLIO projects seem to aggresively prefer abstracting every parameter, response, etc., into
separate schemas. For the provided `examples`, it is a fair bit looser -- it seems to be an example
response for each endpoint, but I have yet to find anywhere that these are actually referenced
(maybe in the generated docs)?

It seems that the error

```
[INFO] 'host' (OAS 2.0) or 'servers' (OAS 3.0) not defined in the spec. Default to [http://localhost] for server URL [http://localhost/{endpoint}/]
```

cannot be fixed.
