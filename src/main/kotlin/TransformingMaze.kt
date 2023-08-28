import java.security.InvalidParameterException

//https://www.codewars.com/kata/5b86a6d7a4dcc13cd900000b
@Deprecated("Not an optimal solution @see Java code")
class TransformingMaze (originalMaze: Array<Array<Any>>?){
    companion object {
        fun mazeToString(maze: Array<out Array<out Any>>): String {
            var out: String = ""
            for (i in 0 until maze.size) {
                for (j in 0 until maze[i].size) {
                    out += "${maze[i][j]}\t"
                }
                out += "\n"
            }
            return out
        }
        fun rotateCell(v: Int): Int {
            return (v shl 1) or (v shr 3) and 0xf
        }
        fun convertIntArrayMazeToAnyArray(r : Array<IntArray>) : Array<Array<Any>>{
            var entryFound:Int = 0
            var exitFound:Int = 0
            try {
                val result =  r.map { it ->
                    it.map {
                        if(it == -1)
                           entryFound++
                        else if(it == -2)
                            exitFound++
                        else if(it < 0 || it > 15)
                            throw Exception("Out of range")

                        when (it) {
                            -1 ->  'B'
                            -2 -> 'X'
                            else -> it
                        }
                    }.toTypedArray()
                }.toTypedArray() as Array<Array<Any>>

                if(entryFound != 1 || exitFound < 1)
                    throw Exception("Invalid number of entry or exit points")
                return result
            }catch (e:Exception){
                return emptyArray<Array<Any>>()
            }
        }
    }
    private var mazeRotations : Array<Array<Array<Any>>> = Array(4) { _ -> emptyArray<Array<Any>>() }

    init {
        originalMaze?.let {
            mazeRotations[0] = copyMaze(originalMaze)
            for(i in 1 until 4){
                mazeRotations[i] = rotateMaze(mazeRotations[i-1])
            }
        }
    }
    constructor (r: Array<IntArray>) : this(convertIntArrayMazeToAnyArray(r))

    private fun rotateMaze(originalMaze: Array<Array<Any>>) : Array<Array<Any>> {
        val mazeCopy = copyMaze(originalMaze)
        for (i in 0 until mazeCopy.size){
            for (j in 0 until mazeCopy[i].size){
                (mazeCopy[i][j] as? Int)?.let {
                    mazeCopy[i][j] = rotateCell(it)
                }
            }
        }
        return mazeCopy
    }

    private inline fun copyMaze(maze: Array<Array<Any>>) : Array<Array<Any>>{
        var out = Array<Array<Any>>(maze.size, {_ -> emptyArray<Any>() })
        for(i in 0 until maze.size){
            out[i] = Array<Any>(maze[i].size,{j -> maze[i][j]})
        }
        return out
    }

    fun getMaze(rotationIndex: Int) : Array<out Array<out Any>>{
        return mazeRotations[Math.abs(rotationIndex) % 4]
    }

    private fun makeStep(i:Int, j:Int, direction: Direction): Triple<Int,Int,Direction>{
        return when(direction){
            Direction.N -> Triple(i -1, j, direction.invert())
            Direction.S -> Triple(i + 1, j, direction.invert())
            Direction.E -> Triple(i, j + 1, direction.invert())
            Direction.W -> Triple(i, j - 1, direction.invert())
        }
    }

    private fun validateStep(maze:Array<out Array<out Any>>,i:Int, j:Int, direction: Direction) : Boolean{
        val (x,y,d) = makeStep(i,j, direction)

        if (x < 0 || x >= maze.size || y < 0 || y >= maze[i].size)
            return false
        val firstWall:Boolean = maze[i][j] == 'B' || (maze[i][j] as? Int)?.and(direction.bitwise) == 0

        return when(maze[x][y]){
            'X' -> firstWall
            is Int -> return (maze[x][y] as Int) and d.bitwise == 0 && firstWall
            else -> false
        }

    }

    private val routs: MutableList<MazeRoute> =  mutableListOf()
    private var minimalRotationPathCount = Int.MAX_VALUE

    fun findRoute(rotationEnabled:Boolean = true, isMinimalRotationsOnly: Boolean = false) : List<MazeRoute>{
        if (!mazeRotations.isEmpty())
            for (i in 0 until mazeRotations[0].size)
                for (j in 0 until mazeRotations[0][i].size)
                    if (mazeRotations[0][i][j] == 'B')
                        return findRoute(i, j, rotationEnabled,isMinimalRotationsOnly)
        return emptyList<MazeRoute>()

    }
    private fun findRoute(i:Int, j:Int, rotationEnabled:Boolean = true, isMinimalRotationsOnly: Boolean = false) : List<MazeRoute>{
        routs.clear()
        minimalRotationPathCount = Int.MAX_VALUE
        findRoute(i,j, MazeRoute(i,j),rotationEnabled,isMinimalRotationsOnly)
        return routs
    }
    private fun findRoute(i:Int, j:Int, route:MazeRoute, rotationEnabled:Boolean = true, isMinimalRotationsOnly:Boolean = false) {
        var tmpRotationCount = route.rotationCount

        if(isMinimalRotationsOnly && tmpRotationCount > minimalRotationPathCount)
            return

        for(r in 0 until mazeRotations.size) {
            if(!rotationEnabled && r > 0)
                break
            val currentMaze = getMaze((tmpRotationCount) % mazeRotations.size)
            for (d in Direction.values()) {
                if (validateStep(currentMaze, i, j, d)) {
                    val (x, y, _) = makeStep(i, j, d)

                    if (Pair(x, y) in route)
                        continue

                    val tmpRoute = route.clone()
                    tmpRoute.add(x,y,tmpRotationCount)
                    if (currentMaze[x][y] == 'X') {
                        routs.add(tmpRoute)
                        if(minimalRotationPathCount > tmpRotationCount)
                            minimalRotationPathCount = tmpRotationCount
                        return
                    }
                    findRoute(x, y, tmpRoute,rotationEnabled, isMinimalRotationsOnly)
                }
            }
            tmpRotationCount++
        }

    }
    override fun toString(): String {
        var out:String = ""
        for (i in 0 until mazeRotations.size){
            out += "Rotation index: $i\n"
            out += mazeToString(mazeRotations[i])
        }
        return out
    }

}

data class MazeRoute(val route:MutableList<Pair<Int,Int>> = mutableListOf(), private val rotationStates:HashMap<Pair<Int,Int>,Int> = HashMap(), var rotationCount:Int = 0) {


    constructor(i:Int, j: Int) : this() {
        route.add(Pair(i,j))
    }
    fun add(i:Int, j:Int, rotationCount: Int){
        route.add(Pair(i,j))
        if(this.rotationCount != rotationCount) {
            rotationStates[Pair(i, j)] = rotationCount
            this.rotationCount = rotationCount
        }
    }

    operator fun contains(pair: Pair<Int,Int>) : Boolean{
        return pair in route
    }


    fun clone():MazeRoute{
        return MazeRoute(mutableListOf<Pair<Int,Int>>().apply { addAll(route)}
            , HashMap<Pair<Int, Int>, Int>().apply { putAll(rotationStates)}
            ,rotationCount)
    }

    fun getByDirection(): List<String>{
        val list: MutableList<String> = mutableListOf()
        for(i in 0 until route.size - 1){
            val deltaX = route[i + 1].first - route[i].first
            val deltaY = route[i + 1].second - route[i].second
            when{
                deltaX == -1 -> list.add("N")
                deltaX ==  1 -> list.add("S")
                deltaY == -1 -> list.add("W")
                deltaY ==  1 -> list.add("E")
            }
        }
        return list
    }
    fun composeDirectionString(): String{
        var routeDelta = 0
        val sBuilder = StringBuilder()
        for(i in 0 until route.size - 1){
            if(rotationStates.contains(route[i+1])) {
                rotationStates.get(route[i+1])?.let {
                    for (i in 0 until it - routeDelta)
                        sBuilder.append(",")
                    if(!simpleFormatted)
                        sBuilder.append(" ")
                    routeDelta = it
                }
            }
            val deltaX = route[i + 1].first - route[i].first
            val deltaY = route[i + 1].second - route[i].second
            when{
                deltaX == -1 -> sBuilder.append("N")
                deltaX ==  1 -> sBuilder.append("S")
                deltaY == -1 -> sBuilder.append("W")
                deltaY ==  1 -> sBuilder.append("E")

            }

        }
        return sBuilder.toString()
    }
    var simpleFormatted:Boolean = false
    override fun toString(): String {
        if(simpleFormatted)
            return composeDirectionString()
        val builder = StringBuilder("{ $rotationCount : ")
        builder.append(composeDirectionString())
        builder.append("}")
        return builder.toString()
    }
}

enum class Direction(val bitwise: Int) {
    N(0x8),
    W(0x4),
    E(0x1),
    S(0x2),
    ;

    fun isDirection(bitwise: Int, direction: Direction): Boolean {
        return direction.bitwise and bitwise == direction.bitwise
    }
    fun invert():Direction{
        return when(this){
            N -> S
            S -> N
            W -> E
            E -> W
        }
    }
}



fun main() {

}