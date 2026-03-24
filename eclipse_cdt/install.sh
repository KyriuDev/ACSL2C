#!/bin/bash

for f in /home/quentin/Documents/Post-doc/Nicola/Eclipse_CDT/jars/*.jar; do
  name=$(basename "$f" .jar)
  mvn install:install-file \
    -Dfile="$f" \
    -DgroupId=local.libs \
    -DartifactId="$name" \
    -Dversion=1.0 \
    -Dpackaging=jar
done
