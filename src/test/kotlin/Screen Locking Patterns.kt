import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.security.InvalidAlgorithmParameterException

class `Screen Locking Patterns` {

    //https://www.codewars.com/kata/585894545a8a07255e0002f1/kotlin

    fun initScreenMap():Array<Array<Char>> = arrayOf<Array<Char>>(
                arrayOf('A', 'B', 'C'),
                arrayOf('D', 'E', 'F'),
                arrayOf('G', 'H', 'I'),
            )
    val DIRECTIONS:Array<Pair<Int,Int>> = arrayOf((0 to 1),(0 to -1),(1 to 0),(-1 to 0),
        (1 to 1),(1 to -1),(-1 to 1),(-1 to -1),
        (1 to 2),(2 to 1),(1 to -2),(-2 to 1),(-2 to -1),(2 to -1),(-1 to 2),(-1 to -2)
    )
    fun evaluateStartPoint(map: Array<Array<Char>>, firstPoint: Char):Pair<Int,Int>?{
        map.forEachIndexed {x, chars ->
            val y = chars.indexOf(firstPoint); if(y != -1) {map[x][y] = 'x'; return Pair(x,y)}
        }
        return null
    }

    fun makeStep( map:Array<Array<Char>>, direction:Pair<Int,Int>, currentX:Int, currentY:Int):Triple<Char,Int,Int>?{
        var (stepX, stepY) = (currentX + direction.first to currentY + direction.second)
        while (true) {
            if (stepX < 0 || stepX > map.lastIndex || stepY < 0 || stepY > map[0].lastIndex)
                return null
            if(map[stepX][stepY] != 'x')
                break
            stepX += direction.first
            stepY += direction.second
        }
        val ch = map[stepX][stepY]
        map[stepX][stepY] = 'x'
        return Triple(ch,stepX,stepY)
    }

    val paths: MutableList<List<Char>> = mutableListOf<List<Char>>()
    fun findPaths(map: Array<Array<Char>>, direction: Pair<Int,Int>, currentX: Int, currentY: Int, path: MutableList<Char>, length:Int ):Int{
        var sum = 0
        makeStep(map,direction, currentX,currentY)?.let {
            path.add(it.first)
            if(length - 1 == 0){
                paths.add(path)
                return 1
            }
            DIRECTIONS.forEach { direction ->
              sum += findPaths(
                    map.map { arr -> arr.map { it }.toTypedArray() }.toTypedArray(),
                    direction,
                    it.second,
                    it.third,
                    path.map { it }.toMutableList(),
                    length - 1
                )
            }
        }
        return sum
    }

    fun countPatternsFrom(firstPoint: String, length: Int): Int {
        if(length < 2)
            return length
        paths.clear()
        val map = initScreenMap()
        evaluateStartPoint(map, firstPoint.first())?.let {
            var sum = 0
            DIRECTIONS.forEach { direction ->
              sum +=  findPaths(
                    map.map { arr -> arr.map { it }.toTypedArray() }.toTypedArray(),
                    direction,
                    it.first,
                    it.second,
                    mutableListOf(firstPoint.first()),
                    length - 1
                )
            }
            return sum
        }?: throw InvalidAlgorithmParameterException()

//        return paths.size;
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

    @Test
    fun prerequisitesTest(){

        val screenMapCopy = initScreenMap()
        evaluateStartPoint(screenMapCopy,'A')?.let {
            //rest of the code
        }?: throw InvalidAlgorithmParameterException()

        while (makeStep(screenMapCopy,DIRECTIONS[2],0,0) != null) {}
        val a = 1
    }
}