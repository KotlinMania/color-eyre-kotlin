// port-lint: source lib.rs
package io.github.kotlinmania.coloreyre

/**
 * The kind of type erased error being reported.
 */
public sealed class ErrorKind {
    /**
     * A non recoverable error, also known as a panic.
     */
    public data class NonRecoverable(
        public val payload: Any?,
    ) : ErrorKind()

    /**
     * A recoverable error, also known as an error value.
     */
    public data class Recoverable(
        public val error: Throwable,
    ) : ErrorKind()
}
