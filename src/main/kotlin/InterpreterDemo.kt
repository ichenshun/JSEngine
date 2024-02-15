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

    code = """
        var factor = 120
        console.log(factor)
        console.log(factor++)
        console.log(++factor)
        var vv = 1
        console.log(vv)
        console.log(vv--)
        console.log(--vv)
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        var magic = "bbb"
        var person = {
            firstName:"John",
            lastName:"Doe",
            age:50,
            eyeColor:"blue",
            "123": "abc",
            true: "abcdefg",
            null: "nullll",
            122: "122",
            [adde(1, 23)]: "add function"
        };
        function adde(a, b) {
            return magic + a + b 
        }
        console.log(person.firstName + " is " + person.age + " years old.")
        magic = "ccc"
        console.log(person.ccc24)
        console.log(person.true)
        console.log(person.null)
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")

    code = """
        console.log(123+"456")
        console.log(123+456)
        console.log("456" + 123)
        console.log(true+"123")
    """
    println("code:\n$code\nresult\n${Engine().evaluate(code)}\n")
}