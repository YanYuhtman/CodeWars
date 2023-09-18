import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Plants and Zombies` {
    //https://www.codewars.com/kata/5a5db0f580eba84589000979/kotlin

    object PNZ {
        fun plantsAndZombies(lawn:Array<String>,zombies:Array<IntArray>): Int? {
            // your code goes here. you can do it!
            return null
        }
    }

    @Test
    fun runExamples() = exampleSols.zip(exampleTests).forEach { (sol,tst) -> assertEquals(sol,PNZ.plantsAndZombies(tst.first,tst.second)) }

    private val exampleSols = listOf(10,12,20,19,null)

    private val exampleTests = listOf(
        Pair(
            arrayOf(
                "2       ",
                "  S     ",
                "21  S   ",
                "13      ",
                "2 3     "),
            arrayOf(
                intArrayOf(0,4,28),
                intArrayOf(1,1,6),
                intArrayOf(2,0,10),
                intArrayOf(2,4,15),
                intArrayOf(3,2,16),
                intArrayOf(3,3,13))),
        Pair(
            arrayOf(
                "11      ",
                " 2S     ",
                "11S     ",
                "3       ",
                "13      "),
            arrayOf(
                intArrayOf(0,3,16),
                intArrayOf(2,2,15),
                intArrayOf(2,1,16),
                intArrayOf(4,4,30),
                intArrayOf(4,2,12),
                intArrayOf(5,0,14),
                intArrayOf(7,3,16),
                intArrayOf(7,0,13))),
        Pair(
            arrayOf(
                "12        ",
                "3S        ",
                "2S        ",
                "1S        ",
                "2         ",
                "3         "),
            arrayOf(
                intArrayOf(0,0,18),
                intArrayOf(2,3,12),
                intArrayOf(2,5,25),
                intArrayOf(4,2,21),
                intArrayOf(6,1,35),
                intArrayOf(6,4,9),
                intArrayOf(8,0,22),
                intArrayOf(8,1,8),
                intArrayOf(8,2,17),
                intArrayOf(10,3,18),
                intArrayOf(11,0,15),
                intArrayOf(12,4,21))),
        Pair(
            arrayOf(
                "12      ",
                "2S      ",
                "1S      ",
                "2S      ",
                "3       "),
            arrayOf(
                intArrayOf(0,0,15),
                intArrayOf(1,1,18),
                intArrayOf(2,2,14),
                intArrayOf(3,3,15),
                intArrayOf(4,4,13),
                intArrayOf(5,0,12),
                intArrayOf(6,1,19),
                intArrayOf(7,2,11),
                intArrayOf(8,3,17),
                intArrayOf(9,4,18),
                intArrayOf(10,0,15),
                intArrayOf(11,4,14))),
        Pair(
            arrayOf(
                "1         ",
                "SS        ",
                "SSS       ",
                "SSS       ",
                "SS        ",
                "1         "),
            arrayOf(
                intArrayOf(0,2,16),
                intArrayOf(1,3,19),
                intArrayOf(2,0,18),
                intArrayOf(4,2,21),
                intArrayOf(6,3,20),
                intArrayOf(7,5,17),
                intArrayOf(8,1,21),
                intArrayOf(8,2,11),
                intArrayOf(9,0,10),
                intArrayOf(11,4,23),
                intArrayOf(12,1,15),
                intArrayOf(13,3,22)))
    )
}