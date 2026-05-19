// port-lint: source config.rs
package io.github.kotlinmania.coloreyre

import io.github.kotlinmania.coloreyre.section.PanicMessage
import io.github.kotlinmania.owocolors.Style
import io.github.kotlinmania.owocolors.style

/**
 * Configuration options for customizing the behavior of the provided panic and
 * error reporting hooks.
 */

/**
 * A theme used by color-eyre.
 */
public data class Theme(
    public val file: Style = Style(),
    public val lineNumber: Style = Style(),
    public val spantraceTarget: Style = Style(),
    public val spantraceFields: Style = Style(),
    public val activeLine: Style = Style(),
    public val error: Style = Style(),
    public val helpInfoNote: Style = Style(),
    public val helpInfoWarning: Style = Style(),
    public val helpInfoSuggestion: Style = Style(),
    public val helpInfoError: Style = Style(),
    public val dependencyCode: Style = Style(),
    public val crateCode: Style = Style(),
    public val codeHash: Style = Style(),
    public val panicHeader: Style = Style(),
    public val panicMessage: Style = Style(),
    public val panicFile: Style = Style(),
    public val panicLineNumber: Style = Style(),
    public val hiddenFrames: Style = Style(),
) {
    /**
     * Styles printed paths.
     */
    public fun file(style: Style): Theme = copy(file = style)

    /**
     * Styles the line number of a file.
     */
    public fun lineNumber(style: Style): Theme = copy(lineNumber = style)

    /**
     * Styles the span trace target, such as the module and function name.
     */
    public fun spantraceTarget(style: Style): Theme = copy(spantraceTarget = style)

    /**
     * Styles fields associated with a tracing span.
     */
    public fun spantraceFields(style: Style): Theme = copy(spantraceFields = style)

    /**
     * Styles the selected line of displayed code.
     */
    public fun activeLine(style: Style): Theme = copy(activeLine = style)

    /**
     * Styles errors printed by the eyre handler.
     */
    public fun error(style: Style): Theme = copy(error = style)

    /**
     * Styles the note section header.
     */
    public fun helpInfoNote(style: Style): Theme = copy(helpInfoNote = style)

    /**
     * Styles the warning section header.
     */
    public fun helpInfoWarning(style: Style): Theme = copy(helpInfoWarning = style)

    /**
     * Styles the suggestion section header.
     */
    public fun helpInfoSuggestion(style: Style): Theme = copy(helpInfoSuggestion = style)

    /**
     * Styles the error section header.
     */
    public fun helpInfoError(style: Style): Theme = copy(helpInfoError = style)

    /**
     * Styles code that is not part of your crate.
     */
    public fun dependencyCode(style: Style): Theme = copy(dependencyCode = style)

    /**
     * Styles code that is in your crate.
     */
    public fun crateCode(style: Style): Theme = copy(crateCode = style)

    /**
     * Styles the hash after dependency code and crate code.
     */
    public fun codeHash(style: Style): Theme = copy(codeHash = style)

    /**
     * Styles the header of a panic.
     */
    public fun panicHeader(style: Style): Theme = copy(panicHeader = style)

    /**
     * Styles the message of a panic.
     */
    public fun panicMessage(style: Style): Theme = copy(panicMessage = style)

    /**
     * Styles paths of a panic.
     */
    public fun panicFile(style: Style): Theme = copy(panicFile = style)

    /**
     * Styles the line numbers of a panic.
     */
    public fun panicLineNumber(style: Style): Theme = copy(panicLineNumber = style)

    /**
     * Styles the "N frames hidden" message.
     */
    public fun hiddenFrames(style: Style): Theme = copy(hiddenFrames = style)

    public companion object {
        /**
         * Creates a blank theme.
         */
        public fun new(): Theme = Theme()

        /**
         * Returns a theme for dark backgrounds. This is the default.
         */
        public fun dark(): Theme = Theme(
            file = style().purple(),
            lineNumber = style().purple(),
            activeLine = style().white().bold(),
            error = style().brightRed(),
            helpInfoNote = style().brightCyan(),
            helpInfoWarning = style().brightYellow(),
            helpInfoSuggestion = style().brightCyan(),
            helpInfoError = style().brightRed(),
            dependencyCode = style().green(),
            crateCode = style().brightRed(),
            codeHash = style().brightBlack(),
            panicHeader = style().red(),
            panicMessage = style().cyan(),
            panicFile = style().purple(),
            panicLineNumber = style().purple(),
            hiddenFrames = style().brightCyan(),
            spantraceTarget = style().brightRed(),
            spantraceFields = style().brightCyan(),
        )

        /**
         * Returns a theme for light backgrounds.
         */
        public fun light(): Theme = Theme(
            file = style().purple(),
            lineNumber = style().purple(),
            spantraceTarget = style().red(),
            spantraceFields = style().blue(),
            activeLine = style().bold(),
            error = style().red(),
            helpInfoNote = style().blue(),
            helpInfoWarning = style().brightRed(),
            helpInfoSuggestion = style().blue(),
            helpInfoError = style().red(),
            dependencyCode = style().green(),
            crateCode = style().red(),
            codeHash = style().brightBlack(),
            panicHeader = style().red(),
            panicMessage = style().blue(),
            panicFile = style().purple(),
            panicLineNumber = style().purple(),
            hiddenFrames = style().blue(),
        )
    }
}

/**
 * A representation of a frame from a backtrace or span trace.
 */
public data class Frame(
    /**
     * Frame index.
     */
    public val n: Int,
    /**
     * Frame symbol name.
     */
    public val name: String? = null,
    /**
     * Source line number.
     */
    public val lineno: UInt? = null,
    /**
     * Source file path.
     */
    public val filename: PathBuf? = null,
) {
    internal fun isDependencyCode(): Boolean {
        val symbolPrefixes = listOf(
            "std::",
            "core::",
            "backtrace::backtrace::",
            "_rust_begin_unwind",
            "color_traceback::",
            "__rust_",
            "___rust_",
            "__pthread",
            "_main",
            "main",
            "__scrt_common_main_seh",
            "BaseThreadInitThunk",
            "_start",
            "__libc_start_main",
            "start_thread",
        )

        if (name != null && symbolPrefixes.any { name.startsWith(it) }) {
            return true
        }

        val filePrefixes = listOf(
            "/rustc/",
            "src/libstd/",
            "src/libpanic_unwind/",
            "src/libtest/",
        )
        val path = filename?.toString()
        return path != null &&
            (filePrefixes.any { path.startsWith(it) } || path.contains("/.cargo/registry/src/"))
    }

    /**
     * Heuristically determine whether a frame is likely to be a post panic
     * frame.
     */
    internal fun isPostPanicCode(): Boolean {
        val symbolPrefixes = listOf(
            "_rust_begin_unwind",
            "rust_begin_unwind",
            "core::result::unwrap_failed",
            "core::option::expect_none_failed",
            "core::panicking::panic_fmt",
            "color_backtrace::create_panic_handler",
            "std::panicking::begin_panic",
            "begin_panic_fmt",
            "failure::backtrace::Backtrace::new",
            "backtrace::capture",
            "failure::error_message::err_msg",
            "<failure::error::Error as core::convert::From<F>>::from",
        )
        return name?.let { current -> symbolPrefixes.any { current.startsWith(it) } } ?: false
    }

    /**
     * Heuristically determine whether a frame is likely to be part of the
     * language runtime.
     */
    internal fun isRuntimeInitCode(): Boolean {
        val symbolPrefixes = listOf(
            "std::rt::lang_start::",
            "test::run_test::run_test_inner::",
            "std::sys_common::backtrace::__rust_begin_short_backtrace",
        )
        val currentName = name ?: return false
        val currentFile = filename?.toString() ?: return false

        if (symbolPrefixes.any { currentName.startsWith(it) }) {
            return true
        }

        return currentName == "{{closure}}" && currentFile == "src/libtest/lib.rs"
    }
}

private data class StyledFrame(
    private val frame: Frame,
    private val theme: Theme,
) {
    override fun toString(): String = buildString {
        val isDependencyCode = frame.isDependencyCode()
        append(frame.n.toString().padStart(2))
        append(": ")

        val rawName = frame.name ?: "<unknown>"
        val hasHashSuffix = rawName.length > 19 &&
            rawName.substring(rawName.length - 19, rawName.length - 16) == "::h" &&
            rawName.substring(rawName.length - 16).all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
        val hashSuffix = if (hasHashSuffix) rawName.substring(rawName.length - 19) else "<unknown>"
        val displayName = if (hasHashSuffix) rawName.substring(0, rawName.length - 19) else rawName

        append(if (isDependencyCode) theme.dependencyCode.style(displayName) else theme.crateCode.style(displayName))
        append(theme.codeHash.style(hashSuffix))

        val file = frame.filename?.toString() ?: "<unknown source file>"
        val line = frame.lineno?.toString() ?: "<unknown line>"
        append('\n')
        append("    at ")
        append(theme.file.style(file))
        append(':')
        append(theme.lineNumber.style(line))

        if (libVerbosity() >= Verbosity.Full || panicVerbosity() >= Verbosity.Full) {
            append(SourceSection(frame, theme))
        }
    }
}

private data class SourceSection(
    private val frame: Frame,
    private val theme: Theme,
) {
    override fun toString(): String {
        val lineno = frame.lineno ?: return ""
        frame.filename ?: return ""
        val source = SourceLookup.linesAround(frame.filename, lineno, 2, 2)
        if (source.isEmpty()) {
            return ""
        }
        return buildString {
            for ((line, currentLineNumber) in source) {
                append('\n')
                if (currentLineNumber == lineno) {
                    append(theme.activeLine.style(currentLineNumber.toString().padStart(8)))
                    append(' ')
                    append(theme.activeLine.style(">"))
                    append(' ')
                    append(theme.activeLine.style(line))
                } else {
                    append(currentLineNumber.toString().padStart(8))
                    append(" | ")
                    append(line)
                }
            }
        }
    }
}

/**
 * Builder for customizing the behavior of the global panic and error report
 * hooks.
 */
public data class HookBuilder(
    private val filters: List<FilterCallback> = emptyList(),
    private val captureSpanTraceByDefault: Boolean = false,
    private val displayEnvSection: Boolean = true,
    private val displayLocationSection: Boolean = true,
    private val panicSection: Any? = null,
    private val panicMessage: PanicMessage? = null,
    private val theme: Theme = Theme.dark(),
    private val issueUrl: String? = null,
    private val issueMetadata: List<Pair<String, Any>> = emptyList(),
    private val issueFilter: IssueFilterCallback = IssueFilterCallback { true },
) {
    /**
     * Set the global styles that color-eyre should use.
     */
    public fun theme(theme: Theme): HookBuilder = copy(theme = theme)

    /**
     * Add a custom section to the panic hook that will be printed in the panic
     * message.
     */
    public fun panicSection(section: Any): HookBuilder = copy(panicSection = section)

    /**
     * Overrides the main error message printing section at the start of panic
     * reports.
     */
    public fun panicMessage(section: PanicMessage): HookBuilder = copy(panicMessage = section)

    /**
     * Set an upstream GitHub repository and enable issue reporting URL
     * generation.
     */
    public fun issueUrl(url: Any): HookBuilder = copy(issueUrl = url.toString())

    /**
     * Add a new entry to the metadata table in generated GitHub issue URLs.
     */
    public fun addIssueMetadata(key: Any, value: Any): HookBuilder =
        copy(issueMetadata = issueMetadata + (key.toString() to value))

    /**
     * Configures a filter for disabling issue URL generation for certain kinds
     * of errors.
     */
    public fun issueFilter(predicate: IssueFilterCallback): HookBuilder = copy(issueFilter = predicate)

    /**
     * Configures the default capture mode for span traces in error reports and
     * panics.
     */
    public fun captureSpanTraceByDefault(cond: Boolean): HookBuilder = copy(captureSpanTraceByDefault = cond)

    /**
     * Configures the environment variable info section and whether it is
     * displayed.
     */
    public fun displayEnvSection(cond: Boolean): HookBuilder = copy(displayEnvSection = cond)

    /**
     * Configures the location info section and whether it is displayed.
     */
    public fun displayLocationSection(cond: Boolean): HookBuilder = copy(displayLocationSection = cond)

    /**
     * Add a custom filter to the set of frame filters.
     */
    public fun addFrameFilter(filter: FilterCallback): HookBuilder = copy(filters = filters + filter)

    /**
     * Install the given hook as the global error report hook.
     */
    public fun install(): Result<Unit> {
        val (panicHook, eyreHook) = tryIntoHooks().getOrThrow()
        eyreHook.install().getOrThrow()
        panicHook.install()
        return Result.success(Unit)
    }

    /**
     * Add the default set of filters to this builder's configuration.
     */
    public fun addDefaultFilters(): HookBuilder =
        addFrameFilter(::defaultFrameFilter)
            .addFrameFilter(::eyreFrameFilters)

    /**
     * Create a panic hook and eyre hook from this builder.
     */
    public fun intoHooks(): Pair<PanicHook, EyreHook> = tryIntoHooks().getOrThrow()

    /**
     * Create a panic hook and eyre hook from this builder.
     */
    public fun tryIntoHooks(): Result<Pair<PanicHook, EyreHook>> {
        val resolvedPanicMessage = panicMessage ?: DefaultPanicMessage(theme)
        val panicHook = PanicHook(
            filters = filters,
            section = panicSection,
            panicMessage = resolvedPanicMessage,
            theme = theme,
            captureSpanTraceByDefault = captureSpanTraceByDefault,
            displayEnvSection = displayEnvSection,
            issueUrl = issueUrl,
            issueMetadata = issueMetadata,
            issueFilter = issueFilter,
        )
        val eyreHook = EyreHook(
            filters = panicHook.filters,
            captureSpanTraceByDefault = captureSpanTraceByDefault,
            displayEnvSection = displayEnvSection,
            displayLocationSection = displayLocationSection,
            theme = theme,
            issueUrl = issueUrl,
            issueMetadata = issueMetadata,
            issueFilter = issueFilter,
        )
        return Result.success(panicHook to eyreHook)
    }

    public companion object {
        /**
         * Construct a HookBuilder.
         */
        public fun new(): HookBuilder =
            blank()
                .addDefaultFilters()
                .captureSpanTraceByDefault(true)

        /**
         * Construct a HookBuilder with minimal features enabled.
         */
        public fun blank(): HookBuilder = HookBuilder()
    }
}

internal fun defaultFrameFilter(frames: MutableList<Frame>) {
    val topCutoff = frames.indexOfLast { it.isPostPanicCode() }
        .let { if (it >= 0) it + 2 else 0 }
    val bottomCutoff = frames.indexOfFirst { it.isRuntimeInitCode() }
        .let { if (it >= 0) it else frames.size }
    frames.retainAll { it.n in topCutoff..bottomCutoff }
}

internal fun eyreFrameFilters(frames: MutableList<Frame>) {
    val filters = listOf(
        "<color_eyre::Handler as eyre::EyreHandler>::default",
        "eyre::",
        "color_eyre::",
    )
    frames.retainAll { frame ->
        val name = frame.name ?: return@retainAll true
        filters.none { name.startsWith(it) }
    }
}

private data class DefaultPanicMessage(private val theme: Theme) : PanicMessage {
    override fun display(panicInfo: PanicInfo, out: Appendable) {
        out.appendLine(theme.panicHeader.style("The application panicked (crashed)."))
        val payload = panicInfo.payload ?: "<non string panic payload>"
        out.append("Message:  ")
        out.appendLine(theme.panicMessage.style(payload))
        out.append("Location: ")
        out.append(LocationSection(panicInfo.location, theme).toString())
    }
}

/**
 * A type representing an error report for a panic.
 */
public data class PanicReport(
    private val hook: PanicHook,
    private val panicInfo: PanicInfo,
    private val backtrace: Backtrace? = null,
    private val spanTrace: SpanTrace? = null,
) {
    override fun toString(): String = printPanicInfo(this)

    internal fun render(): String = buildString {
        hook.panicMessage.display(panicInfo, this)
        val verbosity = panicVerbosity()
        val captureBacktrace = verbosity != Verbosity.Minimal

        hook.section?.let {
            append("\n\n")
            append(it)
        }

        spanTrace?.let {
            append("\n\n")
            append(FormattedSpanTrace(it))
        }

        backtrace?.let {
            append("\n\n")
            append(hook.formatBacktrace(it))
        }

        if (hook.displayEnvSection) {
            append("\n\n")
            append(EnvSection(captureBacktrace, spanTrace))
        }

        val url = hook.issueUrl
        if (url != null && hook.issueFilter.filter(ErrorKind.NonRecoverable(panicInfo.payload))) {
            append("\n\n")
            append(
                io.github.kotlinmania.coloreyre.section.github.IssueSection(url, panicInfo.payload ?: "")
                    .withBacktrace(backtrace)
                    .withLocation(panicInfo.location)
                    .withMetadata(hook.issueMetadata),
            )
        }
    }
}

internal fun printPanicInfo(report: PanicReport): String = report.render()

/**
 * A panic reporting hook.
 */
public data class PanicHook(
    internal val filters: List<FilterCallback>,
    internal val section: Any?,
    internal val panicMessage: PanicMessage,
    internal val theme: Theme,
    private val captureSpanTraceByDefault: Boolean,
    internal val displayEnvSection: Boolean,
    internal val issueUrl: String?,
    internal val issueMetadata: List<Pair<String, Any>>,
    internal val issueFilter: IssueFilterCallback,
) {
    internal fun formatBacktrace(trace: Backtrace): BacktraceFormatter =
        BacktraceFormatter(filters, trace, theme)

    internal fun spantraceCaptureEnabled(): Boolean =
        Env.varOrNull("RUST_SPANTRACE")?.let { it != "0" } ?: captureSpanTraceByDefault

    /**
     * Install self as a global panic hook.
     */
    public fun install() {
        PanicRuntime.setHook(intoPanicHook())
    }

    /**
     * Convert self into the type expected by the panic runtime.
     */
    public fun intoPanicHook(): (PanicInfo) -> Unit = { panicInfo ->
        Console.errLine(panicReport(panicInfo).toString())
    }

    /**
     * Construct a panic reporter that prints its panic report through display.
     */
    public fun panicReport(panicInfo: PanicInfo): PanicReport {
        val verbosity = panicVerbosity()
        val captureBacktrace = verbosity != Verbosity.Minimal
        val spanTrace = if (spantraceCaptureEnabled()) SpanTrace.capture() else null
        val backtrace = if (captureBacktrace) Backtrace.new() else null
        return PanicReport(
            hook = this,
            panicInfo = panicInfo,
            spanTrace = spanTrace,
            backtrace = backtrace,
        )
    }
}

/**
 * An eyre reporting hook used to construct eyre handlers.
 */
public data class EyreHook(
    internal val filters: List<FilterCallback>,
    private val captureSpanTraceByDefault: Boolean,
    private val displayEnvSection: Boolean,
    private val displayLocationSection: Boolean,
    private val theme: Theme,
    private val issueUrl: String?,
    private val issueMetadata: List<Pair<String, Any>>,
    private val issueFilter: IssueFilterCallback,
) {
    internal fun default(error: Throwable): Handler {
        val backtrace = if (libVerbosity() != Verbosity.Minimal) Backtrace.new() else null
        val spanTrace = if (spantraceCaptureEnabled() && getDeepestSpantrace(error) == null) {
            SpanTrace.capture()
        } else {
            null
        }
        return Handler(
            filters = filters,
            backtrace = backtrace,
            suppressBacktrace = false,
            spanTrace = spanTrace,
            sections = mutableListOf(),
            displayEnvSection = displayEnvSection,
            displayLocationSection = displayLocationSection,
            issueUrl = issueUrl,
            issueMetadata = issueMetadata,
            issueFilter = issueFilter,
            theme = theme,
            location = null,
        )
    }

    internal fun spantraceCaptureEnabled(): Boolean =
        Env.varOrNull("RUST_SPANTRACE")?.let { it != "0" } ?: captureSpanTraceByDefault

    /**
     * Installs self as the global eyre handling hook.
     */
    public fun install(): Result<Unit> = EyreRuntime.setHook(intoEyreHook())

    /**
     * Convert self into the boxed type expected by the eyre hook registry.
     */
    public fun intoEyreHook(): HookFunc = { error -> default(error) }
}

internal typealias HookFunc = (Throwable) -> Handler

internal data class BacktraceFormatter(
    internal val filters: List<FilterCallback>,
    internal val inner: Backtrace,
    internal val theme: Theme,
) {
    override fun toString(): String = buildString {
        append(" BACKTRACE ".center(80, '-'))
        val frames = inner.frames().flatMapIndexed { index, frame ->
            frame.symbols().map { symbol ->
                Frame(
                    name = symbol.name(),
                    lineno = symbol.lineno(),
                    filename = symbol.filename(),
                    n = index + 1,
                )
            }
        }
        val filteredFrames = frames.toMutableList()
        when (Env.varOrNull("COLORBT_SHOW_HIDDEN")) {
            "1", "on", "y" -> Unit
            else -> filters.forEach { it.filter(filteredFrames) }
        }

        if (filteredFrames.isEmpty()) {
            append("\n<empty backtrace>")
            return@buildString
        }

        filteredFrames.sortBy { it.n }
        var lastN = 0
        for (frame in filteredFrames) {
            val frameDelta = frame.n - lastN - 1
            if (frameDelta != 0) {
                append('\n')
                append(theme.hiddenFrames.style(hiddenFrames(frameDelta)).center(80, ' '))
            }
            append('\n')
            append(StyledFrame(frame, theme))
            lastN = frame.n
        }

        val lastFilteredN = filteredFrames.last().n
        val lastUnfilteredN = frames.last().n
        if (lastFilteredN < lastUnfilteredN) {
            append('\n')
            append(theme.hiddenFrames.style(hiddenFrames(lastUnfilteredN - lastFilteredN)).center(80, ' '))
        }
    }
}

private fun hiddenFrames(n: Int): String {
    val plural = if (n == 1) "" else "s"
    return ". $n frame$plural hidden ."
}

internal enum class Verbosity {
    Minimal,
    Medium,
    Full,
}

internal fun panicVerbosity(): Verbosity = when (val value = Env.varOrNull("RUST_BACKTRACE")) {
    "full" -> Verbosity.Full
    null, "0" -> Verbosity.Minimal
    else -> Verbosity.Medium
}

internal fun libVerbosity(): Verbosity = when (val value = Env.varOrNull("RUST_LIB_BACKTRACE") ?: Env.varOrNull("RUST_BACKTRACE")) {
    "full" -> Verbosity.Full
    null, "0" -> Verbosity.Minimal
    else -> Verbosity.Medium
}

/**
 * Callback for filtering a vector of frames.
 */
public fun interface FilterCallback {
    public fun filter(frames: MutableList<Frame>)
}

/**
 * Callback for filtering issue URL generation in error reports.
 */
public fun interface IssueFilterCallback {
    public fun filter(kind: ErrorKind): Boolean
}
