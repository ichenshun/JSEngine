package js.interprete


class BuiltInObject {

    fun register(context: ExecutionContext) {
        context.setVariable("console", Value(ValueType.OBJECT, Console()))
    }

}