import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class `Sum by Factors` {
    //https://www.codewars.com/kata/54d496788776e49e6b00052f

    val Int.isPrime:Boolean get() {
        for(i in 2 .. Math.floor(Math.sqrt(Math.abs(this).toDouble())).toInt())
            if(this % i == 0)
                return false
        return true
    }
    fun findPrimeFactors(I:IntArray, largestPrime: Int):IntArray {
        val result = mutableListOf<Int>()
        I.forEach {
            var n = if(largestPrime > 0) largestPrime else Math.abs(it)
            for (i in n downTo 2) {
                if (it % i == 0 && i.isPrime) {
                    if (largestPrime < 2) return intArrayOf(i)
                    result.add(i)
                }
            }
        }
        return result.distinct().sorted().toIntArray()
    }

    fun sumOfDivided(I: IntArray): String {
        if(I.isEmpty())
            return ""
        val maxPrime = findPrimeFactors(intArrayOf( Math.max(Math.abs(I.max()),Math.abs(I.min()))),-1)[0]
        return findPrimeFactors(I,maxPrime)
            .fold(mutableListOf<String>()){acc, i -> acc.add("($i ${I.sumOf {if(it%i == 0) it else 0 }})"); acc}
            .joinToString("")

    }


    @Test
    fun testOne() {
        val lst = intArrayOf(12, 15)
        assertEquals("(2 12)(3 27)(5 15)",
            sumOfDivided(lst))
    }

    @Test
    fun testTwo() {
        val lst = intArrayOf(15, 21, 24, 30, 45)
        assertEquals("(2 54)(3 135)(5 90)(7 21)",
            sumOfDivided(lst))
    }

    @Test
    fun testThree() {
        val lst = intArrayOf(107, 158, 204, 100, 118, 123, 126, 110, 116, 100)
        assertEquals("(2 1032)(3 453)(5 310)(7 126)(11 110)(17 204)(29 116)(41 123)(59 118)(79 158)(107 107)",
            sumOfDivided(lst))
    }

    @Test
    fun testFour() {
        val lst = intArrayOf()
        assertEquals("",
            sumOfDivided(lst))
    }

    @Test
    fun testFive() {
        val lst = intArrayOf(1070, 1580, 2040, 1000, 1180, 1230, 1260, 1100, 1160, 1000)
        assertEquals("(2 12620)(3 4530)(5 12620)(7 1260)(11 1100)(17 2040)(29 1160)(41 1230)(59 1180)(79 1580)(107 1070)",
            sumOfDivided(lst))
    }


}