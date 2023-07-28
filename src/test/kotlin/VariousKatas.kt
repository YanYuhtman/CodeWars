import kotlin.test.assertEquals
import kotlin.test.Test

class PersistenceNumber {
    fun persistence(num: Int) : Int {
        var n = num
        var count = 0
        while (n / 10 != 0){
            n = persistenceStep(n)
            count++
        }
        return count
    }
    fun persistenceStep(num: Int) : Int {
        var mult = 1
        var n = num
        do{
            mult *= n % 10
            n /= 10
        }while (n != 0)
        return mult
    }
    @Test
    fun `Basic Tests`() {
        assertEquals(3, persistence(39))
        assertEquals(0, persistence(4))
        assertEquals(2, persistence(25))
        assertEquals(4, persistence(999))
    }

}

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


class TestExample {
    fun rgb(r: Int, g: Int, b: Int): String {
       return String.format("%2X%2X%2X",correctColor(r) ,correctColor(g) , correctColor(b)).replace(' ', '0')
    }
    inline fun correctColor(c:Int):Int {return Math.min(Math.max(0,c),255)}
    @Test
    fun testFixed() {
        assertEquals("000000", rgb(0, 0, 0))
        assertEquals("000000", rgb(0, 0, -20))
        assertEquals("FFFFFF", rgb(300,255,255))
        assertEquals("ADFF2F", rgb(173,255,47))
        assertEquals("9400D3", rgb(148, 0, 211))
    }
}

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




