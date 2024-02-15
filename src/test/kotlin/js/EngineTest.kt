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
    fun setUp() {
        // 这个方法在每个@Test标记的测试方法执行前都会被执行
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
    }

    @Test
    fun testAdditiveExpression() {
        val code = "console.log(1 + 2)"
        Engine().evaluate(code)
        assertEquals("3.0\n", outputStreamCaptor.toString())
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
        assertEquals("testvar=aaa1.02.0\n", outputStreamCaptor.toString())
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
        assertEquals("John is 50.0 years old.\n" +
                "add function\n" +
                "abcdefg\n" +
                "nullll\n",
            outputStreamCaptor.toString())
    }
}