# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 9/9 (100.0%)
- **Function parity:** 63/73 matched (target 116) — 86.3%
- **Class/type parity:** 36/43 matched (target 46) — 83.7%
- **Combined symbol parity:** 99/116 matched (target 162) — 85.3%
- **Average inline-code cosine:** 0.49 (function body across 8 matched files)
- **Average documentation cosine:** 0.44 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 6 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. writers

- **Target:** `coloreyre.Writers`
- **Similarity:** 0.33
- **Dependents:** 0
- **Priority Score:** 51906.7
- **Functions:** 5/7 matched (target 22)
- **Missing functions:** `write_str`, `fmt`
- **Types:** 9/12 matched (target 11)
- **Missing types:** `WriterExt`, `SpanTraceOmited`, `BacktraceOmited`

### 2. config

- **Target:** `coloreyre.Config`
- **Similarity:** 0.62
- **Dependents:** 0
- **Priority Score:** 44903.8
- **Functions:** 33/35 matched (target 61)
- **Missing functions:** `fmt`, `from`
- **Types:** 12/14 matched (target 12)
- **Missing types:** `FilterCallback`, `IssueFilterCallback`

### 3. section.github

- **Target:** `section.Github`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 31506.3
- **Functions:** 6/8 matched (target 10)
- **Missing functions:** `new`, `fmt`
- **Types:** 6/7 matched (target 6)
- **Missing types:** `Display`

### 4. section.help

- **Target:** `section.Help`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 21407.0
- **Functions:** 11/12 matched
- **Missing functions:** `fmt`
- **Types:** 1/2 matched (target 6)
- **Missing types:** `Return`

### 5. handler

- **Target:** `coloreyre.Handler`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 10704.1
- **Functions:** 6/7 matched (target 6)
- **Missing functions:** `fmt`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 6. section.mod

- **Target:** `section.Section [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10610.0
- **Functions:** 1/2 matched
- **Missing functions:** `fmt`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_

### 7. fmt

- **Target:** `coloreyre.Fmt`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10210.0
- **Functions:** 0/1 matched
- **Missing functions:** `fmt`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 8. lib

- **Target:** `coloreyre.Context`
- **Similarity:** 0.67
- **Dependents:** 0
- **Priority Score:** 303.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 4)
- **Missing types:** _none_

### 9. private

- **Target:** `coloreyre.Private`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/color-eyre/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/coloreyre kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
