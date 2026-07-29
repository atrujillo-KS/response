# Katalon Studio Test Automation

Automated test suite for financial product recommendation engines, goal-setting workflows, API services, and post-release validation.

## Projects

| Project | Description | Test Suite |
|---------|-------------|------------|
| **RESPONSE** | Financial product recommendation engine — auto, bond, budget, business, card, college, credit line, fund, home, HSA, insurance, life insurance, paycheck, retirement, Roth IRA, savings, stock | `Headless-PROD` (8 concurrent instances) |
| **GS2.0** | Goal Setting 2.0 — new home buyer, refinance mortgage, student loans, business credit, debt consolidation, vehicle finance, payment processing | `Headless-PROD` (6 concurrent instances) |
| **API_Services** | API endpoint testing via CCV2 runner | `RES-API-Collection` |
| **Educators 2.0** | Education product tests — auto, banking, college planning, credit, home, insurance, investing, retirement, small business, taxes | `QA-Headless` |
| **PostRelease** | Post-release regression — client heartbeat checks, white-label platform validation (PROD/QA/STG) | `POST_RELEASE_ALL` |
| **Response_Services** | Shared API services | `RESPONSE API` |

## Jenkins CI/CD

### Daily Automated Run

The **All_Automation** orchestrator job runs daily at **3:00 AM** server time, executing RESPONSE then GS2.0 sequentially.

### Job Structure

```
All_Automation (orchestrator)
  ├── RES_2.0 (RESPONSE tests)
  └── GS_2.0 (GS2.0 tests)
```

### Running Individual Projects

Each project has its own Jenkins job that can be triggered independently. The Jenkinsfiles are thin wrappers that load shared pipeline logic from `Jenkinsfile-runner.groovy`.

### Reports

Each test run generates:
- **Test Summary Report** — pass/fail counts, failure details with commands and actual vs expected values
- **Katalon Test Report** — native Katalon HTML report
- **Resource Usage Report** — memory, CPU load, and process saturation metrics sampled every 60s

## Tech Stack

- **Katalon Studio** (v10.4.3) — test framework
- **Groovy** — test scripts and custom keywords
- **Chrome** (headless) — browser execution
- **Jenkins** — CI/CD orchestration
- **katalonc** — CLI execution engine

## Project Layout

```
Katalon Studio/
├── Jenkinsfile                     # Orchestrator pipeline
├── Jenkinsfile-runner.groovy       # Shared pipeline logic
├── RESPONSE/
│   ├── Jenkinsfile                 # Thin wrapper
│   ├── RESPONSE.prj               # Katalon project file
│   ├── Test Suites/               # Test suite collections
│   ├── Test Cases/                # Individual test cases
│   ├── Object Repository/         # UI element selectors
│   ├── Keywords/                  # Custom keyword libraries
│   ├── Include/resources/         # JSON test data
│   ├── Include/verifier/          # Expected layout/results
│   └── Profiles/                  # Environment profiles (PROD, QA, STG)
├── GS2.0/                         # Same structure
├── API_Services/
├── Educators 2.0/
├── PostRelease/
└── Response_Services/
```

## Branch & Environment Strategy

Jenkins jobs read the Jenkinsfile from the **main** branch. The Jenkinsfile then checks out the environment-specific branch based on the `ENVIRONMENT` parameter:

1. Jenkins loads the Jenkinsfile from **main**
2. The Jenkinsfile checks out the selected branch (`PROD`, `QA`, `STG`, or `AWS`)
3. The shared runner executes using the code and profiles from that branch

**What goes where:**

- **main branch** — Jenkinsfiles, pipeline logic, orchestrator config only. Main is a launcher; it never runs tests.
- **Environment branches (PROD, QA, STG, AWS)** — Test project changes: JSON test data, profiles, test cases, verifiers, Keywords, test suites.

**Rules:**
- Jenkinsfile/pipeline changes → commit to **main**, then merge into environment branches
- Test project changes → commit directly to the relevant **environment branch(es)**

## Environment Promotion Workflow

Test changes flow through environments in this order:

```
QA → STG → PROD
         → AWS
```

All test work starts on **QA**. When ready for staging, merge QA into STG. When STG is validated, merge STG into PROD and/or AWS.

| Command        | Action                              |
|----------------|-------------------------------------|
| "Update STG"   | `git checkout STG && git merge QA && git push`     |
| "Update PROD"  | `git checkout PROD && git merge STG && git push`   |
| "Update AWS"   | `git checkout AWS && git merge STG && git push`    |

### Promoting Jenkinsfile / Pipeline Changes

Pipeline changes live on **main** and must be merged down into each environment branch:

```bash
git checkout QA  && git merge main && git push
git checkout STG && git merge main && git push
git checkout PROD && git merge main && git push
git checkout AWS  && git merge main && git push
```

## Environment Profiles

Each project supports multiple environments via Katalon execution profiles:
- **PROD** — production
- **QA** — QA environment
- **STG** — staging
- **default** — fallback
