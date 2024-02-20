package js.interprete

import js.ast.FunctionBody
import js.ast.Token

abstract class Function() {
    abstract fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value
}

class FunctionNative(private val nativeFunction: (List<Value>) -> Value): Function() {

    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value {
        return nativeFunction(arguments)
    }
}

class FunctionCustom(private val parameters: List<Token>, private val functionBody: FunctionBody): Function() {
    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value {
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