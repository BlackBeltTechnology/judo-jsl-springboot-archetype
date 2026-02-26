# JUDO JSL Springboot Archetype - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/judo-jsl-springboot-archetype
**License:** Eclipse Public License 2.0 (EPL-2.0)
**Java Version:** 21
**Build System:** Maven 3.9.4+ with archetype-packaging extension

1. A **Maven Archetype** that generates Spring Boot applications with JUDO JSL (JUDO Specific Language) support
2. Part of the [judo-community](https://github.com/BlackBeltTechnology/judo-community) ecosystem — provides the project bootstrapping entry point for JUDO-based Spring Boot apps
3. Generated projects use **model-driven architecture**: developers write `.jsl` domain model files, and the `judo-spring-maven-plugin` generates DAOs, APIs, Spring configuration, and Liquibase database migrations at build time
4. Template files use Apache Velocity syntax with special conventions: `__paramName__` filenames, `${symbol_pound}`/`${symbol_dollar}`/`${symbol_escape}` for escaping, and `__dot__` prefix for dotfiles

## Directory Structure

```
judo-jsl-springboot-archetype/
├── pom.xml                          # Main POM — archetype packaging, profiles, version properties
├── README.md                        # Project overview and usage
├── CONTRIBUTING.md                  # Development setup and contribution guidelines
├── .github/
│   ├── workflows/                   # GitHub Actions CI/CD (build, release, merge automation)
│   └── CIFLOW.md                    # Detailed CI/CD flow documentation with diagrams
├── src/
│   ├── main/resources/
│   │   ├── META-INF/maven/
│   │   │   └── archetype-metadata.xml  # Archetype descriptor (parameters, file sets, validation)
│   │   └── archetype-resources/        # Template files for generated projects
│   │       ├── pom.xml                 # Generated project POM (Velocity template)
│   │       ├── README.md               # Generated project README
│   │       ├── __dot__gitignore        # → .gitignore in generated project
│   │       └── src/                    # Generated project source tree
│   └── test/resources/
│       └── projects/basic/             # Integration test: archetype generation + build verification
├── .vscode/settings.json           # VS Code Java settings
├── .zed/settings.json              # Zed editor Java settings
└── openspec/                        # OpenSpec configuration
```

## Core Modules

This is a **single-module** project (no `<modules>` in pom.xml). The archetype packaging type means Maven treats the entire `src/main/resources/archetype-resources/` directory as the template payload.

| Component | Type | Purpose |
|-----------|------|---------|
| `archetype-metadata.xml` | Archetype descriptor | Defines required parameters (`modelName`, `groupId`, etc.), validation regexes, default values, and which template files to include/filter |
| `archetype-resources/` | Velocity templates | The complete project template — POM, Java sources, JSL model, Spring config, test files |
| `projects/basic/` | Integration test | Verifies the archetype generates a valid project that builds successfully (uses `modelName=Shop`) |

## Technology Stack

### Core Technologies
- **Maven Archetype Plugin** 3.2.1 — project generation framework
- **Apache Velocity** — template engine for file content and filename substitution
- **Spring Boot** 3.5.0 — target framework for generated projects
- **JUDO JSL** — domain-specific language for model-driven code generation

### JUDO Framework Components (in generated projects)
- **judo-tatami-jsl** 1.1.4.x — JSL model processing and transformation
- **judo-spring-boot-starter** 1.0.4.x — Spring Boot integration for JUDO runtime
- **judo-spring-maven-plugin** 1.0.0.x — Maven plugin that runs model-to-code generation in `generate-sources` phase

### Build & Quality
- **Maven Surefire** 3.5.1 — test execution
- **Maven Invoker** 3.3.0 — archetype integration testing
- **JaCoCo** 0.8.12 — code coverage
- **SonarQube** 3.9.1.2184 — static analysis
- **Logback** 1.5.12 / **SLF4J** 2.0.16 — logging

## Build Commands

Maven wrapper (`./mvnw`) is available for consistent builds.

```bash
# Run tests
mvn clean test

# Full build and install
mvn clean install

# Run archetype integration tests (generates a Shop project and builds it)
mvn -ParchetypeTest clean install

# Sync dependency versions from pom.xml properties into archetype templates
mvn -PupdateVersions validate

# Update EPL-2.0 license headers on source files
mvn -Pupdate-source-code-license process-sources
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `updateVersions` | Replaces version placeholders in `archetype-metadata.xml` and `archetype.properties` with current dependency versions from pom.xml properties (active by default) |
| `archetypeTest` | Runs integration tests via Maven Invoker — generates a project from the archetype and verifies it builds (active by default) |
| `sign-artifacts` | Signs build artifacts with GPG via `sign-maven-plugin` |
| `release-dummy` | Deploys to local `/tmp/` directory for testing |
| `release-judong` | Deploys to internal Nexus repository at `nexus.judo.technology` |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH |
| `generate-github-asciidoc-diagrams` | Renders PlantUML diagrams from AsciiDoc files to PNG |
| `update-source-code-license` | Adds/updates EPL-2.0 license headers on all source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Archetype POM — defines all dependency versions, profiles, and build plugins |
| `src/main/resources/META-INF/maven/archetype-metadata.xml` | Archetype descriptor — parameters with validation regexes, file sets, filtering rules |
| `src/main/resources/archetype-resources/pom.xml` | Template POM for generated projects — Spring Boot parent, JUDO plugins |
| `src/main/resources/archetype-resources/src/main/resources/application.yml` | Template Spring config — HSQLDB datasource, Liquibase changelog, JUDO model name |
| `src/main/resources/archetype-resources/src/main/resources/model/__modelName__.jsl` | Sample JSL model with Person entity |
| `src/test/resources/projects/basic/archetype.properties` | Integration test parameters (modelName=Shop, groupId=hu.blackbelt) |
| `.mvn/jvm.config` | JVM flags: 2GB heap, UTF-8, `--add-opens` for Java 21 module system |
| `logback-test.xml` | Test logging configuration |

## Development Environment

**Required:**
- Java 21 JDK (Zulu JDK recommended)
- Maven 3.9.4+
- Git

**JVM Configuration** (`.mvn/jvm.config`):
- `-Xms1024m -Xmx2048m` heap size
- `-Dfile.encoding=UTF-8`
- `--add-opens` flags for `java.lang`, `java.util`, `java.time` (required for JUDO runtime reflection)

## Git Workflow

- **Main Branch:** `develop`
- **Release Branch:** `master`
- **Versioning:** `1.0.6-SNAPSHOT` (current), uses CI-flatten for development builds
- **Strategy:** GitFlow — feature branches merge to `develop`, release branches merge to both `master` and `develop`
- **CI/CD:** GitHub Actions with automated build, deploy, release, and PR merge workflows
- See [CIFLOW.md](.github/CIFLOW.md) for detailed workflow diagrams

## Important Notes

1. **Version sync is critical:** After changing dependency versions in the root `pom.xml` properties, run `mvn -PupdateVersions validate` to propagate those versions into the archetype metadata and test properties. The `updateVersions` profile is active by default, so this happens automatically during normal builds.
2. **Do NOT reformat** the `<requiredProperty>` lines in `archetype-metadata.xml` that have `<!-- DO NOT REFORMAT -->` comments — the `maven-replacer-plugin` uses regex to find and replace version strings, and reformatting breaks the regex patterns.
3. **Velocity escaping:** Template files use `${symbol_pound}` for `#`, `${symbol_dollar}` for `$`, and `${symbol_escape}` for `\` to avoid conflicts with Velocity's own syntax. The `__dot__` prefix in filenames becomes `.` in the generated project.
4. **All commits must reference a JIRA ticket** (`JNG-xxx`).
5. **Integration tests** generate a sample `Shop` project and run a full Maven build on it. If archetype template changes break the generated project's build, the integration tests will catch it.

## Related Documentation

- [README.md](README.md) — Project overview and usage instructions
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and contribution guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD workflow documentation with Mermaid diagrams
- [JUDO Documentation](https://documentation.judo.technology) — Full JUDO platform documentation
- [judo-community](https://github.com/BlackBeltTechnology/judo-community) — Aggregator project for the JUDO ecosystem
