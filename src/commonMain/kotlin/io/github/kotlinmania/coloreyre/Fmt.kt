// port-lint: source fmt.rs
package io.github.kotlinmania.coloreyre

/**
 * Module for new types that isolate complex formatting.
 */

internal data class LocationSection(
    private val location: Location?,
    private val theme: Theme,
) {
    override fun toString(): String {
        val loc = location ?: return "<unknown>"
        return buildString {
            append(theme.panicFile.style(loc.file))
            append(':')
            append(theme.panicLineNumber.style(loc.line.toString()))
        }
    }
}

