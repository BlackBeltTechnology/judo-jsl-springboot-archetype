#set( $pound = '#' )
$pound ${applicationName}

This application is a standard Spring Boot application with [JUDO Toolbox](https://www.judo.codes/) support.

$pound$pound Pre-requisites

- JDK 11 (The JUDO Team recommends [Zulu JDK](https://www.azul.com/downloads/?version=java-11-lts&package=jdk))
- [Maven 3.8.x](https://maven.apache.org/download.cgi)

$pound$pound Building the project

```bash
mvn clean install
```

$pound$pound JUDO Documentation

For details about the JUDO modeling language, and the generated tools (SDK, APIs, etc...) please visit:
https://documentation.judo.technology.

$pound$pound Bootstrapped sources

$pound$pound$pound application.yml

Located under: `src/main/resources`.

This is a standard Spring Boot resource which by default is generated to utilize HSQL Database (we support PostgreSQL as well).

$pound$pound$pound ${modelName}.jsl

Located under: `src/main/resources/model`.

> This is just a starter model, feel free to adjust  / replace.

$pound$pound$pound ${applicationName}.java

Located under: `src/main/java/...`

This is the entry point of a bare-bones Spring Boot Application similar to what you'd get if you'd have used the
[https://start.spring.io](start.spring.io) generator.

$pound$pound$pound ${applicationName}Tests.java

Located under: `src/test/java/...`.

This is a traditional Spring Boot test class which has smoke-tests generated into it for the generated model file and
SDK.

$pound$pound$pound Logs

The generated application comes with [Logback](https://logback.qos.ch/) configurations for both application and test phases.

These configurations extend the Spring Boot defaults therefore they are pretty lean, and should be familiar to most.

- [src/main/resources/logback.xml]()
- [src/test/resources/logback-test.xml]()
