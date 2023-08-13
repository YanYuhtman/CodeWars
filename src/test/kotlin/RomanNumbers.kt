import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RomanNumbers {

    private val Char.toDecimal: Pair<Int,Int>
        get() {
            return when (this) {
                'I' -> Pair(0,1)
                'V' -> Pair(1,5)
                'X' -> Pair(2,10)
                'L' -> Pair(3,50)
                'C' -> Pair(4,100)
                'D' -> Pair(5,500)
                'M' -> Pair(6,1000)
                else -> Pair(-1,0)
            }
        }

    fun decode(str: String): Int {
        if(str.isEmpty())
            return 0

        return  str.map { it.toDecimal }
            .reduceRight{v1,v2 -> if(v2.first - v1.first == 1 || (v2.first - v1.first == 2 && v2.first % 2 == 0))
                Pair(v1.first,v2.second - v1.second) else Pair(v1.first,v2.second + v1.second)}
            .second
    }

    @Test
    fun basic() {
        assertEquals(0, decode(""))
        assertEquals(1, decode("I"))
        assertEquals(21, decode("XXI"))
        assertEquals(2008, decode("MMVIII"))
        assertEquals(1666, decode("MDCLXVI"))
        assertEquals(1139, decode("MCXXXIX"))

    }

}