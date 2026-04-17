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
- **All projects** now use the shared runner pattern (migrated in v1.14.0).

## Jenkins Job Names

| Project         | Jenkins Job Name | Jenkinsfile Path                          |
|-----------------|------------------|-------------------------------------------|
| Orchestrator    | All_Automation   | Katalon Studio/Jenkinsfile                |
| RESPONSE        | RES_2.0          | Katalon Studio/RESPONSE/Jenkinsfile       |
| GS2.0           | GS_2.0           | Katalon Studio/GS2.0/Jenkinsfile          |

## Key Conventions

- **Jenkinsfile version**: Always noted in commit messages and file headers (currently v1.14.0)
- **Execution**: `katalonc` CLI, Chrome headless, orgID `2333388`
- **Test Suites**: Main runner suite is `Test Suites/Headless-PROD` (parallel execution)
- **Concurrency**: Configured via `maxConcurrentInstances` in `.ts` files (typically 6-8)
- **Reports**: 3 types generated per run — Katalon Test Report, Test Summary Report, Resource Usage Report
- **Credentials**: `katalon-api-key` stored in Jenkins credentials

## Test Data

- JSON files in each project's `Include/resources/` directory
- Verifier/layout files in `Include/verifier/`
- Spanish translations in `Include/verifier/es/`

## Branch & Environment Strategy

Jenkins jobs are configured to read the Jenkinsfile from the **main** branch. The Jenkinsfile itself then checks out the environment-specific branch based on the `ENVIRONMENT` parameter:

1. Jenkins loads the Jenkinsfile from **main** (this is where parameter definitions like `ENVIRONMENT` choices live)
2. The Jenkinsfile runs `deleteDir()` + `checkout` to switch to the selected branch (`PROD`, `QA`, `STG`, or `AWS`)
3. The shared runner (`Jenkinsfile-runner.groovy`) executes using the code and profiles from that branch
4. The `executionProfile` is set to match the environment (e.g., `PROD` profile on `PROD` branch)

**What goes where:**

- **main branch** — Only Jenkinsfiles, pipeline logic (`Jenkinsfile-runner.groovy`), orchestrator, and project wrapper configs. Main is a launcher only; it never runs tests. Test project changes on main are irrelevant.
- **Environment branches (PROD, QA, STG, AWS)** — Test project changes: JSON test data, profiles, test cases, verifiers, Keywords, test suites. These are the branches that actually execute.

**Rules:**
- Jenkinsfile/pipeline changes → commit to **main**, then merge main into environment branches
- Test project changes → commit directly to the relevant **environment branch(es)**, do NOT push to main

## When Editing Jenkinsfiles

- Edit `Jenkinsfile-runner.groovy` for pipeline logic changes (affects all projects using shared runner)
- Edit individual project `Jenkinsfile` only for project-specific config (dir, project, suite)
- Edit `Katalon Studio/Jenkinsfile` for orchestration changes (job order, scheduling, report links)
- Always mention the Jenkinsfile version when pushing changes
- Jenkinsfile changes must be on **main** to take effect — merge into environment branches after
