import com.maihao.mox.Mox
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths


class MoxTest {

    @Test
    fun testControlFlowIfElse() {
        testScript(
            chapter = "controlflow",
            "IfElseTest"
        )
    }

    @Test
    fun testControlFlowForLoop() {
        testScript(
            chapter = "controlflow",
            "ForLoopTest"
        )
    }

    @Test
    fun testFunctionsReturnStmt() {
        testScript(
            chapter = "functions",
            "FunctionFiboTest"
        )
    }

    @Test
    fun testClassesDeclaration() {
        testScript(
            chapter = "classes",
            "declareClassTest"
        )
    }

    @Test
    fun testClassesInstancesAndMethod() {
        testScript(
            chapter = "classes",
            "InstanceAndMethodTest"
        )
    }

    @Test
    fun testClassesThis() {
        testScript(
            chapter = "classes",
            "thisClassTest"
        )
    }

    @Test
    fun testClassesInit() {
        testScript(
            chapter = "classes",
            "initTest"
        )
    }

    @Test
    fun testClassesInheritance() {
        testScript(
            chapter = "classes",
            "classInheritanceTest"
        )
    }

    @Test
    fun testClassesSuper() {
        testScript(
            chapter = "classes",
            filename = "superTest"
        )
    }

    private fun testScript(chapter: String, filename: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteArrayOutputStream)

        // 保存原始的 System.out
        val oldOut = System.out

        // 设置新的 System.out
        System.setOut(printStream)

        Mox.runFile("src/test/kotlin/testfiles/$chapter/$filename")

        System.setOut(oldOut)

        // 获取输出的结果
        val result = byteArrayOutputStream.toString().trim()

        val expected = Files.readString(Paths.get("src/test/kotlin/testfiles/$chapter/${filename}.expected")).trim()

        assertEquals(expected, result)
    }

}