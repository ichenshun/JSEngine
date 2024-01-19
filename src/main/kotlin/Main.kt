import js.ast.CharStream
import js.ast.Parser
import js.ast.Lexer
import java.io.File

fun main(args: Array<String>) {
    // Try adding program arguments at Run/Debug configuration
    println("Program arguments: ${args.joinToString()}")
    var file = File(args[0])
    var stream = CharStream(file.readText())
    var parser = Parser(Lexer(stream))
    parser.parse()
}