# CI CD pipeline

````markdown
# 🧩 Continuous Integration and Continuous Deployment (CI/CD) — Detailed Notes

## 📘 Introduction

**CI/CD** stands for:
- **Continuous Integration (CI)** — integrating code frequently into a shared repository.
- **Continuous Deployment/Delivery (CD)** — automatically building, testing, and deploying applications.

The main goals of CI/CD:
- Deliver software **faster**
- Maintain **quality**
- Reduce **manual effort and risk**
- Enable **quick rollback and recovery**

---

## ⚙️ CI/CD Pipeline Overview

A **CI/CD pipeline** is a series of automated steps to take code from source to production.

### Typical Stages:
1. **Source** → Developers push code (e.g., to GitHub or GitLab)
2. **Build** → Compile and build artifacts (e.g., `.jar`, `.war`, Docker image)
3. **Test** → Run automated tests (unit, integration, etc.)
4. **Package** → Create and store deployable artifacts
5. **Deploy** → Deploy to staging or production
6. **Monitor** → Track performance, logs, and alerts post-deployment

---

## 🔄 Continuous Integration (CI)

### Core Ideas:
- Developers frequently integrate changes into a shared repository.
- Every commit triggers an **automated build and test process**.
- Detects and fixes bugs early.

### Benefits:
✅ Detects issues early  
✅ Reduces integration conflicts  
✅ Increases code quality  
✅ Enables faster development cycles  

### Example CI Flow:
1. Code pushed → triggers pipeline  
2. Automated build and unit tests  
3. Code analyzed for quality (SonarQube)  
4. Artifacts stored for deployment  

---

## 🚀 Continuous Deployment / Delivery (CD)

### Continuous Delivery
- Automatically deploys to a **staging** environment.
- Requires **manual approval** for production deployment.

### Continuous Deployment
- Fully **automated release** to production once tests pass.
- No human intervention.

### Benefits:
✅ Faster feature releases  
✅ More reliable delivery  
✅ Reduced deployment risks  

````

## 🧱 Example: Jenkins CI/CD Pipeline

```yaml
pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "your.repo.io"
        IMAGE_NAME = "myapp"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/project.git'
            }
        }

        stage('Build (Gradle)') {
            steps {
                // Use the Gradle wrapper from the repo
                sh './gradlew clean build'
            }
        }

        stage('Test (Gradle)') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                sh "docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} ."
                withCredentials([usernamePassword(credentialsId: 'docker-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo $DOCKER_PASS | docker login ${DOCKER_REGISTRY} -u $DOCKER_USER --password-stdin"
                }
                sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Deploy to Staging') {
            steps {
                // Example: update k8s image (requires kubectl configured on agent)
                sh "kubectl set image deployment/${IMAGE_NAME} ${IMAGE_NAME}=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} --namespace=staging"
                sh "kubectl rollout status deployment/${IMAGE_NAME} --namespace=staging"
            }
        }
    }

    post {
        success {
            echo "Build ${IMAGE_TAG} succeeded."
        }
        failure {
            echo "Build ${IMAGE_TAG} failed."
        }
    }
}
```

---

## 🧰 Popular CI/CD Tools

| Category               | Tools                                        |
| ---------------------- | -------------------------------------------- |
| CI Servers             | Jenkins, GitHub Actions, GitLab CI, CircleCI |
| Artifact Repositories  | Nexus, JFrog Artifactory                     |
| Containerization       | Docker, Podman                               |
| Orchestration          | Kubernetes, OpenShift                        |
| Monitoring             | Prometheus, Grafana, ELK Stack               |
| Infrastructure as Code | Terraform, Ansible, Helm                     |

---

## 🧩 CI/CD in Containerized Environments

### Typical Flow:

1. Build Docker image using **Dockerfile**
2. Push to registry (Docker Hub, ECR, GCR)
3. Deploy via **Kubernetes manifests** or **Helm charts**
4. Automatically roll out changes with a strategy (rolling, blue-green, etc.)

### Example Dockerfile:

```dockerfile
FROM openjdk:17-jdk
COPY target/myapp.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## ⚙️ Deployment Strategies in CI/CD

Deployment strategies define **how updates are rolled out** to minimize downtime and risk.

### 1️⃣ Rolling Deployment

* Gradually replaces old instances with new ones.
* Ensures zero downtime.
* Ideal for stateless services.

**Pros:**

* Smooth rollout
* No extra infrastructure

**Cons:**

* Harder rollback
* Risk if new version is unstable

**Example (Kubernetes):**

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 1
    maxSurge: 1
```

---

### 2️⃣ Blue-Green Deployment

* Maintain **two environments**:

    * **Blue** → current live version
    * **Green** → new version

Switch traffic from blue → green after testing.

**Pros:**

* Instant rollback
* No downtime

**Cons:**

* Requires double infrastructure

**Example Flow:**

```
1. Deploy new version to Green
2. Test in Green
3. Switch traffic (Blue → Green)
4. Old Blue kept as backup
```

---

### 3️⃣ Canary Deployment

* Gradually release new version to a **subset of users**.
* Monitor performance before full rollout.

**Pros:**

* Real-world testing on limited users
* Easy rollback

**Cons:**

* Complex routing setup

**Example:**

* 10% users → version 2.0
* 90% users → version 1.0
* If stable → shift all traffic to 2.0

---

### 4️⃣ Recreate Deployment

* Stops the old version completely before starting the new one.

**Pros:**

* Clean and simple

**Cons:**

* Causes downtime

Used in small projects or non-critical systems.

---

### 5️⃣ Shadow Deployment

* Deploy new version **in parallel** with production traffic (read-only).
* Monitors performance without serving real users.

**Pros:**

* Real load testing
* No risk to users

**Cons:**

* Requires additional infra & traffic duplication

---

## 🧠 Best Practices

1. **Use feature flags** to control new releases safely.
2. **Automate tests** for unit, integration, and performance levels.
3. **Separate environments** — Dev, QA, Staging, Prod.
4. **Secure secrets** using Vault, GitHub Secrets, or AWS Secrets Manager.
5. **Enable monitoring & rollback** mechanisms.
6. **Use container orchestration** (Kubernetes, ECS).
7. **Apply proper branching strategy** (GitFlow, Trunk-based).

---

## 🧩 Example CI/CD Flow Diagram (Conceptual)

```
   Developer Commit
          ↓
   ┌──────────────┐
   │ Source Code  │
   └──────────────┘
          ↓
   ┌──────────────┐
   │ Build Stage  │
   └──────────────┘
          ↓
   ┌──────────────┐
   │ Test Stage   │
   └──────────────┘
          ↓
   ┌──────────────┐
   │ Package      │
   └──────────────┘
          ↓
   ┌──────────────┐
   │ Deploy (Blue/Green, Rolling, etc.) │
   └──────────────┘
          ↓
   ┌──────────────┐
   │ Monitor      │
   └──────────────┘
```

---

## 🧾 Summary

| Concept             | Description                                                |
| ------------------- | ---------------------------------------------------------- |
| **CI**              | Continuous Integration — frequent commits, automated tests |
| **CD (Delivery)**   | Automated deployment to staging with manual prod approval  |
| **CD (Deployment)** | Fully automated deployment to production                   |
| **Rolling**         | Gradual update without downtime                            |
| **Blue-Green**      | Two environments, instant switch                           |
| **Canary**          | Partial rollout for testing stability                      |
| **Recreate**        | Stop old → deploy new (downtime)                           |
| **Shadow**          | Parallel, non-user facing testing                          |

---

## 🏁 Final Thoughts

A robust **CI/CD pipeline**:

* Reduces risk and downtime
* Increases developer productivity
* Promotes DevOps culture
* Enables faster, safer, and more reliable releases

**CI/CD is not just automation — it’s a mindset of continuous improvement.**

```
