# Deployment Testing

This is similar to the
[sample deployment for the RMB-based modules](https://github.com/folio-org/folio-sample-modules/tree/master/hello-vertx).

First, the module is built with Maven and Docker per the process defined in `build-process.md`. This
docker module can then be ran with `docker run -t -i -p 8080:8080 mod-calendar`, exposing the
container's 8080 as local 8080. Please note that environment variables must be provided or this will
likely fail (due to lack of a database connection); see `.env.sample` if you are not sure what these
should be. Okapi will provide these if deployed through its API.

If Okapi is running in a Vagrant box (likely), the build process should be performed in the VM.
Using shared folders is recommended (by default, the folder with the Vagrantfile is mounted in
`/vagrant`).

All of the steps in this file, as well as those in the [build-process.md](build-process.md), are
executed as part of [`scripts/build.sh`](/scripts/build.sh) script. See `./scripts/build.sh -h` for
usage information.

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

## Test Module

In order to test the module, it must be enabled for a tenant.

This can be done using (where `2.0.0` is the version and `diku` is the tenant to enable the module
on):

```sh
curl -w '\n' -X POST -D - \
  -H "Content-type: application/json" \
  -d '{"id":"mod-calendar-2.0.0-SNAPSHOT"}' \
  http://localhost:9130/_/proxy/tenants/diku/modules
```

From here, you can use the module's routes directly on Okapi's `:9130` so long as you provide
`-H "X-Okapi-Tenant: diku"` on your requests.

### Running Locally

Please note that the module can be ran locally. It is recommended to do this _on top of/alongside_
an existing Okapi environment (a la Vagrant) where the module has been deployed the first time
(which will create the permissions/databases/etc. needed) with the database exposed. Once the
environment is ready to go (and defined in a `.env` file -- an example is provided in
`.env.sample`), Java can execute the application normally. A sample launch configuration for VS code
is in `.vscode/launch.json`.

Running it this way does not allow testing any of the features of Okapi (permissions, deployments,
tenants, etc), however, it makes testing things much quicker.
