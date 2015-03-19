#!/bin/sh
rm -rf dist/
mkdir dist

mvn clean install -DskipTests -P github.pr.status
cp target/github-pr-poller*.jar dist/

mvn clean install -DskipTests -P stash.pr.status
cp target/stash-pr-poller*.jar dist/

mvn clean install -DskipTests -P gerrit.cs.status
cp target/gerrit-cs-poller*.jar dist/
