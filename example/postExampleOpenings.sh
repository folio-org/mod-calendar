#!/bin/bash

OKAPI_URL="http://localhost:9130"
TENANT="diku"

if ! [ -x "$(command -v jq)" ]; then
  echo "Error: jq is not installed. Install it by running
  sudo apt install jq" >&2
  exit 1
fi

if ! [ -x "$(command -v curl)" ]; then
  echo "Error: jq is not installed. Install it by running
  sudo apt install curl" >&2
  exit 1
fi

usage="$(basename "$0") [options] -- post example opening hours located in current directory

where:
    -h  show this help text
    -T,--tenant   set the tenant's name (default: $TENANT)
    -t, --token   token for OKAPI authentication
    -o, --okapi   OKAP URL (default: $OKAPI_URL)
    -p, --path    path of JSON files containing calendar events"

TOKEN=""
PATH="."

while [[ $# -gt 0 ]]
  do
    key="$1"

    case $key in
      -o|--okapi)
      OKAPI_URL="$2"
      shift
      shift
      ;;
      -T|--tenant)
      TENANT="$2"
      shift
      shift
      ;;
      -t|--token)
      TOKEN="$2"
      shift
      shift
      ;;
      -p|--path)
      PATH="$2"
      shift
      shift
      ;;
      -h|--help)
       echo "$usage"
       exit
       ;;
      *)    # unknown option
      shift # past argument
      ;;
    esac
done

if [[ -z "${TOKEN// }" ]]; then
  echo "missing argument for token
  "
  echo "$usage"
  exit 1
fi
echo $TOKEN
COUNTER=0
echo $OKAPI_URL
shopt -s nullglob
for i in $PATH/*.json; do
  SERVICE_POINT_ID=$(/bin/cat $i |  /usr/bin/jq -r '.servicePointId')
  URI="$OKAPI_URL/calendar/periods/$SERVICE_POINT_ID/period" 
  
  response=$(/usr/bin/curl --request POST --write-out %{http_code} --silent --output /dev/null -H "Content-Type: application/json" -H "x-okapi-tenant: $TENANT" -H "x-okapi-token: $TOKEN" --data @$i $URI)
  if [ "$response" -ge 200 -a "$response" -le 300 ]; then
    COUNTER=$[$COUNTER +1];
  else
    echo "Posting $i was not succesfull. HTTP status code: $response"
  fi
done
echo "$COUNTER file(s) were imported"
