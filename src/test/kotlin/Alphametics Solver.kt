import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Alphametics Solver` {
    //https://www.codewars.com/kata/5b5fe164b88263ad3d00250b

    //Up to 10 letters
    fun combinations (len:Int, from:List<Int>):List<IntArray>{
        val results = mutableListOf<IntArray>()
        from.forEach { component ->
            if(len > 1)
                combinations(len - 1, from.mapNotNull { v -> if (v != component) v else null })
                    .forEach {
                        results.add(it + component)
                    }
            else
                results.add(intArrayOf(component))
        }
        return results
    }
    fun getSumVariations(sumChars: List<Char>, sum:Int, addition: Int, from: List<Int>):List<Pair<Map<Char,Int>,Int>>{
        val addition = addition + sumChars.sumOf { if(it.isDigit()) it.digitToInt() else 0 }
        val sumCharsMap = sumChars.filter { it.isLetter() }.groupBy { it }.map { it.key to it.value.size }

        val combinations = combinations(sumCharsMap.size,from)

        val result:MutableList<Pair<Map<Char,Int>,Int>> = mutableListOf()

        combinations.forEach {combination->
            val tmp = sumCharsMap.zip(combination.toList()).map { it.first.first to intArrayOf(it.first.second,it.second) }.toMap()
            val sumOf  = tmp.values.sumOf { it[0]*it[1] } + addition
            if(sumOf % 10 == sum)
                result.add(tmp.map { (it.key to it.value[1])}.toMap() to sumOf/10)
        }
        return result
    }


    fun alphametics(puzzle: String, digits:List<Int>,results:MutableList<String>, rIndex:Int = 1, addition: Int = 0){
        val words = puzzle.split("\\s*[+=]\\s*".toRegex())
        if (words.any { it.startsWith('0') })
            return
        if(rIndex > words.last().length) {
            if(words.sumOf { if(it === words.last()) 0 else it.toInt() } == words.last().toInt())
                results.add(puzzle)
            return
        }

        val eqChar = puzzle.let { it[it.length - rIndex] }
        val _digits = if(eqChar.isLetter()) digits else listOf(eqChar.digitToInt())
        for (digit in _digits){
            val tmpDigits = digits.toMutableList().apply { this.remove(digit) }

            val _puzzle = puzzle.replace(eqChar,digit.digitToChar())
            val sumChars = _puzzle.split("\\s*[+=]\\s*".toRegex())
                .let{words->words.mapNotNull { if(words.last() === it || it.length < rIndex) null else it[it.length - rIndex] }}

            val replacementChars = sumChars.mapNotNull { if(it.isLetter()) it else null }
            val additions = sumChars.sumOf { if(it.isDigit()) it.digitToInt() else 0 } + addition
            if(replacementChars.isEmpty() && additions % 10 == digit)
                alphametics(_puzzle, tmpDigits, results, rIndex + 1, additions / 10)
            else {
                val sumVariations = getSumVariations(replacementChars,digit,additions,tmpDigits)
                sumVariations.forEach { values ->
                    val outDigits = tmpDigits.toMutableList().apply { values.first.forEach { this.remove(it.value) } }
                    var tmpPuzzle = _puzzle
                    values.first.forEach { k,v-> tmpPuzzle = tmpPuzzle.replace(k,v.digitToChar()) }
                    alphametics(tmpPuzzle, outDigits, results,rIndex + 1, values.second)
                }
            }
        }
    }

    fun alphametics(puzzle: String): String {
        val results = mutableListOf<String>()
        alphametics(puzzle, (0..9).toList(),results)
        return results[0]
    }
    private fun runTest(puzzle:String,sol:String) = assertEquals(sol,alphametics(puzzle))

    @Test
    fun testVariations2(){
        val _testVariations:(expectedItems:Int,items:List<Pair<Map<Char,Int>,Int>>)->Unit = {expectedItems, items ->
            println( items.map { "${it.first.map { "${it}" }},(${it.second})" })
            assertEquals(expectedItems,items.size)
        }
//        _testVariations(6,getSumVariations("ABC".toList(),6,0, listOf(1,2,3)))
//        _testVariations(0,getSumVariations("ABC".toList(),6,0, listOf(1,2)))
//        _testVariations(1,getSumVariations("AAC".toList(),5,0, listOf(1,2)))
//        _testVariations(2,getSumVariations("AABB".toList(),7,1, listOf(1,2)))
//        _testVariations(12,getSumVariations("AABCD".toList(),7,0, listOf(0,1,2,3,4,5)))

        _testVariations(4,getSumVariations("AB".toList(),1,0, listOf(9,2,1,0)))
    }


    @Test
    fun testCombinations(){
        val _testCombinations:(expectedItems:Int,items:List<IntArray>)->Unit = {expectedItems, items ->
            assertEquals(expectedItems,items.size)
            println( items.map { "${it.map { "$it" }}" })
        }
        _testCombinations(2,combinations(2, listOf(0,1)))
        _testCombinations(6,combinations(2, listOf(0,1,2)))
        _testCombinations(20,combinations(2, listOf(0,1,2,3,4)))
        _testCombinations(60,combinations(3, listOf(0,1,2,3,4)))
    }

    @Test
    fun `Example Tests`() {
//        runTest("SEND = SEND", "3210 = 3210")
        runTest("SEND + MORE = MONEY","9567 + 1085 = 10652")
        runTest("ZEROES + ONES = BINARY","698392 + 3192 = 701584")
        runTest("COUPLE + COUPLE = QUARTET","653924 + 653924 = 1307848")
        runTest("DO + YOU + FEEL = LUCKY","57 + 870 + 9441 = 10368")
        runTest("ELEVEN + NINE + FIVE + FIVE = THIRTY","797275 + 5057 + 4027 + 4027 = 810386")
    }

}