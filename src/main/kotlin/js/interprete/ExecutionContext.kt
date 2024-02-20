package js.interprete

class ExecutionContext(private val parentContext: ExecutionContext?) {
    private val variables = mutableMapOf<String, Value>()

    fun getVariable(name: String): Value {
        // 首先检查本地变量
        // 如果未找到，则检查父上下文
        // 如果还是未找到，则抛出异常
        var value = variables[name]
        if (value == null && parentContext != null) {
            value = parentContext.getVariable(name)
        }
        return value ?: throw RuntimeException("Variable '$name' not found")
    }

    fun setVariable(name: String, value: Value) {
        variables[name] = value
    }

    fun removeVariable(name: String) = variables.remove(name)
}
