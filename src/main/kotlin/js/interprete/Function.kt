package js.interprete

import js.ast.FunctionDeclaration

open class Function() {
    open fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value {
        return Value.UNDEFINED
    }
}

class FunctionNative(private val nativeFunction: (List<Value>) -> Value): Function() {
    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value {
        return nativeFunction(arguments)
    }
}

class FunctionCustom(private val functionDeclaration: FunctionDeclaration): Function() {
    override fun call(context: ExecutionContext, interpreter: Interpreter, arguments: List<Value>): Value {
        // 给形参赋值，创建一个执行上下文，类似函数调用栈
        val parameters = functionDeclaration.parameters
        val executionContext = ExecutionContext(context)
        for (i in parameters.indices) {
            executionContext.setVariable(parameters[i].value, arguments[i])
        }

        // 执行函数体
        val functionBody = functionDeclaration.body
        val result = interpreter.evaluateFunctionBody(executionContext, functionBody)
        return result
    }
}