import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.text.StringBuilder

class `Plants and Zombies` {
    //https://www.codewars.com/kata/5a5db0f580eba84589000979/kotlin

    object PNZ {
        class Game(val lawn:Array<String>, var zombies: Array<IntArray>){

            fun makeStep(){
                zombies.forEach {
                    it[0] -= 1
                    if(it[0] < 1) {
                        val r = it[1]
                        val c = lawn[r].length - 1 + it[0]
                        lawn[r] = lawn[r].mapIndexed { col, ch -> if (col == c && ch != ' ') 'x' else ch }.joinToString("")
                    }

                }
            }
            fun checkWinningStatus():Int{
                if(zombies.any { -it[0] >= lawn[it[1]].length})
                    return -1
                if(zombies.isEmpty())
                    return 1
                return 0
            }
            fun firePonds(){
                val soldiers = lawn.mapIndexed { r, s-> s.mapNotNull{p-> if (p.isDigit()) (r to p.digitToInt()) else null } }.flatten()
                soldiers.forEach { soldier->
                    for(i in (1 .. soldier.second))
                        zombies.filter { it[0] <= 0 && it[1] == soldier.first }.minByOrNull { it[0] }
                            ?.let { it[2]-=1; if(it[2] < 0) zombies = zombies.toMutableList().apply { this.remove(it) }.toTypedArray() }
                            ?: break
                }
            }
            fun fireASes() {
                val soldiers =
                    lawn.mapIndexed { r, s -> s.mapIndexedNotNull { c, S -> if (S == 'S') (r to c) else null } }
                        .flatten()
                        .sortedWith(compareByDescending <Pair<Int, Int>> { it.second }.thenBy { it.first })
                soldiers.forEach { S ->
                    val targets = zombies.mapIndexedNotNull { i, z ->
                        var out: Pair<IntArray, Pair<Double,Int>>? = null
                        val dr = z[1] - S.first
                        val dc = lawn[z[1]].length - 1 + z[0] - S.second
                        if (z[0] <= 0 && dc > 0) {
                            val angle = if (dr == 0) 0.0 else dr / dc.toDouble()
                            if (abs(angle) == 1.0 || angle == 0.0)
                                out = (z to (angle to dr * dr + dc * dc))

                        }
                        out
                    }.sortedWith(compareBy<Pair<IntArray, Pair<Double, Int>>> { it.second.first }.thenBy { it.second.second }).distinctBy { it.second.first }

                    targets.forEach {
                        it.first[2] -= 1
                        if (it.first[2] < 0)
                            zombies = zombies.toMutableList().apply { this.remove(it.first) }.toTypedArray()
                    }

                }
            }
            var moveNumber = 0
            fun play():Int?{

                var status = 0

                while (status == 0) {
                    println(this)
                    makeStep()
                    println(this)
                    firePonds()
                    println(this)
                    fireASes()
                    moveNumber += 1
                    status = checkWinningStatus()
                }
                return when (status){
                    -1 -> moveNumber
                    else -> null
                }
            }

            override fun toString(): String {
                val zombies = zombies.filter { it[0] <= 0 }.associateBy { (it[1] to lawn[0].length - 1 + it[0]) }
                val out = StringBuilder("  " + (0 until lawn[0].length).toList().toString()).append("\n")
                return lawn.foldIndexed(out) { r, S, s->
                    S.append("$r|")
                    s.mapIndexed{ c, ch->
                        val per = if(c == 0) 2 else 3
                        val _ch = zombies[(r to c)]?.let { "Z${it[2]}" } ?: if(ch == ' ') "${'\u00B7'}" else "$ch"
                        S.append("%${per}s".format(_ch))
                    }
                    S.append("\n");
                }.toString()

            }


        }
        fun plantsAndZombies(lawn:Array<String>,zombies:Array<IntArray>): Int? {
            return Game(lawn,zombies).play()
        }
    }

    @Test
    fun runExamples() = exampleSols.zip(exampleTests).forEach { (sol,tst) ->
        assertEquals(sol,PNZ.plantsAndZombies(tst.first,tst.second)) }

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
//        Pair(
//            arrayOf(
//                "11      ",
//                " 2S     ",
//                "11S     ",
//                "3       ",
//                "13      "),
//            arrayOf(
//                intArrayOf(0,3,16),
//                intArrayOf(2,2,15),
//                intArrayOf(2,1,16),
//                intArrayOf(4,4,30),
//                intArrayOf(4,2,12),
//                intArrayOf(5,0,14),
//                intArrayOf(7,3,16),
//                intArrayOf(7,0,13))),
//        Pair(
//            arrayOf(
//                "12        ",
//                "3S        ",
//                "2S        ",
//                "1S        ",
//                "2         ",
//                "3         "),
//            arrayOf(
//                intArrayOf(0,0,18),
//                intArrayOf(2,3,12),
//                intArrayOf(2,5,25),
//                intArrayOf(4,2,21),
//                intArrayOf(6,1,35),
//                intArrayOf(6,4,9),
//                intArrayOf(8,0,22),
//                intArrayOf(8,1,8),
//                intArrayOf(8,2,17),
//                intArrayOf(10,3,18),
//                intArrayOf(11,0,15),
//                intArrayOf(12,4,21))),
//        Pair(
//            arrayOf(
//                "12      ",
//                "2S      ",
//                "1S      ",
//                "2S      ",
//                "3       "),
//            arrayOf(
//                intArrayOf(0,0,15),
//                intArrayOf(1,1,18),
//                intArrayOf(2,2,14),
//                intArrayOf(3,3,15),
//                intArrayOf(4,4,13),
//                intArrayOf(5,0,12),
//                intArrayOf(6,1,19),
//                intArrayOf(7,2,11),
//                intArrayOf(8,3,17),
//                intArrayOf(9,4,18),
//                intArrayOf(10,0,15),
//                intArrayOf(11,4,14))),
//        Pair(
//            arrayOf(
//                "1         ",
//                "SS        ",
//                "SSS       ",
//                "SSS       ",
//                "SS        ",
//                "1         "),
//            arrayOf(
//                intArrayOf(0,2,16),
//                intArrayOf(1,3,19),
//                intArrayOf(2,0,18),
//                intArrayOf(4,2,21),
//                intArrayOf(6,3,20),
//                intArrayOf(7,5,17),
//                intArrayOf(8,1,21),
//                intArrayOf(8,2,11),
//                intArrayOf(9,0,10),
//                intArrayOf(11,4,23),
//                intArrayOf(12,1,15),
//                intArrayOf(13,3,22)))
    )
}