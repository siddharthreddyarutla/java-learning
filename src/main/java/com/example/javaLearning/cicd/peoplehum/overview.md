Got it — you want **ONE single, clean, continuous text-based flowchart** that explains the *entire overview* end-to-end, including:

* What triggers
* How Jenkins gives the version
* How Gradle builds using that version
* How artifacts are uploaded
* How deployment repo updates
* How deployment happens

Here is the **simple, compact, single-flow text chart** 👇

---

# ⭐ **SINGLE FLOW-CHART (END-TO-END OVERVIEW)**

### *CI/CD Pipeline for Your Service (shift-schedule-service)*

```
┌─────────────────────────────────────────────────────────────┐
│                 1. Developer pushes code                    │
│                 (to Git repository)                         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│        2. Jenkins Pipeline is automatically triggered        │
│        (Based on branch: dev_deploy, master, etc.)          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│   3. Jenkins loads shared versioning library (versions.*)    │
│   → Determines build type (release or snapshot)              │
│   → Computes VERSION (ex: 2.3.14)                            │
│   → THIS VERSION IS GIVEN TO GRADLE                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│   4. Jenkins runs Gradle Build with the calculated VERSION    │
│                                                              │
│   gradle clean build                                          │
│       -PappVersion=VERSION  <── Jenkins injects version       │
│       -Pph_enable_nexus=yes                                   │
│       -PrepoUrl=<Nexus URL>                                   │
│       -PrepoUser/<password>                                   │
│                                                              │
│  Inside Gradle build:                                        │
│     • Reads dependencies from Nexus                           │
│     • Compiles Java                                           │
│     • Packages jar: shift-schedule-service-VERSION.jar        │
│     • Copies/renames jars as needed                           │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│      5. Jenkins uploads artifacts to PeopleHum Artifactory   │
│                                                              │
│      artifacts.publishPeoplehum(                             │
│          "shift-schedule-service-VERSION.jar",               │
│          "shift-schedule-service/VERSION"                    │
│      )                                                       │
│                                                              │
│  → Jar + config files stored under versioned folder          │
│  → This becomes the source for deployment                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│      6. For release builds: Update Deployment Repo           │
│                                                              │
│   Jenkins commits VERSION into manifest-qa.yaml in           │
│   deploy-peoplehum.git                                       │
│                                                              │
│   Example manifest:                                           │
│     shift_schedule_service:                                  │
│         version: VERSION                                      │
│         artifactPath: shift-schedule-service/VERSION         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│       7. Deployment Pipeline (Ansible) Starts                │
│                                                              │
│   Reads deployment repo manifest                             │
│   → Finds: service name + VERSION + artifact path            │
│   → Downloads the EXACT same artifact from Nexus             │
│   → Starts rolling deployment                                │
│                                                              │
│   Deploy flow:                                               │
│      Node 1 → deploy → health check → OK → Node 2            │
│      If any node fails → rollback to previous version        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     8. Final Status Sent                     │
│              (Teams webhook + Jenkins cleanup)               │
└─────────────────────────────────────────────────────────────┘
```

---

# ⭐ **Plain-English Summary (Super Simple)**

1. **Developer pushes code** → Jenkins triggers.
2. Jenkins calculates the **VERSION** (ex: 2.3.14).
3. Jenkins passes this version to **Gradle** using `-PappVersion=<version>`.
4. Gradle builds the jar **using that version**.
5. Jenkins uploads the built artifact to **PeopleHum Artifactory** under that version folder.
6. Jenkins writes the same version into the **deployment repo manifest**.
7. Ansible reads manifest → deploys **the exact same version** to Node1 then Node2.
8. Rollback happens automatically if Node1 or Node2 fails.
9. Teams notification sent → pipeline ends.
