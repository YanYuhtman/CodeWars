import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class TransformatingMazeTests {

    //https://www.codewars.com/kata/5b86a6d7a4dcc13cd900000b/kotlin
    val maze = arrayOf(
        intArrayOf(4, 2, 5, 4),
        intArrayOf(4, 15, 11, 1),
        intArrayOf(-1, 9, 6, 8),
        intArrayOf(12, 7, 7, -2)
    )

    enum class Direction(val bitwise: Int) {
        E(0x1), N(0x8), S(0x2),  W(0x4);

        val inverse: Direction
            get() = when (this) {
                N -> S; S -> N; W -> E; E -> W
            }
    }

    fun rotateMaze(maze: Array<IntArray>): Array<IntArray> {
        return maze.map {
            it.map { if (it > 0) (it shl 1) or (it shr 3) and 0xf else it }.toIntArray()
        }.toTypedArray()
    }

    fun getStartPoint(maze: Array<IntArray>): Pair<Int, Int> {
        maze.forEachIndexed { x, ints -> ints.forEachIndexed { y, i -> if (i == -1) return Pair(x, y) } }
        throw IllegalArgumentException("Starting point isn't found")
    }

    fun makeStep(maze: Array<IntArray>, currentX: Int, currentY: Int, direction: Direction, all:Boolean=false): Pair<Int, Int>? {
        val (x, y) = when (direction) {
            Direction.N -> (currentX - 1 to currentY)
            Direction.S -> (currentX + 1 to currentY)
            Direction.W -> (currentX to currentY - 1)
            Direction.E -> (currentX to currentY + 1)
        }
        if ((x < 0 || x > maze.lastIndex || y < 0 || y > maze[maze.lastIndex].lastIndex)
            || (maze[currentX][currentY] > 0 && (maze[currentX][currentY] and direction.bitwise != 0))
            || (!all && maze[x][y] < 0 && maze[x][y] != -2)
            || (maze[x][y] > 0 && (maze[x][y] and direction.inverse.bitwise != 0))
        ) return null
        return (x to y)
    }

    fun findPath(maze: Array<IntArray>, x:Int, y:Int, rotations:Int, directions:List<String>, results:MutableList<List<String>>, dontRotate: Boolean = false, allPaths:Boolean = false){
        if(!allPaths) {
            results.minOfOrNull { it.size }?.let {
                if (directions.size >= it)
                    return
            }
        }

        if(maze[x][y] == -2) {
           results.add(directions)
           return
        }
        Direction.values().forEach { direction ->
            makeStep(maze, x, y, direction)?.also {

                val _directions =
                    directions.mapIndexed { index, s -> if (index == directions.lastIndex) s + direction else s }
                val _maze = maze.map { it.map { it }.toIntArray() }.toTypedArray()
                _maze[x][y] = -1
                findPath(_maze, it.first, it.second, 0, _directions, results, dontRotate,allPaths)
            }
        }


        if (!dontRotate && rotations < 3)
            findPath(rotateMaze(maze), x, y, rotations + 1, directions + "", results, dontRotate,allPaths)
        else
            return
    }

    fun mergeMazes(maze: Array<IntArray>, startPoint:Pair<Int,Int>):Array<IntArray>{
        var _maze = maze.map { it.map { it }.toIntArray() }.toTypedArray()
        var rotatedMaze = rotateMaze(_maze)
        for (r in 1..3) {
            for (x in 0..maze.lastIndex) {
                for (y in 0..maze[x].lastIndex) {
                    Direction.values().forEach {
                        if (makeStep(_maze, x, y, it) == null && makeStep(rotatedMaze, x, y, it,true) != null
                            && (_maze[x][y] > 0 && _maze[x][y] and it.bitwise != 0))
                            _maze[x][y] -= it.bitwise
                    }
                }
            }
            rotatedMaze = rotateMaze(rotatedMaze)
        }
        return _maze
    }
    fun isSolvable(maze: Array<IntArray>, startPoint:Pair<Int,Int>):Boolean{
        var _maze =mergeMazes(maze, startPoint)
        var results = mutableListOf<List<String>>()
        findPath(_maze, startPoint.first, startPoint.second, 0, mutableListOf(""), results,true)

        return results.isNotEmpty()
    }
    fun mazeSolver0(maze: Array<IntArray>): Array<String>? {
//        println(maze.map { it.joinToString(",") }.joinToString("\n"))
        val startPoint = getStartPoint(maze)
        if(!isSolvable(maze,startPoint))
            return null
        println("Checked solvable")
        var results = mutableListOf<List<String>>()
        findPath(maze, startPoint.first, startPoint.second, 0, mutableListOf(""), results)
        return if (results.isNotEmpty()) results.sortedBy { it.size }.first().toTypedArray() else null

    }

    fun mazeSolver(maze: Array<IntArray>): Array<String>? {
        val startPoint = getStartPoint(maze)
        val _maze = mergeMazes(maze,startPoint)
        var results = mutableListOf<List<String>>()
        findPath(_maze,startPoint.first,startPoint.second,0, mutableListOf(""),results,true,true)
        return null
    }


    @Test
    fun `Efficiency test`(){

       val tests = 1


       for(a in 1 .. tests) {
           val startTime = System.currentTimeMillis()

           val list = mutableListOf<MutableList<Int>>()

           val maxRandomSize = 0
           val height = 20 + (maxRandomSize * Math.random()).toInt()
           val width = 20 + (maxRandomSize * Math.random()).toInt()
           for (i in 1..height) {
               val tmp = mutableListOf<Int>()
               for (k in 1..width)
                   tmp.add((15 * Math.random()).toInt())
               list.add(tmp)
           }
           list[(Math.random() * list.lastIndex).toInt()][0] = -1
           list[(Math.random() * list.lastIndex).toInt()][list.first().lastIndex] = -2

           println("++++++++++++++++++++++++\nTesting maze:\n ${list.map { it.joinToString (",") }.joinToString("\n")}\n" )

           println("Result: ${mazeSolver(list.map { it.map { it }.toIntArray() }.toTypedArray())?.joinToString(",")}")

           println("Solving took ${System.currentTimeMillis() - startTime}")
       }
    }

    fun verifyPath(maze:Array<IntArray>, startPoint: Pair<Int, Int>,path:String): Array<String>?{
        var (x,y) = startPoint
        val results = mutableListOf<String>("")
        var rotations = 0
        var _maze = maze.map { it.map { it }.toIntArray() }.toTypedArray()
        path.forEach {dChar->

            do {
                val step = makeStep(maze, x, y, Direction.valueOf("$dChar"))?.apply {
                    results[results.lastIndex] = results[results.lastIndex] + dChar
                    rotations = 0
                }
                if(step == null){
                    _maze = rotateMaze(_maze)
                    rotations++
                    results.add("")
                }
            }while (step == null && rotations < 3)
        }
        return if(rotations > 3) null else results.toTypedArray()
    }

    @Test
    fun stackMaze(){

        val startTime = System.currentTimeMillis()

        val sMaze =   arrayOf(
                intArrayOf(8,5,14,7,-2),
                intArrayOf( 2,0,11,11,3),
                intArrayOf( 10,0,6,8,3),
                intArrayOf( -1,8,4,5,2),
                intArrayOf( 7,7,13,1,13))

        println("Result: ${mazeSolver(sMaze)?.joinToString(",")}")

        println("Solving took ${System.currentTimeMillis() - startTime}")

    }
    @Test
    fun `Example Tests`() {
        val startTime = System.currentTimeMillis()

        val testCases = arrayOf(
            Pair(
                arrayOf(
                    intArrayOf(4,2,5,4),
                    intArrayOf(4,15,11,1),
                    intArrayOf(-1,9,6,8),
                    intArrayOf(12,7,7,-2)),
                arrayOf("NNE","EES","S","S")),
            Pair(
                arrayOf(
                    intArrayOf(6,3,10,4,11),
                    intArrayOf(8,10,4,8,5),
                    intArrayOf(-1,14,11,3,-2),
                    intArrayOf(15,3,4,14,15),
                    intArrayOf(14,7,15,5,5)),
                arrayOf("NE","","E","","ES","E")),
            Pair(
                arrayOf(
                    intArrayOf(9,1,9,0,13,0),
                    intArrayOf(14,1,11,2,11,4),
                    intArrayOf(-1,2,11,0,0,15),
                    intArrayOf(4,3,9,6,3,-2)),
                arrayOf("EE","","EE","","S","E")),
            Pair(
                arrayOf(
                    intArrayOf(-1,6,12,15,11),
                    intArrayOf(8,7,15,7,10),
                    intArrayOf(13,7,13,15,-2),
                    intArrayOf(11,10,8,1,3),
                    intArrayOf(12,6,9,14,7)),
                null),
            Pair(
                arrayOf(
                    intArrayOf(6,3,0,9,14,13,14),
                    intArrayOf(-1,14,9,11,15,14,15),
                    intArrayOf(2,15,0,12,6,15,-2),
                    intArrayOf(4,10,7,6,15,5,3),
                    intArrayOf(7,3,13,13,14,7,0)),
                null),
            Pair(
                arrayOf(
                    intArrayOf(12, 3, 13, 12, 6, 12),
                    intArrayOf(9, 5, 10, 9, 9, -2),
                    intArrayOf(-1, 8, 4, 7, 0, 8),
                    intArrayOf(0, 3, 3, 0, 15, 14)
                ), arrayOf("E","","E","E","","EEN")
            ),
            Pair(
                arrayOf(
                    intArrayOf(-1, 6, 15, 14, 11, 2, -2),
                    intArrayOf(11, 3, 9, 10, 10, 8, 7),
                    intArrayOf(12, 0, 6, 11, 14, 7, 3),
                    intArrayOf(1, 1, 1, 11, 13, 5, 13)
                ), arrayOf("","S","","SEEN","","","EEENE")
            ),
            Pair(
                arrayOf(
                    intArrayOf(-1, 0, 10, 11, 5, 11),
                    intArrayOf(11, 1, 6, 12, 1, 1),
                    intArrayOf(4, 12, 6, 15, 12, 13),
                    intArrayOf(9, 0, 0, 0, 9, 7),
                    intArrayOf(5, 5, 7, 6, 12, -2),
                    intArrayOf(10, 11, 8, 10, 7, 6)
                ), arrayOf("EEE","","","SES","","S","","SE")
            ),
            Pair(
                arrayOf(
                    intArrayOf( 3,15,4,11),
                    intArrayOf(5,6,1,-2),
                    intArrayOf( -1,9,10,14),
                    intArrayOf(  6,14,12,0),
                ), arrayOf("E","NEE")
            ),
            Pair(arrayOf(
                intArrayOf(15,13,8,1,5,11,-2),
                intArrayOf(11,9,5,10,13,7,4),
                intArrayOf(12,3,3,0,2,6,14),
                intArrayOf(-1,15,7,0,9,5,1),
            ),arrayOf("NEN","","","E","NE","E","S","E","","E","N")),

            Pair(arrayOf(
                intArrayOf(0,11,13,14),
                intArrayOf(3,4,15,4),
                intArrayOf(4,12,12,-2),
                intArrayOf(1,14,9,13),
                intArrayOf(-1,4,4,7),
            ),arrayOf("","EE","","E","N","","N")),

            Pair(arrayOf(
                intArrayOf(0,6,0,2),
                intArrayOf(2,2,3,-2),
                intArrayOf(13,5,14,8),
                intArrayOf(-1,7,7,1),
                intArrayOf(1,9,11,3),
                intArrayOf(10,12,5,14),
            ),arrayOf("N","","NE","","ENE","S")),

            Pair(arrayOf(
                intArrayOf(11,4,8,14,14),
                intArrayOf(11,4,8,14,14),
                intArrayOf(14,1,2,12,11),
                intArrayOf(0,1,1,1,14),
                intArrayOf(7,10,4,2,2),
                intArrayOf(1,0,5,14,15),
                intArrayOf(0,4,6,11,9),
                intArrayOf(-1,14,3,0,-2),
            ),arrayOf("NN","EE","","","S","SEE")),

            Pair(arrayOf(
                intArrayOf(0,9,0,6),
                intArrayOf(8,1,12,-2),
                intArrayOf(-1,4,0,2),
                intArrayOf(0,11,10,15),
                intArrayOf(12,8,10,2),
                intArrayOf(2,6,5,14),
            ),arrayOf("NESEEN")),

            Pair(arrayOf(
                intArrayOf(6,3,1,10,3),
                intArrayOf(11,13,0,1,2),
                intArrayOf(9,11,7,9,11),
                intArrayOf(2,4,2,11,-2),
                intArrayOf(-1,0,14,0,3),
                intArrayOf(14,12,11,0,11),
                intArrayOf(10,10,10,13,15),
            ),arrayOf("ENEE","","E")),

            Pair(arrayOf(
                intArrayOf(-1,8,3,0,7),
                intArrayOf(0,1,6,6,2),
                intArrayOf(6,15,13,1,-2),
                intArrayOf(11,4,0,9,10),
            ),arrayOf("ES","","ES","EE")),

            Pair(arrayOf(
                intArrayOf(14,9,13,0,1,2,-2),
                intArrayOf(6,14,12,14,10,13,0),
                intArrayOf(2,14,0,7,1,3,11),
                intArrayOf(-1,8,4,2,8,9,2),
                intArrayOf(6,9,7,12,13,3,6),
            ),arrayOf("E","","EN","E","","EN","","NE","E")),

            Pair(arrayOf(
                intArrayOf(14,13,11,9,-2),
                intArrayOf(-1,10,8,4,9),
                intArrayOf(5,7,1,1,12),
                intArrayOf(9,2,6,13,7),
            ),arrayOf("EES","ENEN")),

            Pair(arrayOf(
                intArrayOf(0,1,10,7,9,7,10),
                intArrayOf(-1,5,4,10,11,14,4),
                intArrayOf(7,6,5,0,14,2,5),
                intArrayOf(9,6,14,12,8,5,-2),
                intArrayOf(11,6,2,7,14,2,14),
            ),arrayOf("NESS","EE","E","","E","E","S")),

            Pair(arrayOf(
                intArrayOf(-1,13,4,8,10,15,1),
                intArrayOf(14,7,11,2,12,0,3),
                intArrayOf(10,12,6,12,2,10,-2),
                intArrayOf(6,12,12,0,11,8,1),
                intArrayOf(3,14,1,10,11,5,11),
                intArrayOf(13,2,14,2,3,6,9),
            ),arrayOf("","E","","E","ES","ES","EE")),

            Pair(arrayOf(
                intArrayOf(0,2,3,8),
                intArrayOf(3,0,1,12),
                intArrayOf(-1,5,7,5),
                intArrayOf(11,2,9,-2),
                intArrayOf(3,5,2,1),
            ),arrayOf("","S","ES","ENE")),

            Pair(arrayOf(
                intArrayOf(-1,1,8,13,5,9,8,1,13),
                intArrayOf(11,11,3,8,4,5,13,13,9),
                intArrayOf(13,7,6,0,8,2,1,0,2),
                intArrayOf(14,0,11,0,4,8,13,11,-2),
                intArrayOf(9,12,15,7,3,6,15,5,10),
            ),arrayOf("E","ESESSEENEES","E")),

            Pair(arrayOf(
                intArrayOf(9,3,5,11,5,8,0,4),
                intArrayOf(15,12,10,11,6,10,7,1),
                intArrayOf(12,5,1,4,11,14,2,13),
                intArrayOf(-1,12,15,1,0,6,10,-2),
                intArrayOf(8,4,5,4,15,15,6,15),
                intArrayOf(15,14,3,11,1,15,9,10),
                intArrayOf(8,2,3,6,14,13,3,15),
                intArrayOf(12,5,10,4,10,6,10,1),
            ),arrayOf("","S","E","EENEE","EE")),

            Pair(arrayOf(
                intArrayOf(1,10,1,8,11,14,7,8),
                intArrayOf(5,4,6,11,10,4,11,5),
                intArrayOf(-1,15,3,3,1,14,5,9),
                intArrayOf(6,0,6,2,14,0,6,-2),
                intArrayOf(2,12,8,3,7,11,0,14),
                intArrayOf(11,12,0,4,10,7,4,3),
                intArrayOf(15,8,3,7,5,12,0,13),
            ),arrayOf("SE","","ES","ES","S","EE","","E","NN","NE")),

            Pair(arrayOf(
                intArrayOf(8,7,5,15,1,2,0,7),
                intArrayOf(-1,0,5,11,14,6,1,13),
                intArrayOf(15,0,3,4,2,4,13,9),
                intArrayOf(6,12,15,14,7,3,7,3),
                intArrayOf(0,3,9,12,0,5,12,13),
                intArrayOf(5,14,5,14,12,10,6,14),
                intArrayOf(13,15,15,12,7,5,5,4),
                intArrayOf(15,7,1,10,5,13,10,9),
                intArrayOf(5,14,10,9,1,8,14,-2),
            ),arrayOf("ESE","E","","E","","ES","S","WS","S","EEES","","S")),

            Pair(arrayOf(
                intArrayOf(1,11,13,8,12,0,6),
                intArrayOf(5,5,1,12,4,13,6),
                intArrayOf(8,5,15,0,15,12,-2),
                intArrayOf(12,3,3,8,2,5,12),
                intArrayOf(0,15,0,12,7,6,6),
                intArrayOf(-1,6,8,14,4,0,2),
            ),arrayOf("NNENN","EESS","E","","","EE","N")),

            Pair(arrayOf(
                intArrayOf(7,9,2,3,14,15,14,15,13),
                intArrayOf(11,12,9,5,8,4,14,8,15),
                intArrayOf(15,5,6,7,6,5,5,3,10),
                intArrayOf(10,5,2,3,13,12,15,1,3),
                intArrayOf(7,10,8,8,12,15,3,9,14),
                intArrayOf(-1,10,1,3,4,12,11,11,13),
                intArrayOf(9,3,6,12,12,1,10,2,-2),
                intArrayOf(9,7,5,1,14,13,0,10,4),
                intArrayOf(13,14,15,14,2,14,5,2,8),
                intArrayOf(7,0,3,14,8,9,11,1,10),
            ),arrayOf("EENES","EES","EEE")),

            Pair(arrayOf(
                intArrayOf(15,2,3,5),
                intArrayOf(7,10,0,3),
                intArrayOf(13,7,9,13),
                intArrayOf(14,12,6,-2),
                intArrayOf(-1,13,8,15),
                intArrayOf(3,5,12,9),
                intArrayOf(13,13,15,10),
                intArrayOf(8,10,3,13),
                intArrayOf(14,10,9,3),
                intArrayOf(10,6,4,5),
            ),arrayOf("S","EE","","N","","NE")),

            Pair(arrayOf(
                intArrayOf(9,14,1,1),
                intArrayOf(3,5,8,2),
                intArrayOf(6,6,4,14),
                intArrayOf(2,5,5,-2),
                intArrayOf(-1,0,5,10),
                intArrayOf(0,8,7,4),
                intArrayOf(8,1,13,6)
            ),arrayOf("EN","EE"))
        )
//        testCases.forEach { (maze, sol) ->
//            println("arrayOf(${mazeSolver(maze)?.map { "\"$it\"" }?.joinToString(",")?: "null"})),") }

        println("Solving took ${System.currentTimeMillis() - startTime}")


        testCases.forEach { (maze, sol) ->
            val result = mazeSolver(maze)
            assertTrue(result.contentDeepEquals(sol),"Result:  ${result?.joinToString(",")?: "null"} \nSolution: ${sol?.joinToString(",")?: "null"}") }
    }

    @Test
    fun rotateTest() {
        maze.contentDeepEquals(maze)
        assertTrue(
            rotateMaze(maze).contentDeepEquals(
                arrayOf(
                    intArrayOf(8, 4, 10, 8),
                    intArrayOf(8, 15, 7, 2),
                    intArrayOf(-1, 3, 12, 1),
                    intArrayOf(9, 14, 14, -2)
                )
            ), "The input matrix is unequal to the target (1 rot) "
        )

        assertTrue(
            rotateMaze(rotateMaze(maze)).contentDeepEquals(
                arrayOf(
                    intArrayOf(1, 8, 5, 1),
                    intArrayOf(1, 15, 14, 4),
                    intArrayOf(-1, 6, 9, 2),
                    intArrayOf(3, 13, 13, -2)
                )
            ), "The input matrix is unequal to the target (2 rot)"
        )

        assertTrue(
            rotateMaze(rotateMaze(rotateMaze(maze))).contentDeepEquals(
                arrayOf(
                    intArrayOf(2, 1, 10, 2),
                    intArrayOf(2, 15, 13, 8),
                    intArrayOf(-1, 12, 3, 4),
                    intArrayOf(6, 11, 11, -2)
                )
            ), "The input matrix is unequal to the target (3 rot)"
        )

        assertTrue(
            rotateMaze(rotateMaze(rotateMaze(rotateMaze(maze)))).contentDeepEquals(maze),
            "The input matrix is unequal to the target (4 rot)"
        )
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
    fun validatePath() {
        val input = arrayOf(
            arrayOf(4, 2, 5, 4),
            arrayOf(4, 15, 11, 1),
            arrayOf('B', 9, 6, 8),
            arrayOf(12, 7, 7, 'X')
        )
        val maze = TransformingMaze(input as Array<Array<Any>>)
        val mazeArray = maze.getMaze(2)
//        val isValid = maze.validateStep(mazeArray,2, 2, Direction.S)
        val a = 0
    }

    @Test
    fun testConversion() {
//        val input = arrayOf(intArrayOf(25,56))
//        val input = arrayOf(
//            intArrayOf(4,2,5,4),
//            intArrayOf(4,15,11,1),
//            intArrayOf(-1,9,6,8),
//            intArrayOf(12,7,7,-2))

        val input = arrayOf(
            intArrayOf(-1, 6, 12, 15, 11),
            intArrayOf(8, 7, 15, 7, 10),
            intArrayOf(13, 7, 13, 15, -2),
            intArrayOf(11, 10, 8, 1, 3),
            intArrayOf(12, 6, 9, 14, 7)
        )
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
        } catch (e: NoSuchElementException) {

        }
    }


    @Test
    fun `Basic Tests`() {
        assertEquals(3, persistence(39))
        assertEquals(0, persistence(4))
        assertEquals(2, persistence(25))
        assertEquals(4, persistence(999))
    }

    fun persistence(num: Int): Int {
        var n = num
        var count = 0
        while (n / 10 != 0) {
            n = persistenceStep(n)
            count++
        }
        return count
    }

    fun persistenceStep(num: Int): Int {
        var mult = 1
        var n = num
        do {
            mult *= n % 10
            n /= 10
        } while (n != 0)
        return mult
    }


}

