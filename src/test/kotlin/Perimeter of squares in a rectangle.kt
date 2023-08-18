import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals


class `Perimeter of squares in a rectangle` {
//    https://www.codewars.com/kata/559a28007caad2ac4e000083

    fun perimeter(n: Int) = (1..n).toList()
        .fold(mutableListOf(0, 1)) { acc, i -> acc.add(acc[acc.lastIndex - 1] + acc[acc.lastIndex]);acc }
        .reduce { acc, i -> acc + i } * 4

    @Test
    fun test(){
        assertEquals(80, perimeter(5))
        assertEquals(216, perimeter(7))
    }
}