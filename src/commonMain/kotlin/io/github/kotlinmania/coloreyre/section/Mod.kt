// port-lint: source section/mod.rs
package io.github.kotlinmania.coloreyre.section

import io.github.kotlinmania.coloreyre.PanicInfo
import io.github.kotlinmania.coloreyre.Sealed

/**
 * Helpers for adding custom sections to error reports.
 */

/**
 * An indented section with a header for an error report.
 *
 * # Details
 *
 * This helper provides two functions to help with constructing nicely formatted
 * error reports. First, it handles indentation of every line of the body for
 * you, and makes sure it is consistent with the rest of color-eyre's output.
 * Second, it omits outputting the header if the body itself is empty,
 * preventing unnecessary pollution of the report for sections with dynamic
 * content.
 */
public data class IndentedSection<H, B>(
    private val header: H,
    private val body: B,
) {
    override fun toString(): String {
        val renderedBody = body.toString()
        if (renderedBody.isEmpty()) {
            return ""
        }
        return buildString {
            append(header)
            append('\n')
            append(renderedBody.prependIndent("   "))
        }
    }
}

/**
 * Extension trait for constructing sections with commonly used formats.
 */
public interface SectionExt {
    /**
     * Add a header to a [Section] and indent the body.
     *
     * # Details
     *
     * Bodies are always indented to the same level as error messages and spans.
     * The header is not printed if the display impl of the body produces no
     * output.
     */
    public fun <C> header(header: C): IndentedSection<C, Any>
}

/**
 * Add a header to a value and indent the body, returning an [IndentedSection].
 */
public fun Any.header(header: Any): IndentedSection<Any, Any> =
    IndentedSection(header, this)

/**
 * A helper trait for attaching informational sections to error reports to be
 * displayed after the chain of errors.
 */
public interface Section : Sealed {
    /**
     * The return type of each method after adding context.
     */
    public interface Return

    /**
     * Add a section to an error report, to be displayed after the chain of
     * errors.
     */
    public fun section(section: Any): Any

    /**
     * Add a section to an error report lazily.
     */
    public fun withSection(section: () -> Any): Any

    /**
     * Add an error section to an error report.
     */
    public fun error(error: Throwable): Any

    /**
     * Add an error section to an error report lazily.
     */
    public fun withError(error: () -> Throwable): Any

    /**
     * Add a note to an error report.
     */
    public fun note(note: Any): Any

    /**
     * Add a note to an error report lazily.
     */
    public fun withNote(f: () -> Any): Any

    /**
     * Add a warning to an error report.
     */
    public fun warning(warning: Any): Any

    /**
     * Add a warning to an error report lazily.
     */
    public fun withWarning(f: () -> Any): Any

    /**
     * Add a suggestion to an error report.
     */
    public fun suggestion(suggestion: Any): Any

    /**
     * Add a suggestion to an error report lazily.
     */
    public fun withSuggestion(f: () -> Any): Any

    /**
     * Whether to suppress printing of a collected backtrace, if any.
     */
    public fun suppressBacktrace(suppress: Boolean): Any
}

/**
 * Trait for printing a panic error message for the given panic info.
 */
public interface PanicMessage {
    /**
     * Display-trait equivalent for implementing the display logic.
     */
    public fun display(panicInfo: PanicInfo, out: Appendable)
}