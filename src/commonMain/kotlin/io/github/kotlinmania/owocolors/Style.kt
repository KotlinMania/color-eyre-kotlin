package io.github.kotlinmania.owocolors

public data class Style(
    public val isBold: Boolean = false,
    public val color: String? = null,
) {
    public fun style(target: Any?): String = target?.toString() ?: ""
    public fun purple(): Style = copy(color = "purple")
    public fun white(): Style = copy(color = "white")
    public fun bold(): Style = copy(isBold = true)
    public fun red(): Style = copy(color = "red")
    public fun brightRed(): Style = copy(color = "brightRed")
    public fun cyan(): Style = copy(color = "cyan")
    public fun brightCyan(): Style = copy(color = "brightCyan")
    public fun yellow(): Style = copy(color = "yellow")
    public fun brightYellow(): Style = copy(color = "brightYellow")
    public fun green(): Style = copy(color = "green")
    public fun blue(): Style = copy(color = "blue")
    public fun brightBlack(): Style = copy(color = "brightBlack")
}

public fun style(): Style = Style()
