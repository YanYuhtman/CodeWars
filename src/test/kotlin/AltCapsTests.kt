import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AltCapsTests {
    fun capitalize(text:String) : List<String>{
        return listOf<String>(
            String(text.mapIndexed { index, value -> if (index % 2 == 0) value.uppercaseChar() else value }.toCharArray()),
            String(text.mapIndexed { index, value -> if (index % 2 == 1) value.uppercaseChar() else value }.toCharArray())
        )
    }
    @Test
    fun basicTests() {
        assertEquals(listOf("AbCdEf", "aBcDeF"), capitalize("abcdef"))
        assertEquals(listOf("CoDeWaRs", "cOdEwArS"), capitalize("codewars"))
        assertEquals(listOf("AbRaCaDaBrA", "aBrAcAdAbRa"), capitalize("abracadabra"))
        assertEquals(listOf("CoDeWaRrIoRs", "cOdEwArRiOrS"), capitalize("codewarriors"))
    }
}