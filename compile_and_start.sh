#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Please provide at least the working directory"
    exit 1
fi

JAR_DIR="/home/quentin/IdeaProjects/ACSLParser/main_code/target"
JAR_NAME="main-1.0-SNAPSHOT.jar"

mvn clean
mvn validate
mvn compile
mvn test
mvn package
mvn verify
mvn install

java -jar $JAR_DIR/$JAR_NAME --working-dir=$1
