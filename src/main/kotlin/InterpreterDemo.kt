import js.Engine


fun main() {
    val engine = Engine()
    var code = "1 + 2"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "1 + 2*3"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "(1 + 2)*3"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code  = "1 + 2*(2**3)"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "1 + 2*2**3 / 3"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "1>2"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "1>2+1"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "1<2 +1"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "(1<2) + 1"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "(1>2) + 1"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "\"abc\""
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")
}