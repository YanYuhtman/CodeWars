import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Range Extraction` {
    //https://www.codewars.com/kata/51ba717bb08c1cd60f00002f/train/kotlin
    fun rangeExtraction(arr: IntArray): String {
        return ""
    }
    @Test
    fun basicTests() {
        assertEquals("-6,-3-1,3-5,7-11,14,15,17-20", rangeExtraction(intArrayOf(-6, -3, -2, -1, 0, 1, 3, 4, 5, 7, 8, 9, 10, 11, 14, 15, 17, 18, 19, 20)))
        assertEquals("-3--1,2,10,15,16,18-20", rangeExtraction(intArrayOf(-3, -2, -1, 2, 10, 15, 16, 18, 19, 20)))
    }
}