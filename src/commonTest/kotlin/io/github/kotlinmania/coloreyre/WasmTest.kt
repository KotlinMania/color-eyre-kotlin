// port-lint: source tests/wasm.rs
package io.github.kotlinmania.coloreyre

import io.github.kotlinmania.coloreyre.section.note
import io.github.kotlinmania.coloreyre.section.suggestion
import kotlin.test.Test
import kotlin.test.assertTrue

class WasmTest {

    @Test
    fun color_eyre_simple() {
        EyreRuntime.reset()
        install().getOrThrow()

        val error = RuntimeException("Base Error")
        val handler = Handler(
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
        handler.note("A note")
        handler.suggestion("A suggestion")

        val errStr = handler.debug(error)
        println("Error String is:\n\n$errStr")
        assertTrue(errStr.contains("A note"), "Report should contain 'A note'")
        assertTrue(errStr.contains("A suggestion"), "Report should contain 'A suggestion'")
        assertTrue(errStr.contains("Base Error"), "Report should contain 'Base Error'")
    }
}