package js.interprete

open class Object {
    private val properties = mutableMapOf<String, Value>()

    fun getProperty(name: String): Value {
        return properties[name]?: Value.UNDEFINED
    }

    fun setProperty(name: String, value: Value) {
        properties[name] = value
    }

    override fun toString(): String {
        return super.toString()
    }
}

class Array : Object() {

    private val elements = mutableListOf<Value>()

    fun getElement(index: Int): Value {
        return elements[index]
    }

    fun append(value: Value) {
        val index = elements.size
        elements.add(value)
        setProperty(index.toString(), value)
    }

    fun setElement(index: Int, value: Value) {
        elements[index] = value
        setProperty(index.toString(), value)
    }

    override fun toString(): String {
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
