import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class `Sum by Factors` {
    //https://www.codewars.com/kata/54d496788776e49e6b00052f

    val Int.isPrime:Boolean get() {
        for(i in 2 .. Math.floor(Math.sqrt(Math.abs(this).toDouble())).toInt())
            if(this % i == 0)
                return false
        return true
    }
    fun findPrimeFactors(n:Int, largesPrime: Int):IntArray {
        var _n = Math.max(Math.abs(n), largesPrime)
        val result = mutableListOf<Int>()
        while (_n-- > 1)
            for(i in _n .. 2)
                if(_n % i == 0 && i.isPrime){
                    if(largesPrime < 2) return intArrayOf(i)
                    result.add(i)
                }
        return result.toTypedArray() as IntArray
    }

    fun getPrimeFactorSums(I:IntArray): List<IntArray>{
        val maxPrime = findPrimeFactors(Math.max(Math.abs(I.max()),Math.abs(I.min())),-1)[0]
        val pFactors = findPrimeFactors(I.sumOf { Math.abs(it) },maxPrime)

        return listOf()
    }

    @Test
    fun test(){
        assertEquals(listOf(intArrayOf(2,12), intArrayOf(3,27), intArrayOf(5,15))
            ,getPrimeFactorSums(intArrayOf(12,15)))
    }


}