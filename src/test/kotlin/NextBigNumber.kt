import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NextBigNumber {

    //TODO: The solution is not optimal. The best solution is to find a number to swap and reorder all digits after it
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