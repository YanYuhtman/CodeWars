import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.zip.ZipEntry
import kotlin.test.assertEquals

class `Last digit of a large number` {
    //https://www.codewars.com/kata/5511b2f550906349a70004e1/train/kotlin


    fun lastDigit(a: BigInteger, b:BigInteger):Int{
        when (a.mod(BigInteger.TEN).toInt()) {
            1 -> listOf(1)
            2 -> listOf(2, 4, 8, 6)
            3 -> listOf(3, 9, 7, 1)
            4 -> listOf(4, 6)
            5 -> listOf(5)
            6 -> listOf(6)
            7 -> listOf(7, 9, 3, 1)
            8 -> listOf(8, 4, 2, 6)
            9 -> listOf(9, 1)
            else -> listOf(0)
        }.also {
            if(b == BigInteger.ZERO)
                return 1
            val index = (b % BigInteger.valueOf(it.size.toLong())).toInt()
            return it[(if(index <= 0) it.size else index) - 1]
        }
    }

    @Test
    fun test(){
        for(i in 1L.. 9) {
            for(k in 1L .. 9){
                println("$i,$k")
                assertEquals(Math.pow(i.toDouble(), k.toDouble()).toInt() % 10, lastDigit(BigInteger.valueOf(i), BigInteger.valueOf(k)) )
                }
        }
//        for( i in 1L .. 9)
//                lastDigit(BigInteger.valueOf(i),BigInteger.valueOf(9))
//        assertEquals(0, lastDigit(BigInteger.valueOf(0),BigInteger.valueOf(100)))
//        assertEquals(1, lastDigit(BigInteger.valueOf(1212313),BigInteger.valueOf(0)))
//        assertEquals(6, lastDigit(BigInteger.valueOf(2),BigInteger.valueOf(4)))
//        assertEquals(2, lastDigit(BigInteger.valueOf(2),BigInteger.valueOf(5)))
    }
    @Test
    fun `Basic Tests`() {

//        assertEquals(1, lastDigit(BigInteger("38478283309666793836849742090710316189560762987099451409"), BigInteger("7401111768466259882705341284413760954653319772025364")))
        assertEquals(4, lastDigit(BigInteger("4"), BigInteger("1")))
        assertEquals(6, lastDigit(BigInteger("4"), BigInteger("2")))
        assertEquals(9, lastDigit(BigInteger("9"), BigInteger("7")))
        assertEquals(0, lastDigit(BigInteger("10"), BigInteger("10000000000")))
        assertEquals(1, lastDigit(BigInteger("10"), BigInteger("0")))
        assertEquals(1, lastDigit(BigInteger("9435756757744477447576867898089079079808908347583277453475"), BigInteger("0")))
        assertEquals(6, lastDigit(BigInteger("1606938044258990275541962092341162602522202993782792835301376"), BigInteger("2037035976334486086268445688409378161051468393665936250636140449354381299763336706183397376")))
        assertEquals(7, lastDigit(BigInteger("3715290469715693021198967285016729344580685479654510946723"), BigInteger("68819615221552997273737174557165657483427362207517952651")))
    }

}