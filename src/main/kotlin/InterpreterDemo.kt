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

    code = """
        var a=1
        var b=1+a
        var c=5+a+b
        if (c<1) {
            2
        } else {
            3
        }
    """
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = "console.log(\"hello js\")"
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")

    code = """
        var a=123
        console.log(a)
    """
    println("code:\n$code\nresult\n${engine.evaluate(code)}\n")
}