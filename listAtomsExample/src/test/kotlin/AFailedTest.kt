import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class AFailedTest {
    @Test
    @DisplayName("This Test Fails by design")
    fun failTest() {
        assert(false)
    }
}