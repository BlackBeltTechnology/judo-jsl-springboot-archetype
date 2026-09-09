# judo-jsl-springboot-archetype — module agent doctrine

## Module purpose

`judo-jsl-springboot-archetype` is the front door to the JSL stack: it is a
Maven archetype (`<packaging>maven-archetype</packaging>`) whose entire
deliverable is a Velocity-templated project skeleton that turns a single
`modelName` answer into a runnable, model-driven Spring Boot application. It
carries no Java of its own — the artifact published to Nexus/Central is the
template payload under `src/main/resources/archetype-resources` plus the
descriptor that parameterises it.

**What the generated project contains.** A `pom.xml` inheriting from
`spring-boot-starter-parent`, depending on `judo-spring-boot-starter`,
`spring-boot-starter-{web,jdbc,logging,test}`, Lombok and HSQLDB, and binding
`judo-spring-maven-plugin`'s `transformerWorkflow` goal to `generate-sources`;
a JSL domain model at `src/main/resources/model/<ModelName>.jsl` seeded with a
`Person` entity carrying `firstName`, `lastName` and a derived
`fullName <= self.firstName + " " + self.lastName`; a
`<ApplicationName>.java` `@SpringBootApplication` that
`@Import`s the *generated* `<ModelName>DaoModules` class and logs on
`ApplicationReadyEvent`; an `application.yml` wiring an in-memory HSQLDB
datasource plus the Liquibase changelog the build generates
(`classpath:model/<model>-liquibase_hsqldb.changelog.xml`) and `judo.modelName`;
Logback configs for main and test; a `<ApplicationName>Tests.java` skeleton; and
a `.gitignore` (shipped as `__dot__gitignore`). So the developer writes `.jsl`,
and the plugin generates DAOs, APIs, Spring wiring and database migrations at
build time — the checked-in Java is a handful of lines.

**The invocation a user actually runs:**

```bash
mvn org.apache.maven.plugins:maven-archetype-plugin:3.1.1:generate -B \
  -DarchetypeGroupId=hu.blackbelt.judo.jsl \
  -DarchetypeArtifactId=judo-jsl-springboot-archetype \
  -DarchetypeVersion=1.0.4 \
  -DgroupId=com.example \
  -DartifactId=hello \
  -DmodelName=Hello
```

`modelName` is the only parameter with no default (regex `^[a-zA-Z0-9]+$`).
`groupId` defaults to `com.example`; `artifactId` defaults to
`${modelName.toLowerCase()}`; `package` defaults to `${groupId}.${artifactId}`;
`applicationName` defaults to `<ModelName>Application`. Four version properties
(`springbootVersion`, `jslSpringbootVersion`, `jslTatamiVersion`,
`springMavenPluginVersion`) carry defaults that the build rewrites — see the
version-sync note below. A `dot` property (regex `^\.{1}$`, default `.`) exists
solely so `__dot__`-prefixed template filenames land as dotfiles.

## Reactor map

`pom.xml` declares **no `<modules>`** — this is a single-module
(non-aggregator) reactor.

<modules>
  (none — single-module maven-archetype reactor)
</modules>

What this one POM contributes:

- **`maven-archetype` packaging** (archetype-packaging extension, archetype
  plugin 3.2.1) — makes the whole `archetype-resources` tree the published
  template payload.
- **`replacer` plugin** under the `updateVersions` profile — rewrites the
  version defaults inside the archetype descriptor and the integration-test
  `archetype.properties` from this POM's `<properties>`, so a generated project
  always pins the estate versions this build was cut against.
- **`maven-invoker-plugin` 3.3.0** under `archetypeTest` — generates a project
  from the archetype and runs a full Maven build on it.
- **Surefire 3.5.1**, **JaCoCo 0.8.12**, **Sonar 3.9.1.2184**, Logback 1.5.12 /
  SLF4J 2.0.16 — test execution, coverage, static analysis, logging.
- Release plumbing: GPG signing plus the dummy / Judo-Nexus / Maven-Central
  deployment profiles.

## Archetype anatomy

**Descriptor.** `src/main/resources/META-INF/maven/archetype-metadata.xml`
declares the `requiredProperties` above (each with its validation regex and
default) and the `fileSets`: everything is `filtered="true"` UTF-8, with
`src/main/java` and `src/test/java` marked `packaged="true"` so they land under
the chosen `package`, and `src/main/resources` contributing `**/*.xml`,
`**/*.yml` and `**/*.jsl`. Root-level `__dot__gitignore` and `README.md` are
filtered from the template root.

**Templating conventions.** Filenames use `__paramName__` substitution
(`__modelName__.jsl`, `__applicationName__.java`) and the `__dot__` prefix for
dotfiles. File content is Apache Velocity, so `#` and `$` must be escaped as
`${symbol_pound}` / `${symbol_dollar}` / `${symbol_escape}` — the generated
Java files open with the `#set(...)` preamble that defines them.

**Integration test.** `src/test/resources/projects/basic` holds the invoker
scenario: `archetype.properties` pins `modelName=Shop`,
`applicationName=ShopApplication`, `groupId=hu.blackbelt`, `artifactId=shop`,
`package=hu.blackbelt.shop` plus the four version properties. It generates that
Shop project and builds it, so a template change that breaks the generated
build fails here rather than in a user's terminal.

## Directory structure

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
│   │   ├── META-INF/maven/          # archetype-metadata.xml — parameters, file sets, validation
│   │   └── archetype-resources/     # Template files for generated projects
│   │       ├── pom.xml              # Generated project POM (Velocity template)
│   │       ├── README.md            # Generated project README
│   │       ├── __dot__gitignore     # → .gitignore in generated project
│   │       └── src/                 # Generated project source tree
│   └── test/resources/
│       └── projects/basic/          # Integration test: archetype generation + build verification
├── .vscode/settings.json           # VS Code Java settings
├── .zed/settings.json              # Zed editor Java settings
└── openspec/                        # OpenSpec configuration
```

## Technology stack

### Core technologies
- **Maven Archetype Plugin** 3.2.1 — project generation framework
- **Apache Velocity** — template engine for file content and filename substitution
- **Spring Boot** 3.5.0 — target framework for generated projects
- **JUDO JSL** — domain-specific language for model-driven code generation

### JUDO framework components (in generated projects)
- **judo-tatami-jsl** 1.1.4.x — JSL model processing and transformation
- **judo-spring-boot-starter** 1.0.4.x — Spring Boot integration for JUDO runtime
- **judo-spring-maven-plugin** 1.0.0.x — Maven plugin that runs model-to-code generation in `generate-sources` phase

## Build commands

A Maven wrapper (`./mvnw`) is available for consistent builds.

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

### Maven profiles

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

Test logging is configured by the root `logback-test.xml`.

## Development environment

**Required:**
- Java 21 JDK (Zulu JDK recommended)
- Maven 3.9.4+
- Git

**JVM configuration** (`.mvn/jvm.config`):
- `-Xms1024m -Xmx2048m` heap size
- `-Dfile.encoding=UTF-8`
- `--add-opens` flags for `java.lang`, `java.util`, `java.time` (required for JUDO runtime reflection)

## Git workflow

- **Main branch:** `develop`
- **Release branch:** `master`
- **Versioning:** `1.0.6-SNAPSHOT` (current), uses CI-flatten for development builds
- **Strategy:** GitFlow — feature branches merge to `develop`, release branches merge to both `master` and `develop`
- **CI/CD:** GitHub Actions with automated build, deploy, release, and PR merge workflows
- See [CIFLOW.md](.github/CIFLOW.md) for detailed workflow diagrams

## Code instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Important notes

1. **Version sync is critical:** After changing dependency versions in the root `pom.xml` properties, run `mvn -PupdateVersions validate` to propagate those versions into the archetype metadata and test properties. The `updateVersions` profile is active by default, so this happens automatically during normal builds.
2. **Do NOT reformat** the `<requiredProperty>` lines in `archetype-metadata.xml` that have `<!-- DO NOT REFORMAT -->` comments — the `maven-replacer-plugin` uses regex to find and replace version strings, and reformatting breaks the regex patterns.
3. **Velocity escaping:** Template files use `${symbol_pound}` for `#`, `${symbol_dollar}` for `$`, and `${symbol_escape}` for `\` to avoid conflicts with Velocity's own syntax. The `__dot__` prefix in filenames becomes `.` in the generated project.
4. **All commits must reference a JIRA ticket** (`JNG-xxx`).
5. **Integration tests** generate a sample `Shop` project and run a full Maven build on it. If archetype template changes break the generated project's build, the integration tests will catch it.

## Project facts

- **Repository:** BlackBeltTechnology/judo-jsl-springboot-archetype
- **License:** Eclipse Public License 2.0 (EPL-2.0)
- **Java version:** 21
- **Build system:** Maven 3.9.4+ with archetype-packaging extension
- Part of the [judo-community](https://github.com/BlackBeltTechnology/judo-community) ecosystem — the project-bootstrapping entry point for JUDO-based Spring Boot apps

## Related documentation

- [README.md](README.md) — Project overview and usage instructions
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and contribution guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD workflow documentation with Mermaid diagrams
- [JUDO Documentation](https://documentation.judo.technology) — Full JUDO platform documentation
- [judo-community](https://github.com/BlackBeltTechnology/judo-community) — Aggregator project for the JUDO ecosystem

<!-- dox-doctrine -->
## Documentation Update Protocol (WRITE discipline)

Per-directory `AGENTS.md` files form a tree. Each directory `AGENTS.md` is the
per-file record for the files in that directory. This module-root `AGENTS.md`
holds doctrine + architecture pointers only — never a per-file index.

**Keep the root lean.** This file loads into every agent turn — every byte costs
tokens on every turn. A verbose root file buries the rules the model must follow
(signal dilution) and measurably degrades adherence; a lean file keeps doctrine
salient. Default assumption: your update does NOT belong in the root — route it
by the table below.

**Route every doc update by kind:**

| Kind of update | Goes in |
|---|---|
| New file in a directory, or its per-file detail / change history | Nearest directory `AGENTS.md`. Add a `` | `<basename>` | <purpose> | `` row, path-alphabetical. |
| Data flow, protocol, architecture rationale | `docs/architecture.md` or a `docs/<topic>.md` |
| End-user / developer setup | `README.md` |
| Cross-cutting rule every agent needs every turn (rare) | this module-root `AGENTS.md` |

**Read before editing (chain walk).** Before editing a file, read the nearest
`AGENTS.md` chain root→leaf so you know the file's recorded purpose, contracts,
and change history. Do not edit blind.

**Update after editing (closeout pass).** After changing a file, update its row
in the nearest directory `AGENTS.md`: find the file's row, update its purpose in
place; if absent, add it in path-alphabetical order. New directory → scaffold
its `AGENTS.md`. One row per file. The purpose carries a one-line summary, key
exported symbols, contracts/invariants, and `See change: <id>` history.

**Row style (caveman).** Short declarative fragments. Drop articles. Subject →
verb → object, present tense. One fact per row. Prefer concrete tokens (paths,
symbols, env vars) over prose. Keep identifiers verbatim.

**Size rule — split an over-large directory `AGENTS.md` file-based.** pi
auto-injects a directory `AGENTS.md` on every turn when cwd sits at/below it, so
an over-large directory `AGENTS.md` is not supported. Split it file-based: a row
exceeding the length threshold promotes to a per-file `<File>.AGENTS.md`
sidecar carrying that file's full detail (including every `See change:`). The
sidecar is pull-only — its name is not `AGENTS.md`, so pi never auto-injects it
— yet it stays search-indexed (`agents` doc_type). The directory `AGENTS.md`
keeps a one-line summary plus a `→ see `<File>.AGENTS.md`` pointer. Rows within
the threshold stay verbatim (lossless).

## Finding docs (READ discipline)

`kb_*` tools are faster and cheaper than raw search — they return a one-line
purpose + key exports per file, not raw bytes. **This fires on the ACTION, not
the intent** — before you `grep`/`rg` for a symbol, `cat`/read a file to learn
what it does, or chase an import, the kb call goes first. It fires **even
mid-task when you already know the file**; knowing the file does not exempt you.
When your reflex is the left column, run the right column instead:

| You're about to… | Do this FIRST instead |
|---|---|
| `grep -rn "SymbolName" src/` — find where a fn / type / const lives | `kb_search --doc-type agents "SymbolName"` — tree indexes key exports per file |
| `grep -rn "feature\|topic" src/` — how does X work / where's X handled | `kb_search "feature topic"` |
| `cat` / read a file just to learn its purpose before editing | `kb agents <path>` — one-line purpose + exports + change history |
| chase imports / callers across files | `kb_neighbors <path\|heading>` |
| read one doc section in full | `kb_get <path> <section>` |

**Fall-through (explicit):** if the kb call returns nothing relevant, `rg` /
source read is allowed — then add the missing directory `AGENTS.md` row per the
WRITE discipline. kb does NOT replace grep; it goes first.
