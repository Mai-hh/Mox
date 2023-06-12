import com.maihao.mox.Mox
import org.junit.jupiter.api.Test


class MoxTest {

    @Test
    fun testControlFlowForLoop() {
        testScript("ControlFlowTest")
    }

    private fun testScript(filename: String) {
        Mox.runFile("src/test/kotlin/testfiles/controlflow/$filename")
    }

}