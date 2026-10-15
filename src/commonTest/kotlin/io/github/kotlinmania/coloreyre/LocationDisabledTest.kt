// port-lint: source tests/location_disabled.rs
package io.github.kotlinmania.coloreyre

import kotlin.test.Test
import kotlin.test.assertFalse

class LocationDisabledTest {
    @Test
    fun disabled() {
        EyreRuntime.reset()
        HookBuilder
            .new()
            .displayLocationSection(false)
            .install()
            .getOrThrow()

        val error = RuntimeException("error occured")
        val handler =
            Handler(
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
            report.contains("Location:"),
            "Report should not contain 'Location:' when location section is disabled",
        )
    }
}
