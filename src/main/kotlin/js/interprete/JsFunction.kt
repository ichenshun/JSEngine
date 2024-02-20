package js.interprete

import js.ast.FunctionBody
import js.ast.Token


abstract class JsFunction: JsValue {
     abstract fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<JsValue>): JsValue
     override fun asBoolean(): Boolean {
         throw UnsupportedOperationException("Cannot convert function to boolean")
     }

     override fun asNumber(): Double {
         throw UnsupportedOperationException("Cannot convert function to number")
     }

     override fun asString(): String {
         throw UnsupportedOperationException("Cannot convert function to string")
     }
 }

class JsFunctionNative(private val nativeFunction: (List<JsValue>) -> JsValue): JsFunction() {

    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<JsValue>): JsValue {
        return nativeFunction(arguments)
    }
}

class JsFunctionCustom(private val parameters: List<Token>, private val functionBody: FunctionBody): JsFunction() {
    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<JsValue>): JsValue {
        // 给形参赋值，创建一个执行上下文，类似函数调用栈
        val executionContext = ExecutionContext(context)
        for (i in parameters.indices) {
            executionContext.setVariable(parameters[i].value, arguments[i])
        }

        // 执行函数体
        val functionBody = functionBody
        val result = interpreter.evaluateFunctionBody(executionContext, functionBody)
        return result
    }
}