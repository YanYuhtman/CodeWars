import kotlin.test.Test
import kotlin.test.assertEquals
import TransformingMaze.Companion.mazeToString
import org.junit.jupiter.api.Assertions.assertTrue

internal class  TransformatingMazeTests {

    val maze = arrayOf(
        intArrayOf(4, 2, 5, 4),
        intArrayOf(4, 15, 11, 1),
        intArrayOf(-1, 9, 6, 8),
        intArrayOf(12, 7, 7, -2)
    )
    enum class Direction(val bitwise: Int) {
        N(0x8),E(0x1), W(0x4), S(0x2);
        val inverse:Direction
            get() =  when(this){ N -> S; S -> N; W -> E; E -> W}
    }
    fun rotateMaze(maze:Array<IntArray>) :Array<IntArray>{
        return maze.map {
            it.map{ if(it > 0)  (it shl 1) or (it shr 3) and 0xf  else it }.toIntArray()
        }.toTypedArray()
    }

    fun getStartPoint(maze: Array<IntArray>):Pair<Int,Int>{
        maze.forEachIndexed{ x, ints -> ints.forEachIndexed { y, i ->  if(i == -1) return Pair(x,y)} }
        throw IllegalArgumentException("Starting point isn't found")
    }

    fun makeStep(maze:Array<IntArray>, currentX:Int, currentY: Int, direction: Direction):Pair<Int,Int>? {
        val (x, y) = when (direction) {
            Direction.N -> (currentX - 1 to currentY)
            Direction.S -> (currentX + 1 to currentY)
            Direction.W -> (currentX to currentY - 1)
            Direction.E -> (currentX to currentY + 1)
        }
        if ((x < 0 || x > maze.lastIndex || y < 0 || y > maze[maze.lastIndex].lastIndex)
            || (maze[currentX][currentY] > 0 && (maze[currentX][currentY] and direction.bitwise != 0))
            || (maze[x][y] < 0 && maze[x][y] != -2)
            || (maze[x][y] > 0 && (maze[x][y] and direction.inverse.bitwise != 0))
        ) return null
        maze[currentX][currentY] = -1
        return (x to y)
    }

    fun findPath(maze: Array<IntArray>, x:Int, y:Int, rotations:Int, directions:List<String>, results:MutableList<List<String>>){
        results.maxOfOrNull { it.size }?.let {
            if(directions.size >= it)
                return
        }

        if(maze[x][y] == -2) {
           results.add(directions)
           return
        }


        Direction.values().forEach {direction->
            makeStep(maze,x,y,direction)?.also {
                val _directions = directions.mapIndexed { index, s -> if(index == directions.lastIndex) s + direction else s }
                findPath(maze.map { it.map { it }.toIntArray() }.toTypedArray(),it.first,it.second,0,_directions,results)
            }
        }
        if(rotations < 4)
            findPath(rotateMaze(maze),x,y,rotations + 1, directions + "",results)
        else
            return
    }

    fun mazeSolver(maze: Array<IntArray>): Array<String>? {
        val startPoint = getStartPoint(maze)
        var results = mutableListOf<List<String>>()
        findPath(maze,startPoint.first,startPoint.second, 0, mutableListOf(""),results)
        return results.sortedBy { it.size }.first().toTypedArray()

    }


    @Test fun `Example Tests`() {
        val testCases = arrayOf(
            Pair(
                arrayOf(
                    intArrayOf(4,2,5,4),
                    intArrayOf(4,15,11,1),
                    intArrayOf(-1,9,6,8),
                    intArrayOf(12,7,7,-2)),
                arrayOf("NNE", "EE", "S", "SS")),
//            Pair(
//                arrayOf(
//                    intArrayOf(6,3,10,4,11),
//                    intArrayOf(8,10,4,8,5),
//                    intArrayOf(-1,14,11,3,-2),
//                    intArrayOf(15,3,4,14,15),
//                    intArrayOf(14,7,15,5,5)),
//                arrayOf("", "", "E", "", "E", "NESE")),
//            Pair(
//                arrayOf(
//                    intArrayOf(9,1,9,0,13,0),
//                    intArrayOf(14,1,11,2,11,4),
//                    intArrayOf(-1,2,11,0,0,15),
//                    intArrayOf(4,3,9,6,3,-2)),
//                arrayOf("E", "SE", "", "E", "E", "E")),
//            Pair(
//                arrayOf(
//                    intArrayOf(-1,6,12,15,11),
//                    intArrayOf(8,7,15,7,10),
//                    intArrayOf(13,7,13,15,-2),
//                    intArrayOf(11,10,8,1,3),
//                    intArrayOf(12,6,9,14,7)),
//                null),
//            Pair(
//                arrayOf(
//                    intArrayOf(6,3,0,9,14,13,14),
//                    intArrayOf(-1,14,9,11,15,14,15),
//                    intArrayOf(2,15,0,12,6,15,-2),
//                    intArrayOf(4,10,7,6,15,5,3),
//                    intArrayOf(7,3,13,13,14,7,0)),
//                null)
        )

        testCases.forEach { (maze,sol) -> assertTrue(mazeSolver(maze).contentDeepEquals(sol)) }
    }

    @Test
    fun rotateTest() {
        maze.contentDeepEquals(maze)
       assertTrue(rotateMaze(maze).contentDeepEquals(arrayOf(
            intArrayOf(8, 4, 10, 8),
            intArrayOf(8, 15, 7, 2),
            intArrayOf(-1, 3, 12, 1),
            intArrayOf(9, 14, 14, -2)
        )),"The input matrix is unequal to the target (1 rot) ")

        assertTrue(rotateMaze(rotateMaze(maze)).contentDeepEquals(arrayOf(
            intArrayOf(1, 8, 5, 1),
            intArrayOf(1, 15, 14, 4),
            intArrayOf(-1, 6, 9, 2),
            intArrayOf(3, 13, 13, -2)
        )),"The input matrix is unequal to the target (2 rot)")

        assertTrue(rotateMaze(rotateMaze(rotateMaze(maze))).contentDeepEquals(arrayOf(
            intArrayOf(2, 1, 10, 2),
            intArrayOf(2, 15, 13, 8),
            intArrayOf(-1, 12, 3, 4),
            intArrayOf(6, 11, 11, -2)
        )),"The input matrix is unequal to the target (3 rot)")

        assertTrue(rotateMaze(rotateMaze(rotateMaze(rotateMaze(maze)))).contentDeepEquals(maze),"The input matrix is unequal to the target (4 rot)")
    }

    @Test
    fun findSimpleRoute() {
        val input = arrayOf(
            arrayOf(4, 'X', 5, 4),
            arrayOf(4, 15, 11, 1),
            arrayOf('B', 9, 6, 8),
            arrayOf(12, 'X', 7, 'X')
        )
        val maze = TransformingMaze(input as Array<Array<Any>>)
        val routs = maze.findRoute(false).forEach { r -> println(r) }

    }
    @Test
    fun findComplexRoute() {
        val input = arrayOf(
            arrayOf(4, 2, 5, 4),
            arrayOf(4, 15, 11, 1),
            arrayOf('B', 9, 6, 8),
            arrayOf(12, 7, 7, 'X')
        )
        val maze = TransformingMaze(input as Array<Array<Any>>)
        maze.findRoute(true)
            .sortedBy { it.rotationCount }
            .forEach { r -> println(r) }

    }

    @Test
    fun validatePath(){
        val input = arrayOf(
            arrayOf(4, 2, 5, 4),
            arrayOf(4, 15, 11, 1),
            arrayOf('B', 9, 6, 8),
            arrayOf(12, 7, 7, 'X')
        )
        val maze = TransformingMaze(input as Array<Array<Any>>)
        val mazeArray =  maze.getMaze(2)
//        val isValid = maze.validateStep(mazeArray,2, 2, Direction.S)
        val a = 0
    }

    @Test
    fun testConversion(){
//        val input = arrayOf(intArrayOf(25,56))
//        val input = arrayOf(
//            intArrayOf(4,2,5,4),
//            intArrayOf(4,15,11,1),
//            intArrayOf(-1,9,6,8),
//            intArrayOf(12,7,7,-2))

        val input = arrayOf(
            intArrayOf(-1,6,12,15,11),
            intArrayOf(8,7,15,7,10),
            intArrayOf(13,7,13,15,-2),
            intArrayOf(11,10,8,1,3),
            intArrayOf(12,6,9,14,7))
        val maze = TransformingMaze(input)
        val m = maze.findRoute(true, true)
        val min = m.minByOrNull { it.rotationCount }?.rotationCount ?: 0
        try {
            var result: Array<String>? = m
                .mapNotNull { v -> if (v.rotationCount == min) v else null }
                .last().also { it?.simpleFormatted = true }
                .toString().split(",")/*.toTypedArray()*/
                .also { println(it) }
                .toTypedArray()
            }catch (e:NoSuchElementException){

            }
    }


    @Test
    fun `Basic Tests`() {
        assertEquals(3, persistence(39))
        assertEquals(0, persistence(4))
        assertEquals(2, persistence(25))
        assertEquals(4, persistence(999))
    }
    fun persistence(num: Int) : Int {
       var n = num
       var count = 0
       while (n / 10 != 0){
           n = persistenceStep(n)
           count++
       }
       return count
    }
    fun persistenceStep(num: Int) : Int {
        var mult = 1
        var n = num
        do{
            mult *= n % 10
            n /= 10
        }while (n != 0)
        return mult
    }


}

