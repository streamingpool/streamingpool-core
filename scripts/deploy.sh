#!/usr/bin/env bash
set -e # exit with nonzero exit code if anything fails
echo "<TRAVIS_TAG>"
echo $TRAVIS_TAG
echo "</TRAVIS-TAG>"
./gradlew bintrayUpload -DDEPLOYMENT=1 --info
