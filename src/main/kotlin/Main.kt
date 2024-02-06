import js.Engine
import java.io.File

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")
    val file = File(args[0])
    val engine = Engine()
    engine.evaluate(file.readText())
}