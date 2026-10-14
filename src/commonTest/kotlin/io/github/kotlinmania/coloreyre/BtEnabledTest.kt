// port-lint: source tests/bt_enabled.rs
package io.github.kotlinmania.coloreyre

import kotlin.test.Test
import kotlin.test.assertTrue

class BtEnabledTest {

    @Test
    fun enabled() {
        EyreRuntime.reset()
        HookBuilder
            .new()
            .displayEnvSection(true)
            .install()
            .getOrThrow()

        val error = RuntimeException("error occured")
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
        val report = handler.debug(error)
        assertTrue(
            report.contains("RUST_BACKTRACE"),
            "Report should contain RUST_BACKTRACE when env section is displayed",
        )
    }
}