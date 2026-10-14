// port-lint: source tests/install.rs
package io.github.kotlinmania.coloreyre

import kotlin.test.Test
import kotlin.test.assertTrue

class InstallTest {

    @Test
    fun double_install_should_not_panic() {
        EyreRuntime.reset()
        install().getOrThrow()
        val second = install()
        assertTrue(second.isFailure, "Second install should fail")
    }
}