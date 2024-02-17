package js.interprete


enum class ValueType {
    NUMBER, STRING, BOOLEAN, OBJECT, FUNCTION, NULL, UNDEFINED
}

data class Value(val valueType: ValueType, val value: Any) {
    companion object {
        val UNDEFINED = Value(ValueType.UNDEFINED, 0)
    }

    fun toBoolean(): Boolean {
        return when (valueType) {
            ValueType.NUMBER -> (value as Number) != 0.0
            ValueType.STRING -> (value as String).isEmpty()
            ValueType.BOOLEAN -> value as Boolean
            ValueType.NULL -> false
            ValueType.UNDEFINED -> false
            else -> throw IllegalStateException("Cannot convert $valueType to Boolean")
        }
    }

    fun toDouble(): Double {
        return when (valueType) {
            ValueType.NUMBER -> value as Double
            ValueType.BOOLEAN -> if (value as Boolean) 1.0 else 0.0
            ValueType.NULL -> 0.0
            ValueType.UNDEFINED -> Double.NaN
            else -> throw IllegalStateException("Cannot convert $valueType to Double")
        }
    }

    fun asString(): String {
        return when (valueType) {
            ValueType.NUMBER, ValueType.STRING, ValueType.BOOLEAN, ValueType.OBJECT-> value.toString()
            ValueType.NULL -> "null"
            ValueType.UNDEFINED -> "undefined"
            else -> throw IllegalStateException("Cannot convert $valueType to String")
        }
    }
}

