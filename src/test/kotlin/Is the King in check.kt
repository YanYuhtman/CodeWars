import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.sqrt

class `Is the King in check` {

    //https://www.codewars.com/kata/5e320fe3358578001e04ad55/

    fun isCheck(b:Array<Array<String>>)=b.mapIndexed{y,S->S.mapIndexed{x,s->listOf(s[0].code*1f,x*1F,y*1F)}}
            .flatten().filter{it[0]!=32F}.sortedBy{it[0]}
            .let{m->m.map{(s,x,y)->Triple(s.toChar(),x-m[0][1],y-m[0][2])
                .let{(s,a,b)->listOf(s to abs(a/b),s to a*a+b*b)}}
            }.sortedBy{it[1].second}.distinctBy{it[0].second
            }.flatten().any{it in("♛♛♜♝♞♟").toCharArray().zip(listOf(1F,1/0F,1/0F,1F,5F,2F))}

    fun isCheck5(b:Array<Array<String>>)=b.mapIndexed{y,s->s.mapIndexed{x,s->listOf(s[0].toFloat(),x*1F,y*1F)}}
            .flatten().filter{it[0]!=32F}.sortedBy{it[0]}
            .let{m->m.map{(s,x,y)->(x-m[0][1]to y-m[0][2])
                .let{(a,b)->listOf(s.toChar()to abs(a/b),s.toChar()to a*a+b*b)}}
            }.sortedBy{it[1].second}.distinctBy{it[0].second
            }.flatten().any{it in("♛♛♜♝♞♟").toCharArray().zip(listOf(1F,1/0F,1/0F,1F,5F,2F))}

    fun isCheck4(b:Array<Array<String>>)=b.mapIndexed{y,s->s.mapIndexed{x,s->listOf(s[0].toFloat(),x*1F,y*1F)}}
        .flatten().filter{it[0]!=32F}.sortedBy{it[0]}
        .let{m->m.map{(s,x,y)->(x-m[0][1]to y-m[0][2])
            .let{(a,b)->listOf(s,abs(a/b),a*a+b*b)}}}
        .sortedBy{it[2]}.distinctBy{it[1]}
        .any {(s,a,c)->
            when(s.toChar()){
                '♟'->c==2F
                '♞'->c==5F
                '♝'->a==1F
                '♜'->a==1/0F
                '♛'->a==1F||a==1/0F
                else->false}}


//The correct one:
//    when(s.toChar()){
//        '♞'->x==0.5F&&y==5F
//        '♝'->x==1F
//        '♟'->y==2F
//        '♜'->x==1/0F
//        '♛'->x==1F||x==1/0F
//        else->false}}

    fun isCheck3(b: Array<Array<String>>):Boolean {
        b.forEach { it.forEach {
            val l = when (it){
                "♛"-> "Q"
                "♟"-> "p"
                "♝"-> "B"
                "♜"-> "R"
                "♞"-> "k"
                "♔"-> "K"
                else->"*"
            }
            print(" $l ") }; println()}
        println()
       return b.mapIndexed { y, s -> s.mapIndexed { x, s -> s to arrayOf(x * 1.0, y * 1.0) } }.flatten().toMap().let {
            it["♔"]!!.let { k -> it.mapValues { (_, v) -> arrayOf(v[0] - k[0], v[1] - k[1]) } }
                .mapValues { (k, v) -> arrayOf(v[0] / v[1], sqrt(v[0] * v[0] + v[1] * v[1])) }.toList()
                .sortedBy { it.second[1] }
                .distinctBy { it.second[0] }.toMap()
        }.let { m ->
            mapOf(
                "♛" to arrayOf(0.0, 1 / 0.0, 1.0, 12.0),
                "♟" to arrayOf(1.0, sqrt(2.0)),
                "♝" to arrayOf(1.0, 12.0),
                "♜" to arrayOf(0.0, 1 / 0.0, 8.0),
                "♞" to arrayOf(0.5, 2.0, sqrt(5.0))
            ).any { e -> e.value.any { v -> m[e.key]?.let { p -> abs(p[0]) == v && p[1] <= e.value.last() } ?: false } }
        }
    }
    fun isCheck1(board: Array<Array<String>>):Boolean{

//        val configArray = arrayOf(arrayOf("♔",0.0),arrayOf("♛",0,1.0,-1.0),arrayOf("♜",0),arrayOf("♝",1.0,-1.0),arrayOf("♞",1/2.0,-1/2.0),arrayOf("♟",1.0,-1.0))
//        val tmp = board.mapIndexed{x, s -> s.mapIndexed{ y, s -> s to (x to y) } }.flatten().distinctBy { it.first }
//        val tmp1 = tmp.zip(configArray, transform = { a, b -> if(a.first == b[0]) listOf(a,b) })
//        val tmp2 = tmp.associateBy { it.first }


//        val tmp4 = board.mapIndexed {y,s->s.mapIndexed{x,s->s to (x to y)}}.flatten().toSet()
//        a.run{(s to (x to y))}
//        val tmp4 = board.foldIndexed(mutableMapOf(" " to arrayOf(0.0,0.0))){y,a,s->a.also{s.mapIndexed{x,s->it+=s to arrayOf(x.toDouble(),y.toDouble())}}}
//            .let{m->m["♔"]!!.let {k->m.mapValues{e-> arrayOf(e.value[0]-k[0],e.value[1]-k[1])}}}
//                { arrayOf(it.value[0]-k[0],it.value[1]-k[1])}}}
        val boardMap = board.mapIndexed{ y, s->s.mapIndexed{ x, s->s to arrayOf(x*1.0,y*1.0)}}.flatten().toMap()
            .let{it["♔"]!!.let{k->it.mapValues{(_,v)->arrayOf(v[0]-k[0],v[1]-k[1])}}}

//        val boardMap2 = board.mapIndexed{y,s->s.mapIndexed{x,s->s to arrayOf(x*1.0,y*1.0)}}.flatten().toMap()
//            .let{it["♔"]!!.let{k->it.mapValues{(_,v)->arrayOf(v[0]-k[0],v[1]-k[1]) }
//                .mapValues{(_,v)->
//                    arrayOf(v[1]/v[0],sqrt(v[0]*v[0]+v[1]*v[1]))}}}.mapValues {(k,v)->  }
        val boardMap2 = board.mapIndexed{y,s->s.mapIndexed{x,s->s to arrayOf(x*1.0,y*1.0)}}.flatten().toMap()
            .let{it["♔"]!!.let{k->it.mapValues{(_,v)->arrayOf(v[0]-k[0],v[1]-k[1]) }}
                .mapValues {(k,v)->
                    arrayOf(v[0]/v[1],sqrt(v[0]*v[0]+v[1]*v[1]))}.toList().distinctBy { it.second[0] }
                .toMap()
            }
//                .map{(k,v)->
//                    listOf(k,v[1]/v[0],sqrt(v[0]*v[0]+v[1]*v[1]))
//                }.distinctBy{it[1]}}}

//        val boardList       = board.mapIndexed{y,s->mapIn}
        val configMap = mapOf("♛" to arrayOf(0.0,1/0.0,1.0,12.0),"♟" to arrayOf(1.0, sqrt(2.0)),"♝" to arrayOf(1.0,12.0),"♜" to arrayOf(0.0,1/0.0,8.0),"♞" to arrayOf(0.5,2.0,sqrt(5.0)))

        val result2 = boardMap.let { m->
            configMap.any{e->e.value.any{v->m[e.key]?.let{
                    p->(p[1]==v||abs((p[0])/(p[1]))==v)&&sqrt(p[0]*p[0]+p[1]*p[1])<=e.value.last()
            }?:false}}}

        val result3 =  boardMap2.let { m->
            configMap.any{e->e.value.any{v->m[e.key]?.let{
                    p->abs(p[0])==v&&p[1]<=e.value.last()
            }?:false}}}

        return result3

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
    @Test
    fun randomCheck() {
        val board = arrayOf(
        arrayOf(" "," "," "," "," "," "," "," "),
        arrayOf(" ","♛"," "," "," "," "," "," "),
        arrayOf(" "," "," "," "," "," "," "," "),
        arrayOf("♝"," ","♟","♛"," ","♛"," "," "),
        arrayOf(" "," "," "," ","♔"," "," "," "),
        arrayOf(" "," "," "," "," "," "," "," "),
        arrayOf(" "," "," "," "," ","♛"," "," "),
        arrayOf(" "," "," "," "," "," "," "," "))
        assertEquals(true, isCheck(board))


    }

}