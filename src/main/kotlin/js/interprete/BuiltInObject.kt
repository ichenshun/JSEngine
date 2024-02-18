package js.interprete


class BuiltInObject {

    fun register(context: ExecutionContext) {
        context.setVariable("console", Value(ValueType.OBJECT, Console()))
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

}