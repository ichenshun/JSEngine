package js.interprete

import js.ast.SingleExpression


open class Object {
    private val properties = mutableMapOf<String, Value>()

    fun getProperty(name: String): Value {
        return properties[name]?: Value.UNDEFINED
    }

    fun setProperty(name: String, value: Value) {
        properties[name] = value
    }
}

class Console : Object() {

    init {
        setProperty("log", Value(ValueType.FUNCTION, FunctionNative(this::log)))
    }

    private fun log(arguments: List<Value>): Value {
        println(arguments.joinToString(separator = " ") { it.asString() })
        return Value.UNDEFINED
    }
}