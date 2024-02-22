package js.interprete

open class JsObject : JsValue {
    val indexProperties = sortedMapOf<String, JsValue>(compareBy { it.toInt() })
    val nameProperties = mutableMapOf<String, JsValue>()

    fun getProperty(name: String): JsValue {
        return if (name.isJsInt()) {
            indexProperties[name] ?: JsUndefined
        } else {
            nameProperties[name] ?: JsUndefined
        }
    }

    fun setProperty(name: String, value: JsValue) {
        if (name.isJsInt()) {
            indexProperties[name] = value
        } else {
            nameProperties[name] = value
        }
    }

    override fun asBoolean(): Boolean {
        return true
    }

    override fun asNumber(): Double {
        throw UnsupportedOperationException("Cannot convert object to number")
    }

    override fun asString(): String {
        val sb = StringBuilder("{")
        var count = 0
        val map = mutableMapOf<String, JsValue>()
        map.putAll(indexProperties)
        map.putAll(nameProperties)
        for ((name, value) in map) {
            sb.append(' ').appendName(name).append(": ").appendValue(value)
            count++
            if (count < map.size) {
                sb.append(",")
            } else {
                sb.append(' ')
            }
        }
        sb.append("}")
        return sb.toString()
    }
}

class JsArray : JsObject() {
    override fun asString(): String {
        val sb = StringBuilder()
        sb.append("[")
        var count = 0
        val size = indexProperties.size + nameProperties.size
        for ((_, value) in indexProperties) {
            sb.append(' ').appendValue(value)
            count++
            if (count < size) {
                sb.append(",")
            } else {
                sb.append(' ')
            }
        }
        for ((name, value) in nameProperties) {
            sb.append(' ').appendName(name).append(": ").appendValue(value)
            count++
            if (count < size) {
                sb.append(",")
            } else {
                sb.append(' ')
            }
        }
        sb.append("]")
        return sb.toString()
    }
}

private fun String.isJsInt(): Boolean {
    when (this.length) {
        0 -> return false
        1 -> return this[0].isDigit()
        else -> {
            if (this[0] == '0') {
                return false
            }
            for (c in this.substring(1)) {
                if (!c.isDigit()) {
                    return false
                }
            }
            return true
        }
    }
}

private fun StringBuilder.appendName(name: String): StringBuilder {
    if (name.toDoubleOrNull() != null) {
        this.append('\'').append(name).append('\'')
    } else {
        this.append(name)
    }
    return this
}

private fun StringBuilder.appendValue(value: JsValue): StringBuilder {
    if (value is JsString) {
        this.append('\'').append(value.asString()).append('\'')
    } else {
        this.append(value.asString())
    }
    return this
}
