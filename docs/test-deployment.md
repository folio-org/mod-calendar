# Deployment Testing

This is similar to the
[sample deployment for the RMB-based modules](https://github.com/folio-org/folio-sample-modules/tree/master/hello-vertx).

First, the module is built with Maven and Docker per the process defined in `build-process.md`. This
docker module can then be ran with `docker run -t -i -p 8082:8082 mod-calendar`, exposing the
container's 8082 as local 8082. Please note that environment variables must be provided or this will
likely fail (due to lack of a database connection).

**_TBD: Investigate
https://wiki.folio.org/plugins/servlet/mobile?contentId=25729430#content/view/25729430_**

If Okapi is running in a Vagrant box (likely), the build process should be performed in the VM.
Using shared folders is recommended (by default, the folder with the Vagrantfile is mounted in
`/vagrant`).

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
