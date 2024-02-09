package js.interprete


class BuiltInJsObject {

    fun register(context: JsExecutionContext) {
        context.setVariable("console", JsValue(ValueType.OBJECT, Console()))
    }

}