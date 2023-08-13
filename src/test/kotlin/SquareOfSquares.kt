import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SquareOfSquares {
    //https://www.codewars.com/kata/54eb33e5bc1a25440d000891


    object Decomp {

        fun decompose(n: Long): String {
           return getTheSquareSumOf(n*n, n-1 )?.let {
               return it.second.map { it.toString() }
                   .reduce{acc, s -> "$acc $s"}
           }?: "null"
        }
        fun getTheSquareSumOf(n: Long, k:Long): Pair<Long,MutableList<Long>>?{
            if(n == 0L)
                return Pair(0, mutableListOf())
            if(n < 0)
                return null

            for(i in k downTo 1){
                val i_2 = i * i
                val list = getTheSquareSumOf(n - i_2,i-1)
                list?.let {
                    if(it.first + i_2 == n)
                        return Pair(it.first + i_2,it.second.also { it.add(i) })
                    else
                        return null
                }

            }
            return null
        }
    }
    private fun dotest(n: Long, sexpr: String) {
        println(n)
        val success: Boolean
        val sact: String = Decomp.decompose(n)
        val st: Boolean
        val t: Boolean
        System.out.printf("Expected %s and got %s\n", sexpr, sact, "\n")
        if ((sact == "null" && sexpr == "null") || (sact != "null" && sact == sexpr)) {
            System.out.printf("Same as Expected\n")
            success = true
        } else {
            if (sact == "null") {
                success = false
            } else {
//                val intarr1: LongArray = Helper.string2LongArray(sact)
//                st = Helper.isSorted(intarr1)
//                t = Helper.total(intarr1, n * n)
//                success = if (!st || !t) {
//                    println("** Error. Not increasinly sorted or bad sum of squares **")
//                    false
//                } else {
//                    println("Correct; Increasing and total correct")
//                    true
//                }
            }
        }
//        assertEquals(true, success)
    }

    @Test
    fun basicTests() {
        assertEquals("null",Decomp.decompose(2))
        assertEquals( "1 2 4 10",Decomp.decompose(11))
        assertEquals( "1 2 3 7 9",Decomp.decompose(12))
        assertEquals( "null",Decomp.decompose(4))

    }
}