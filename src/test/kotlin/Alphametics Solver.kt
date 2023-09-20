import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Alphametics Solver` {
    //https://www.codewars.com/kata/5b5fe164b88263ad3d00250b

    //Up to 10 letters
    fun combinations (len:Int, from:List<Int>):List<IntArray>{
        var results = mutableListOf<IntArray>()
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


    fun alphametics(puzzle: String): String {
        // your code goes here. you can do it!
        return ""
    }
    private fun runTest(puzzle:String,sol:String) = assertEquals(sol,alphametics(puzzle))


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
        runTest("SEND + MORE = MONEY","9567 + 1085 = 10652")
        runTest("ZEROES + ONES = BINARY","698392 + 3192 = 701584")
        runTest("COUPLE + COUPLE = QUARTET","653924 + 653924 = 1307848")
        runTest("DO + YOU + FEEL = LUCKY","57 + 870 + 9441 = 10368")
        runTest("ELEVEN + NINE + FIVE + FIVE = THIRTY","797275 + 5057 + 4027 + 4027 = 810386")
    }

}