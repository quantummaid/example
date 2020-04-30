#!/usr/bin/env bash

aws cloudformation package \
    --template-file ./lambda-with-api-gateway.yaml \
    --s3-bucket quantummaid-demo \
    --output-template-file target/packaged-lambda-with-api-gateway.yaml

aws cloudformation deploy \
    --template-file target/packaged-lambda-with-api-gateway.yaml \
    --stack-name quantummaid-demo-lambda \
    --parameter-overrides StackIdentifier=quantummaid-demo-lambda \
    --capabilities CAPABILITY_NAMED_IAM