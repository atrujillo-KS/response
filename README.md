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

## Environment Profiles

Each project supports multiple environments via Katalon execution profiles:
- **PROD** — production
- **QA** — QA environment
- **STG** — staging
- **default** — fallback
