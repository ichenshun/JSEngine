package js.interprete


class BuiltInJsObject {
    private val objectMap = mutableMapOf<String, JsObject>()
    init {
        objectMap["console"] = Console()
    }

    fun get(name: String): JsObject? {
        return objectMap[name]
    }

}