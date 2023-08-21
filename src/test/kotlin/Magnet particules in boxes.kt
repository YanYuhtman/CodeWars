import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.text.DecimalFormat

class `Magnet particules in boxes` {
    //https://www.codewars.com/kata/56c04261c3fcf33f2d000534


    //Funny was wasting couple of hours trying to optimize a solution when brute force tactics works :))

    fun doubles(maxk: Int, maxn: Int): Double {
        var sum: Double = 0.0
        for (n in 1..maxn)
            for (k in 1..maxk)
                sum += 1.0 / (k * Math.pow((n + 1.0), 2.0 * k))
        return sum
    }

    private fun assertFuzzyEquals(act:Double, exp:Double) {
        val inrange = Math.abs(act - exp) <= 1e-6
        if (inrange == false)
        {
            val df = DecimalFormat("#0.000000")
            println("At 1e-6: Expected must be " + df.format(exp) + ", but got " + df.format(act))
        }
        assertEquals(true, inrange)
    }
    @Test
    fun test1() {
        println("Fixed Tests: doubles")
        assertFuzzyEquals(doubles(1, 10), 0.5580321939764581)
        assertFuzzyEquals(doubles(10, 1000), 0.6921486500921933)

    }

}