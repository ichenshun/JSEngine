package js.ast

import kotlin.String


class CharStream(private val text: String) {
    private var position: Int = 0
    private var markPosition: Int = 0

    fun nextChar():Char? {
        if (position >= text.length) {
            return null;
        }
        return text[position++]
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