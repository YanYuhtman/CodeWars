import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Combination Lock` {
    //https://www.codewars.com/kata/630e55d6c8e178000e1badfc

    fun turn(a:Int, b:Int, clockwise:Boolean):Int{
        return if(clockwise)
            if(b > a) 40 - b + a else a - b
        else
            if(b >= a) b - a else 40 - a + b
    }
    fun degreesOfLock(initial: Int, first: Int, second: Int, third: Int) =
         (360/40) * (2 * 40 + turn(initial, first,true) + 40 + turn(first,second,false) + turn(second,third,true))

    @Test
    fun sampleTest() {
        assertEquals(1350, degreesOfLock(0, 30, 0, 30))
        assertEquals(1350, degreesOfLock(5, 35, 5, 35))
        assertEquals(1620, degreesOfLock(0, 20, 0, 20))
        assertEquals(1620, degreesOfLock(7, 27, 7, 27))
        assertEquals(1890, degreesOfLock(0, 10, 0, 10))
        assertEquals(1890, degreesOfLock(9, 19, 9, 19))
        assertEquals(1377, degreesOfLock(5, 30, 39, 30))
        assertEquals(1530, degreesOfLock(4, 24, 36, 18))
    }
}