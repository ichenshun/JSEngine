package js

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
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
    fun testAdditiveExpression() {
        val code = """
            console.log(1 + 2)
            console.log(1/2)
        """
        Engine().evaluate(code)
        val excepted = """
            3
            0.5
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }

    @Test
    fun testOutput() {
        val code = "console.log('Hello, World!')"
        Engine().evaluate(code)
        assertEquals("Hello, World!\n", outputStreamCaptor.toString())
    }

    @Test
    fun testFunctionCallBeforeDeclare() {
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
    fun testObjectDefine() {
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
    fun testArrayDefine() {
        val code = """
            var arr = [1,2,3,4,5];
            console.log(arr[0]);
            console.log(arr[1]);
            console.log(arr);
        """
        Engine().evaluate(code)
        val excepted = """
            1
            2
            [ 1, 2, 3, 4, 5 ]
        """.trimIndent() + "\n"
        assertEquals(excepted, outputStreamCaptor.toString())
    }
}