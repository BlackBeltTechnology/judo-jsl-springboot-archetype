# Contributing to JUDO

## Development Environment

Your development environment must comply with the requirements in the parent project's [CONTRIBUTING guide](https://github.com/BlackBeltTechnology/judo-community/blob/develop/CONTRIBUTING.adoc). In summary:

- **Java 21** JDK (Zulu JDK recommended)
- **Maven 3.9.4+** (or use the included `./mvnw` wrapper)

## Code Structure

This project is a single-module Maven Archetype. The key directories are:

| Directory | Purpose |
|-----------|---------|
| `src/main/resources/archetype-resources/` | Template files that become the generated project |
| `src/main/resources/META-INF/maven/` | Archetype descriptor (parameters, file sets) |
| `src/test/resources/projects/basic/` | Integration test configuration for archetype generation |

Template files use **Apache Velocity** syntax — `${modelName}`, `${groupId}`, etc. are replaced during project generation. Filenames with double underscores (e.g. `__applicationName__.java`) are renamed to the parameter value.

## Build Commands

```bash
# Run tests
mvn clean test

# Full build and install
mvn clean install

# Run archetype integration tests (generates a project from the archetype and verifies it builds)
mvn -ParchetypeTest clean install
```

## Submitting an Issue

Before submitting, please search the [issue tracker](https://github.com/BlackBeltTechnology/judo-jsl-springboot-archetype/issues) — your problem may already have a resolution or workaround.

To help us reproduce and fix the issue, please include:

- Output of `java -version` and `mvn -version`
- `pom.xml` or `.flattened-pom.xml` (when applicable)
- A minimal use-case that fails

We require a minimal reproduction to isolate the problem before fixing it.

You can file new issues using the [issue form](https://github.com/BlackBeltTechnology/judo-jsl-springboot-archetype/issues/new/choose).

## Submitting a PR

This project follows [GitHub's standard forking model](https://guides.github.com/activities/forking/). Please fork the project to submit pull requests.

> **Important:** All commits must reference a JIRA ticket number (`JNG-xxx`). There is no commit without a ticket number.
