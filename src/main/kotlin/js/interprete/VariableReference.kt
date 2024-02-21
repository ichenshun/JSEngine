package js.interprete


interface VariableReference {
    fun getValue(): JsValue
    fun setValue(value: JsValue)
}

class IdentifierVariableReference(
    private val context: ExecutionContext,
    private val name: String
): VariableReference {
    override fun getValue(): JsValue {
        return context.getVariable(name)
    }

    override fun setValue(value: JsValue) {
        context.setVariable(name, value)
    }
}

class MemberVariableReference(
    private val context: ExecutionContext,
    private val jsObject: JsObject,
    private val propertyName: String
): VariableReference {
    override fun getValue(): JsValue {
        val value = jsObject.getProperty(propertyName)
        if (value is JsFunction) {
            // 将函数和对象绑定
            context.setVariable("this", jsObject)
        }
        return value
    }

    override fun setValue(value: JsValue) {
        jsObject.setProperty(propertyName, value)
    }
}