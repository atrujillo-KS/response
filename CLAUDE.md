# Katalon Studio Test Automation Repository

## Project Structure

```
Katalon Studio/
  Jenkinsfile                    # Orchestrator — triggers child jobs daily at 3 AM
  Jenkinsfile-runner.groovy      # Shared pipeline logic (single source of truth)
  RESPONSE/                      # Main financial product recommendation engine tests
  GS2.0/                         # Goal Setting 2.0 tests
  API_Services/                  # API testing
  Educators 2.0/                 # Education product tests
  PostRelease/                   # Post-release/regression testing
  Response_Services/             # Shared API services
```

## Jenkins Architecture

- **Orchestrator** (`Katalon Studio/Jenkinsfile`) runs on `built-in` agent, triggers `RES_2.0` then `GS_2.0` sequentially. Publishes report links. Marks itself failed if any child fails.
- **Shared Runner** (`Jenkinsfile-runner.groovy`) contains all pipeline logic: preflight, Katalon execution, resource monitoring, HTML report generation, JUnit parsing.
- **Project Wrappers** (e.g., `RESPONSE/Jenkinsfile`, `GS2.0/Jenkinsfile`) are thin ~10-line files that `load` the shared runner with project-specific config (`katalon_dir`, `katalon_project`, `katalon_suite`).
- **Other projects** (API_Services, Educators 2.0, PostRelease, Response_Services) still use full standalone Jenkinsfiles — to be migrated to the shared runner pattern.

## Jenkins Job Names

| Project         | Jenkins Job Name | Jenkinsfile Path                          |
|-----------------|------------------|-------------------------------------------|
| Orchestrator    | All_Automation   | Katalon Studio/Jenkinsfile                |
| RESPONSE        | RES_2.0          | Katalon Studio/RESPONSE/Jenkinsfile       |
| GS2.0           | GS_2.0           | Katalon Studio/GS2.0/Jenkinsfile          |

## Key Conventions

- **Jenkinsfile version**: Always noted in commit messages and file headers (currently v1.13.1)
- **Execution**: `katalonc` CLI, Chrome headless, orgID `2333388`
- **Test Suites**: Main runner suite is `Test Suites/Headless-PROD` (parallel execution)
- **Concurrency**: Configured via `maxConcurrentInstances` in `.ts` files (typically 6-8)
- **Reports**: 3 types generated per run — Katalon Test Report, Test Summary Report, Resource Usage Report
- **Credentials**: `katalon-api-key` stored in Jenkins credentials

## Test Data

- JSON files in each project's `Include/resources/` directory
- Verifier/layout files in `Include/verifier/`
- Spanish translations in `Include/verifier/es/`

## When Editing Jenkinsfiles

- Edit `Jenkinsfile-runner.groovy` for pipeline logic changes (affects all projects using shared runner)
- Edit individual project `Jenkinsfile` only for project-specific config (dir, project, suite)
- Edit `Katalon Studio/Jenkinsfile` for orchestration changes (job order, scheduling, report links)
- Always mention the Jenkinsfile version when pushing changes
