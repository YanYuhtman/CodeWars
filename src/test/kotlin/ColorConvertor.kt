import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ColorConvertor {
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