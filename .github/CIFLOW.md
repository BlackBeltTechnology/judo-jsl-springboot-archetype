# Development Version and Branch Handling

This document describes the Git branching strategy, version numbering scheme, and CI/CD pipeline used by JUDO NG modules.

## Branching Strategy

The project uses a **GitFlow**-based workflow. All branches serve specific purposes in the release lifecycle:

| Branch Pattern | Purpose | Based On |
|----------------|---------|----------|
| `develop` | Main development branch — contains latest sources of the active version | — |
| `feature/JNG-xxx_summary` | New feature work for the active version | `develop` |
| `release/x.y.z` or `x_y_z` | Release preparation and stabilization | `develop` |
| `bugfix/JNG-xxx_summary` | Fixes for issues found during release testing | Release branch |
| `support/JNG-xxx_summary` | Minor updates to a previous release | Release branch |
| `hotfix/JNG-xxx_summary` | Critical fixes applied to both release and master | `master` |
| `master` | Latest released sources of the active version | Release merges |

```mermaid
gitGraph
    commit id: "initial"
    branch develop
    checkout develop
    commit id: "dev-1"
    branch feature/JNG-1
    commit id: "feat-1"
    commit id: "feat-2"
    checkout develop
    merge feature/JNG-1 id: "merge-feat"
    commit id: "dev-2"
    branch release/1.0
    commit id: "rc-1"
    branch bugfix/JNG-4
    commit id: "fix-1"
    checkout release/1.0
    merge bugfix/JNG-4 id: "merge-fix"
    checkout develop
    merge release/1.0 id: "merge-release-to-dev"
    checkout main
    merge release/1.0 id: "v1.0" tag: "v1.0"
```

## Version Numbers

Version numbers follow **semantic versioning** with these rules:

| Situation | Version Change | Example |
|-----------|---------------|---------|
| Starting a `feature/` branch | No change | — |
| Starting a release branch | 2nd number incremented on `develop` | `1.1.0` → `1.2.0-SNAPSHOT` |
| `bugfix/` branches | No change (fixes go into release before merging to master) | — |
| Starting a `support/` branch | 3rd number incremented | `1.0.0` → `1.0.1` |
| Starting a `hotfix/` branch | 4th number incremented | `1.0.0` → `1.0.0.1` |

### Development vs. Release Versions

- **Development builds** (from `develop` or `increment/*`): `major.minor.qualifier.timestamp_commitId_branchName`
- **Release builds** (from `master` or `release/*`): `major.minor.qualifier` (clean, no SNAPSHOT)

## GitHub Actions Workflows

The CI/CD system is composed of four interconnected workflows that trigger each other:

```mermaid
flowchart TB
    subgraph Triggers
        push_dev["Push to develop"]
        pr["PR to develop/master/\nincrement/*/release/*"]
        push_master["Push to master"]
        push_tag["Push merge-pr/* tag"]
        manual["Manual trigger\n(with version)"]
    end

    subgraph Workflows
        build["build.yml"]
        merge["merge-pr-tagged.yml"]
        release_master["create-release-on-master.yml"]
        release["release.yml"]
    end

    push_dev --> build
    pr --> build
    build -->|"increment/*, release/*"| merge
    push_tag --> merge
    merge -->|"major.minor.qualifier"| push_master
    merge -->|"other format"| push_dev
    push_master --> release_master
    manual --> release
    release -->|"PR to master"| build
    release -->|"PR to develop"| build
```

### build.yml — Main CI Pipeline

Triggers on pushes to `develop` and PRs to `develop`, `master`, `increment/*`, and `release/*` branches.

```mermaid
flowchart TD
    A["Push or PR"] --> B{"Branch type?"}
    B -->|"master, release/*"| C["Version from pom.xml\n(without -SNAPSHOT)"]
    B -->|"develop, increment/*"| D["Version: major.minor.qualifier\n.timestamp_commitId_branch"]
    C --> E["Build & Deploy to Nexus"]
    D --> E
    E --> F["Create git tag\nv<version>"]
    F --> G{"Branch type?"}
    G -->|"increment/*, release/*"| H["Create merge-pr/<version> tag\n→ triggers merge-pr-tagged.yml"]
    G -->|"develop"| I["Build changelog\n→ Create GitHub pre-release"]
```

### merge-pr-tagged.yml — PR Merge Automation

Triggered when a `merge-pr/*` tag is pushed. Routes the PR to the correct target branch based on version format:

```mermaid
flowchart TD
    A["merge-pr/* tag pushed"] --> B["Extract version from tag"]
    B --> C{"Version format?"}
    C -->|"major.minor.qualifier\n(release)"| D["Merge PR to master\n→ triggers create-release-on-master.yml"]
    C -->|"Other format\n(development)"| E["Squash PR to develop\n→ triggers build.yml"]
    D --> F["Delete merge-pr tag"]
    E --> F
```

### create-release-on-master.yml — Release Publishing

Triggered on pushes to `master`. Creates a GitHub release with a generated changelog.

### release.yml — Manual Release Trigger

Manually triggered with a version parameter (either `auto` to read from pom.xml, or an explicit `major.minor.qualifier` version). Creates two PRs:

1. **PR to `master`** with the release version
2. **PR to `develop`** with the next development version (qualifier + 1)

Both PRs trigger `build.yml` for validation.

## Development Rules

> **Important:** All commits must reference a JIRA ticket number (`JNG-xxx`). There is no commit without a ticket number.

Issue tracking: [JIRA Dashboard](https://blackbelt.atlassian.net/jira/dashboards)
