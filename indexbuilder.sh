#!/bin/sh

#JAVA_HOME=/usr/java/jdk1.7.0_55
#echo $JAVA_HOME

APP_HOME=./indexing/src/com/lucene/index
APP_MAINCLASS=luceneIndexer

CLASSPATH=".:dependencies/commons-cli-1.4.jar:gson-2.8.6.jar:lucene-queryparser-4.7.2.jar
:lucene-analyzers-common-4.7.2.jar:lucene-core-4.7.2.jar:lucene-demo-4.7.2"

echo "classpath="$CLASSPATH

java -classpath $CLASSPATH $APP_MAINCLASS $1
