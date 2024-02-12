package js

import js.ast.CharStream
import js.ast.Lexer
import js.ast.Parser
import js.interprete.Interpreter
import js.interprete.ExecutionContext
import js.interprete.Value

 class Engine {

    fun evaluate(code: String): Value {
        val stream = CharStream(code)
        val parser = Parser(Lexer(stream))
        val tree = parser.parse()
        println(tree)
        val interpreter = Interpreter()
        // TODO 不同Engine实例的全局上下文应该分开
        return interpreter.evaluate(ExecutionContext.globalContext, tree)
    }
}