// port-lint: source tests/bt_disabled.rs
package io.github.kotlinmania.coloreyre

import kotlin.test.Test
import kotlin.test.assertFalse

class BtDisabledTest {

    @Test
    fun disabled() {
        EyreRuntime.reset()
        HookBuilder
            .new()
            .displayEnvSection(false)
            .install()
            .getOrThrow()

        val error = RuntimeException("error occured")
        val handler = Handler(
            filters = emptyList(),
            backtrace = null,
            suppressBacktrace = false,
            spanTrace = null,
            sections = mutableListOf(),
            displayEnvSection = false,
            displayLocationSection = false,
            issueUrl = null,
            issueMetadata = emptyList(),
            issueFilter = IssueFilterCallback { true },
            theme = Theme.dark(),
            location = null,
        )
        val report = handler.debug(error)
        assertFalse(
            report.contains("RUST_BACKTRACE"),
            "Report should not contain RUST_BACKTRACE when env section is disabled",
        )
    }
}