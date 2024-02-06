package js.interprete


enum class ValueType {
    NUMBER, STRING, BOOLEAN, OBJECT, FUNCTION, NULL, UNDEFINED
}

data class JsValue(val valueType: ValueType, val value: Any?) {
    companion object{
        val UNDEFINED = JsValue(ValueType.UNDEFINED, null)
    }
}

