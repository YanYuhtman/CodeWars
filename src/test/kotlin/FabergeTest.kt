import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class FabergeTest {

    @Test
    fun check100(){
        val n = 90
        for(k in 1 until n){
            var tmpK = k /*+ 10*/
            var sum = 0
            while (tmpK > 1 && sum < n ){
                sum += tmpK
                tmpK -=1
            }
            if(sum >= n) {
                println("Found k: $k sum: $sum")
                break
            }
        }

    }
    fun minAttemptsVerification(eggs: Int, height: Int) : Int{
        println("New Height: ${(height*height + height)/2}" )
        if(eggs < 2)
            return height
        if(eggs == 2)
            return Math.ceil(0.5 * (Math.sqrt(1 + 8 * height.toDouble()) -1)).toInt()
        var result = Int.MAX_VALUE

//        for(a in 2 until height){
        val tmp = minAttemptsVerification(eggs - 1, height)
        val variant = minAttemptsVerification(2, tmp)
        if(result > variant)
            result = variant
//        }
        return result

    }
    fun heightNotMine(n: BigInteger, m: BigInteger): BigInteger {
        if(n < BigInteger.TWO || m < BigInteger.TWO)
            return m
        if(n == BigInteger.TWO)
            return (m*m + m)/ BigInteger.TWO
        if(n == m)
            return BigInteger.TWO.pow(n.toInt()) - BigInteger.ONE


        return heightNotMine(n - BigInteger.ONE , m - BigInteger.ONE ) + heightNotMine(n,m - BigInteger.ONE) + BigInteger.ONE
    }

    val cache = mutableMapOf<Pair<BigInteger, BigInteger>, BigInteger>()
    fun getCachedValueOrCall(n: BigInteger, m: BigInteger): BigInteger {
        val pair = Pair(n,m)
        cache[pair].takeIf { it == null }.let {
            cache[pair] = height(n,m);
        }
        return cache[pair]!!
    }
    fun printCache(){
        val mat = Array<Array<String>>(cache.let { it.keys.maxOf { it.first.toInt() } } + 2)
        { Array<String>(cache.let { it.keys.maxOf { it.second.toInt() } } + 2) { "" } }
        cache.forEach { t, u ->
            if(mat[0][t.second.toInt()+1].isBlank())
                mat[0][t.second.toInt()+1] = t.toString()
            mat[t.first.toInt()+1][0] = t.toString()
            mat[t.first.toInt()+1][t.second.toInt()+1] = u.toString()}

        for(i in mat.indices) {
            for (j in mat[i].indices) {
                print( "%5s".format("${mat[i][j]}\t"))
            }
            print("\r\n");

        }



    }
    fun height(n: BigInteger, m: BigInteger): BigInteger {

        if(n < BigInteger.TWO || m < BigInteger.TWO) {
            cache.put(Pair(n,m), m)
            return m
        }
        if(n == m - BigInteger.ONE) {
            val tmp =  BigInteger.TWO.pow(m.toInt()) - BigInteger.TWO
            cache.put(Pair(n,m),tmp)
            return tmp
        }

        if(n == m - BigInteger.TWO) {
            val tmp = BigInteger.valueOf(4) * (BigInteger.TWO.pow(n.toInt()) - BigInteger.ONE) - n
            cache.put(Pair(n,m),tmp)
            return tmp
        }


        if(n == m)
            return BigInteger.TWO.pow(n.toInt()) - BigInteger.ONE

        if(n == BigInteger.TWO) {
            return (m * m + m) / BigInteger.TWO
        }
        val prev = getCachedValueOrCall(n - BigInteger.ONE,m - BigInteger.ONE)
//        val next = if(m.toInt() == 1) BigInteger.ONE else (BigInteger.TWO.pow(m.toInt()) - BigInteger.ONE)
        val next = getCachedValueOrCall(n,m - BigInteger.ONE)
        val result = prev + next + BigInteger.ONE


        if(!cache.contains(Pair(n,m))){
            arr.add(Triple(n.toInt()-1,m.toInt() - 1,result.toInt()))
//            println("prev: f(${n.toInt()-1},${m.toInt() - 1})=$prev next  f($n,${m.toInt() -1})=$next result: $result")

        }

        return result
    }
    var arr = mutableListOf<Triple<Int,Int,Int>>()



    private fun test(a: Int, b: Int, shouldBe: Int) {
        assertEquals(BigInteger.valueOf(shouldBe.toLong()), height(BigInteger.valueOf(a.toLong()), BigInteger.valueOf(b.toLong())))
    }

    private fun test(a: String, b: String, shouldBe: String) {
        assertEquals(BigInteger(shouldBe), height(BigInteger(a), BigInteger(b)))
    }

    @Test
    fun verification(){
        println("${minAttemptsVerification(2, 14)}")
        println("${minAttemptsVerification(2, 13)}")
        println("${minAttemptsVerification(2, 12)}")
        println("2,100: ${minAttemptsVerification(2, 100)}")
//        println("4,100: ${minAttemptsVerification(4, 100)}")
//        println("4,3213: ${minAttemptsVerification(4, 3213)}")
        val bp = 0
    }

    @Test
    @Throws(Exception::class)
    fun basicTests() {
//        test(1, 51, 51)
//        test(2, 1, 1)
//        test(2, 2, 3)
//        test(4, 5, 30)
//        test(4, 6, 56)
//        test(4, 7, 98)
//        test(4, 8, 162)
//        test(4, 9, 255)
//        test(4, 10, 385)
//        test(4, 11, 561)
//        test(4, 12, 793)

        val list = mutableListOf<Pair<Int,Int>>()
        for (i in 2..4)
            for (j in 2..20 )
                list.add(Pair(i, i + j))

        list.map {
            Triple(
                it.first,
                it.second,
                height(BigInteger.valueOf(it.first.toLong()), BigInteger.valueOf(it.second.toLong())).toInt()
            )

        }.filter { (it.second - it.first) % 2 == 0 }
            .sortedBy { it.first - it.second }
//            .sortedWith { o1: Triple<Int, Int, Int>?, o2: Triple<Int, Int, Int>? ->
//            when {
//                o1!!.first - o2!!.first > 0 -> 1
//                o1!!.first - o2!!.first < 0 -> -1
//                o1!!.second - o2!!.second > 0 -> 1
//                o1!!.second - o2!!.second < 0 -> -1
//                else -> 0
//            }
//        }
//            .let { list ->
//                list.mapIndexedNotNull { index, triple ->
//                    if (index < list.size - 1) Triple(
//                        triple.first,
//                        triple.second,
//                        list[index + 1].third - triple.third
////                        (Math.log((list[index + 1].third - triple.third).toDouble())/Math.log(2.0)).toInt()
//                    ) else null
//                }
//            }
            .map { "f(${it.first},${it.second})=${it.third} leftover: ${((4 * (Math.pow(2.0,it.first.toDouble()) - 1) - it.first)).toInt() - it.third }" }
            .forEach {
                println(it)
            }

        cache.map { Triple<Int,Int,Int>(it.key.first.toInt(),it.key.second.toInt(),it.value.toInt())  }
            .sortedBy { it.first }
            .filter { it.first != 2 }
            .groupBy { it.first }
//            .let { map ->
//                map.mapValues {
//                    val t = it.value.reduce { acc, triple -> if(triple.first == triple.second + 1) triple else acc }
//                    it.value.map { Triple(it.first,it.second,it.third - t.third)  }
//                }
//            }
            .flatMap { it.value }
//            .sortedBy { it.third }
//            .let { it
//                .mapIndexed {index, triple -> if(index > 0)
//                    Triple(triple.first,triple.second, triple.third - it[index - 1].third)
//                    else
//                        triple
//                }
//
//            }
//            .filter { it.second == 7 }

            .map { "f(${it.first},${it.second})=${it.third} "  }
            .forEach {
//                println(it)
            }

//        test(5,10, 637)
//        test(4, 17, 3213)
//        test(16, 19, 524096)
//        test(23, 19, 524287)

//        printCache()
    }
    fun printFn(n: Long, m:Long){

        val difference = height(BigInteger.valueOf(n), BigInteger.valueOf(m)).toInt()
        var arr = mutableListOf<Int>()
        for(index in m+1 .. 20)
            arr.add(height(BigInteger.valueOf(n), BigInteger.valueOf(index)).toInt() - difference)
        println(arr)
        var a1 = arr.mapIndexed {index, i -> if(index > 0) i - arr[index-1] else 0}
        println(a1)
        var a2 = a1.mapIndexed {index, i -> if(index > 0) i.toFloat()/a1[index-1] else 0}
        println(a2)
        var a3 = a2.mapIndexed {index, i -> if(index > 0) i.toFloat() - -a2[index-1].toFloat() else 0}
        println(a3)

    }

    @Test
    @Throws(Exception::class)
    fun advancedTests() {
        test("13", "550", "60113767426276772744951355")
//        test("271", "550", "1410385042520538326622498273346382708200418583791594039531058458108130216985983794998105636900856496701928202738750818606797013840207721579523618137220278767326000095")
//        test("531", "550", "3685510180489786476798393145496356338786055879312930105836138965083617346086082863365358130056307390177215209990980317284932211552658342317904346433026688858140133147")
    }
}