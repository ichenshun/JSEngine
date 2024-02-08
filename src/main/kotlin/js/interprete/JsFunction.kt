package js.interprete



class JsFunction(val nativeFunction: (List<JsValue>) -> JsValue) {
    fun call(arguments: List<JsValue>): JsValue {
        return nativeFunction(arguments)
    }
}