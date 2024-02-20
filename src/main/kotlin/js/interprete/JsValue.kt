package js.interprete


interface JsValue {

    fun asBoolean(): Boolean

    fun asNumber(): Double

    fun asString(): String
}

data class JsNumber(private val value: Double) : JsValue {
    override fun asBoolean(): Boolean {
        return value != 0.0
    }

    override fun asNumber(): Double {
        return value
    }

    override fun asString(): String {
        val integer = value.toInt()
        return if (integer.toDouble() == value) {
            integer.toString()
        } else {
            value.toString()
        }
    }
}

data class JsString(private val value: String) : JsValue {
    override fun asBoolean(): Boolean {
        return value != "false"
    }

    override fun asNumber(): Double {
        return value.toDouble()
    }

    override fun asString(): String {
        return value
    }
}

data class JsBoolean(private val value: Boolean): JsValue {
   override fun asBoolean(): Boolean {
        return value
    }

    override fun asNumber(): Double {
        return if (value) 1.0 else 0.0
    }

    override fun asString(): String {
        return if (value) "true" else "false"
    }
}

object JsNull : JsValue {
    override fun asBoolean(): Boolean {
        return false
    }

    override fun asNumber(): Double {
        return 0.0
    }

    override fun asString(): String {
        return "null"
    }
}

object JsUndefined : JsValue {
    override fun asBoolean(): Boolean {
        return false
    }

    override fun asNumber(): Double {
        return Double.NaN
    }

    override fun asString(): String {
        return "undefined"
    }
}