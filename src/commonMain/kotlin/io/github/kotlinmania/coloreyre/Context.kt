// port-lint: source lib.rs
package io.github.kotlinmania.coloreyre

import io.github.kotlinmania.coloreyre.section.HelpInfo

/**
 * A custom handler type for eyre reports which provides colorful error reports
 * and tracing-error support.
 */
public data class Handler(
    internal val filters: List<FilterCallback>,
    internal val backtrace: Backtrace?,
    internal var suppressBacktrace: Boolean,
    internal val spanTrace: SpanTrace?,
    internal val sections: MutableList<HelpInfo>,
    internal val displayEnvSection: Boolean,
    internal val displayLocationSection: Boolean,
    internal val issueUrl: String?,
    internal val issueMetadata: List<Pair<String, Any>>,
    internal val issueFilter: IssueFilterCallback,
    internal val theme: Theme,
    internal var location: Location?,
) {
    override fun toString(): String = "redacted"
}

