// port-lint: source writers.rs
package io.github.kotlinmania.coloreyre

internal class HeaderWriter<H, W : Appendable>(
    private val inner: W,
    private val header: H,
    private var started: Boolean = false,
) : Appendable {
    fun ready(): ReadyHeaderWriter<H, W> {
        started = false
        return ReadyHeaderWriter(this)
    }

    fun inProgress(): ReadyHeaderWriter<H, W> {
        started = true
        return ReadyHeaderWriter(this)
    }

    internal fun appendToInner(value: CharSequence?) {
        if (!started && !value.isNullOrEmpty()) {
            inner.append(header.toString())
            started = true
        }
        inner.append(value)
    }

    override fun append(value: CharSequence?): Appendable {
        appendToInner(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable {
        appendToInner(value?.subSequence(startIndex, endIndex))
        return this
    }

    override fun append(value: Char): Appendable {
        appendToInner(value.toString())
        return this
    }
}

internal fun interface WriterExt<W : Appendable> {
    fun header(header: Any): HeaderWriter<Any, W>
}

internal class ReadyHeaderWriter<H, W : Appendable>(
    private val writer: HeaderWriter<H, W>,
) : Appendable {
    override fun append(value: CharSequence?): Appendable {
        writer.append(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable {
        writer.append(value, startIndex, endIndex)
        return this
    }

    override fun append(value: Char): Appendable {
        writer.append(value)
        return this
    }
}

internal fun Appendable.header(header: Any): HeaderWriter<Any, Appendable> =
    HeaderWriter(this, header)

internal interface DisplayExt {
    fun withHeader(header: Any): Header<DisplayExt, Any> = Header(this, header)

    fun withFooter(footer: Any): Footer<DisplayExt, Any> = Footer(this, footer)
}

internal class FooterWriter<W : Appendable>(
    private val inner: W,
    var hadOutput: Boolean = false,
) : Appendable {
    override fun append(value: CharSequence?): Appendable {
        if (!hadOutput && !value.isNullOrEmpty()) {
            hadOutput = true
        }
        inner.append(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable = append(value?.subSequence(startIndex, endIndex))

    override fun append(value: Char): Appendable {
        hadOutput = true
        inner.append(value)
        return this
    }
}

internal data class Footer<B, H>(
    private val body: B,
    private val footer: H,
) {
    override fun toString(): String {
        val out = StringBuilder()
        val writer = FooterWriter(out)
        writer.append(body.toString())
        if (writer.hadOutput) {
            out.append(footer)
        }
        return out.toString()
    }
}

internal data class Header<B, H>(
    private val body: B,
    private val h: H,
) {
    override fun toString(): String =
        buildString {
            val writer = HeaderWriter(this, h)
            writer.ready().append(body.toString())
        }
}

internal data class FormattedSpanTrace(
    private val spanTrace: SpanTrace,
) {
    override fun toString(): String {
        if (spanTrace.status() != SpanTraceStatus.Captured) {
            return ""
        }
        return spanTrace.colorized().prependIndent("  ")
    }
}

internal data class EnvSection(
    internal val btCaptured: Boolean,
    internal val spanTrace: SpanTrace?,
) {
    override fun toString(): String =
        buildString {
            val verbosity = if (PanicRuntime.panicking()) panicVerbosity() else libVerbosity()
            append(BacktraceOmitted(!btCaptured))
            val separated = HeaderedStringBuilder(this, "\n")
            separated.ready { append(SourceSnippets(verbosity)) }
            separated.ready { append(SpanTraceOmitted(spanTrace)) }
        }
}

private data class SpanTraceOmitted(
    private val spanTrace: SpanTrace?,
) {
    override fun toString(): String {
        if (spanTrace?.status() == SpanTraceStatus.Unsupported) {
            return "Warning: SpanTrace capture is Unsupported.\n" +
                "Ensure that you've set up a tracing-error ErrorLayer and the semver versions are compatible"
        }
        return ""
    }
}

private data class BacktraceOmitted(
    private val omitted: Boolean,
) {
    override fun toString(): String =
        if (omitted) {
            "Backtrace omitted. Run with RUST_BACKTRACE=1 environment variable to display it."
        } else {
            "Run with COLORBT_SHOW_HIDDEN=1 environment variable to disable frame filtering."
        }
}

private data class SourceSnippets(
    private val verbosity: Verbosity,
) {
    override fun toString(): String =
        if (verbosity <= Verbosity.Medium) {
            "Run with RUST_BACKTRACE=full to include source snippets."
        } else {
            ""
        }
}
