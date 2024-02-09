package js.interprete

class JsExecutionContext {
    private val variables = mutableMapOf<String, JsValue>()

    fun initGlobalContext() {

    }

    fun getVariable(name: String): JsValue {
        return variables[name]?: globalContext.variables[name]?: throw RuntimeException("Variable '$name' not found")
    }

    fun setVariable(name: String, value: JsValue) {
        variables[name] = value
    }

    fun removeVariable(name: String) = variables.remove(name)

    companion object {
        val globalContext = JsExecutionContext()
        init {
            BuiltInJsObject().register(globalContext)
        }
    }
}
