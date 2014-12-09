#!/bin/bash
set -e
rm -rf *.zip
./gradlew clean
./gradlew test
./gradlew install

