package js

import js.ast.CharStream
import js.ast.Lexer
import js.ast.Parser
import js.interprete.BuiltInJsObject
import js.interprete.Interpreter
import js.interprete.JsExecutionContext
import js.interprete.JsValue

 class Engine {
    private val executionContext: JsExecutionContext
    init {
        val variables = mutableMapOf<String, JsValue>()
        BuiltInJsObject().register(variables)
        executionContext = JsExecutionContext(variables)
    }

    fun evaluate(code: String): JsValue {
        val stream = CharStream(code)
        val parser = Parser(Lexer(stream))
        val tree = parser.parse()
        println(tree)
        val interpreter = Interpreter()
        return interpreter.evaluate(executionContext, tree)
    }
}