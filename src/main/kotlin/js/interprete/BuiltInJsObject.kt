package js.interprete


class BuiltInJsObject {

    fun register(variablesMap: MutableMap<String, JsValue>) {
        variablesMap["console"] = JsValue(ValueType.OBJECT, Console())
    }

}