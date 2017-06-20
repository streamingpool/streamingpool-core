#!/usr/bin/env bash
set -e # exit with nonzero exit code if anything fails

./gradlew bintrayUpload -DDEPLOYMENT=1 --info
