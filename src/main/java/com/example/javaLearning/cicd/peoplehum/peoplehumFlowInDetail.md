Nice, this is a *really* good snapshot of how your company’s pipeline is wired. Let’s walk through it like a story, end-to-end, and tie Gradle + Nexus + Jenkins + Ansible together.

I’ll explain in this order:

1. Big-picture CI/CD flow in your company
2. What your **Gradle build** is doing
3. What your **Jenkins pipeline** is doing (stage by stage)
4. How this connects to **Ansible deployments** and your two nodes
5. Short recap for interviews / documentation

---

## 1️⃣ Big-Picture Flow (What happens from code → deployment)

From what you shared, your company’s flow is roughly:

1. **Developer pushes code** → Git repo (this project: `shift-schedule-service`).
2. **Jenkins pipeline triggers** for that branch.
3. Jenkins:

    * Figures out the **version** (using the shared `versions` library).
    * Runs **Gradle build**:

        * Uses **Nexus** to download dependencies.
        * Builds the Spring Boot JAR.
        * Names the JAR using the calculated `version`.
    * **Publishes artifacts** (JAR + config files) to a central artifact store (via `artifacts.publishPeoplehum`, which likely sits on top of Nexus or some internal artifactory).
4. For release builds:

    * Jenkins **pushes tags** back to Git (source repo).
    * Jenkins **updates a separate deployment repo** (`deploy-peoplehum.git`) with the new version in `manifest-qa.yaml`.
5. A separate **Ansible-based deployment pipeline** (or job) uses that **deployment repo + manifest**:

    * Reads service name & version
    * Fetches JAR from artifact repo (Nexus/Peoplehum artifactory)
    * Deploys to **two nodes**, one by one (rolling style)
    * If first node fails health check → **rollback**.

So there are **two Git repos** involved:

* **Code repo**: `shift-schedule-service` (where your Gradle & Jenkinsfile live)
* **Deployment repo**: `deploy-peoplehum` (Ansible manifests & playbooks)

---

## 2️⃣ Gradle Build File – What it’s actually doing

Let’s break down your `build.gradle` in logical sections.

### 2.1 `buildscript { ... }`

This part configures the **build classpath** (plugins and tools Gradle itself needs).

```groovy
buildscript {
    repositories {
        mavenCentral()
        if (project.hasProperty("ph_enable_nexus")) {
            maven {
                name 'nexus'
                url repoUrl
                credentials {
                    username System.getenv("NEXUS_CREDENTIALS_USR")
                    password System.getenv("NEXUS_CREDENTIALS_PSW")
                }
            }
        } else {
            mavenLocal()
        }
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("com.nethum.common:custom-plugin-service:${commonsVersion}")
        classpath("org.mariadb.jdbc:mariadb-java-client:${mariaClientVersion}")
    }
}
```

Key points:

* `mavenCentral()` always used.
* If `-Pph_enable_nexus` property is passed → it also uses the **Nexus repository** at `repoUrl` with **credentials from environment** (`NEXUS_CREDENTIALS_USR/PSW`).

    * These env vars are injected by Jenkins via `NEXUS_CREDENTIALS`.
* Else, it uses `mavenLocal()` (your local `~/.m2` / local cache).

So **for CI builds**, Jenkins passes `-Pph_enable_nexus=yes`, so Gradle uses **Nexus** to fetch buildscript dependencies (Spring Boot plugin, custom plugin, MariaDB driver).

---

### 2.2 Plugins & basic project config

```groovy
plugins {
    id "org.sonarqube" version "3.3"
    id 'org.ajoberstar.grgit' version '4.1.1'
}
apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'idea'
apply plugin: 'org.springframework.boot'
apply plugin: "jacoco"
apply plugin: 'propertyFileCheck'
```

* `sonarqube` → code quality analysis.
* `grgit` → git integration (used to get branch name).
* `java`, `spring-boot` → standard Java + Spring Boot project.
* `jacoco` → code coverage.
* `propertyFileCheck` → custom plugin to validate property files (probably ensures required keys per environment).

---

### 2.3 Versioning logic

```groovy
if (project.hasProperty('appVersion')) {
    version="${appVersion}"
} else {
    version="${version}-SNAPSHOT-${grgit.branch.current().name}"
}
```

* If Jenkins passes `-PappVersion=...` → that becomes the **project version**.
* Else, it falls back to some default (version + `-SNAPSHOT-branchName`).

In your pipeline, Jenkins **always passes** `-PappVersion=${version}`, where `version` is computed earlier using the shared `versions` lib.
So effectively, **CI controls the version**, not the static `build.gradle`.

This `version` is used by Gradle to name the jar:

> `build/libs/shift-schedule-service-${version}.jar`

---

### 2.4 Repositories for app dependencies

```groovy
repositories {
    mavenCentral()
    if (project.hasProperty("ph_enable_nexus")) {
        maven {
            name 'nexus'
            url repoUrl
            credentials {
                username System.getenv("NEXUS_CREDENTIALS_USR")
                password System.getenv("NEXUS_CREDENTIALS_PSW")
            }
        }
    } else {
        mavenLocal()
    }
}
```

Same logic as `buildscript`, but this time for **your application dependencies** (Spring Boot, Kafka, your internal SDKs, etc).

* With `-Pph_enable_nexus=yes`, Gradle will pull dependencies from your Nexus repo (and probably your internal `com.peoplehum.*` artifacts live there).

---

### 2.5 Dependencies

Huge list of `implementation(...)` etc. Key idea:

* **Spring Boot starters**, `spring-data-jpa`, `spring-data-redis`, `springdoc-openapi`, etc.
* **Internal SDKs**:

    * `com.nethum.common:*`
    * `com.peoplehum.common:*`
* DB drivers (MariaDB, MySQL).
* Redis, Kafka, Consul, Vault, etc.
* Lombok, Jackson, Guava, logging libraries.

These dependencies are all:

> **Downloaded from Nexus (or mavenCentral) during the Gradle build.**

---

### 2.6 `copyJar` task

```groovy
task copyJar(type: Copy) {
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  from 'build/libs/'
  into 'build/libs/'
  include('shift-schedule-service-*.jar')
  rename { fileName -> "shift-schedule-service-2.0.0-SNAPSHOT.jar" }
}
build.finalizedBy copyJar
```

* After the `build` task completes, `copyJar` runs (`finalizedBy`).
* It takes the built jar(s) matching `shift-schedule-service-*.jar` and **duplicates/renames** one as:

    * `shift-schedule-service-2.0.0-SNAPSHOT.jar`

This looks like:

* A **fixed-name jar** that some other process (maybe older Ansible playbooks, Docker builds, or scripts) expect.
* Meanwhile, Jenkins itself publishes the **versioned jar**: `${appName}-${version}.jar`.

So you end up with:

* `shift-schedule-service-<computedVersion>.jar`
* `shift-schedule-service-2.0.0-SNAPSHOT.jar` (fixed name)

---

### 2.7 `build.dependsOn propertyFileCheck`

```groovy
build.dependsOn propertyFileCheck
```

* The `build` task **depends on** `propertyFileCheck`.
* If property validation fails → whole build fails.
* This ensures you **never produce an artifact with wrong/missing config keys**.

---

### 2.8 Gradle lifecycle in your build

When Jenkins runs:

```bash
gradle clean build -Pph_enable_nexus=yes -PappVersion=${version} ...
```

Roughly:

1. **Initialization** – loads build.gradle, sets project, reads properties.
2. **Configuration** – configures tasks, sets version, repositories, dependencies.
3. **Execution**:

    * `clean` → deletes `build/`.
    * `propertyFileCheck` → validates configs.
    * `compileJava`, `processResources`, `test`, `bootJar` → builds Spring Boot jar.
    * `build` wrapper task.
    * `copyJar` runs after `build`.

Result: jar(s) in `build/libs/`.

---

## 3️⃣ Jenkins Pipeline – What each stage is doing

Your Jenkinsfile:

```groovy
@Library('jenkins-library@0.30.0') _
appName = 'shift-schedule-service'
```

This imports a **shared Jenkins library** (`jenkins-library`) that provides reusable steps:

* `versions` (version handling)
* `artifacts` (artifact publishing)
* `manifest` (deployment manifest publishing)
* `notification` (Teams notifications)
* etc.

---

### 3.1 Pipeline setup

```groovy
pipeline {
  agent { label 'master' }
  options {
    disableConcurrentBuilds()
    skipDefaultCheckout true
  }
  tools{
    jdk 'Java17'
    gradle "gradle-8.0.2"
  }

  environment {
    webhookUrl = credentials('ph-build-status')
    NEXUS_CREDENTIALS = credentials('nexus-artifactory')
  }
  ...
}
```

* Runs on nodes with label `master` (or later overrides).
* No parallel builds for the same job (`disableConcurrentBuilds`).
* `skipDefaultCheckout` because you manually control `checkout scm`.
* Installs/configures:

    * JDK 17
    * Gradle 8.0.2
* Environment:

    * `webhookUrl` → used for Teams notifications.
    * `NEXUS_CREDENTIALS` → Jenkins credential, which exposes:

        * `NEXUS_CREDENTIALS_USR`
        * `NEXUS_CREDENTIALS_PSW`

Those env vars are used by **Gradle** to authenticate with Nexus.

---

### 3.2 Stage: “Clean WS and checkout SCM”

```groovy
stage('Clean WS and checkout SCM') {
  steps {
    deleteDir()
    checkout scm
  }
}
```

* Cleans workspace.
* Checks out your **code repo** (current branch).

---

### 3.3 Stage: “Increment Version”

```groovy
stage('Increment Version') {
  steps {
    script {
      if (versions.isReleaseBuild()) {
        versions.increment('java')
      }
      version = versions.getVersion('java', false)
    }
  }
}
```

* Uses `versions` helper from shared library.
* `versions.isReleaseBuild()` likely checks:

    * Branch name (`master`, `release/*`) or tags.
* If it’s a **release build**:

    * `versions.increment('java')` bumps the version in some file (like `version.properties` / `gradle.properties`).
* `version = versions.getVersion('java', false)`:

    * Reads the current version value and stores it in a pipeline variable `version`.
* This `version` is passed to Gradle as `-PappVersion`, and used in:

    * Jar name
    * Artifact publishing path
    * Manifest updates

So **Jenkins owns the versioning scheme**.

---

### 3.4 Stage: “Build and publish artifacts”

This is the core CI build.

#### Agent selection

```groovy
agent {
  node {
    label "${env.BRANCH_NAME == 'dev_deploy' ? 'common' : env.BRANCH_NAME == 'java_dev_deploy' ? 'common' : env.BRANCH_NAME == 'master' ? 'backend' : 'backend'}"
  }
}
```

* Chooses node type based on **branch**:

    * `dev_deploy` / `java_dev_deploy` → `common`
    * `master` → `backend`
    * others → `backend`
* This lets you run builds on different pools depending on branch.

#### Steps

```groovy
steps {
  deleteDir()
  checkout scm
  script {
    if (env.BRANCH_NAME == 'dev_deploy') {
      repoUrl = 'https://nexus.peoplehum.org/repository/peoplehum-artifactory/'
    } else if (env.BRANCH_NAME == 'java_dev_deploy') {
      repoUrl = 'https://nexus.peoplehum.org/repository/peoplehum-artifactory/'
    } else {
      repoUrl = 'https://nexus.peoplehum.org/repository/ph-master-artifactory/'
    }
    sh 'java -version'
    sh 'gradle --version'
    sh " gradle clean build -Pph_enable_nexus=yes -PappVersion=${version} -PrepoUser=${NEXUS_CREDENTIALS_USR} -PrepoPassword=${NEXUS_CREDENTIALS_PSW} -PrepoUrl=${repoUrl} -Prunenv=dev --refresh-dependencies"
    artifacts.publishPeoplehum("build/libs/${appName}-${version}.jar", "${appName}/${version}")
    artifacts.publishPeoplehum("config/logback-spring.xml", "${appName}/${version}/config")
    artifacts.publishPeoplehum("config/logback-access-spring.xml", "${appName}/${version}/config")
  }
}
```

What’s happening:

1. Clean & checkout again (to ensure fresh workspace on the chosen agent).

2. Decide **which Nexus repo** to use:

    * Dev branches → `peoplehum-artifactory`
    * Master → `ph-master-artifactory`

3. Log Java & Gradle versions.

4. Run Gradle:

   ```bash
   gradle clean build \
     -Pph_enable_nexus=yes \
     -PappVersion=${version} \
     -PrepoUser=${NEXUS_CREDENTIALS_USR} \
     -PrepoPassword=${NEXUS_CREDENTIALS_PSW} \
     -PrepoUrl=${repoUrl} \
     -Prunenv=dev \
     --refresh-dependencies
   ```

    * `ph_enable_nexus=yes` → build uses Nexus.
    * `appVersion` → sets project `version`.
    * `repoUrl` → passed so Gradle knows which Nexus repo to use.
    * `runenv=dev` → probably used by `propertyFileCheck` or your config logic.
    * `--refresh-dependencies` → forces fresh dependency resolve (no stale cache).

5. After build succeeds:

    * `artifacts.publishPeoplehum("build/libs/${appName}-${version}.jar", "${appName}/${version}")`
    * This uploads:

        * Jar to path: `shift-schedule-service/<version>/shift-schedule-service-<version>.jar`
    * And also uploads config files:

        * `config/logback-spring.xml`
        * `config/logback-access-spring.xml`
    * Under: `shift-schedule-service/<version>/config`

So this stage:

> **Builds & publishes versioned artifacts** to your centralized artifact storage (backed by Nexus or related infra).

---

### 3.5 Stage: “Push to Git and Update Deployment Repo”

```groovy
stage ('Push to Git and Update Deployment Repo') {
  steps {
    script {
      if (versions.isReleaseBuild()) {
        sshagent(['jenkins-peoplehum']) {
          sh "git push origin HEAD:$BRANCH_NAME --tags"
        }
        manifest.publish(
          'shift_schedule_service',
          'git@gitlab.peoplehum.org:PeopleHum/deploy-peoplehum.git',
          'envs/qa',
          versions.getVersion('java'),
          "$WORKSPACE/deploy-peoplehum/manifest-qa.yaml"
        )
      }
    }
  }
}
```

Only for **release builds**:

1. Uses SSH credentials `jenkins-peoplehum` to:

    * `git push origin HEAD:$BRANCH_NAME --tags`
    * Push current branch and tags (i.e., new version tag) to remote.

2. Calls `manifest.publish(...)`:

    * Service name: `shift_schedule_service` (note: underscore, not hyphen).
    * Deployment repo: `deploy-peoplehum.git`.
    * Path: `envs/qa` → indicates this is **QA environment** manifest.
    * Version: `versions.getVersion('java')` → same version used in build.
    * Writes/updates: `manifest-qa.yaml` in that deployment repo.

This typically does something like:

```yaml
services:
  shift_schedule_service:
    version: 1.2.3
    artifactPath: shift-schedule-service/1.2.3
```

(Exact format hidden inside `manifest.publish`.)

This repo + manifest is what **Ansible** or another deploy tool uses later to know:

* Which **artifact version** to deploy.
* For which **service**.
* In which **environment** (here QA).

---

### 3.6 Post actions

```groovy
post {
  always {
    script {
      notification.buildStatusTeams(webhookUrl)
    }
    cleanWs()
  }
}
```

* After **every build** (success or failure):

    * Sends build status to Teams via `notification.buildStatusTeams(webhookUrl)`.
    * Cleans workspace.

---

## 4️⃣ Where Ansible fits in (deploying to two nodes)

You mentioned earlier:

> “We have two nodes / instances, Ansible deploys, checks first node, then second; rollback if failure.”

Given your Jenkinsfile:

* **Build pipeline** ends by:

    * Publishing JAR + configs to artifact repository.
    * Publishing version info to deployment repo (`deploy-peoplehum`).

Then deployment flow probably is:

1. A **separate Jenkins job or Ansible Tower/AWX template** reads from `deploy-peoplehum` repo.
2. It loads `manifest-qa.yaml` to know:

    * `shift_schedule_service` version (e.g., `1.2.3`)
    * Where artifact lives (artifact path in Nexus / Peoplehum artifactory).
3. Ansible Playbook runs:

    * **Hosts**: your two app servers for `shift_schedule_service` (maybe in group `[shift_schedule_service]`).
    * **Serial**: 1 (deploy one node at a time, rolling).
4. Steps per node (typical pattern):

    * Download JAR from artifact repo using version in manifest.
    * Stop existing service (or do rolling restart).
    * Replace JAR.
    * Start service.
    * Health check (HTTP /actuator/health or custom).
    * If OK → move to next node.
    * If fail → rollback JAR from previous version & restart.

So your end-to-end story is:

> Code → Jenkins build with Gradle → Artifact + configs published → Version written to deployment manifest → Ansible reads manifest → Rolling deployment to two nodes with health-check-based rollback.

---

## 5️⃣ Short Recap (For “explain our CI/CD pipeline”)

You can describe it like this:

> “We use a Gradle + Jenkins + Nexus + Ansible based CI/CD pipeline.
>
> When we push code, Jenkins triggers a pipeline that calculates the application version using a shared `versions` library. It then runs a Gradle build with `-PappVersion`, using Nexus as the dependency and artifact repository. Gradle builds a Spring Boot jar named `shift-schedule-service-<version>.jar`, validates property files, and produces the artifacts under `build/libs`.
>
> Jenkins then publishes the versioned jar and related config files to our internal artifact storage using `artifacts.publishPeoplehum`, storing them under `shift-schedule-service/<version>/...`. For release builds, the pipeline also pushes tags back to Git and updates a separate deployment repo (`deploy-peoplehum`) by writing the new version into a manifest file like `envs/qa/manifest-qa.yaml`.
>
> Our Ansible-based deployment pipeline then uses this manifest to know which version to deploy. It fetches the jar from the artifact store and performs a rolling deployment across our two nodes: it upgrades one node at a time, checks health, and only proceeds to the second node if the first is healthy; otherwise, it rolls back to the previous version.”

If you want, next I can:

* Mock a **sample `manifest-qa.yaml`** based on this flow
* Sketch an example **Ansible playbook** that would implement the “two-node rolling with rollback” logic you described.
