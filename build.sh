#!/usr/bin/env bash
SONATYPE_PASSWORD=$1
BUILD_NUMBER=$2

# decrypt the secrets
openssl aes-256-cbc -pass pass:$SONATYPE_PASSWORD -in secring.gpg.enc -out local.secring.gpg -d
openssl aes-256-cbc -pass pass:$SONATYPE_PASSWORD -in pubring.gpg.enc -out local.pubring.gpg -d
openssl aes-256-cbc -pass pass:$SONATYPE_PASSWORD -in credentials.sbt.enc -out local.credentials.sbt -d

cat <<EOF >local.version
$BUILD_NUMBER
EOF

#next teamcity will run sbt test publishSigned sonatypeReleaseAll