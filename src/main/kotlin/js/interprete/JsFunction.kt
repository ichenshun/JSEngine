package js.interprete

import js.ast.FunctionDeclaration

open class JsFunction() {
    open fun call(interpreter: Interpreter, arguments: List<JsValue>): JsValue {
        return JsValue.UNDEFINED
    }
}

class JsFunctionNative(private val nativeFunction: (List<JsValue>) -> JsValue): JsFunction() {
    override fun call(interpreter: Interpreter, arguments: List<JsValue>): JsValue {
        return nativeFunction(arguments)
    }
}

class JsFunctionCustom(private val functionDeclaration: FunctionDeclaration): JsFunction() {
    override fun call(interpreter: Interpreter, arguments: List<JsValue>): JsValue {
        // 给形参赋值，创建一个执行上下文，类似函数调用栈
        val parameters = functionDeclaration.parameters
        val parameterValues = mutableMapOf<String, JsValue>()
        for (i in parameters.indices) {
            parameterValues[parameters[i].value] = arguments[i]
        }
        val executionContext = JsExecutionContext(parameterValues)
        // 执行函数体
        val functionBody = functionDeclaration.body
        val result = interpreter.evaluateFunctionBody(executionContext, functionBody)
        return result
    }
}