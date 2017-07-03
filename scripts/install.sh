#!/bin/bash

set -e
set -x

if [[ ! -z $TRAVIS_TAG ]]; then
    sed -i "s/1.0.0/$TRAVIS_TAG/g" "pom.xml"
    cp ../.travis.settings.xml $HOME/.m2/settings.xml
    mvn deploy
else
    mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
fi
