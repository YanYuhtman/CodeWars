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
    fun getSumVariations(addition:Int, len:Int, from:List<Int>, sum:Int) = combinations(len,from).filter { (it.sum() + addition) % 10 ==  sum}


    val resultVariants:MutableList<String> = mutableListOf()
    fun alphametics(puzzle: String, digits:List<Int>, rIndex:Int = 1, addition: Int = 0){
        val words = puzzle.split("\\s*[+=]\\s*".toRegex())
        if(rIndex > words.last().length) {
            if(words.sumOf { if(it === words.last()) 0 else it.toInt() } == words.last().toInt()) {
                resultVariants.add(puzzle)
            }
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
            if(replacementChars.isEmpty() && additions % 10 == digit) {
                alphametics(_puzzle, tmpDigits, rIndex + 1, additions / 10)
                println(_puzzle)
            }
            else {
                val sumVariations = getSumVariations(additions, replacementChars.size, tmpDigits, digit)
                sumVariations.forEach { values ->
                    val outDigits = tmpDigits.toMutableList().apply { values.forEach { this.remove(it) } }
                    var tmpPuzzle = _puzzle
                    val replacement = replacementChars.zip(values.toList()) { a: Char, b: Int -> a to b }.toMap()
                    replacement.forEach { tmpPuzzle = tmpPuzzle.replace(it.key, it.value.digitToChar()) }
                    alphametics(tmpPuzzle, outDigits, rIndex + 1, values.sum() / 10)
                    print(tmpPuzzle); print("\t" + outDigits);print("\t " + replacement);println("\t " + replacementChars)



                }
            }
        }
    }

    fun alphametics(puzzle: String): String {
        alphametics(puzzle, (0..9).toList())
        return resultVariants[0]
    }
    private fun runTest(puzzle:String,sol:String) = assertEquals(sol,alphametics(puzzle))


    @Test
    fun testVariations(){
        val _testVariations:(expectedItems:Int,items:List<IntArray>)->Unit = {expectedItems, items ->
            assertEquals(expectedItems,items.size)
            println( items.map { "${it.map { "$it" }}" })
        }
        _testVariations(2,getSumVariations(0,2, listOf(0,1),1))
        _testVariations(0,getSumVariations(0,2, listOf(1,2),1))
        _testVariations(1,getSumVariations(0,1, listOf(1),1))
        _testVariations(2,getSumVariations(1,2, listOf(0,1),2))
        _testVariations(2,getSumVariations(0,2, listOf(1,5,6),1))
        _testVariations(6,getSumVariations(0,2, listOf(0,1,2,3,4,5,6,7),1))
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
//        runTest("SEND + MORE = MONEY","9567 + 1085 = 10652")
        runTest("ZEROES + ONES = BINARY","698392 + 3192 = 701584")
//        runTest("COUPLE + COUPLE = QUARTET","653924 + 653924 = 1307848")
//        runTest("DO + YOU + FEEL = LUCKY","57 + 870 + 9441 = 10368")
//        runTest("ELEVEN + NINE + FIVE + FIVE = THIRTY","797275 + 5057 + 4027 + 4027 = 810386")
    }

}