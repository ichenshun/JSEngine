package js

import js.ast.SyntaxError
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class EngineTest {

    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    // 这个方法在每个@Test标记的测试方法执行前都会被执行，每次都会创建新的EngineTest实例
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
    }

    @Test
    fun testArithmeticExpression() {
        val code = """
            console.log(1 + 2)
            console.log(1/2)
            console.log(1 + 2 * 3)
            console.log(2 * 3 + 4)
            console.log(1 * 2 + 3 * 4)
            console.log((1 + 2)*3)
            console.log(10 * (2 + 3))
            console.log(10 * (2 + 3) + 2)
            console.log(10 * 20 * 30 + 40)
            console.log(10 * 20 / 40 - 40 * 50)
            console.log(10 * 20 / ( 30 - 40 ) * 50)
            console.log(1 + 2*(2**3))
            console.log(1 + 2*2**3 / 2)
            console.log(60 + 70 - 80 - 10 * 20 / ( 30 - 40 ) * 50)
            console.log((1<2) + 1)
            console.log((1>2) + 1)
            var c = 120
            console.log(c)
            console.log(c++)
            console.log(++c)
            var vv = 1
            console.log(vv)
            console.log(vv--)
            console.log(--vv)
        """
        Engine().evaluate(code)
        val excepted = """
            3
            0.5
            7
            10
            14
            9
            50
            52
            6040
            -1995
            -1000
            17
            9
            1050
            2
            1
            120
            120
            122
            1
            1
            -1
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }

    @Test
    fun testLogicalExpression() {
        val code = """
            console.log(1 < 2 && 2 < 3)
            console.log(1 < 2 || 2 < 3)
            console.log(!(1 < 2))
            console.log(!(1 > 2))
            var d = 10
            var zero = 0
            var c = "this is c"
            console.log(d == 10 || c)
            console.log(d != 10 || c)
            console.log(d == 10 && c)
            console.log(d != 10 && c)
            console.log(!zero)
            console.log(!d)
        """
        Engine().evaluate(code)
        val excepted = """
            true
            true
            false
            true
            true
            this is c
            this is c
            false
            true
            false
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }

    @Test
    fun testIfStatement() {
        val code = """
            var a=1
            var b=1+a
            var c=5+a+b
            if (c<1) {
                console.log(2)
            } else {
                console.log(3)
            }
            if (a<b) {
                console.log(4)
            }
            if (c>b) {
                console.log(c)
            }
        """
        Engine().evaluate(code)
        val expected = """
            3
            4
            8
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun testMultiArguments() {
        val code = """
            var name = "Bob"
            console.log(1>2, name, 123, "abc")
        """
        Engine().evaluate(code)
        assertEquals("false Bob 123 abc\n", outputStreamCaptor.toString())
    }

    @Test
    fun stringCanBeDefinedAndConcatenated() {
        val code = """
            console.log('Hello, World!')
            console.log(123+"456")
            console.log(123+456)
            console.log("456" + 123)
            console.log(true+"123")
            console.log("abc" + "bcd")
        """
        Engine().evaluate(code)
        val excepted = """
            Hello, World!
            123456
            579
            456123
            true123
            abcbcd
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }

    @Test
    fun stringCannotBeTerminatedWithLineBreak() {
        val code = """
            console.log("Hello,
                World!")
        """
        assertThrows<SyntaxError> {
            Engine().evaluate(code)
        }
    }

    @Test
    fun functionCanBeDeclaredAndCall() {
        val code = """
            function add(a, b) {
                return a + b
            }
            console.log(add(1, 2))
            console.log(add(52, 4))
        """
        Engine().evaluate(code)
        val expected = """
            3
            56
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun functionCanBeCalledBeforeDeclare() {
        val code = """
            var magic = "aaa"
            var testvar = addabc(1, 2)
            console.log("testvar=" + testvar)
            function addabc(a, b) {
                return magic + a + b 
            }
        """
        Engine().evaluate(code)
        assertEquals("testvar=aaa12\n", outputStreamCaptor.toString())
    }

    @Test
    fun multiFunctionCanBeDeclaredAndCalledAndGlobalVariableCanBeReferenced() {
        val code = """
            var factor = 20
            var factor2 = 10
            function minus(a, b) {
                return a - b - factor
            }
            function add(a, b) {
                return a + b + factor2
            }
            function test(a, b) {
                var c = a+b
                return add(a, c) + minus(a, b)
            }
            console.log(test(50, 200))
        """
        Engine().evaluate(code)
        assertEquals("140\n", outputStreamCaptor.toString())
    }

    @Test
    fun localVariablesCannotBeReferencedFromOutside() {
        val code = """
            function test() {
                var a = 10
                var b = 20
                return a + b
            }
            console.log(test())
            console.log(a)
        """
        assertThrows<RuntimeException> {
            Engine().evaluate(code)
        }

        assertEquals("30\n", outputStreamCaptor.toString())
    }

    @Test
    fun testVariableReferenceInFunction() {
        val code = """
            var varInGlobal = "varInGlobal"
            function first() {
                var varInFirstFunction = "varInFirstFunction"
                console.log(varInGlobal)
                function second() {
                    var varInSecondFunction = "varInSecondFunction"
                    console.log(varInFirstFunction)
                    console.log(varInGlobal)
                    function third() {
                        console.log(varInFirstFunction)
                        console.log(varInSecondFunction)
                        console.log(varInGlobal)
                    }
                    third()
                }
                second()
            }
            first()
        """
        Engine().evaluate(code)
        val expected = """
            varInGlobal
            varInFirstFunction
            varInGlobal
            varInFirstFunction
            varInSecondFunction
            varInGlobal
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun objectPropertyNameCanBeLiteral() {
        val code = """
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
                [adde("1", "23")]: "add function"
            };
            function adde(a, b) {
                return magic + a + b 
            }
            console.log(person.firstName + " is " + person.age + " years old.")
            magic = "ccc"
            console.log(person.bbb123)
            console.log(person.true)
            console.log(person.null)
        """
        Engine().evaluate(code)
        val expected = """
            John is 50 years old.
            add function
            abcdefg
            nullll
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun objectPropertyCanBeFunction() {
        val code = """
            const person = {
                name: ["Bob", "Smith"],
                age: 32,
                bio: function () {
                    console.log(`${'$'}{this.name[0]} ${'$'}{this.name[1]} 现在 ${'$'}{this.age} 岁了。`);
                },
                introduceSelf: function () {
                    console.log(`你好！我是 ${'$'}{this.name[0]}。`);
                },
            };
            person.name;
            person.name[0];
            person.age;
            person.bio();
            person.introduceSelf();
        """
        Engine().evaluate(code)
        val expected = """
            Bob Smith 现在 32 岁了。
            你好！我是 Bob。
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun objectPropertyCanBeObject() {
        val code = """
            const person = {
                name: {
                    first: "Bob",
                    last: "Smith",
                },
            };
            console.log(person.name.first, person["name"]["last"])
        """
        Engine().evaluate(code)
        assertEquals("Bob Smith", outputStreamCaptor.toString().trim())
    }

    @Test
    fun arrayCanBeDefinedCorrectly() {
        val code = """
            var arr = [1,2,3,4,5];
            console.log(arr[0]);
            console.log(arr[1]);
            console.log(arr[1,2,3])
            console.log(arr);
        """
        Engine().evaluate(code)
        val excepted = """
            1
            2
            4
            [ 1, 2, 3, 4, 5 ]
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }

    @Test
    fun ternaryExpressionCanBeEvaluatedCorrectly() {
        val code = """
            var a = 1;
            console.log(a == 1 ? "a is 1" : "a is not 1")
            console.log(a > 0 ? a < 10 ? a > 5 ? "a is between 5 and 10" : 
                    "a is between 0 and 5" : "a is great than 10" : " a is less than 0");
        """
        Engine().evaluate(code)
        val expected = """
            a is 1
            a is between 0 and 5
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun engineInstancesCannotShareStateAmongEachOther() {
        val code = """
            var globalVar = 1;
            console.log(globalVar);
        """
        Engine().evaluate(code)
        assertEquals("1\n", outputStreamCaptor.toString())

        val code2 = """
            console.log(globalVar);
        """
        assertThrows<RuntimeException> {
            Engine().evaluate(code2)
        }
    }

    @Test
    fun thisCanBeUsedInGlobalContext() {
        val code = """
            console.log(this);
            this.name = ["Bob", "Smith"]
            console.log(this.name[0])
        """
        Engine().evaluate(code)
        val expected = """
            {}
            Bob
        """.trimIndent() + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun templateStringCanBeUsedCorrectly() {
        val code = """
            var a = 1;
            console.log(`a is ${'$'}{a}`)
            const a = 5;
            const b = 10;
            console.log(`Fifteen is ${'$'}{a + b} and
                not ${'$'}{2 * a + b}.`);
        """
        Engine().evaluate(code)
        val expected = """
            a is 1
            Fifteen is 15 and
        """.trimIndent() + """
                not 20.""" + "\n"
        assertEquals(expected, outputStreamCaptor.toString())
    }

    @Test
    fun emptyExpressionInTemplateStringIsNotAllowed() {
        val code =  """
            console.log(`${'$'}{}`
        """
        assertThrows<SyntaxError> {
            Engine().evaluate(code)
        }
    }
}
