# color-eyre-kotlin

<div align="center">

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/color-eyre-kotlin?color=blue&logo=apachemaven)](https://central.sonatype.com/artifact/io.github.kotlinmania/color-eyre-kotlin)
[![CI](https://img.shields.io/github/actions/workflow/status/KotlinMania/color-eyre-kotlin/ci.yml?branch=main&label=CI&logo=githubactions&logoColor=white)](https://github.com/KotlinMania/color-eyre-kotlin/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Platform: KMP](https://img.shields.io/badge/Platform-Multiplatform-blue.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Swift Export](https://img.shields.io/badge/Swift_Export-Ready-F05138.svg?logo=swift&logoColor=white)](https://github.com/KotlinMania/color-eyre-kotlin)
[![License: MIT / Apache-2.0](https://img.shields.io/badge/License-MIT%20%2F%20Apache--2.0-green.svg)](https://opensource.org/licenses/MIT)
[![GitHub Release](https://img.shields.io/github/v/release/KotlinMania/color-eyre-kotlin?color=purple&logo=github)](https://github.com/KotlinMania/color-eyre-kotlin/releases)
[![Upstream Crate](https://img.shields.io/crates/v/color-eyre.svg?label=upstream%20color-eyre&color=orange&logo=rust)](https://crates.io/crates/color-eyre)

**Colorful, consistent, and well-formatted error and panic reports for Kotlin Multiplatform.**

*A faithful line-by-line transliteration of Jane Lusby's ([@yaahc](https://github.com/yaahc)) [`color-eyre`](https://github.com/eyre-rs/eyre/tree/master/color-eyre) Rust crate.*

</div>

---

## Overview

In terminal applications, CLI utilities, and backend services, plain exception stack traces are often noisy, difficult to scan, and lacking in operational context. Diagnostic information such as causal chains, command outputs, suggestions, and relevant environment details is easily lost.

`color-eyre-kotlin` brings the beloved error reporting workflow of Rust's `color-eyre` crate to Kotlin Multiplatform:

1. **Rich ANSI Coloring**: Distinct syntax highlighting for file paths, line numbers, error messages, active source frames, notes, and warnings powered by `owo-colors-kotlin`.
2. **Causal Chain Formatting**: Clear, indented representation of root causes and wrapped exceptions (`Throwable.chain()`).
3. **Structured Informational Sections**: First-class support for attaching suggestions, warnings, notes, auxiliary error logs, or custom indented sections to reports.
4. **Custom Panic & Exception Hooks**: Unifies unhandled exception/panic reporting with configurable filters and display formats.
5. **Automated GitHub Issue URLs**: Automatically constructs issue reporting links pre-populated with markdown-formatted backtraces, span traces, and environment metadata.
6. **Kotlin Multiplatform**: Seamless support across JVM, Android, iOS, macOS, Linux, Windows, JavaScript, and WebAssembly (Wasm-JS & Wasm-WASI).

---

## Features

- 🎨 **Terminal Styling via `owo-colors`**: Full ANSI styling with dark and light themes, completely customizable per UI component.
- ⛓️ **Exception Chain Propagation**: Traverses exception causes and displays each failure step clearly.
- 💡 **Informative Help Sections**: Attach notes (`.note()`), warnings (`.warning()`), suggestions (`.suggestion()`), or child errors (`.error()`).
- 📑 **Indented Content Headers**: Use `.header("Title:")` to cleanly format multi-line stdout, stderr, or configuration payloads while suppressing empty sections.
- 🔍 **Configurable Report Verbosity**: Minimal, short, and full report formats with source line context display.
- 🧹 **Frame Filtering**: Filter out runtime internals, test runners, or dependency frames to keep reports actionable.
- 🐙 **One-Click Issue Reporting**: Optional GitHub issue URL generation with collapsed backtrace/spantrace markdown blocks and environment metadata tables.
- 🍏 **Native Swift Export**: First-class Swift PM package generation ready for direct Xcode consumption.

---

## Installation

### Gradle (Kotlin DSL)

Add `color-eyre-kotlin` to your `commonMain` dependencies:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kotlinmania:color-eyre-kotlin:0.1.0")
        }
    }
}
```

### Gradle Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
color-eyre = "0.1.0"

[libraries]
color-eyre = { module = "io.github.kotlinmania:color-eyre-kotlin", version.ref = "color-eyre" }
```

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.color.eyre)
        }
    }
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.kotlinmania</groupId>
    <artifactId>color-eyre-kotlin</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

## Quick Start

### Basic Installation

To install the default error and panic report hooks at application startup, call `install()`:

```kotlin
import io.github.kotlinmania.coloreyre.install

fun main() {
    // Install default error and panic reporting hooks
    install().getOrThrow()

    // Application logic proceeds with color-eyre formatting enabled
}
```

---

## Configuration & Advanced Usage

### Customizing with `HookBuilder`

`HookBuilder` provides complete control over the reporting pipeline:

```kotlin
import io.github.kotlinmania.coloreyre.HookBuilder
import io.github.kotlinmania.coloreyre.Theme

fun configureErrorReporting() {
    HookBuilder.new()
        // Select dark or light theme, or supply a custom Theme
        .theme(Theme.dark())
        // Display environment variable guidance (e.g. RUST_BACKTRACE hints)
        .displayEnvSection(true)
        // Display source file/line location where the error occurred
        .displayLocationSection(true)
        // Install frame filters to omit noise from dependencies
        .addDefaultFilters()
        // Configure automated GitHub issue creation links
        .issueUrl("https://github.com/my-org/my-project/issues/new")
        .addIssueMetadata("Version", "1.0.0")
        .addIssueMetadata("Target", "Kotlin Multiplatform")
        .install()
        .getOrThrow()
}
```

---

### Attaching Custom Sections to Error Reports

You can attach contextual sections to error reports using the `section` extension functions from `io.github.kotlinmania.coloreyre.section`:

```kotlin
import io.github.kotlinmania.coloreyre.Handler
import io.github.kotlinmania.coloreyre.section.error
import io.github.kotlinmania.coloreyre.section.header
import io.github.kotlinmania.coloreyre.section.note
import io.github.kotlinmania.coloreyre.section.suggestion
import io.github.kotlinmania.coloreyre.section.warning
import io.github.kotlinmania.coloreyre.section.withSection

fun handleProcessFailure(handler: Handler, exitCode: Int, stdout: String, stderr: String) {
    handler
        .note("Configuration loaded from /etc/app/config.json")
        .warning("Process exited with non-zero exit code: $exitCode")
        .suggestion("Verify database credentials and network connectivity, then retry.")
        // Attach multi-line command output with an indented header (omitted if body is empty)
        .withSection {
            stderr.trim().header("Stderr:")
        }
        .withSection {
            stdout.trim().header("Stdout:")
        }
}
```

When rendered, the resulting report cleanly formats each section with distinct colorized labels:

```text
Error: Failed to execute process

Location:
    at src/commonMain/kotlin/com/example/Process.kt:42

Stderr:
    Connection refused: port 5432 unreachable

Note: Configuration loaded from /etc/app/config.json
Warning: Process exited with non-zero exit code: 1
Suggestion: Verify database credentials and network connectivity, then retry.
```

---

### Indented Sections via `.header(...)`

`IndentedSection` ensures consistent indentation across all custom sections and automatically suppresses headers when content is blank:

```kotlin
import io.github.kotlinmania.coloreyre.section.header

val stdout = "  "
val section = stdout.trim().header("Output:")

println(section.toString()) // Produces empty string — does not pollute the report!
```

---

### Automated GitHub Issue URL Generation

When an `issueUrl` is configured on `HookBuilder`, recoverable errors or panics can append an autogenerated link that opens a pre-filled GitHub issue in the user's browser:

```kotlin
HookBuilder.new()
    .issueUrl("https://github.com/KotlinMania/color-eyre-kotlin/issues/new")
    .addIssueMetadata("App Version", "0.1.0")
    .addIssueMetadata("OS", "macOS")
    .install()
    .getOrThrow()
```

The generated URL includes:
- Markdown table containing application and system metadata.
- Pre-formatted exception chain and message.
- Collapsible `<details>` blocks for backtraces and span traces.

---

### Customizing Themes with `owo-colors-kotlin`

The `Theme` data class allows fine-grained control over colors and font styles:

```kotlin
import io.github.kotlinmania.coloreyre.Theme
import io.github.kotlinmania.owocolors.style

val customTheme = Theme.dark()
    .file(style().cyan())
    .lineNumber(style().yellow())
    .error(style().brightRed().bold())
    .helpInfoWarning(style().yellow().bold())
    .helpInfoSuggestion(style().green())
    .helpInfoNote(style().blue())

HookBuilder.new()
    .theme(customTheme)
    .install()
    .getOrThrow()
```

---

## Core API Reference

| Component / Function | Return Type | Description |
|---|---|---|
| `install()` | `Result<Unit>` | Installs default panic and error report hooks. |
| `HookBuilder.new()` | `HookBuilder` | Constructs a fluent builder for configuring hooks. |
| `HookBuilder.theme(Theme)` | `HookBuilder` | Configures the visual theme for rendered reports. |
| `HookBuilder.displayEnvSection(Boolean)` | `HookBuilder` | Toggles display of the environment variables advice section. |
| `HookBuilder.displayLocationSection(Boolean)` | `HookBuilder` | Toggles display of the source location section. |
| `HookBuilder.issueUrl(String)` | `HookBuilder` | Sets the target URL for GitHub issue generation. |
| `HookBuilder.addIssueMetadata(String, Any)` | `HookBuilder` | Attaches key-value metadata to generated GitHub issues. |
| `HookBuilder.addDefaultFilters()` | `HookBuilder` | Adds standard frame filters to remove internal noise. |
| `Handler.note(Any)` | `Handler` | Attaches a styled note section to the report. |
| `Handler.warning(Any)` | `Handler` | Attaches a styled warning section to the report. |
| `Handler.suggestion(Any)` | `Handler` | Attaches a styled suggestion section to the report. |
| `Handler.error(Throwable)` | `Handler` | Attaches an auxiliary error chain to the report. |
| `Handler.withSection(() -> Any)` | `Handler` | Lazily evaluates and attaches a custom section. |
| `Any.header(Any)` | `IndentedSection` | Wraps content with an indented header, omitted if content is blank. |
| `Throwable.chain()` | `List<Throwable>` | Returns the complete causal chain from root to cause. |

---

## Supported Targets

`color-eyre-kotlin` is compiled and verified across all KotlinMania target platforms:

| Platform | Target Identifiers |
|---|---|
| **JVM** | `jvm` (Java 21+ toolchain) |
| **Android** | `android` (AAR, API 24+), `androidNativeArm64`, `androidNativeX64` |
| **Apple** | `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`, `tvosArm64`, `tvosSimulatorArm64`, `watchosArm64`, `watchosDeviceArm64`, `watchosSimulatorArm64` |
| **Linux** | `linuxX64`, `linuxArm64` |
| **Windows** | `mingwX64` |
| **Web** | `js` (Browser & Node.js), `wasmJs` (Browser & Node.js), `wasmWasi` (WASI) |

---

## Acknowledgements & Attribution

`color-eyre-kotlin` is a direct transliteration of the [`color-eyre`](https://github.com/eyre-rs/eyre/tree/master/color-eyre) Rust crate.

- **Original Author**: [Jane Lusby](https://github.com/yaahc) ([@yaahc](https://github.com/yaahc)) and the [`eyre-rs`](https://github.com/eyre-rs) contributors.
- **Upstream Crate**: Published on [crates.io/crates/color-eyre](https://crates.io/crates/color-eyre) with repository at [eyre-rs/eyre](https://github.com/eyre-rs/eyre).
- **Original Project Intent**: Providing colorful, user-friendly, and actionable error reports with panic hooks, backtrace customization, and informative help sections for command-line applications.
- **Kotlin Multiplatform Port**: Maintained by Sydney Renee and The Solace Project as part of the [KotlinMania](https://github.com/KotlinMania) initiative to bring high-quality, native systems and diagnostics libraries from the Rust ecosystem to Kotlin Multiplatform.

All architectural design, formatting layout, and conceptual credit belong to Jane Lusby and the upstream `eyre` maintainers.

---

## License

Dual-licensed under either of:

- [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
- [MIT License](http://opensource.org/licenses/MIT)

at your option, matching the upstream Rust crate license.
