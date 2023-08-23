import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val multipleOf7 = "(0|111|100((1|00)0)*011|(101|100((1|00)0)*(1|00)1)(1((1|00)0)*(1|00)1)*(01|1((1|00)0)*011)|(110|100((1|00)0)*010|(101|100((1|00)0)*(1|00)1)(1((1|00)0)*(1|00)1)*(00|1((1|00)0)*010))(1|0(1((1|00)0)*(1|00)1)*(00|1((1|00)0)*010))*0(1((1|00)0)*(1|00)1)*(01|1((1|00)0)*011))+"
class `Regular Expression - Check if divisible by 0b111 (7)` {

    //https://www.codewars.com/kata/56a73d2194505c29f600002d
    //https://codegolf.stackexchange.com/questions/3503/hard-code-golf-regex-for-divisibility-by-7


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
        for (n in 1..99) {
//            println("${n.toString(2)} = ${n} ${if (n%3 == 0) "v" else "" }")
//            if(n % 3 == 0) println(n.toString(2))
            assertEquals(n % 7 == 0, regex.matches(n.toString(2)), "Testing for: $n")
        }
    }

}