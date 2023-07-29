import kotlin.test.Test
import kotlin.test.assertEquals
import TransformingMaze.Companion.mazeToString
import TransformingMaze.Companion.rotateCell

internal class  TransformatingMazeTests {

    val maze = arrayOf(
        arrayOf(4, 2, 5, 4),
        arrayOf(4, 15, 11, 1),
        arrayOf('B', 9, 6, 8),
        arrayOf(12, 7, 7, 'X')
    )


    @Test
    fun rotateTest() {


        assertEquals(2, rotateCell(1))
        assertEquals(4, rotateCell(2))
        assertEquals(8, rotateCell(4))
        assertEquals(1, rotateCell(8))
        assertEquals(15,rotateCell(15))
        assertEquals(6, rotateCell(3))

        assertEquals(3,rotateCell( rotateCell(rotateCell(rotateCell(3)))))

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

