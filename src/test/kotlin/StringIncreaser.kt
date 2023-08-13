import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class StringIncreaser{

    fun incrementString(str:String):String{
        var isNegative = BigInteger.ONE
        return "(.*?)(-?)([\\d]*)\$".toRegex().find(str)
            ?.groupValues
            ?.mapIndexedNotNull { index, value ->
                if(index == 0)
                    null

                else if(index == 2) {
                    if(value == "-") {
                        isNegative = -BigInteger.ONE
                        value
                    }else
                        null
                }else if(index == 3){
                    if(value.isBlank())
                        "1"
                    else {
                        val tmp = value.toBigInteger().add(BigInteger.ONE.multiply(isNegative)).toString()
                        if (tmp.length >= value.length)
                            tmp
                        else
                            value.replaceRange(value.length - tmp.length, value.length, tmp)
                    }
                }
                else
                    value
            }!!.joinToString("")

    }



    @Test
    fun FixedTests() {
        assertEquals(incrementString("foobar000"), "foobar001")
        assertEquals(incrementString("foobar999"), "foobar1000")
        assertEquals(incrementString("foobar00999"), "foobar01000")
        assertEquals(incrementString("foo"), "foo1")
        assertEquals(incrementString("foobar001"), "foobar002")
        assertEquals(incrementString("foobar1"), "foobar2")
        assertEquals(incrementString("1"), "2")
        assertEquals(incrementString(""), "1")
        assertEquals(incrementString("009"), "010")
        assertEquals("fooo-0098",incrementString("fooo-0099"))
    }
}