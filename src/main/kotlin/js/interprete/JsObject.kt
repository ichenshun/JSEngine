package js.interprete


open class JsObject {
    open fun get(name: String): JsValue {
        return JsValue.UNDEFINED
    }
}

class Console : JsObject() {
    private val properties = mutableMapOf<String, JsValue>()
    init {
        properties["log"]= JsValue(ValueType.FUNCTION, JsFunction(this::log))
    }

    override fun get(name: String): JsValue {
        return properties[name]?: JsValue.UNDEFINED
    }

    private fun log(arguments: List<JsValue>): JsValue {
        println(arguments.joinToString(separator = " ") { it.toDisplayString() })
        return JsValue.UNDEFINED
    }
}