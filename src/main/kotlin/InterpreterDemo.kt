import js.Engine


fun main() {
    var code = "1 + 2"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "1 + 2*3"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "(1 + 2)*3"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code  = "1 + 2*(2**3)"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "1 + 2*2**3 / 3"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "1>2"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "1>2+1"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "1<2 +1"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "(1<2) + 1"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "(1>2) + 1"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "\"abc\""
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

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
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = "console.log(\"hello js\")"
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        var a=123
        console.log(a)
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        var name = "Bob"
        console.log(1>2, name, 123, "abc")
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        function add(a, b) {
            return a + b
        }
        console.log(add(1, 2))
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        function add(a, b) {
            return a + b
        }
        console.log(add(52, 2))
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        var factor = 120
        function minus(a, b) {
            return a - b - factor
        }
        function add(a, b) {
            return a + b + factor
        }
        function test(a, b) {
            var c = a+b
            return add(a, c) + minus(a, b)
        }
        console.log(test(50, 200))
        console.log(c)
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        var factor = 120
        function minus(a, b) {
            return a - b - factor
        }
        function add(a, b) {
            return a + b + factor
        }
        function test(a, b) {
            var c = a+b
            return add(a, c) + minus(a, b)
            console.log(c)
        }
        console.log(test(50, 200))
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")
}