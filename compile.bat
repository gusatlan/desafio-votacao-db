@ECHO OFF

gradlew clean test build
del build/libs/*-plain.jar
gradlew --stop

ECHO ON
