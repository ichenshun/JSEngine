package js.interprete


open class Object {
    open fun get(name: String): Value {
        return Value.UNDEFINED
    }
}

class Console : Object() {
    private val properties = mutableMapOf<String, Value>()
    init {
        properties["log"]= Value(ValueType.FUNCTION, FunctionNative(this::log))
    }

    override fun get(name: String): Value {
        return properties[name]?: Value.UNDEFINED
    }

    private fun log(arguments: List<Value>): Value {
        println(arguments.joinToString(separator = " ") { it.toDisplayString() })
        return Value.UNDEFINED
    }
}