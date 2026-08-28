package io.github.kotlinmania.url

public object Url {
    public fun withParams(url: String, params: List<Pair<String, String>>): String {
        if (params.isEmpty()) return url
        val query = params.joinToString("&") { (k, v) ->
            val encodedKey = encodeQueryComponent(k)
            val encodedVal = encodeQueryComponent(v)
            "$encodedKey=$encodedVal"
        }
        val separator = if (url.contains("?")) "&" else "?"
        return "$url$separator$query"
    }

    private fun encodeQueryComponent(s: String): String =
        buildString {
            for (ch in s) {
                when (ch) {
                    in 'a'..'z', in 'A'..'Z', in '0'..'9', '-', '_', '.', '~' -> append(ch)
                    ' ' -> append("+")
                    else -> {
                        val bytes = ch.toString().encodeToByteArray()
                        for (b in bytes) {
                            append('%')
                            append(((b.toInt() ushr 4) and 0xF).toString(16).uppercase())
                            append((b.toInt() and 0xF).toString(16).uppercase())
                        }
                    }
                }
            }
        }
}
