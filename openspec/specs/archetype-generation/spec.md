# archetype-generation Specification

## Purpose

Generates a complete Spring Boot project with JUDO JSL model-driven architecture from a Maven archetype template. The archetype takes user parameters (model name, group ID, artifact ID, etc.) and produces a ready-to-build project with generated source code, configuration, and a sample JSL model.

## Architecture

The archetype consists of three main components:

- **Archetype Descriptor** (`archetype-metadata.xml`): Defines required parameters with validation regexes and default values, and specifies which template files are included and filtered through Velocity
- **Template Files** (`archetype-resources/`): Velocity-processed templates including a Maven POM (with Spring Boot parent and JUDO plugins), Java application class, JSL model file, Spring configuration, and test scaffolding
- **Version Synchronization**: The `updateVersions` Maven profile uses `maven-replacer-plugin` to keep dependency versions in templates consistent with the root POM properties

Key classes in the generated project:
- `${applicationName}.java`: `@SpringBootApplication` entry point that imports `${modelName}DaoModules.class`
- `${modelName}.jsl`: Sample JSL model defining a `Person` entity with derived attributes

## Requirements

### Requirement: Parameter validation SHALL enforce naming conventions

The archetype descriptor SHALL validate all required parameters against regex patterns to ensure generated code compiles.

#### Scenario: Valid model name accepted
- **GIVEN** a user invokes the archetype
- **WHEN** `modelName` is set to `Shop` (matching `^[a-zA-Z0-9]+$`)
- **THEN** the archetype generates a project with `Shop.jsl` and `ShopApplication.java`

#### Scenario: Invalid model name rejected
- **GIVEN** a user invokes the archetype
- **WHEN** `modelName` contains special characters (e.g., `my-model`)
- **THEN** the archetype generation fails with a validation error

#### Scenario: Default values derived from parameters
- **GIVEN** a user invokes the archetype with `modelName=Shop` and does not specify `artifactId`
- **WHEN** the archetype processes default values
- **THEN** `artifactId` is set to `shop` (lowercased modelName), `applicationName` is set to `ShopApplication`, and `package` is set to `${groupId}.shop`

### Requirement: Generated project SHALL build successfully

The generated project SHALL be a valid Maven project that compiles and passes tests without manual modifications.

#### Scenario: Integration test verifies generated project
- **GIVEN** the archetype is installed locally
- **WHEN** `mvn -ParchetypeTest clean install` is executed
- **THEN** a project is generated with `modelName=Shop`, built with Maven, and all tests pass

#### Scenario: Generated POM inherits Spring Boot parent
- **GIVEN** a project is generated from the archetype
- **WHEN** the generated `pom.xml` is examined
- **THEN** it declares `spring-boot-starter-parent` as parent with the configured `springbootVersion`, includes `judo-spring-boot-starter` dependency, and configures `judo-spring-maven-plugin` for `generate-sources` phase

### Requirement: Version synchronization SHALL keep templates consistent

The `updateVersions` profile SHALL propagate dependency version changes from the root POM properties into the archetype metadata and test properties.

#### Scenario: Spring Boot version updated
- **GIVEN** `springboot-version` property in root `pom.xml` is changed to `3.6.0`
- **WHEN** `mvn -PupdateVersions validate` is executed
- **THEN** `archetype-metadata.xml` reflects `3.6.0` as the default for `springbootVersion`, and `archetype.properties` test file reflects `springbootVersion=3.6.0`

#### Scenario: JUDO component versions updated
- **GIVEN** `judo-tatami-jsl-version`, `judo-jsl-springboot-starter-version`, or `judo-spring-maven-plugin-version` properties are changed
- **WHEN** `mvn -PupdateVersions validate` is executed
- **THEN** the corresponding `<defaultValue>` entries in `archetype-metadata.xml` and property values in `archetype.properties` are updated

### Requirement: Generated project SHALL use model-driven code generation

The generated project SHALL include `judo-spring-maven-plugin` configured to transform JSL models into Java code during the `generate-sources` Maven phase.

#### Scenario: JSL model processed at build time
- **GIVEN** a generated project contains `src/main/resources/model/Shop.jsl`
- **WHEN** `mvn compile` is executed
- **THEN** `judo-spring-maven-plugin:transformerWorkflow` runs during `generate-sources` and produces DAO classes, API classes, Spring configuration modules, and Liquibase changelogs

#### Scenario: Application imports generated modules
- **GIVEN** a generated project with `modelName=Shop`
- **WHEN** the application class is compiled
- **THEN** `ShopApplication.java` imports and configures `ShopDaoModules.class` via `@Import`

### Requirement: Generated project SHALL include HSQLDB as default database

The generated `application.yml` SHALL configure an HSQLDB in-memory database with Liquibase migration support.

#### Scenario: Default datasource configuration
- **GIVEN** a project is generated from the archetype
- **WHEN** `application.yml` is examined
- **THEN** it configures `org.hsqldb.jdbc.JDBCDriver` with `jdbc:hsqldb:mem:testdb` URL and references a Liquibase changelog file named `${modelName}-liquibase_hsqldb.changelog.xml`
