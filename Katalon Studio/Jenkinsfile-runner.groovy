// Jenkinsfile-runner v1.13.3 — Shared pipeline logic
// Usage: node('ec2-agent-01') { checkout scm; load('...').run(config) }

def run(Map config) {
    def JENKINSFILE_VERSION = '1.13.1'
    def KATALON_DIR         = config.katalon_dir
    def KATALON_PROJECT     = config.katalon_project
    def KATALON_SUITE       = config.katalon_suite ?: 'Test Suites/Headless-PROD'

    timestamps {
        withCredentials([string(credentialsId: 'katalon-api-key', variable: 'KATALON_API_KEY')]) {
            withEnv([
                "JENKINSFILE_VERSION=${JENKINSFILE_VERSION}",
                "KATALON_DIR=${KATALON_DIR}",
                "KATALON_PROJECT=${KATALON_PROJECT}",
                "KATALON_SUITE=${KATALON_SUITE}",
                "KATALON_BROWSER=Chrome"
            ]) {
                try {
                    stage('Info') {
                        echo "Jenkinsfile v${JENKINSFILE_VERSION} | Project: ${KATALON_PROJECT} | Suite: ${KATALON_SUITE}"
                    }

                    stage('Preflight') {
                        sh '''
                            set -eux
                            echo "Workspace: $WORKSPACE"
                            hostname
                            pwd
                            ls -la "$WORKSPACE/$KATALON_DIR/"
                            which git || true
                            git --version || true
                            which java || true
                            java -version || true
                            which katalonc || true

                            test -f "$WORKSPACE/$KATALON_DIR/$KATALON_PROJECT"
                        '''
                    }

                    stage('Run Katalon') {
                        sh '''
                            set -ux

                            # Clean old reports so only this run's results are captured
                            rm -rf "$WORKSPACE/$KATALON_DIR/Reports"

                            # Background resource monitor — samples every 60s to CSV
                            RESLOG="$WORKSPACE/resource-log.csv"
                            CORES=$(nproc)
                            echo "time,mem_total,mem_used,mem_free,mem_available,mem_pct,load_1m,load_5m,load_15m,cores,procs_waiting,cpu_saturated" > "$RESLOG"
                            (set +x; while true; do
                                TS=$(date +%H:%M:%S)
                                read -r TOTAL USED FREE AVAIL PCT <<< $(free -m | awk '/Mem:/{printf "%s %s %s %s %.0f", $2,$3,$4,$7,$3/$2*100}')
                                read -r L1 L5 L15 <<< $(uptime | awk -F'load average: ' '{gsub(/,/," ",$2); print $2}')
                                WAITING=$(awk '{printf "%.0f", $1 - '"$CORES"'}' <<< "$L1")
                                [ "$WAITING" -lt 0 ] 2>/dev/null && WAITING=0
                                if [ "$WAITING" -gt 0 ]; then SAT="YES"; else SAT="no"; fi
                                echo "[RESOURCES] Mem: ${PCT}% (${USED}/${TOTAL}M) | Load: ${L1} | Cores: ${CORES} | Waiting: ${WAITING} | Saturated: ${SAT}"
                                echo "${TS},${TOTAL},${USED},${FREE},${AVAIL},${PCT},${L1},${L5},${L15},${CORES},${WAITING},${SAT}" >> "$RESLOG"
                                sleep 60
                            done) &
                            MONITOR_PID=$!

                            katalonc \
                              -noSplash \
                              -runMode=console \
                              -projectPath="$WORKSPACE/$KATALON_DIR/$KATALON_PROJECT" \
                              -testSuiteCollectionPath="$KATALON_SUITE" \
                              -apiKey="$KATALON_API_KEY" \
                              -orgID="2333388" \
                              -retry=0 \
                              --config -webui.autoUpdateDrivers=true || true

                            kill $MONITOR_PID 2>/dev/null || true
                        '''
                    }
                } finally {
                    stage('Reports') {
                        sh '''#!/bin/bash
set +x
REPORT_DIR="$WORKSPACE/$KATALON_DIR/Reports"
OUT="$WORKSPACE/custom-report/index.html"

# Find the latest run directory (most recent timestamp folder)
LATEST_RUN=$(find "$REPORT_DIR" -maxdepth 1 -mindepth 1 -type d 2>/dev/null | sort | tail -1)

if [ -z "$LATEST_RUN" ]; then
    echo "No report directories found — skipping report generation."
    exit 0
fi

echo "Using reports from: $LATEST_RUN"

# Copy the Katalon-generated HTML report to workspace for publishing
KATALON_HTML=$(find "$LATEST_RUN" -name "index.html" -type f 2>/dev/null | head -1)
if [ -n "$KATALON_HTML" ]; then
    mkdir -p "$WORKSPACE/katalon-report"
    cp "$KATALON_HTML" "$WORKSPACE/katalon-report/index.html"
    echo "Katalon report copied to: katalon-report/index.html"
else
    echo "No Katalon index.html found in $LATEST_RUN"
fi

# Remove collection-level JUnit XMLs (they aggregate all suites and cause
# double-counted totals and duplicate failure entries).  Detect them by
# counting <testsuite > occurrences — individual suite XMLs have exactly 1.
echo "=== JUnit XMLs found under $LATEST_RUN ==="
find "$LATEST_RUN" -name "JUnit_Report.xml" -type f 2>/dev/null | while IFS= read -r xml; do
    SC=$(grep -c '<testsuite ' "$xml" 2>/dev/null || echo 0)
    echo "  $xml  (testsuite count: $SC)"
    if [ "$SC" -gt 1 ]; then
        echo "  -> Removing collection-level aggregate"
        rm -f "$xml"
    fi
done

# --- Generate custom HTML report ---

# Extract environment info from first JUnit XML
FIRST_XML=$(find "$LATEST_RUN" -name "JUnit_Report.xml" -type f 2>/dev/null | head -1)
ENV_BROWSER=""; ENV_OS=""; ENV_KATALON=""
if [ -n "$FIRST_XML" ]; then
    ENV_BROWSER=$(awk -F'"' '/<property /{n="";v=""; for(i=1;i<NF;i++){if($i~/name=/)n=$(i+1); if($i~/value=/)v=$(i+1)}; if(n=="browser") print v}' "$FIRST_XML" | head -1)
    ENV_OS=$(awk -F'"' '/<property /{n="";v=""; for(i=1;i<NF;i++){if($i~/name=/)n=$(i+1); if($i~/value=/)v=$(i+1)}; if(n=="os") print v}' "$FIRST_XML" | head -1)
    ENV_KATALON=$(awk -F'"' '/<property /{n="";v=""; for(i=1;i<NF;i++){if($i~/name=/)n=$(i+1); if($i~/value=/)v=$(i+1)}; if(n=="katalonVersion") print v}' "$FIRST_XML" | head -1)
fi

TOTAL_TESTS=0; TOTAL_PASS=0; TOTAL_FAIL=0; TOTAL_ERROR=0; TOTAL_SKIP=0
FAILED_DETAILS=""

while IFS= read -r -d '' xml; do
    SUITE=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /name=/) print $(i+1) }' "$xml" | head -1)
    TESTS=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /tests=/) print $(i+1) }' "$xml" | head -1)
    FAILS=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /failures=/) print $(i+1) }' "$xml" | head -1)
    ERRORS=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /errors=/) print $(i+1) }' "$xml" | head -1)
    SKIPS=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /skipped=/) print $(i+1) }' "$xml" | head -1)
    TIME=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ / time=/) print $(i+1) }' "$xml" | head -1)
    TIMESTAMP=$(awk -F'"' '/<testsuite/{ for(i=1;i<NF;i++) if($i ~ /timestamp=/) print $(i+1) }' "$xml" | head -1)

    TESTS=${TESTS:-0}; FAILS=${FAILS:-0}; ERRORS=${ERRORS:-0}; SKIPS=${SKIPS:-0}; TIME=${TIME:-0}
    PASS=$((TESTS - FAILS - ERRORS - SKIPS))
    [ "$PASS" -lt 0 ] && PASS=0

    TOTAL_TESTS=$((TOTAL_TESTS + TESTS))
    TOTAL_PASS=$((TOTAL_PASS + PASS))
    TOTAL_FAIL=$((TOTAL_FAIL + FAILS))
    TOTAL_ERROR=$((TOTAL_ERROR + ERRORS))
    TOTAL_SKIP=$((TOTAL_SKIP + SKIPS))

    SUITE_FAILS=$((FAILS + ERRORS))

    SUITE_FAILED_TC=""
    while IFS="$(printf '\t')" read -r tc_name tc_status tc_msg tc_time tc_cmd tc_actual; do
        [ -z "$tc_name" ] && continue
        tc_short="${tc_name#Test Cases/}"

        # Build display message: command (with actual value) + failure reason
        tc_display=""
        if [ -n "$tc_cmd" ]; then
            cmd_display="${tc_cmd}"
            if [ -n "$tc_actual" ]; then
                cmd_display="${cmd_display} | Actual: ${tc_actual}"
            fi
            tc_display="<div class=\"cmd-line\">${cmd_display}</div>"
        fi
        if [ -n "$tc_msg" ]; then
            tc_msg="${tc_msg:0:300}"
            tc_display="${tc_display}<div class=\"fail-reason\">${tc_msg}</div>"
        fi

        case "$tc_status" in
            FAIL|ERROR) TC_CLASS="tc-fail"; PILL_CLASS="fail" ;; SKIP) TC_CLASS="tc-skip"; PILL_CLASS="skip" ;; *) TC_CLASS="tc-pass"; PILL_CLASS="pass" ;;
        esac
        tc_time="${tc_time:-0}"
        if [ "$tc_status" = "FAIL" ] || [ "$tc_status" = "ERROR" ]; then
            SUITE_FAILED_TC="${SUITE_FAILED_TC}<tr class=\"tc-fail\"><td>${tc_short}</td><td>${tc_time}s</td><td>${tc_display}</td></tr>"
        fi
    done < <(awk -F'"' 'BEGIN{OFS=sprintf("%c",9); SQ=sprintf("%c",39)}
        /<testcase / {
            tcname=""; status="PASS"; tctime=""; lastcmd=""; failmsg=""; actual=""
            for(i=1;i<NF;i++) {
                if($i ~ /name=/) tcname=$(i+1)
                if($i ~ / time=/) tctime=$(i+1)
            }
            insysout=0
        }
        /<failure/ { status="FAIL" }
        /<error / { status="ERROR" }
        /<skipped/ { status="SKIP" }
        /system-out/ { insysout=1 }
        insysout && /Command:/ {
            idx=index($0,"Command: ")
            if(idx>0) lastcmd=substr($0,idx)
        }
        insysout && (index($0,"Failed to ")>0 || index($0,"Failure at ")>0) {
            idx=index($0,"Failed to ")
            if(idx==0) idx=index($0,"Failure at ")
            if(idx>0) failmsg=substr($0,idx)
        }
        insysout && /StepFailedException:/ {
            idx=index($0,"StepFailedException: ")
            if(idx>0) {
                tmp=substr($0,idx+21)
                paren=index(tmp," (Root cause:")
                if(paren>0) tmp=substr(tmp,1,paren-1)
                if(failmsg=="" && tmp !~ /^at /) failmsg=tmp
            }
        }
        /actual text / || /Actual: / {
            pat="actual text " SQ
            idx=index($0,pat)
            if(idx==0) { pat="Actual text " SQ; idx=index($0,pat) }
            if(idx>0) {
                tmp=substr($0,idx+length(pat))
                q=index(tmp,SQ)
                if(q>0) actual=substr(tmp,1,q-1)
            }
            if(idx==0) {
                idx=index($0,"Actual: " SQ)
                if(idx>0) {
                    tmp=substr($0,idx+9)
                    q=index(tmp,SQ)
                    if(q>0) actual=substr(tmp,1,q-1)
                }
            }
        }
        index($0,"</testcase>")>0 || (index($0,"<testcase")>0 && index($0,"/>")>0) {
            if(tcname != "") {
                msg=""
                if(status=="FAIL" || status=="ERROR") {
                    if(failmsg != "") msg=failmsg
                } else { lastcmd=""; actual="" }
                print tcname, status, msg, tctime, lastcmd, actual
            }
            tcname=""; status="PASS"; tctime=""; lastcmd=""; failmsg=""; actual=""; insysout=0
        }
    ' "$xml")

    if [ -n "$SUITE_FAILED_TC" ]; then
        FAILED_DETAILS="${FAILED_DETAILS}<h3 class=\"cat-heading\">${SUITE}</h3>"
        FAILED_DETAILS="${FAILED_DETAILS}<table class=\"tc-table\"><tr><th>Test Case</th><th>Duration</th><th>Failure Reason</th></tr>"
        FAILED_DETAILS="${FAILED_DETAILS}${SUITE_FAILED_TC}</table>"
    fi

done < <(find "$LATEST_RUN" -name "JUnit_Report.xml" -type f -print0 2>/dev/null)

if [ "$TOTAL_TESTS" -eq 0 ]; then
    echo "No JUnit XML results found — skipping custom report."
    exit 0
fi

TOTAL_FAILED=$((TOTAL_FAIL + TOTAL_ERROR))
if [ "$TOTAL_FAILED" -gt 0 ]; then STATUS="FAILED"; STATUS_CLASS="status-fail"
else STATUS="PASSED"; STATUS_CLASS="status-pass"; fi

# Calculate percentages for stats bar
if [ "$TOTAL_TESTS" -gt 0 ]; then
    PASS_PCT=$((TOTAL_PASS * 100 / TOTAL_TESTS))
    FAIL_PCT=$((TOTAL_FAIL * 100 / TOTAL_TESTS))
    ERR_PCT=$((TOTAL_ERROR * 100 / TOTAL_TESTS))
    SKIP_PCT=$((TOTAL_SKIP * 100 / TOTAL_TESTS))
else
    PASS_PCT=0; FAIL_PCT=0; ERR_PCT=0; SKIP_PCT=0
fi

mkdir -p "$WORKSPACE/custom-report"
cat > "$OUT" <<HTMLEOF
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Katalon Test Report</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #fff; color: #333034; }
  .header { background: #333034; color: #fff; padding: 1.25rem 2rem; display: flex; align-items: center; gap: 1rem; }
  .header h1 { font-size: 1.25rem; font-weight: 600; }
  .header .badge { padding: 0.25rem 0.75rem; border-radius: 4px; font-size: 0.85rem; font-weight: 600; }
  .header .badge.status-pass { background: #2A8132; color: #fff; }
  .header .badge.status-fail { background: #B5001B; color: #fff; }
  .content { max-width: 1200px; margin: 0 auto; padding: 1.5rem 2rem; }
  .meta-bar { display: flex; gap: 1.5rem; flex-wrap: wrap; padding: 0.75rem 0; margin-bottom: 1rem; font-size: 0.85rem; color: #7D797F; border-bottom: 1px solid #D1D0D1; }
  .meta-bar span { white-space: nowrap; }
  .meta-bar strong { color: #333034; }
  .overview { background: #EDEDFA; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; }
  .stats-row { display: flex; gap: 1rem; flex-wrap: wrap; }
  .stat-card { background: #fff; border-radius: 4px; padding: 1rem 1.5rem; text-align: center; flex: 1; min-width: 120px; border: 1px solid #D1D0D1; }
  .stat-card .number { font-size: 2rem; font-weight: 700; }
  .stat-card .label { color: #7D797F; font-size: 0.8rem; text-transform: uppercase; margin-top: 0.25rem; }
  .stat-card.total .number { color: #333034; }
  .green { color: #2A8132; }
  .red { color: #B5001B; }
  .orange { color: #DD334D; }
  .grey { color: #7D797F; }
  .stats-bar { height: 8px; border-radius: 4px; display: flex; overflow: hidden; margin-top: 1rem; }
  .stats-bar .bar-pass { background: #2A8132; }
  .stats-bar .bar-fail { background: #B5001B; }
  .stats-bar .bar-error { background: #DD334D; }
  .stats-bar .bar-skip { background: #A3A3A3; }
  details { border: 1px solid #D1D0D1; border-radius: 4px; margin: 0.5rem 0; overflow: hidden; }
  details summary { padding: 0.75rem 1rem; cursor: pointer; font-size: 0.9rem; display: flex; align-items: center; gap: 0.5rem; }
  details summary:hover { background: #EDEDFA; }
  details.failed-section { border: 1px solid #EE99A6; }
  details.failed-section summary { font-size: 0.95rem; padding: 1rem; background: #FBE5E9; color: #B5001B; font-weight: 600; }
  .cat-heading { margin: 1rem 0 0.5rem 0; padding: 0.5rem 1rem; background: #333034; color: #fff; border-radius: 4px; font-size: 0.85rem; font-weight: 600; }
  .tc-table { width: 100%; border-collapse: separate; border-spacing: 0 2px; margin: 0; }
  .tc-table th { background: #333034; color: #fff; padding: 0.5rem 1rem; text-align: left; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
  .tc-table th:first-child { border-radius: 4px 0 0 4px; }
  .tc-table th:last-child { border-radius: 0 4px 4px 0; }
  .tc-table td { padding: 0.5rem 1rem; font-size: 0.85rem; background: #fff; border-top: 1px solid #D1D0D1; border-bottom: 1px solid #D1D0D1; }
  .tc-table td:first-child { border-left: 1px solid #D1D0D1; border-radius: 4px 0 0 4px; }
  .tc-table td:last-child { border-right: 1px solid #D1D0D1; border-radius: 0 4px 4px 0; max-width: 400px; word-wrap: break-word; overflow-wrap: break-word; font-size: 0.8rem; color: #7D797F; }
  .tc-fail td { background: #FBE5E9; border-color: #EE99A6; }
  .tc-pass td { background: #D4EFDA; border-color: #A9DEB5; }
  .tc-skip td { background: #EFEEEF; border-color: #BEBCBF; }
  .tc-fail td:nth-child(2) { color: #B5001B; font-weight: 600; }
  .tc-pass td:nth-child(2) { color: #23933B; font-weight: 600; }
  .tc-skip td:nth-child(2) { color: #7D797F; font-weight: 600; }
  .status-pill { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
  .status-pill.pass { background: #D4EFDA; color: #23933B; border: 1px solid #A9DEB5; }
  .status-pill.fail { background: #FBE5E9; color: #B5001B; border: 1px solid #EE99A6; }
  .status-pill.skip { background: #EFEEEF; color: #7D797F; border: 1px solid #BEBCBF; }
  .cmd-line { font-family: monospace; font-size: 0.8rem; color: #4545BF; margin-bottom: 4px; }
  .fail-reason { font-size: 0.8rem; color: #B5001B; }
  h2 { color: #333034; margin-top: 1.5rem; font-size: 1rem; }
  .footer { margin-top: 2rem; padding-top: 1rem; border-top: 1px solid #D1D0D1; color: #7D797F; font-size: 0.8rem; }
</style>
</head>
<body>
<div class="header">
  <h1>Test Suite Collection Report</h1>
  <span class="badge ${STATUS_CLASS}">${STATUS}</span>
</div>
<div class="content">

<div class="meta-bar">
  <span>Suite: <strong>${KATALON_SUITE}</strong></span>
  <span>Build: <strong>#${BUILD_NUMBER}</strong></span>
  <span>Date: <strong>$(date '+%Y-%m-%d %H:%M:%S %Z')</strong></span>
  <span>Browser: <strong>${ENV_BROWSER}</strong></span>
  <span>OS: <strong>${ENV_OS}</strong></span>
  <span>Katalon: <strong>${ENV_KATALON}</strong></span>
</div>

<div class="overview">
  <div class="stats-row">
    <div class="stat-card total"><div class="number">${TOTAL_TESTS}</div><div class="label">Total</div></div>
    <div class="stat-card"><div class="number green">${TOTAL_PASS}</div><div class="label">Passed</div></div>
    <div class="stat-card"><div class="number red">${TOTAL_FAIL}</div><div class="label">Failed</div></div>
    <div class="stat-card"><div class="number orange">${TOTAL_ERROR}</div><div class="label">Errors</div></div>
    <div class="stat-card"><div class="number grey">${TOTAL_SKIP}</div><div class="label">Skipped</div></div>
  </div>
  <div class="stats-bar">
    <div class="bar-pass" style="width:${PASS_PCT}%"></div>
    <div class="bar-fail" style="width:${FAIL_PCT}%"></div>
    <div class="bar-error" style="width:${ERR_PCT}%"></div>
    <div class="bar-skip" style="width:${SKIP_PCT}%"></div>
  </div>
</div>

<details class="failed-section" open>
<summary>Failed Tests — ${TOTAL_FAILED} failures across all suites</summary>
<div style="padding: 0.75rem 1rem;">
${FAILED_DETAILS}
</div>
</details>

<div class="footer">Generated by Jenkinsfile v${JENKINSFILE_VERSION}</div>
</div>
</body>
</html>
HTMLEOF
echo "Custom report generated: custom-report/index.html"
                        '''
                        publishHTML(target: [
                            reportName: 'Katalon Test Report',
                            reportDir: 'katalon-report',
                            reportFiles: 'index.html',
                            keepAll: true,
                            alwaysLinkToLastBuild: true,
                            allowMissing: true
                        ])
                        publishHTML(target: [
                            reportName: 'Test Summary Report',
                            reportDir: 'custom-report',
                            reportFiles: 'index.html',
                            keepAll: true,
                            alwaysLinkToLastBuild: true,
                            allowMissing: true
                        ])
                        sh '''#!/bin/bash
set +x
RESLOG="$WORKSPACE/resource-log.csv"
RESOUT="$WORKSPACE/resource-report/index.html"
mkdir -p "$WORKSPACE/resource-report"

if [ ! -f "$RESLOG" ] || [ "$(wc -l < "$RESLOG")" -le 1 ]; then
    echo "No resource data collected — skipping resource report."
    exit 0
fi

# Read CSV into table rows and find peak values
ROWS=""
PEAK_MEM=0
PEAK_LOAD=""
PEAK_MEM_TIME=""
PEAK_WAITING=0
while IFS=',' read -r ts total used free avail pct l1 l5 l15 cores waiting sat; do
    [ "$ts" = "time" ] && continue
    if [ "$sat" = "YES" ]; then
        SAT_CLASS=" class=\"saturated\""
    else
        SAT_CLASS=""
    fi
    ROWS="${ROWS}<tr><td>${ts}</td><td>${total}M</td><td>${used}M</td><td>${free}M</td><td>${avail}M</td><td><strong>${pct}%</strong></td><td>${l1}</td><td>${l5}</td><td>${l15}</td><td>${cores}</td><td${SAT_CLASS}>${waiting}</td><td${SAT_CLASS}>${sat}</td></tr>"
    if [ "${pct:-0}" -gt "$PEAK_MEM" ] 2>/dev/null; then
        PEAK_MEM="$pct"
        PEAK_MEM_TIME="$ts"
        PEAK_LOAD="$l1"
    fi
    if [ "${waiting:-0}" -gt "$PEAK_WAITING" ] 2>/dev/null; then
        PEAK_WAITING="$waiting"
    fi
done < "$RESLOG"

SAMPLES=$(($(wc -l < "$RESLOG") - 1))

cat > "$RESOUT" <<RESHTML
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Resource Usage Report</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #fff; color: #333034; }
  .header { background: #333034; color: #fff; padding: 1.25rem 2rem; display: flex; align-items: center; gap: 1rem; }
  .header h1 { font-size: 1.25rem; font-weight: 600; }
  .content { max-width: 1200px; margin: 0 auto; padding: 1.5rem 2rem; }
  .meta-bar { display: flex; gap: 1.5rem; flex-wrap: wrap; padding: 0.75rem 0; margin-bottom: 1rem; font-size: 0.85rem; color: #7D797F; border-bottom: 1px solid #D1D0D1; }
  .meta-bar strong { color: #333034; }
  .overview { background: #EDEDFA; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; }
  .stats-row { display: flex; gap: 1rem; flex-wrap: wrap; }
  .stat-card { background: #fff; border-radius: 4px; padding: 1rem 1.5rem; text-align: center; flex: 1; min-width: 140px; border: 1px solid #D1D0D1; }
  .stat-card .number { font-size: 2rem; font-weight: 700; }
  .stat-card .label { color: #7D797F; font-size: 0.8rem; text-transform: uppercase; margin-top: 0.25rem; }
  .green { color: #2A8132; }
  .red { color: #B5001B; }
  .orange { color: #DD334D; }
  table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
  th { background: #333034; color: #fff; padding: 0.5rem 1rem; text-align: left; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
  td { padding: 0.5rem 1rem; font-size: 0.85rem; border-bottom: 1px solid #D1D0D1; }
  tr:hover td { background: #EDEDFA; }
  .saturated { background: #FBE5E9; color: #B5001B; font-weight: 600; }
  .footer { margin-top: 2rem; padding-top: 1rem; border-top: 1px solid #D1D0D1; color: #7D797F; font-size: 0.8rem; }
</style>
</head>
<body>
<div class="header">
  <h1>Resource Usage Report</h1>
</div>
<div class="content">

<div class="meta-bar">
  <span>Build: <strong>#${BUILD_NUMBER}</strong></span>
  <span>Date: <strong>$(date '+%Y-%m-%d %H:%M:%S %Z')</strong></span>
  <span>Instances: <strong>$(awk -F'[<>]' '/maxConcurrentInstances/{print $3}' "$WORKSPACE/$KATALON_DIR/Test Suites/${KATALON_SUITE##*/}.ts" 2>/dev/null || echo "?")</strong></span>
  <span>Samples: <strong>${SAMPLES}</strong> (every 60s)</span>
</div>

<div class="overview">
  <div class="stats-row">
    <div class="stat-card"><div class="number red">${PEAK_MEM}%</div><div class="label">Peak Memory</div></div>
    <div class="stat-card"><div class="number orange">${PEAK_LOAD}</div><div class="label">Peak Load (1m)</div></div>
    <div class="stat-card"><div class="number red">${PEAK_WAITING}</div><div class="label">Peak Procs Waiting</div></div>
    <div class="stat-card"><div class="number">${PEAK_MEM_TIME}</div><div class="label">Peak Time</div></div>
  </div>
</div>

<h2 style="margin-top:1.5rem; font-size:1rem;">Samples</h2>
<table>
<tr><th>Time</th><th>Total</th><th>Used</th><th>Free</th><th>Available</th><th>Used %</th><th>Load 1m</th><th>Load 5m</th><th>Load 15m</th><th>Cores</th><th>Waiting</th><th>Saturated</th></tr>
${ROWS}
</table>

<div class="footer">Generated by Jenkinsfile v${JENKINSFILE_VERSION}</div>
</div>
</body>
</html>
RESHTML
echo "Resource report generated: resource-report/index.html"
                        '''
                        publishHTML(target: [
                            reportName: 'Resource Usage',
                            reportDir: 'resource-report',
                            reportFiles: 'index.html',
                            keepAll: true,
                            alwaysLinkToLastBuild: true,
                            allowMissing: true
                        ])
                        archiveArtifacts artifacts: "${KATALON_DIR}/Reports/**/*", allowEmptyArchive: true
                        junit testResults: "${KATALON_DIR}/Reports/**/JUnit_Report.xml", allowEmptyResults: true
                    }
                }
            }
        }
    }
}

return this
