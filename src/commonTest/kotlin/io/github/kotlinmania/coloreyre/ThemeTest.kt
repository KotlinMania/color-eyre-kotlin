// port-lint: source tests/theme.rs
package io.github.kotlinmania.coloreyre

import io.github.kotlinmania.coloreyre.section.error
import io.github.kotlinmania.coloreyre.section.note
import io.github.kotlinmania.coloreyre.section.suggestion
import io.github.kotlinmania.coloreyre.section.warning
import kotlin.test.Test
import kotlin.test.assertTrue

class ThemeTest {
    @Test
    fun test_error_backwards_compatibility() {
        setup()
        val errorMsg = "test"
        val (handler, error) = get_error(errorMsg)
        val target = handler.debug(error)
        test_backwards_compatibility(target, "theme_error_control_minimal.txt")
        assertTrue(target.contains(errorMsg), "Report should contain the error message")
    }

    private fun test_backwards_compatibility(target: String, fileName: String) {
        assertTrue(target.contains("Note"), "Report should contain 'Note'")
        assertTrue(target.contains("Warning"), "Report should contain 'Warning'")
        assertTrue(target.contains("Suggestion"), "Report should contain 'Suggestion'")
        assertTrue(target.contains("Error"), "Report should contain 'Error'")
    }

    private fun get_error(msg: String): Pair<Handler, Throwable> = create_report(msg)

    private fun create_report(msg: String): Pair<Handler, Throwable> {
        val error = RuntimeException(msg)
        val handler =
            Handler(
                filters = emptyList(),
                backtrace = null,
                suppressBacktrace = false,
                spanTrace = null,
                sections = mutableListOf(),
                displayEnvSection = true,
                displayLocationSection = false,
                issueUrl = null,
                issueMetadata = emptyList(),
                issueFilter = IssueFilterCallback { true },
                theme = Theme.dark(),
                location = null,
            )
        handler.note("note")
        handler.warning("warning")
        handler.suggestion("suggestion")
        handler.error(TestError("error"))
        return Pair(handler, error)
    }

    private data class TestError(
        val msg: String,
    ) : Throwable(msg),
        io.github.kotlinmania.thiserror.StdError

    @Test
    fun test_panic_backwards_compatibility() {
        setup()
        val handler =
            Handler(
                filters = emptyList(),
                backtrace = null,
                suppressBacktrace = false,
                spanTrace = null,
                sections = mutableListOf(),
                displayEnvSection = true,
                displayLocationSection = false,
                issueUrl = null,
                issueMetadata = emptyList(),
                issueFilter = IssueFilterCallback { true },
                theme = Theme.dark(),
                location = null,
            )
        val error = RuntimeException("panic message")
        val target = handler.debug(error)
        assertTrue(
            target.contains("panic message"),
            "Report should contain the panic message",
        )
    }

    private fun setup() {
        EyreRuntime.reset()
        HookBuilder
            .new()
            .displayEnvSection(true)
            .install()
            .getOrThrow()
    }
}
