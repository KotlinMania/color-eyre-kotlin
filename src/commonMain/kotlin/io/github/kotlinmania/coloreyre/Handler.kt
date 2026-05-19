// port-lint: source handler.rs
package io.github.kotlinmania.coloreyre

import io.github.kotlinmania.coloreyre.section.HelpInfo

internal fun Handler.formatBacktrace(trace: Backtrace): BacktraceFormatter =
    BacktraceFormatter(
        filters = filters,
        inner = trace,
        theme = theme,
    )

/**
 * Return a reference to the captured backtrace type.
 */
public fun Handler.backtrace(): Backtrace? = backtrace

/**
 * Return a reference to the captured span trace type.
 */
public fun Handler.spanTrace(): SpanTrace? = spanTrace

internal fun Handler.debug(error: Throwable, alternate: Boolean = false): String {
    if (alternate) {
        return error.toString()
    }

    val errors = error.chain()
    return buildString {
        for ((n, currentError) in errors.withIndex()) {
            appendLine()
            append(" ".repeat(n))
            append(theme.error.style(currentError.message ?: currentError.toString()))
        }

        val separated = HeaderedStringBuilder(this, "\n\n")

        if (displayLocationSection) {
            separated.ready {
                append(LocationSection(location, theme).header("Location:"))
            }
        }

        for (section in sections.filterIsInstance<HelpInfo.Error>()) {
            separated.ready { append(section) }
        }

        for (section in sections.filterIsInstance<HelpInfo.Custom>()) {
            separated.ready { append(section) }
        }

        val currentSpanTrace = spanTrace ?: getDeepestSpantrace(error)
        if (currentSpanTrace != null) {
            separated.ready { append(FormattedSpanTrace(currentSpanTrace)) }
        }

        if (!suppressBacktrace) {
            val currentBacktrace = backtrace
            if (currentBacktrace != null) {
                separated.ready {
                    append(formatBacktrace(currentBacktrace).toString().prependIndent("  "))
                }
            }
        }

        val helpSeparated = HeaderedStringBuilder(this, "\n")
        for (section in sections.filter { it !is HelpInfo.Custom && it !is HelpInfo.Error }) {
            helpSeparated.ready { append(section) }
        }

        if (displayEnvSection) {
            separated.ready { append(EnvSection(backtrace != null, currentSpanTrace)) }
        }

        val url = issueUrl
        if (url != null && issueFilter.filter(ErrorKind.Recoverable(error))) {
            val payload = buildString {
                append("Error: ")
                for ((n, currentError) in errors.withIndex()) {
                    appendLine()
                    append(" ".repeat(n))
                    append(currentError.message ?: currentError.toString())
                }
            }
            separated.ready {
                append(
                    io.github.kotlinmania.coloreyre.section.github.IssueSection(url, payload)
                        .withBacktrace(backtrace)
                        .withMetadata(issueMetadata)
                        .withSpanTrace(currentSpanTrace),
                )
            }
        }
    }
}

internal fun Handler.trackCaller(location: Location) {
    this.location = location
}

internal fun getDeepestSpantrace(error: Throwable): SpanTrace? =
    error.chain()
        .asReversed()
        .firstNotNullOfOrNull { it.spanTraceOrNull() }

