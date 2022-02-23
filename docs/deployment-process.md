# Build Process

This module is built using Apache Maven.

The build steps are defined in `pom.xml` and the entire build process is initiated through a simple
`mvn -e install`.

## Maven Script

As of now, this is largely based on other existing modules' `pom.xml`. There appear to be a few
efforts to standardize versioning of the sub-components (ref FOLSPRINGB-8, FOLSPRINGB-3), however,
none have produced results.

## Module Descriptor

This is used by Okapi to get information about the module.

### `provides`

The main block (besides the meta `_tenant` present in all module) is the `mod-calendar` block
(`@artifactId@`). This block contains the schema of what API calls are allowed and all the
information about them. API versioning here is inconsistent with the package version itself,
however, should be consistent with the Swagger YAML in `mod-calendar.yaml`.

### `permissionSets`

This defines the permissions applicable to the module. These should not be used directly and instead
inherited based on UI-level permissions (declared in that `package.json`).

### `launchDescriptor`

This defines how Okapi should configure an environment in which to run the module. In our case, that
is done through a Docker image (configuration for this not yet created).

The environment variables provide credentials and database information. The needed memory is
currently just the original value and should be re-defined based on actual needs (ref
[MODCAL-55](https://issues.folio.org/projects/MODCAL/issues/MODCAL-55?filter=allopenissues)).

## Deployment Descriptor

The deployment descriptor is much more straightforward -- all this does is run the JAR produced by
Maven. The `exec` key found in many of these descriptors is ignored and pointless -- at some point
they hope to replace the majority of this declare/deploy process with a single install step.

## Compilation Step

`mvn -e clean install` will fully compile the actual module code from scratch, generating the
classes as well as other boilerplate (from Lombok, OpenAPI, etc.). Maven is also responsible for
generating the deployment and module descriptors.

## Dockerization

To build the docker module, use:

```sh
docker build -t docker.ci.folio.org/mod-calendar .
```

Docker will automatically expand the tag's version to `mod-calendar:latest`, therefore, this does
not need to be manually specified. The `docker.ci.folio.org` path is required as Okapi will _only_
search this repository when the module is deployed.
