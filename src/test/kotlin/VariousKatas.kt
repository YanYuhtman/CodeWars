import java.math.BigInteger
import java.util.*
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PersistenceNumber {
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
    @Test
    fun `Basic Tests`() {
        assertEquals(3, persistence(39))
        assertEquals(0, persistence(4))
        assertEquals(2, persistence(25))
        assertEquals(4, persistence(999))
    }

}

class AltCapsTests {
    fun capitalize(text:String) : List<String>{
        return listOf<String>(
            String(text.mapIndexed { index, value -> if (index % 2 == 0) value.uppercaseChar() else value }.toCharArray()),
            String(text.mapIndexed { index, value -> if (index % 2 == 1) value.uppercaseChar() else value }.toCharArray())
        )
    }
    @Test
    fun basicTests() {
        assertEquals(listOf("AbCdEf", "aBcDeF"), capitalize("abcdef"))
        assertEquals(listOf("CoDeWaRs", "cOdEwArS"), capitalize("codewars"))
        assertEquals(listOf("AbRaCaDaBrA", "aBrAcAdAbRa"), capitalize("abracadabra"))
        assertEquals(listOf("CoDeWaRrIoRs", "cOdEwArRiOrS"), capitalize("codewarriors"))
    }
}


class TestExample {
    fun rgb(r: Int, g: Int, b: Int): String {
       return String.format("%2X%2X%2X",correctColor(r) ,correctColor(g) , correctColor(b)).replace(' ', '0')
    }
    inline fun correctColor(c:Int):Int {return Math.min(Math.max(0,c),255)}
    @Test
    fun testFixed() {
        assertEquals("000000", rgb(0, 0, 0))
        assertEquals("000000", rgb(0, 0, -20))
        assertEquals("FFFFFF", rgb(300,255,255))
        assertEquals("ADFF2F", rgb(173,255,47))
        assertEquals("9400D3", rgb(148, 0, 211))
    }
}

class RomanNumbers {

    private val Char.toDecimal: Pair<Int,Int>
        get() {
            return when (this) {
                'I' -> Pair(0,1)
                'V' -> Pair(1,5)
                'X' -> Pair(2,10)
                'L' -> Pair(3,50)
                'C' -> Pair(4,100)
                'D' -> Pair(5,500)
                'M' -> Pair(6,1000)
                else -> Pair(-1,0)
            }
        }

    fun decode(str: String): Int {
        if(str.isEmpty())
            return 0

      return  str.map { it.toDecimal }
            .reduceRight{v1,v2 -> if(v2.first - v1.first == 1 || (v2.first - v1.first == 2 && v2.first % 2 == 0))
                Pair(v1.first,v2.second - v1.second) else Pair(v1.first,v2.second + v1.second)}
            .second
    }

    @Test
    fun basic() {
        assertEquals(0, decode(""))
        assertEquals(1, decode("I"))
        assertEquals(21, decode("XXI"))
        assertEquals(2008, decode("MMVIII"))
        assertEquals(1666, decode("MDCLXVI"))
        assertEquals(1139, decode("MCXXXIX"))

    }

}

class ObservedPinTest {
    class Permutator(input: Array<IntArray>){

        private lateinit var _indexes:IntArray
        var indexes: IntArray
            get() {return _indexes.copyOf()}
            private set(value) { this._indexes = value }

        private val border: IntArray
        private var currentIndex = 0
        init {
            indexes = IntArray(input.size){0}
            border = IntArray(indexes.size)
            input.forEachIndexed { i,arr -> border[i] = arr.size }
            currentIndex = indexes.size - 1
        }
        fun increment() : Boolean{
            if(_indexes[currentIndex] + 1 >= border[currentIndex]){
                for (i in currentIndex until _indexes.size)
                    _indexes[i] = 0
                currentIndex -= 1
                if(currentIndex < 0)
                    return false
                if(_indexes[currentIndex] + 1 >= border[currentIndex])
                    return increment()
            }
            _indexes[currentIndex] += 1
            currentIndex = indexes.size - 1
            return true
        }
    }
    private val keyPad = arrayOf(
        intArrayOf(1,2,3),
        intArrayOf(4,5,6),
        intArrayOf(7,8,9),
        intArrayOf(-1,0,-1)
    )
    private fun getKeyNeighbours(queryKey:Int) : IntArray{
        var output = mutableListOf<Int>()
        keyPad.forEachIndexed {i,arr ->
            arr.forEachIndexed {j,key ->
                if(key == queryKey){
                    validateAndAddToOutput(i,j,output)
                    validateAndAddToOutput(i-1,j,output)
                    validateAndAddToOutput(i+1,j,output)
                    validateAndAddToOutput(i,j-1,output)
                    validateAndAddToOutput(i,j+1,output)
                }
            }
        }
        return output.toIntArray()
    }
    private fun validateAndAddToOutput(i:Int, j:Int, output: MutableList<Int>){
        if(i < 0 || i >= keyPad.size)
            return
        if(j < 0 || j >= keyPad[i].size)
            return
        if(keyPad[i][j] < 0)
            return
        output += keyPad[i][j]
    }
    fun getPINs(observed: String): List<String> {
        val keyOptions = observed.map{it.digitToInt()}.map { getKeyNeighbours(it)}.toTypedArray()
        val permutator = Permutator(keyOptions)
        val indexes = permutator.indexes

        val output:MutableList<String> = mutableListOf()


        do{
            var sDigit = ""
            val indexes = permutator.indexes
            keyOptions.forEachIndexed{ i,arr ->
                sDigit += arr[indexes[i]]
            }
            output.add(sDigit)
        }while (permutator.increment())

        return output
    }

    @Test
    fun testPermutator(){
        val simpleArray = arrayOf(
            intArrayOf(1,2,4),
            intArrayOf(1,2,4)

        )
        val permutator = Permutator(simpleArray)
        do{
            val indexes = permutator.indexes
            simpleArray.forEachIndexed{ i,arr ->
                print(arr[indexes[i]])
            }
            println()
        }while (permutator.increment())

    }
    @Test
    fun sample_tests() {
        setOf(
            "8"         to   listOf("5","7","8","9","0"),
            "11"        to   listOf("11", "22", "44", "12", "21", "14", "41", "24", "42"),
            "369"       to   listOf("339","366","399","658","636","258","268","669","668","266","369","398","256","296","259","368","638","396","238","356","659","639","666","359","336","299","338","696","269","358","656","698","699","298","236","239")
        ).forEach { assertEquals(it.second.sorted(), getPINs(it.first).sorted(),
            message="Come on, detective! You can do it better! These are not the PIN variations for '${it.first}'") }
    }

}

class ObservedPinTest2{
    @Test
    fun combinations(){
        println(combinations( listOf(
            listOf("1","2","3"),
            listOf("1","2"),
        )))
    }

    fun getKeyPadSet(key:Char):List<String>{
        return when(key){
            '1' -> listOf("1","2","4")
            '2' -> listOf("1","2","3","5")
            '3' -> listOf("2","3","6")
            '4' -> listOf("1","4","5","7")
            '5' -> listOf("2","4","5","6","8")
            '6' -> listOf("3","5","6","9")
            '7' -> listOf("4","7","8")
            '8' -> listOf("5","7","8","9","0")
            '9' -> listOf("6","8","9")
            '0' -> listOf("0","8")
            else -> throw AssertionError("Invalid key input value ${key}")
        }

    }
    fun combinations(list: List<List<String>>) : List<String>{
        if(list.size == 1)
            return list[0]
        var outlist = mutableListOf<String>()
        list[0].forEach{s1 ->
            combinations(list.subList(1,list.size))
                .forEach{s2 -> outlist.add(s1 + s2)}
        }
        return outlist
    }

    fun getPINs(observed: String): List<String>{
        return combinations(observed.map { getKeyPadSet(it) })
    }
    @Test
    fun sample_tests() {
        setOf(
            "8"         to   listOf("5","7","8","9","0"),
            "11"        to   listOf("11", "22", "44", "12", "21", "14", "41", "24", "42"),
            "369"       to   listOf("339","366","399","658","636","258","268","669","668","266","369","398","256","296","259","368","638","396","238","356","659","639","666","359","336","299","338","696","269","358","656","698","699","298","236","239")
        ).forEach { assertEquals(it.second.sorted(), getPINs(it.first).sorted(),
            message="Come on, detective! You can do it better! These are not the PIN variations for '${it.first}'") }
    }



    class NextBigNumber {

        fun maxSequence1(arr: List<Int>): Int {
            if(arr.isEmpty())
                return 0
            if(arr.size == 1)
                return arr[0]

            var max = 0
            var tmpMax = 0
            for (i in 0 until arr.size) {
                tmpMax = arr[i]
                for (j in i + 1 until arr.size) {
                    tmpMax += arr[j]
                    if (max < tmpMax) {
                        max = tmpMax
                    }
                }
            }
            return Math.max(max,0)
        }
        fun maxSequence(arr: List<Int>): Int {
            return Math.max(arr.mapIndexed { i, item ->
                arr.subList(i, arr.size)
                    .mapIndexed { j, item -> arr.subList(i, arr.size - j) }
                    .reduce { acc, ints -> listOf(Math.max(acc.sum(), ints.sum())) }
            }.maxByOrNull { ints -> ints[0] }?.get(0) ?: 0, 0)

        }

        fun sequences(arr: List<Int>, max:Int) : Int{
            if(arr.size == 1)
                return arr[0]
//            arr.forEachIndexed {index, i ->  Math.max(index,sequences(index)) }
            return 0
        }
        @Test
        fun `it should work on an empty list`() {
            assertEquals(0, maxSequence(emptyList()))
        }

        @Test
        fun `it should work on the example`() {
            assertEquals(6, maxSequence(listOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)))
        }

    }

}

class NextBigNumber {

    fun combinations(items: List<Any>) : MutableList<List<Any>> {
        var outputList = mutableListOf<List<Any>>()
        if(items.size < 2)
            outputList.add(items)
        else if( items.size == 2){
            outputList.add(items)
            outputList.add(items.reversed())
        }
        else {
            items.forEachIndexed { index, item ->
                combinations(items.subList(0, index) + items.subList(index + 1, items.size))
                    .forEach { outputList.add(listOf(item) + it) }
            }
        }
        return outputList
    }


    fun combinations(str: String) : MutableList<String> {
        var outputList = mutableListOf<String>()
        if(str.length < 2)
            outputList.add(str)
        else if (str.length == 2) {
            outputList.add(str)
            outputList.add(str.reversed())
        } else {
            val combinations = combinations(str.substring(1))
            combinations.forEach { item ->
                str.forEachIndexed { index, c ->
                    outputList.add(StringBuilder(item).insert(index, str[0]).toString())
                }
            }
        }
        return outputList
    }

    fun factorial(num: Int):Int{
        var _num = num
        var result = 1
        if(num <= 1)
            return 1
        while(_num > 1)
            result *= _num--
        return result
    }


//val combinationMap = combinations((0 .. 9).toList().reversed())

    fun <T> MutableList<T>.swap(i:Int, j:Int) : MutableList<T> {
        val tmp: T = this[i]
        this[i] = this[j]
        this[j] = this[i]
        return this
    }
    fun String.swap(i:Int, j:Int):String {
        this.toCharArray()
            .run {
                val tmpChar = this[i]
                this[i] = this[j]
                this[j] = tmpChar
                return String(this)
            }
    }


    @Deprecated("WRONG")
    fun nextBiggerNumberWRONG(n: Long): Long {
        var strNum = Math.abs(n).toString()
        var candidates = mutableListOf<Long>()

        var distance = Long.MAX_VALUE
        var candidate = n
        try {
            strNum.mapIndexed { i, c ->
                for (j in i downTo 0) {
                    val tmp = strNum.swap(i,j).toLong()
                    if(Math.abs(tmp - n) < distance && tmp > n){
                        candidate = tmp
                        distance = Math.abs(tmp - n)
                    }

//                if (n > 0 && strNum[j] < c || n < 0 && strNum[j] > c)
//                    candidates.add(strNum.swap(i, j).toLong())
                }
            }
            return if (candidate == n) -1 else candidate
//        return candidates.sortedWith(Comparator<Long>{o1: Long?, o2: Long? ->
//            when {
//                o1!! > o2!! -> 1
//                o1 < o2 -> -1
//                else -> 0
//            }.run { if (n<0) this * -1 else this}
//        }).reduce { acc, l -> if (l == n) l else if (acc == n) l else acc }
//            .run { if(n < 0 ) this * -1 else this }

        }catch (e:Exception){
            return -1
        }


    }

@Deprecated("WRONG")
fun nextBiggerNumberWRONG1(n: Long): Long {


//    try {
//        if(n < 10)
//            return -1
//        val combinationLenght = combinationMap.first().size
//        val variants = mutableListOf<List<Char>>()
//        val numberArray = n.toString().toCharArray()
//
//        val combinationsNumber = factorial(numberArray.size)
////    if(combinationsNumber > combinationMap.size) {
////        throw ArgumentAccessException("Combination map is too small! ${combinationsNumber}")
////    }
//
//        for (vIndex in 0 until combinationsNumber) {
//            val tmpList = mutableListOf<Char>()
//            for (i in numberArray.indices) {
//                tmpList.add(numberArray[combinationMap[vIndex][combinationLenght - i - 1] as Int])
//            }
//            variants.add(tmpList)
//        }
//        return variants.map { it.joinToString("").toLong() }
//            .sorted()
//            .also { if (it.last() == n) return -1 }
//            .reduce { acc, l -> if (l == n) l else if (acc == n) l else acc }
//    }catch (e:Exception){
//        return -1
//    }



//    try {
//        return combinations(Math.abs(n)
//            .toString().toList())
//            .map { it.joinToString("").toLong() }
//            .sorted()
//            .also { if(it.last() == n) return  -1 }
//            .reduce { acc, l -> if (l == n) l else if (acc == n) l else acc}
//    }catch (e:Exception){
//        return -1
//    }



    return combinations(n.toString())
        .map { it.toLong() }
        .sorted()
        .also { if(it.last() == n) return  -1 }
        .reduce { acc, l -> if (l == n) l else if (acc == n) l else acc}
}

    fun nextBiggerNumber(n: Long): Long {

        return -1
    }

    @Test
    fun basicTests() {
        val start = System.currentTimeMillis()

//        assertEquals(-1, nextBiggerNumber(9))
//        assertEquals(21, nextBiggerNumber(12))
//        assertEquals(-1, nextBiggerNumber(111))
//        assertEquals(531, nextBiggerNumber(513))
////        assertEquals(-513, nextBiggerNumber(-531))
//        assertEquals(2071, nextBiggerNumber(2017))
//        assertEquals(441, nextBiggerNumber(414))
//        assertEquals(414, nextBiggerNumber(144))
//        assertEquals(9000000021, nextBiggerNumber(9000000012))



//        assertEquals(1835656885, nextBiggerNumber(1835656858))
//
//        assertEquals(1978192496, nextBiggerNumber(1978192469))

        assertEquals(1234567098, nextBiggerNumber(1234567890))



//
        val end = System.currentTimeMillis()
        println("DEBUG: Logic A took " + (end - start) + " MilliSeconds")


    }
}
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

    val cache = mutableMapOf<Pair<BigInteger,BigInteger>,BigInteger>()
    fun getCachedValueOrCall(n: BigInteger, m: BigInteger):BigInteger{
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

        val difference = height(BigInteger.valueOf(n),BigInteger.valueOf(m)).toInt()
        var arr = mutableListOf<Int>()
        for(index in m+1 .. 20)
            arr.add(height(BigInteger.valueOf(n),BigInteger.valueOf(index)).toInt() - difference)
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

class SortDistinct(){

    fun countDistinct(text:String) : Int{
        return text.uppercase().toCharArray()
            .groupBy { it }
            .mapNotNull { if(it.value.size > 1) it else null }
            .count()
    }

    @Test
    fun testDistrinct(){
        assertEquals(0,countDistinct("abcdefg"))
        assertEquals(1, countDistinct("abcdefga"))
        assertEquals(2, countDistinct("ab11cdefga"))
    }

}
class SpinWords{
    fun spinWords(text:String): String{
        return text.split(' ')
            .map { if(it.length >= 5) it.reversed() else it}
            .joinToString ( " " )
    }
    @Test
    fun test(){
        assertEquals("Hey wollef sroirraw",  spinWords("Hey fellow warriors"))
        assertEquals("This is a test",  spinWords("This is a test"))
        assertEquals("This is rehtona test",  spinWords("This is another test"))
    }

}
class CompleteBinaryTree{

    fun completeBinaryTree(arr:IntArray):IntArray{
        if (arr.size == 1) return arr
        if (arr.size == 2) return intArrayOf(arr.get(1), arr.get(0))

        val n = 1 shl Math.ceil(Math.log(arr.size.toDouble()) / Math.log(2.0)).toInt()
        val arrayList: MutableList<Int> = arr.toMutableList()

        val result  = mutableListOf<Int>()

        var j = 0
        while (!arrayList.isEmpty()) {
            var i = if (j == 0) arrayList.size - (n - arrayList.size) else arrayList.size - 1
            while (i >= 0) {
                result.add(0, arrayList[i])
                arrayList.removeAt(i)
                i -= 2
            }
            j++
        }

        return result.toIntArray()
    }
    fun completeBinaryTree1(arr:IntArray):IntArray{
            val n = 1 shl Math.ceil(Math.log(arr.size.toDouble()) / Math.log(2.0)).toInt()

            val tmpList = arr.toMutableList()

            val output = mutableListOf<List<Int>>(
                (tmpList.mapIndexedNotNull { index, i -> if (index < tmpList.size - (n - tmpList.size - 1) && index % 2 == 0) i else null }
                    .also { mapped -> tmpList.removeIf { it in mapped } })
            )
            while (tmpList.isNotEmpty()) {
                output.add(
                    (tmpList.mapIndexedNotNull { index, i -> if (index % 2 == 0) i else null })
                        .also { mapped2 -> tmpList.removeIf { it in mapped2 } })
            }
            var tmp =  output.reversed().flatten().toIntArray()
        if(tmp.size != arr.size)
            println(tmp.toList().mapNotNull { if(it in arr) null else it })
        return tmp
    }
    fun bfs(arr: IntArray) : IntArray{
        if(arr.size <= 1)
            return arr
        val output:IntArray = IntArray(arr.size)
        val indexQueue:Queue<Pair<Int,Int>> = LinkedList<Pair<Int,Int>>()

        val n = Math.ceil(Math.log(arr.size.toDouble())/Math.log(2.0))
        val rootIndex =  arr.size - (Math.pow(2.0,n-2).toInt() - 1) - 1

        output[0] = arr[rootIndex]
        var indexArray = 1
        indexQueue.add(Pair(0,rootIndex))
        indexQueue.add(Pair(rootIndex + 1, arr.size))

        while (indexQueue.isNotEmpty()){
            var itemCount = indexQueue.size
            do {
                var i = indexQueue.poll()!!
                var rootIndex = i.first + (i.second - i.first) / 2
                output[indexArray++] = arr[rootIndex]

                if(i.first != rootIndex)
                    indexQueue.add(Pair(i.first, rootIndex))
                if(i.second != rootIndex + 1)
                    indexQueue.add(Pair(rootIndex + 1, i.second))

            }while (--itemCount > 0)
        }

        return output
    }
    @Test
    fun `test single node tree`() {
        val input = intArrayOf(1)
        val expected = intArrayOf(1)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with two nodes`() {
        val input = intArrayOf(1, 2)
        val expected = intArrayOf(2, 1)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with six nodes`() {
        val input = intArrayOf(1, 2, 3, 4, 5, 6)
        val expected = intArrayOf(4, 2, 6, 1, 3, 5)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with ten nodes`() {
        val input = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expected = intArrayOf(7, 4, 9, 2, 6, 8, 10, 1, 3, 5)
        assertContentEquals(expected, completeBinaryTree(input))
    }
    @Test
    fun randomTest(){
        for(i in 0 .. 100) {
            val length = 8000 + (Math.random() * 1000).toInt()
            val tree = completeBinaryTree((1..length).shuffled().toList().toIntArray())
            assertEquals(length,tree.size)

        }
    }
}

class StringIncreaser{

    fun incrementString(str:String):String{
        var isNegative = BigInteger.ONE
        return "(.*?)(-?)([\\d]*)\$".toRegex().find(str)
            ?.groupValues
            ?.mapIndexedNotNull { index, value ->
                if(index == 0)
                    null

                else if(index == 2) {
                    if(value == "-") {
                        isNegative = -BigInteger.ONE
                        value
                    }else
                        null
                }else if(index == 3){
                    if(value.isBlank())
                        "1"
                    else {
                        val tmp = value.toBigInteger().add(BigInteger.ONE.multiply(isNegative)).toString()
                        if (tmp.length >= value.length)
                            tmp
                        else
                            value.replaceRange(value.length - tmp.length, value.length, tmp)
                    }
                }
                else
                    value
            }!!.joinToString("")

    }



    @Test
    fun FixedTests() {
        assertEquals(incrementString("foobar000"), "foobar001")
        assertEquals(incrementString("foobar999"), "foobar1000")
        assertEquals(incrementString("foobar00999"), "foobar01000")
        assertEquals(incrementString("foo"), "foo1")
        assertEquals(incrementString("foobar001"), "foobar002")
        assertEquals(incrementString("foobar1"), "foobar2")
        assertEquals(incrementString("1"), "2")
        assertEquals(incrementString(""), "1")
        assertEquals(incrementString("009"), "010")
        assertEquals("fooo-0098",incrementString("fooo-0099"))
    }
}

class Int32ToIPv4{
    fun Int32ToIPv4(ip: UInt):String{
       return mutableListOf<UInt>()
            .also {
                val bitSet:UInt = 255u
                for(i in 0 until 4)
                    it.add(0,(ip and (bitSet shl i*8)) shr i*8 )
            }.joinToString ( "." )

    }
    @Test
    fun test(){
        assertEquals("128.32.10.1",Int32ToIPv4(2149583361u))
    }
}

