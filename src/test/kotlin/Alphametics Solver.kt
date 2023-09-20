import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Alphametics Solver` {
    //https://www.codewars.com/kata/5b5fe164b88263ad3d00250b

    fun alphametics(puzzle: String): String {
        // your code goes here. you can do it!
        return ""
    }
    private fun runTest(puzzle:String,sol:String) = assertEquals(sol,alphametics(puzzle))

    @Test
    fun `Example Tests`() {
        runTest("SEND + MORE = MONEY","9567 + 1085 = 10652")
        runTest("ZEROES + ONES = BINARY","698392 + 3192 = 701584")
        runTest("COUPLE + COUPLE = QUARTET","653924 + 653924 = 1307848")
        runTest("DO + YOU + FEEL = LUCKY","57 + 870 + 9441 = 10368")
        runTest("ELEVEN + NINE + FIVE + FIVE = THIRTY","797275 + 5057 + 4027 + 4027 = 810386")
    }

}