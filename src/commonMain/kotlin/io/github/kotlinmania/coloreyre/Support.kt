package io.github.kotlinmania.coloreyre

public data class Location(
    public val file: String,
    public val line: UInt,
    public val column: UInt = 0u,
)

public data class PanicInfo(
    public val payload: String? = null,
    public val location: Location? = null,
)

public enum class SpanTraceStatus {
    Captured,
    Unsupported,
    Empty,
}

public class SpanTrace(private val status: SpanTraceStatus = SpanTraceStatus.Captured) {
    public fun status(): SpanTraceStatus = status
    public fun colorized(): String = ""
    override fun toString(): String = ""

    public companion object {
        public fun capture(): SpanTrace = SpanTrace(SpanTraceStatus.Captured)
    }
}

internal object PanicRuntime {
    fun panicking(): Boolean = false
    fun setHook(hook: (PanicInfo) -> Unit) {}
}

internal object Console {
    fun errLine(message: String) {
        println(message)
    }
}

public class PathBuf(public val path: String) {
    override fun toString(): String = path
}

public class BacktraceSymbol(
    private val name: String? = null,
    private val lineno: UInt? = null,
    private val filename: PathBuf? = null,
) {
    public fun name(): String? = name
    public fun lineno(): UInt? = lineno
    public fun filename(): PathBuf? = filename
}

public class BacktraceFrame(
    private val symbols: List<BacktraceSymbol> = emptyList(),
) {
    public fun symbols(): List<BacktraceSymbol> = symbols
}

public class Backtrace(
    private val frames: List<BacktraceFrame> = emptyList(),
) {
    public fun frames(): List<BacktraceFrame> = frames
    override fun toString(): String = ""

    public companion object {
        public fun new(): Backtrace = Backtrace()
        public fun capture(): Backtrace = Backtrace()
    }
}

internal object Env {
    fun varOrNull(name: String): String? = null
}

internal object EyreRuntime {
    fun setHook(hook: HookFunc): Result<Unit> = Result.success(Unit)
}

internal object SourceLookup {
    fun linesAround(file: Any?, line: UInt, before: Int, after: Int): List<Pair<String, UInt>> = emptyList()
}

public fun String.center(width: Int, fillChar: Char = ' '): String {
    if (length >= width) return this
    val leftPadding = (width - length) / 2
    val rightPadding = width - length - leftPadding
    return fillChar.toString().repeat(leftPadding) + this + fillChar.toString().repeat(rightPadding)
}

public fun Throwable.chain(): List<Throwable> {
    val list = mutableListOf<Throwable>()
    var current: Throwable? = this
    while (current != null) {
        list.add(current)
        current = current.cause
    }
    return list
}

public fun Throwable.spanTraceOrNull(): SpanTrace? = null
