#!/bin/bash
set -e # exit with nonzero exit code if anything fails

secreteFilePath=$(pwd)/deployment.key
echo "Deployment key found"

echo "Executing gradle deployToMavenCentral"
./gradlew deployToMavenCentral -PdeploymentVersion=${TRAVIS_TAG} -Psigning.keyId=${signingKeyId} -Psigning.password=${signingPassword}  -Psigning.secretKeyRingFile=$secreteFilePath -PossrhUsername=${ossrhUsername} -PossrhPassword=${ossrhPassword} --stacktrace --info

rm $secreteFilePath
