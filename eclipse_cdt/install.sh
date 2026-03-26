#!/bin/bash

JAR_FILES_DIR="/home/quentin/Documents/Post-doc/Nicola/Eclipse_CDT/jars"

for f in $JAR_FILES_DIR/*.jar; do
  jar_file_name=$(basename "$f" .jar)

  mvn install:install-file \
    -Dfile="$f" \
    -DgroupId=local.libs \
    -DartifactId="$jar_file_name" \
    -Dversion=1.0 \
    -Dpackaging=jar
done
