#!/usr/bin/env bash

curl --header 'Content-Type: application/json' \
  --request POST \
  --data '{"username":"admin","password":"admin"}' \
  'https://wgcpl5vjkf.execute-api.eu-central-1.amazonaws.com/quantummaid-demo-lambda/public/login'
