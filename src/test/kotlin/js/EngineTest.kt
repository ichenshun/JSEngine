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
}