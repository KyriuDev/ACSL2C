#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Please provide at least the working directory (--working-dir=<working_dir_path>)"
    exit 1
fi

JAR_DIR="/home/quentin/IdeaProjects/ACSLParser/main_code/target"
JAR_NAME="main-1.0-SNAPSHOT.jar"

mvn package

java -jar $JAR_DIR/$JAR_NAME "$@"
