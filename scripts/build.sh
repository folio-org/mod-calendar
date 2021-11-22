#!/bin/bash
set -euo pipefail

bold() {
  echo $(tput bold)"$@"$(tput sgr0)
}
green() {
  echo $(tput bold)$(tput setaf 10)"$@"$(tput sgr0)
}
red() {
  echo $(tput bold)$(tput setaf 9)"$@"$(tput sgr0)
}

usage() {
  bold "Usage:"
  bold "  $0 [-hFfbaovvv] [-d [docspath]]"
  echo "           -h:  Print this help and exit"
  echo "           -F:  Fully build everything: implies -fda"
  echo "                  Note: This uses the default docs directory;"
  echo "                        specify -d separately after for a custom outdir"
  echo "           -f:  Rebuild with \`mvn -e clean install\` (overrides -b)"
  echo "           -b:  Rebuild with \`mvn install\`"
  echo "           -d:  Generate docs with folio-tools api-doc (default ../folio-api-docs)"
  echo "           -a:  Redefine the module (needed for API/permission changes)"
  echo "           -o:  Do not rebuild the docker image (only interact with Okapi)"
  echo "           -v:  Verbose"
  echo "          -vv:  More verbose"
  echo "         -vvv:  Even more verbose"
  echo
}

optstring=":hFfbaovvvd:"

MAVEN_BUILD_CMD=""
BUILD_DOCKER="yes"
REDEFINE="no"
DOCS_PATH=""
VERBOSE=0

while getopts ${optstring} arg; do
  case ${arg} in
    h)
      usage
      exit 1
      ;;
    F)
      MAVEN_BUILD_CMD="-e clean install"
      REDEFINE="yes"
      DOCS_PATH="../folio-api-docs"
      ;;
    f)
      MAVEN_BUILD_CMD="-e clean install"
      ;;
    b)
      if [ "$MAVEN_BUILD_CMD" = "" ]; then
        MAVEN_BUILD_CMD="install"
      fi
      ;;
    a)
      REDEFINE="yes"
      ;;
    d)
      DOCS_PATH="$OPTARG"
      ;;
    o)
      BUILD_DOCKER="no"
      ;;
    v)
      ((VERBOSE=VERBOSE+1))
      ;;
    :) # flag with parameter provided, but without parameter
      set
      case ${OPTARG} in
        d)
          DOCS_PATH="../folio-api-docs"
          ;;
        ?)
          red "Option specified without parameter: -${OPTARG}."
          echo
          usage
          exit 2
          ;;
      esac
      ;;
    ?) # default
      red "Invalid option: -${OPTARG}."
      echo
      usage
      exit 2
      ;;
  esac
done

if [ "$VERBOSE" -gt 2 ]; then
  set -x
fi

# Setup after parsing args since some of those exit immediately
error() {
  red "The last command failed unexpectedly.  Please rerun with -v for more information."
}

trap error ERR


findRoot() {
  local DIR="$1"
  echo "Checking $1"
  # TODO: find something better to test for than this?
  if [ -f "${DIR}/src/main/java/org/folio/calendar/CalendarApplication.java" ]; then
    WORKING_DIR="$DIR"
    return 0
  fi

  local PARENT=`dirname "$DIR"`

  if [ "$DIR" = "$PARENT" ]; then
    WORKING_DIR=""
  else
    findRoot "$PARENT"
  fi
}

findRoot "`pwd`"
if [ -z $WORKING_DIR ]; then
  red "Could not find the mod-calendar project in your current working directory or any of its ancestors.  Please cd into the project before running this script."
  exit 1
fi
if [ "$VERBOSE" -gt 0 ]; then
  green "Found working directory: $WORKING_DIR"
fi
cd "$WORKING_DIR"

if [ -n "$MAVEN_BUILD_CMD" ]; then
  if [ "$VERBOSE" -eq 0 ]; then
    MAVEN_BUILD_CMD="-q $MAVEN_BUILD_CMD"
  fi
  green "Running Maven"
  mvn $MAVEN_BUILD_CMD
fi

CURL_ARGS=""
DOCKER_BUILD_ARGS=""
API_DOC_ARGS=""

if [ "$VERBOSE" -gt 1 ]; then
  CURL_ARGS="-D - -w \n"
  API_DOC_ARGS="-l debug"
elif [ "$VERBOSE" -gt 0 ]; then
  CURL_ARGS="-D - -w \n -s -o /dev/null"
  API_DOC_ARGS="-l info"
else
  DOCKER_BUILD_ARGS="--quiet"
  CURL_ARGS="-s -o /dev/null"
  API_DOC_ARGS="-l warning"
fi

# undeploy (done first to ensure we stop asap)
green "Undeploying existing module, if running"
curl $CURL_ARGS -X DELETE \
  http://localhost:9130/_/discovery/modules/mod-calendar-2.0.0-SNAPSHOT

if [ "$BUILD_DOCKER" == "yes" ]; then
  # build
  green "Building Docker container"
  docker build $DOCKER_BUILD_ARGS -t docker.ci.folio.org/mod-calendar .
fi

if [ "$REDEFINE" == "yes" ]; then
  # unassign from diku
  green "Unassigning the module from diku"
  curl $CURL_ARGS -X DELETE \
    http://localhost:9130/_/proxy/tenants/diku/modules/mod-calendar-2.0.0-SNAPSHOT

  # undefine module
  green "Undefining the module"
  curl $CURL_ARGS -X DELETE \
    http://localhost:9130/_/proxy/modules/mod-calendar-2.0.0-SNAPSHOT

  # redefine
  green "Redefining the module"
  curl --fail $CURL_ARGS -X POST \
    -H "Content-type: application/json" \
    -d @target/ModuleDescriptor.json \
    http://localhost:9130/_/proxy/modules
fi

# redeploy
green "Redeploying the module"
curl --fail $CURL_ARGS -X POST \
  -H "Content-type: application/json" \
  -d @target/DeploymentDescriptor.json \
  http://localhost:9130/_/discovery/modules

if [ "$REDEFINE" == "yes" ]; then
  # reassign to diku
  green "Reassigning the module to diku"
  curl --fail $CURL_ARGS -X POST \
    -H "Content-type: application/json" \
    -d '{"id":"mod-calendar-2.0.0-SNAPSHOT"}' \
    http://localhost:9130/_/proxy/tenants/diku/modules
fi

if [ -n "$DOCS_PATH" ]; then
  green "Generating HTML API documentation"
  ~/folio-tools/api-doc/api_doc.py $API_DOC_ARGS \
    -o $DOCS_PATH \
    -t OAS -d src/main/resources/swagger.api
fi
