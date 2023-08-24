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
    lateinit var mazes: Array<Array<IntArray>>

    enum class Direction(val bitwise: Int) {
        N(0x8), W(0x4), E(0x1), S(0x2);
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
            || (maze[currentX][currentY] and direction.bitwise != 0)
            || (maze[x][y] < 0 && maze[x][y] != -2)
            || (maze[x][y] and direction.inverse.bitwise != 0)
        ) return null
        if(maze[x][y] != -2)
            maze[x][y] = -1
        return (x to y)
    }
    fun findPath(maze: Array<IntArray>, x:Int, y:Int, directions:MutableList<Direction>):List<Direction>?{
        if(maze[x][y] == -2)
            return directions

        Direction.values().forEach {direction->
            makeStep(maze,x,y,direction)?.also {
                directions.add(direction)

                return findPath(maze.map { it.map { it }.toIntArray() }.toTypedArray(),it.first,it.second,directions.map { it }.toMutableList())
            }?: return findPath(rotateMaze(maze),x,y,directions.map { it }.toMutableList())
        }

        return null
    }

    fun mazeSolver(maze: Array<IntArray>): Array<String>? {
//        mazes = arrayOf(maze,rotateMaze(maze),rotateMaze(rotateMaze(maze)),rotateMaze(rotateMaze(rotateMaze(maze))))
        val startPoint = getStartPoint(maze)
        val a = findPath(maze,startPoint.first,startPoint.second, mutableListOf<Direction>())?.joinToString("")
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
    fun mazesCreation() {
        val maze = TransformingMaze(maze as Array<Array<Any>>)
        print(mazeToString(maze.getMaze(0)))
        println()
        print(mazeToString(maze.getMaze(1)))
        println()
        print(mazeToString(maze.getMaze(2)))
        println()
        print(mazeToString(maze.getMaze(3)))

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

