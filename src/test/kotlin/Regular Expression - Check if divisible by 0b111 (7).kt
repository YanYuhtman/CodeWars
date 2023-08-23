import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val multipleOf7 = "0|[01]*?(111)|(111[0]+\$)|(10101[0]*\$)|(100011[0]*\$)|(110001[0]*\$)|(1001101\$)|(1011011\$)"
class `Regular Expression - Check if divisible by 0b111 (7)` {

    //https://www.codewars.com/kata/56a73d2194505c29f600002d


    companion object {
        private val regex = Regex(multipleOf7)
    }

    @Test
    fun edgeCases() {
        assertFalse(regex.matches(""), "Testing for: empty string")
        assertTrue(regex.matches("0"), "Testing for: 0")
    }

    @Test
    fun fixedTests() {
        for (n in 1..999) {
            if(n % 7 == 0) println(n.toString(2))
//            assertEquals(n % 7 == 0, regex.matches(n.toString(2)), "Testing for: $n")
        }
    }

}