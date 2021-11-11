set -euxo pipefail

curl -w '\n' -X DELETE http://localhost:9130/_/discovery/modules/mod-calendar-2.0.0-SNAPSHOT

docker build -t docker.ci.folio.org/mod-calendar .

curl -w '\n' -X POST -D - \
	-H "Content-type: application/json" \
	-d @target/DeploymentDescriptor.json \
	http://localhost:9130/_/discovery/modules

