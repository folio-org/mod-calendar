# Install/Upgrade

This document contains instructions for installing (or upgrading an existing version to) the local
version of `mod-calendar` for testing with Okapi.

## Compile the Module

First, compile the module using:

```sh
mvn install
```

Note: this will require that Docker is running to complete the integration tests. If you do not have
Docker, or wish to skip this step, add `-DskipTests` to this command.

## Declare Module

The module descriptor will convey the information about the module to Okapi, as described in
`build-process.md`.

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @target/ModuleDescriptor.json \
  http://localhost:9130/_/proxy/modules
```

This should return successfully (2xx) and echo back the newly defined module.

## Build Module

To build the image, use:

```sh
docker build -t docker.ci.folio.org/mod-calendar .
```

Please note that the `.` is part of the command.

If your IDE does not do any compilation itself, you will likely need to run `mvn install` first.

## Deploy Module

Before the module can be deployed, the name of the node must be known. The following will find it
(usually `localhost` or, on Vagrant, `10.0.2.15`):

```sh
curl -w '\n' -X GET \
  http://localhost:9130/_/discovery/nodes
```

Once this is placed into the generated `target/DeploymentDescriptor.json`, we can deploy it onto the
node:

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d @target/DeploymentDescriptor.json \
  http://localhost:9130/_/discovery/modules
```

If this fails, this means there was an issue deploying the module. You can examine the logs using
`docker logs --follow -n 100 CONTAINER_ID`, where the container ID is found with `docker ps`. Please
note that only the startup of the module will be in the main Okapi logs; after this, the module logs
are only found in its container.

Please note, this uses Okapi's built in deployment feature and is not meant for production
deployments.

## Tenant Enabling

This is the step where things diverge from the base deployment instructions as, instead of just
installing the module, we want to also disable the previous mod-calendar.

This can be done using (where `2.0.0` is the new version and `diku` is the tenant to enable the
module on):

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d '[{"id":"mod-calendar-2.0.0-SNAPSHOT","action":"enable"}]' \
  http://localhost:9130/_/proxy/tenants/diku/install
```

## Running Locally

The module can be ran locally, to increase debugging speed and ease. It is recommended to do this
_on top of/alongside_ an existing Okapi environment (e.g. Vagrant) where the module has been
deployed (which will create the permissions/databases/etc. needed) with the database port exposed.
Once the environment is ready to go (and defined in a `.env` file -- an example is provided in
`.env.sample`), Java can execute the application normally. A sample launch configuration for VS code
is in `.vscode/launch.json`.

Running it in this manner does not allow testing any of the features of Okapi (permissions,
deployments, tenants, etc), however, it makes testing things much quicker.
