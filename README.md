# Multi-Runtime Lambda Demo

Demonstrates how to build a Scala app that can be deployed on multiple AWS Lambda runtimes (e.g. JVM, Node)

## Node Lambda

```sh
sbt node-handler/fastOptJS::webpack
```

## JVM Lambda

```sh
sbt jvm-lambda/assembly
```

## HTTP Server in JVM Docker Container

```sh
sbt fargate-handler/docker:publishLocal
```
