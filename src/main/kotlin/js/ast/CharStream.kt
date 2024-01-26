package js.ast

import kotlin.String


class CharStream(private val text: String) {
    private var position: Int = -1
    private var markPosition: Int = 0

    var currentChar: Char? = nextChar()

    fun nextChar(): Char? {
        if (position >= text.length) {
           return null
        }
        position++
        if (position >= text.length) {
            currentChar = null
            return null
        }
        currentChar = text[position]
        return currentChar
    }

    /**
     * 回退到前一个字符
     */
    fun seekBack() {
        if (position < text.length) {
            position--
        }
    }

    /**
     * 标记当前位置，用于生成substring
     */
    fun mark() {
        markPosition = position
    }

    /**
     * 生成从标记位置到当前位置的substring，不包含当前位置的字符
     */
    fun substring(): String {
        return text.substring(markPosition, position)
    }
}