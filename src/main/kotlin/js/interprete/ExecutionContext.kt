package js.interprete

class ExecutionContext {
    private val variables = mutableMapOf<String, Value>()

    fun initGlobalContext() {

    }

    fun getVariable(name: String): Value {
        return variables[name]?: globalContext.variables[name]?: throw RuntimeException("Variable '$name' not found")
    }

    fun setVariable(name: String, value: Value) {
        variables[name] = value
    }

    fun removeVariable(name: String) = variables.remove(name)

    companion object {
        val globalContext = ExecutionContext()
        init {
            BuiltInObject().register(globalContext)
        }
    }
}
