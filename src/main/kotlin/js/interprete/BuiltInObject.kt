package js.interprete


class BuiltInObject {

    fun register(context: ExecutionContext) {
        context.setVariable("console", Console())
    }

    class Console : JsObject() {

        init {
            setProperty("log", JsFunctionNative(this::log))
        }

        private fun log(arguments: List<JsValue>): JsValue {
            println(arguments.joinToString(separator = " ") { it.asString() })
            return JsUndefined
        }
    }

}