import js.ast.CharStream
import js.ast.Parser
import js.ast.Lexer
import java.io.File

fun main(args: Array<String>) {
    // Try adding program arguments at Run/Debug configuration
    println("Program arguments: ${args.joinToString()}")
    val file = File(args[0])
    val stream = CharStream(file.readText())
    val parser = Parser(Lexer(stream))
    val tree = parser.parse()
    println(tree)
}