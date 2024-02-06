package js

import js.ast.CharStream
import js.ast.Lexer
import js.ast.Parser
import js.interprete.Interpreter
import js.interprete.JsValue


class Engine {
    private val interpreter = Interpreter()
    fun evaluate(code: String): JsValue {
        val stream = CharStream(code)
        val parser = Parser(Lexer(stream))
        val tree = parser.parse()
        println(tree)
        return interpreter.evaluate(tree)
    }
}