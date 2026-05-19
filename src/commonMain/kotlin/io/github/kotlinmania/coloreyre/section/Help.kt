// port-lint: source section/help.rs
package io.github.kotlinmania.coloreyre.section

import io.github.kotlinmania.coloreyre.Handler
import io.github.kotlinmania.coloreyre.Theme

/**
 * Provides an extension trait for attaching sections to error reports.
 */

public fun Handler.note(note: Any): Handler {
    sections.add(HelpInfo.Note(note, theme))
    return this
}

public fun Handler.withNote(note: () -> Any): Handler {
    sections.add(HelpInfo.Note(note(), theme))
    return this
}

public fun Handler.warning(warning: Any): Handler {
    sections.add(HelpInfo.Warning(warning, theme))
    return this
}

public fun Handler.withWarning(warning: () -> Any): Handler {
    sections.add(HelpInfo.Warning(warning(), theme))
    return this
}

public fun Handler.suggestion(suggestion: Any): Handler {
    sections.add(HelpInfo.Suggestion(suggestion, theme))
    return this
}

public fun Handler.withSuggestion(suggestion: () -> Any): Handler {
    sections.add(HelpInfo.Suggestion(suggestion(), theme))
    return this
}

public fun Handler.withSection(section: () -> Any): Handler {
    sections.add(HelpInfo.Custom(section()))
    return this
}

public fun Handler.section(section: Any): Handler {
    sections.add(HelpInfo.Custom(section))
    return this
}

public fun Handler.error(error: Throwable): Handler {
    sections.add(HelpInfo.Error(error, theme))
    return this
}

public fun Handler.withError(error: () -> Throwable): Handler {
    sections.add(HelpInfo.Error(error(), theme))
    return this
}

public fun Handler.suppressBacktrace(suppress: Boolean): Handler {
    suppressBacktrace = suppress
    return this
}

internal sealed class HelpInfo {
    data class Error(val error: Throwable, val theme: Theme) : HelpInfo()
    data class Custom(val section: Any) : HelpInfo()
    data class Note(val note: Any, val theme: Theme) : HelpInfo()
    data class Warning(val warning: Any, val theme: Theme) : HelpInfo()
    data class Suggestion(val suggestion: Any, val theme: Theme) : HelpInfo()

    override fun toString(): String = when (this) {
        is Note -> "${theme.helpInfoNote.style("Note")}: $note"
        is Warning -> "${theme.helpInfoWarning.style("Warning")}: $warning"
        is Suggestion -> "${theme.helpInfoSuggestion.style("Suggestion")}: $suggestion"
        is Custom -> section.toString()
        is Error -> buildString {
            append("Error:")
            for ((n, currentError) in error.chain().withIndex()) {
                appendLine()
                append(" ".repeat(n))
                append(theme.helpInfoError.style(currentError.message ?: currentError.toString()))
            }
        }
    }
}

