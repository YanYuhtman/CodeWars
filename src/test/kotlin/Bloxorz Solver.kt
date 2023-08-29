import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.PriorityQueue


typealias Step = Pair<Int,Int>
typealias Position = Triple<Float,Pair<Int,Int>,Pair<Int,Int>>

val Position.horizontal get() = this.second.first == this.third.first
operator fun Step.plus(other:Step):Step = ((this.first + other.first) to (this.second + other.second))
operator fun Step.minus(other:Step):Step = ((this.first - other.first) to (this.second - other.second))
class `Bloxorz Solver` {


    //https://www.codewars.com/kata/5a2a597a8882f392020005e5
    object Blox {

        lateinit var map: Array<IntArray>
        lateinit var startPos: Position
        lateinit var endPos: Position

        val visitedSet = HashSet<Position>()
        val paths = PriorityQueue<Pair<String, Position>> { o1, o2 ->
            val length = o1.first.length - o2.first.length
            if (length == 0) {
                val distanceDelta = o1.second.first - o2.second.first
                if (distanceDelta < 0F) return@PriorityQueue -1 else if (distanceDelta > 0) return@PriorityQueue 1 else return@PriorityQueue 0
            }
            length
        }

        fun remap(puzzle: Array<String>) {
            visitedSet.clear()
            paths.clear()
            map = puzzle.mapIndexed { x, row ->
                row.mapIndexed { y, c ->
                    when {
                        c == 'B' -> {
                            startPos = Triple(Float.MAX_VALUE, (x to y), (x to y)); -1
                        }

                        c == 'X' -> {
                            endPos = Triple(0F, (x to y), (x to y)); -2
                        }

                        else -> c.digitToInt()
                    }
                }.toIntArray()
            }.toTypedArray()
        }

        enum class Direction(val shortStepDelta: Pair<Int, Int>, val longStepDelta: Pair<Int, Int>) {
            UP(Pair(-1, 0), Pair(-2, 0)),
            DOWN(Pair(1, 0), Pair(2, 0)),
            LEFT(Pair(0, -1), Pair(0, -2)),
            RIGHT(Pair(0, 1), Pair(0, 2)),
            ;

            override fun toString(): String = when (this) {
                UP -> "U"; DOWN -> "D"; LEFT -> "L"; RIGHT -> "R"
            }
        }

        fun distance(left: Pair<Int, Int>, right: Pair<Int, Int>): Float {
            val mid: Pair<Number, Number> = (if (right == left) left
            else if (right.first == left.first)
                (right.first to (right.second + left.second) / 2.0)
            else
                ((right.first + left.first) / 2.0 to right.second))
            return Math.sqrt(
                Math.pow(
                    mid.first.toDouble() - endPos.second.first,
                    2.0
                ) + Math.pow(mid.second.toDouble() - endPos.second.second, 2.0)
            ).toFloat()
        }

        fun checkStep(map: Array<IntArray>, currentPos: Position, direction: Direction): Position? {
            val (s: Step, e: Step) = if (currentPos.second == currentPos.third)
                when (direction) {
                    Direction.UP, Direction.LEFT -> (currentPos.second + direction.longStepDelta to currentPos.third + direction.shortStepDelta)
                    Direction.DOWN, Direction.RIGHT -> (currentPos.second + direction.shortStepDelta to currentPos.third + direction.longStepDelta)
                }
            else {
                if (currentPos.horizontal)
                    when (direction) {
                        Direction.UP, Direction.DOWN -> (currentPos.second + direction.shortStepDelta to currentPos.third + direction.shortStepDelta)
                        Direction.LEFT -> (currentPos.second + direction.shortStepDelta to currentPos.third + direction.longStepDelta)
                        Direction.RIGHT -> (currentPos.second + direction.longStepDelta to currentPos.third + direction.shortStepDelta)
                    }
                else
                    when (direction) {
                        Direction.UP -> (currentPos.second + direction.shortStepDelta to currentPos.third + direction.longStepDelta)
                        Direction.DOWN -> (currentPos.second + direction.longStepDelta to currentPos.third + direction.shortStepDelta)
                        Direction.LEFT, Direction.RIGHT -> (currentPos.second + direction.shortStepDelta to currentPos.third + direction.shortStepDelta)

                    }
            }
            if (s.first < 0 || s.first > map.lastIndex || s.second < 0 || s.second > map.first().lastIndex
                || e.first < 0 || e.first > map.lastIndex || e.second < 0 || e.second > map.first().lastIndex
                || map[s.first][s.second] == 0 || map[e.first][e.second] == 0
            )
                return null
            return Position(distance(s, e), s, e)
        }

        fun bloxSolver(puzzle: Array<String>): String {
            remap(puzzle)

            paths.add("" to startPos)
            while (paths.isNotEmpty()) {
                val path = paths.poll()
                visitedSet.add(path.second)

                if (path.second.first == 0F)
                    return path.first

                Direction.values().forEach { direction ->
                    checkStep(map, path.second, direction)?.let {
                        if (it !in visitedSet)
                            paths.add(path.first + direction to it)
                    }
                }
            }

            return ""
        }
    }


    @Test fun testRemap(){
       Blox.remap( arrayOf(
           "1110000000",
           "1B11110000",
           "1111111110",
           "0111111111",
           "0000011X11",
           "0000001110"))
       Assertions.assertTrue(Blox.map.contentDeepEquals(
           arrayOf(
               intArrayOf(1,1,1,0,0,0,0,0,0,0),
               intArrayOf(1,-1,1,1,1,1,0,0,0,0),
               intArrayOf(1,1,1,1,1,1,1,1,1,0),
               intArrayOf(0,1,1,1,1,1,1,1,1,1),
               intArrayOf(0,0,0,0,0,1,1,-2,1,1),
               intArrayOf(0,0,0,0,0,0,1,1,1,0)
           )));
        Assertions.assertEquals(Position(0F, Pair(1,1),Pair(1,1)),Blox.startPos)
        Assertions.assertEquals(Position(1.0F,  Pair(7,4), Pair(7,4)),Blox.endPos)

    }

    @Test fun testStep(){
        Blox.remap(arrayOf("00X00",
                      "00100",
                      "11B11",
                      "00100",
                      "00100",
            ))
        Assertions.assertEquals(Position(0.5f,(0 to 2),(1 to 2)),Blox.checkStep(Blox.map,Blox.startPos,Blox.Direction.UP))
        Assertions.assertEquals(Position(3.5f,(3 to 2),(4 to 2)),Blox.checkStep(Blox.map,Blox.startPos,Blox.Direction.DOWN))
        Assertions.assertEquals(Position(2.5f,(2 to 0),(2 to 1)),Blox.checkStep(Blox.map,Blox.startPos,Blox.Direction.LEFT))
        Assertions.assertEquals(Position(2.5f,(2 to 3),(2 to 4)),Blox.checkStep(Blox.map,Blox.startPos,Blox.Direction.RIGHT))

        Blox.remap(arrayOf("011X",
                      "1BB1",
                      "0110",
        ))
        Assertions.assertEquals(Position(1.5f,(0 to 1),(0 to 2)),Blox.checkStep(Blox.map,Position(0F,(1 to 1),(1 to 2)),Blox.Direction.UP))
        Assertions.assertEquals(Position(2.5f,(2 to 1),(2 to 2)),Blox.checkStep(Blox.map,Position(0F,(1 to 1),(1 to 2)),Blox.Direction.DOWN))
        Assertions.assertEquals(Position(3.1622777f,(1 to 0),(1 to 0)),Blox.checkStep(Blox.map,Position(0F,(1 to 1),(1 to 2)),Blox.Direction.LEFT))
        Assertions.assertEquals(Position(1f,(1 to 3),(1 to 3)),Blox.checkStep(Blox.map,Position(0F,(1 to 1),(1 to 2)),Blox.Direction.RIGHT))

        Blox.remap(arrayOf("00X00",
                      "01B10",
                      "01B10",
                      "00100",
        ))
        Assertions.assertEquals(Position(0f,(0 to 2),(0 to 2)),Blox.checkStep(Blox.map,Position(0F,(1 to 2),(2 to 2)),Blox.Direction.UP))
        Assertions.assertEquals(Position(3f,(3 to 2),(3 to 2)),Blox.checkStep(Blox.map,Position(0F,(1 to 2),(2 to 2)),Blox.Direction.DOWN))
        Assertions.assertEquals(Position(1.8027756f,(1 to 1),(2 to 1)),Blox.checkStep(Blox.map,Position(0F,(1 to 2),(2 to 2)),Blox.Direction.LEFT))
        Assertions.assertEquals(Position(1.8027756f,(1 to 3),(2 to 3)),Blox.checkStep(Blox.map,Position(0F,(1 to 2),(2 to 2)),Blox.Direction.RIGHT))
    }
    @Test
    fun runExamples() = fixedSols.zip(fixedTests).forEach { (sol,tst) ->
        val s = Blox.bloxSolver(tst)
        assertTrue(sol.contains(s)) }

    private val fixedSols = listOf(
        arrayOf("RRDRRRD","RDDRRDR","RDRRDDR"),
        arrayOf("ULDRURRRRUURRRDDDRU","RURRRULDRUURRRDDDRU"),
        arrayOf("ULURRURRRRRRDRDDDDDRULLLLLLD"),
        arrayOf("DRURURDDRRDDDLD"),
        arrayOf("RRRDRDDRDDRULLLUULUUURRRDDLURRDRDDR","RRRDDRDDRDRULLLUULUUURRDRRULDDRRDDR","RRRDRDDRDDRULLLUULUUURRDRRULDDRRDDR")
    )

    private val fixedTests = listOf(
        arrayOf(
            "1110000000",
            "1B11110000",
            "1111111110",
            "0111111111",
            "0000011X11",
            "0000001110"),
        arrayOf(
            "000000111111100",
            "111100111001100",
            "111111111001111",
            "1B11000000011X1",
            "111100000001111",
            "000000000000111"),
        arrayOf(
            "00011111110000",
            "00011111110000",
            "11110000011100",
            "11100000001100",
            "11100000001100",
            "1B100111111111",
            "11100111111111",
            "000001X1001111",
            "00000111001111"),
        arrayOf(
            "11111100000",
            "1B111100000",
            "11110111100",
            "11100111110",
            "10000001111",
            "11110000111",
            "11110000111",
            "00110111111",
            "01111111111",
            "0110011X100",
            "01100011100"),
        arrayOf(
            "000001111110000",
            "000001001110000",
            "000001001111100",
            "B11111000001111",
            "0000111000011X1",
            "000011100000111",
            "000000100110000",
            "000000111110000",
            "000000111110000",
            "000000011100000")
    )

}

