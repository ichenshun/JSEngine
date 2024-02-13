package js.interprete


open class Object {
    private val properties = mutableMapOf<String, Value>()
    fun get(name: String): Value {
        return properties[name]?: Value.UNDEFINED
    }
    fun set(name: String, value: Value) {
        properties[name] = value
    }
}

class Console : Object() {

    init {
        set("log", Value(ValueType.FUNCTION, FunctionNative(this::log)))
    }

    private fun log(arguments: List<Value>): Value {
        println(arguments.joinToString(separator = " ") { it.toDisplayString() })
        return Value.UNDEFINED
    }
}