# CI/CD Pipeline Overview for Leave Management Service

Based on your `Jenkinsfile` and project setup, here's how the CI/CD pipeline works:

## **1. Pipeline Trigger & Agent Selection**

```
- Branch: PHA-20843 (feature branch)
- Repository: leave-mgmt-service
- Jenkins Agent: Labeled as 'master' (initial), then dynamically selected based on branch
```

The pipeline uses conditional agent selection:
- `dev_deploy` branch → 'common' agent
- `master` branch → 'backend' agent
- Other branches → 'backend' agent

---

## **2. Build Stages Breakdown**

### **Stage 1: Clean Workspace & Checkout**
- Deletes previous workspace content
- Clones the latest code from your Git repository

### **Stage 2: Version Increment**
- If it's a release build, the version is automatically incremented
- Version is retrieved and stored for later use in artifact naming

### **Stage 3: Build & Publish Artifacts**
This is the core CI stage:

```groovy
gradle clean build -Pph_enable_nexus=yes -PappVersion=${version} ...
```

**What happens:**
1. Gradle compiles Java code
2. Runs tests and quality checks
3. Creates a JAR file: `leave-mgmt-service-{version}.jar`
4. Publishes to Nexus repository

**Repository Selection:**
- `dev_deploy` → `peoplehum-artifactory` (development artifacts)
- `master` → `ph-master-artifactory` (production artifacts)

### **Stage 4: Push to Git & Update Deployment**
- For release builds only:
    - Pushes version tags to Git
    - Updates deployment manifest in `deployment-ansible` repository with new version

---

## **3. Nexus Repository Management**

**Purpose:** Centralized artifact storage and versioning

```
Nexus Credentials: Retrieved from Jenkins secrets
├── dev_deploy branch
│   └── Published to: peoplehum-artifactory/
├── master branch
│   └── Published to: ph-master-artifactory/
└── Artifact Structure:
    leave_mgmt_service/
    ├── {version}/
    │   ├── leave-mgmt-service-{version}.jar
    │   └── config/
    │       ├── logback-spring.xml
    │       └── logback-access-spring.xml
```

**Artifacts Published:**
- Application JAR
- Logging configuration files (logback)

---

## **4. Gradle Build Configuration**

The build command passes these parameters:

| Parameter | Purpose |
|-----------|---------|
| `ph_enable_nexus=yes` | Enables Nexus publishing |
| `appVersion=${version}` | Sets artifact version |
| `repoUser/repoPassword` | Nexus authentication |
| `repoUrl` | Target repository URL |
| `runenv=dev` | Development environment flag |
| `--refresh-dependencies` | Forces dependency re-download |

---

## **5. Post-Build Actions**

```groovy
post {
  always {
    notification.buildStatusTeams(webhookUrl)  // Slack/Teams notification
    cleanWs()                                  // Clean workspace after build
  }
}
```

---

## **6. Complete Flow Diagram**

```
Code Push (PHA-20843)
    ↓
Jenkins Detects Change
    ↓
Checkout SCM
    ↓
Increment Version (if release)
    ↓
Select Agent (dev/master/other)
    ↓
Gradle Build (clean build)
    ↓
Publish JAR + Config Files to Nexus
    ↓
Push Tags to Git (if release)
    ↓
Update Deployment Manifest
    ↓
Send Teams Notification
    ↓
Clean Workspace
```

---

## **Key Points**

- **Nexus** acts as your artifact repository for dependency management and release distribution
- **Different repositories** for dev vs. production maintain environment separation
- **Version incrementing** is automatic for releases, enabling semantic versioning
- **Conditional logic** ensures release workflows (Git push, deployment updates) only run on tagged releases
- **Configuration files** are versioned alongside the JAR for environment consistency