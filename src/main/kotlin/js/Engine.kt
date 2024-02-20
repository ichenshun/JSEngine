package js

import js.ast.CharStream
import js.ast.Lexer
import js.ast.Parser
import js.interprete.BuiltInObject
import js.interprete.Interpreter
import js.interprete.ExecutionContext
import js.interprete.Value

class Engine {

    fun evaluate(code: String): Value {
        val stream = CharStream(code)
        val parser = Parser(Lexer(stream))
        val tree = parser.parse()
        val interpreter = Interpreter()
        val globalContext = ExecutionContext(null)
        BuiltInObject().register(globalContext)
        return interpreter.evaluate(globalContext, tree)
    }
}