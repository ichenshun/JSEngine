package js.interprete

open class JsObject : JsValue {
    private val properties = mutableMapOf<String, JsValue>()

    fun getProperty(name: String): JsValue {
        return properties[name]?: JsUndefined
    }

    fun setProperty(name: String, value: JsValue) {
        properties[name] = value
    }

    override fun asBoolean(): Boolean {
        return true
    }

    override fun asNumber(): Double {
        throw UnsupportedOperationException("Cannot convert object to number")
    }

    override fun asString(): String {
        val sb = StringBuilder("{")
        for ((name, value) in properties) {
            sb.append(name).append(": ").append(value.asString()).append(", ")
        }
        sb.append("}")
        return sb.toString()
    }
}

class JsArray : JsObject() {

    private val elements = mutableListOf<JsValue>()

    fun getElement(index: Int): JsValue {
        return elements[index]
    }

    fun append(value: JsValue) {
        val index = elements.size
        elements.add(value)
        setProperty(index.toString(), value)
    }

    fun setElement(index: Int, value: JsValue) {
        elements[index] = value
        setProperty(index.toString(), value)
    }

    override fun asString(): String {
        val buffer = StringBuilder()
        buffer.append("[ ")
        val lastElement = elements.last()
        elements.dropLast(1).forEach {
            buffer.append(it.asString())
            buffer.append(", ")
        }
        buffer.append(lastElement.asString())
        buffer.append(" ]")
        return buffer.toString()
    }
}
