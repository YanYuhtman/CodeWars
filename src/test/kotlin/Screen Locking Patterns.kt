import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

open class Key(
     private val _name:Char,
     val x:Int,
     val y:Int){
    var inPath:Boolean = false
    val name:Char get() = if(inPath) 'X' else _name
}
class `Screen Locking Patterns` {

    //https://www.codewars.com/kata/585894545a8a07255e0002f1/kotlin
    object A:Key( 'A',0,0)
    object B:Key ( 'B',0,1)
    object C:Key ( 'C',0,2)
    object D:Key ( 'D',1,0)
    object E:Key ( 'E',1,1)
    object F:Key ( 'F',1,2)
    object G:Key ( 'G',2,0)
    object H:Key ( 'H',2,1)
    object I:Key ( 'I',2,2)

    fun initScreenMap():Array<Array<Char>> = arrayOf<Array<Char>>(
                arrayOf('A', 'B', 'C'),
                arrayOf('D', 'E', 'F'),
                arrayOf('G', 'H', 'I'),
            )

    enum class DIRECTION(val x:Int, val y:Int){
        NORTH(-1,0),
        NORTH_WEST(-1,-1),
        NORTH_EAST(-1,1),
        SOUTH(1,0),
        SOUTH_WEST(1,-1),
        SOUTH_EAST(1,1),
        WEST(0,-1),
        EAST(0,1)

    }

    fun makeStep(direction:DIRECTION, map:Array<Array<Char>>, currentX:Int, currentY:Int){
        map[currentX+direction.x][currentY+direction.y] = 'x'
    }
    @Test
    fun prerequisitesTest(){
        val screenMapCopy = initScreenMap()
        makeStep(DIRECTION.SOUTH_EAST,screenMapCopy,0,0)
        val a = 1
    }
    fun countPatternsFrom(firstPoint: String, length: Int): Int {

        return 0;
    }
    @Test
    fun `sample tests`() {
        assertEquals(0, countPatternsFrom("A", 10), "Should return 0 for path of length 10 with starting point A")
        assertEquals(0, countPatternsFrom("A", 0), "Should return 0 for path of length 0 with starting point A")
        assertEquals(0, countPatternsFrom("E", 14), "Should return 0 for path of length 14 with starting point E")
        assertEquals(1, countPatternsFrom("B", 1), "Should return 1 for path of length 1 with starting point B")
        assertEquals(5, countPatternsFrom("C", 2), "Should return 5 for path of length 2 with starting point C")
        assertEquals(8, countPatternsFrom("E", 2), "Should return 8 for path of length 2 with starting point E")
        assertEquals(256, countPatternsFrom("E", 4), "Should return 256 for path of length 4 with starting point E")
    }
}