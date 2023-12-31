import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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