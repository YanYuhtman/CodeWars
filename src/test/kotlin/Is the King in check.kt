import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Stroke
import kotlin.math.abs
import kotlin.math.sqrt

class `Is the King in check` {
    //https://www.codewars.com/kata/5e320fe3358578001e04ad55/
    fun isCheck(board: Array<Array<String>>):Boolean{

        val configArray = arrayOf(arrayOf("♔",0.0),arrayOf("♛",0,1.0,-1.0),arrayOf("♜",0),arrayOf("♝",1.0,-1.0),arrayOf("♞",1/2.0,-1/2.0),arrayOf("♟",1.0,-1.0))
        val tmp = board.mapIndexed{x, s -> s.mapIndexed{ y, s -> s to (x to y) } }.flatten().distinctBy { it.first }
        val tmp1 = tmp.zip(configArray, transform = { a, b -> if(a.first == b[0]) listOf(a,b) })
        val tmp2 = tmp.associateBy { it.first }


//        val tmp4 = board.mapIndexed {y,s->s.mapIndexed{x,s->s to (x to y)}}.flatten().toSet()
//        a.run{(s to (x to y))}
//        val tmp4 = board.foldIndexed(mutableMapOf(" " to arrayOf(0.0,0.0))){y,a,s->a.also{s.mapIndexed{x,s->it+=s to arrayOf(x.toDouble(),y.toDouble())}}}
//            .let{m->m["♔"]!!.let {k->m.mapValues{e-> arrayOf(e.value[0]-k[0],e.value[1]-k[1])}}}
//                { arrayOf(it.value[0]-k[0],it.value[1]-k[1])}}}
        val tmp4 = board.mapIndexed{y,s->s.mapIndexed{x,s->s to arrayOf(x*1.0,y*1.0)}}.flatten().toMap()
            .let{it["♔"]!!.let{k->it.mapValues{e->arrayOf(e.value[0]-k[0],e.value[1]-k[1])}}}
//
        val configMap = mapOf("♛" to arrayOf(0.0,1.0,12.0),"♟" to arrayOf(1.0, sqrt(2.0)),"♝" to arrayOf(1.0,12.0),"♜" to arrayOf(0.0,8.0),"♞" to arrayOf(0.5,sqrt(5.0)))
        val configMap2 = listOf("♛|0,1,-1","♟|1,-1","♝|1,-1","♜|0","♞|0.5,-0.5").map{it.split("|").let{it[0] to (it[1].split(",").let{it.map{it.toDouble()}})}}.toMap()


//        val result = tmp4.let{m->configMap.entries.any{e-> m[e.key]!![0]/m[e.key]!![1]==1}}
//        val result2 = tmp4.let {m->m["♔"]?.let {k->
//            configMap.any{e->e.value.any{v->m[e.key]?.let{
//            pos->((pos[1]-k[1])==v||(pos[0]-k[0])/(pos[1]-k[1])==abs(v))
//        }?:false}}}
//            ?:false}

        val result2 = tmp4.let {m->
            configMap.any{e->e.value.any{v->m[e.key]?.let{
                    p->(p[1]==v||abs((p[0])/(p[1]))==v)&&sqrt(p[0]*p[0]+p[1]*p[1])<=e.value.last()
            }?:false}}}


        return result2

    }
    fun isCheck0(board: Array<Array<String>>):Boolean{
        val peaces = mapOf(
            ("♔" to Triple(Double.NaN , Double.NaN , Double.NaN)),
            ("♛" to Triple(Math.PI/2 , Math.atan(1.0) , Double.NaN)),
            ("♜" to Triple(Math.PI/2, Math.PI/2,Double.NaN)),
            ("♝" to Triple(Math.atan(1.0),Math.atan(1.0), Double.NaN)),
            ("♞" to Triple(Math.atan(1/2.0), Math.atan(2/1.0),Math.sqrt(Math.pow(1.0,2.0) + Math.pow(2.0,2.0)))),
            ("♟" to Triple(Math.atan(-1.0),Math.atan(1.0),Math.sqrt(2.0))),
        )


        var tmp = board.foldIndexed(mutableSetOf<Triple<String,Double,Double>>()){ y, acc, a ->
            a.forEachIndexed { x, s -> if(s!=" ") acc.add(Triple(s,x.toDouble(),y.toDouble())) };acc }.sortedBy { it.first }

        val tmp2 = tmp.map{ Triple(it.first,it.second - tmp[0].second,it.third - tmp[0].third) }
        val tmp3 = tmp2.map{ Triple(it.first, Math.atan(it.second/it.third), Math.sqrt(Math.pow(it.second,2.0) + Math.pow(it.third,2.0))) }

        val tmp4 = tmp3.groupingBy { it.second }.reduce {k, a, e -> if(a.third < e.third) a else e }.map { it.value }

        return (tmp4.any { val p = peaces[it.first]!!; (it.second % p.first == 0.0 || it.second % p.second == 0.0)
                &&  (p.third.isNaN() || it.third == p.third)})

    }
    @Test
    fun shouldWorkWithCheckByPawn() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," ","♟"," "," "," "," "),
            arrayOf(" "," ","♔"," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithPawnDirectlyAbove() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," ","♟"," "," "," "," "," "),
            arrayOf(" "," ","♔"," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), false)
    }

    @Test
    fun shouldWorkWithCheckByBishop() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," ","♔"," "," "," "," "," "),
            arrayOf(" "," "," ","♝"," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithCheckByBishop2() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," ","♝"),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf("♔"," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithCheckByRook() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," "," "," ","♜"," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithCheckByKnight() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf("♞"," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithCheckByQueen() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," "," ","♛"," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), true)
    }

    @Test
    fun shouldWorkWithKingAlone() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), false)
    }

    @Test
    fun shouldWorkWhenNoCheck() {
        val board = arrayOf(
            arrayOf("♛"," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," ","♞"," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," ","♛"),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), false)
    }

    @Test
    fun shouldWorkWhenAPieceIsBlockingLineOfSight() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" ","♔"," ","♞","♛"," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(isCheck(board), false)
    }

    @Test
    fun noCheck() {
        val board = arrayOf(
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf("♔"," "," "," "," "," "," ","♞"),
            arrayOf(" "," "," "," "," "," "," "," "),
            arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(false, isCheck(board))
    }
}