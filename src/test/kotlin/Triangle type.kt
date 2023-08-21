import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class `Triangle type` {

    //https://www.codewars.com/kata/53907ac3cd51b69f790006c5/kotlin

    //Could be done by evaluating sorted a^2 + b^2 - c^2 -> == 0: 90, < 0:  acute ᐃ, > 0 obtuse ᐃ

    fun evaluateAngles(edges:DoubleArray):DoubleArray{
        var tmpValue = 0.0
        var angles = DoubleArray(edges.size)
        var _edges = edges
        for(i in _edges.indices) {
            var (a, b, c) = _edges
            angles[i] = Math.acos((Math.pow(a, 2.0) + Math.pow(b, 2.0) - Math.pow(c, 2.0)) / (2.0 * a * b)) * 180.0/Math.PI
            _edges = _edges.mapIndexed { index, d ->
                if (index == 0) {
                    tmpValue = d; _edges[index + 1]
                } else if (index == _edges.lastIndex){
                    tmpValue
                } else
                    _edges[index + 1]
            }.toDoubleArray()
        }
        return angles
    }

    fun triangleType(a: Double, b: Double, c: Double): Int {
        var angles = evaluateAngles(doubleArrayOf(a,b,c))
        return when{
            angles.any{ it.isNaN() } -> 0
            angles.all { it < 90.0 } -> 1
            angles.any { it == 90.0 } -> 2
            angles.any { it > 90.0 } -> 3
            else -> -1
        }
    }

    @Test
    fun testExample() {
//        doTest(Triple(7.0, 3.0, 2.0), 0)  // Not triangle
//        doTest(Triple(2.0, 4.0, 6.0), 0)  // Not triangle
//        doTest(Triple(8.0, 5.0, 7.0), 1)  // Acute
//        doTest(Triple(3.0, 4.0, 5.0), 2)  // Right
//        doTest(Triple(7.0, 12.0, 8.0), 3) // Obtuse
//        doTest(Triple(3.0, 3.0, 1.0), 1)
        doTest(Triple(1.0, 2.0, 3.0), 3)

    }
    private fun doTest(sides: Triple<Double,Double,Double>, expected: Int) {
        val (a, b, c) = sides
        val types = arrayOf("INVALID", "ACUTE", "RIGHT", "OBTUSE")
        val msg = "Sides = (${a}, ${b}, ${c}), type = ${types[expected]}"
        assertEquals(expected, triangleType(a, b, c), msg)
    }
}