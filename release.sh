#!/bin/sh
rm -rf dist/
mkdir dist

mvn clean install -DskipTests -P github.pr.status
cp target/github-pr-status*.jar dist/

mvn clean install -DskipTests -P stash.pr.status
cp target/stash-pr-status*.jar dist/

mvn clean install -DskipTests -P gerrit.cs.status
cp target/gerrit-cs-status*.jar dist/

mvn clean install -DskipTests -P gitlab.mr.status
cp target/gitlab-mr-status*.jar dist/
