// port-lint: source lib.rs
package io.github.kotlinmania.coloreyre

/**
 * Install the default panic and error report hooks.
 */
public fun install(): Result<Unit> = HookBuilder.new().install()

