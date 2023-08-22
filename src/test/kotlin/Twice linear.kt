import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.PriorityQueue
import java.util.Queue
import java.util.SortedSet

class `Twice linear` {

    //https://www.codewars.com/kata/5672682212c8ecf83e000050/train/kotlin


    fun dblLinear(n :Int):Int{
        val queue:Queue<Int> = PriorityQueue<Int>().also { it.add(1) }
        val resultSet:SortedSet<Int> = sortedSetOf<Int>()

        do{
            val tmp = queue.poll()
            resultSet.add(tmp)
            queue.add(tmp * 2 + 1)
            queue.add(tmp * 3 + 1)

        }while (resultSet.size <= n)
        return resultSet.toIntArray()[n]
    }




    @Test
    fun test() {
        println("Fixed Tests dblLinear")
        testing(dblLinear(0), 1)
        testing(dblLinear(4), 9)
        testing(dblLinear(10), 22)
        testing(dblLinear(20), 57)
        testing(dblLinear(30), 91)
        testing(dblLinear(500), 3355)
        testing(dblLinear(60000), 1511311)


    }
    companion object {
        private fun testing(actual:Int, expected:Int) {
            assertEquals(expected.toLong(), actual.toLong())
        }
    }
}