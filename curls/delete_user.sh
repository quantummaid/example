#!/usr/bin/env bash
set -eu

URL='https://wgcpl5vjkf.execute-api.eu-central-1.amazonaws.com/quantummaid-demo-lambda'

JWT="$(curl --header 'Content-Type: application/json' \
  --request 'POST' \
  --data '{"username":"admin","password":"admin"}' \
  --silent \
  "${URL}/public/login")"

curl --header 'Content-Type: application/json' \
  --header "Authorization: ${JWT}" \
  --request POST \
  --data '{
             "userIdentifier": "foo"
          }' \
  "${URL}/admin/delete"