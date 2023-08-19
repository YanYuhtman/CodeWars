import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals


class `Perimeter of squares in a rectangle` {
//    https://www.codewars.com/kata/559a28007caad2ac4e000083

    fun perimeter0(n: Int) = (1..n).toList()
        .fold(mutableListOf(0, 1)) { acc, i -> acc.add(acc[acc.lastIndex - 1] + acc[acc.lastIndex]);acc }
        .reduce { acc, i -> acc + i } * 4



    class FibMatrix(rawMat: Array<Array<BigInteger>>?){
        constructor() : this(null)
        private val _rawMatrix = rawMat ?: _init
        companion object {
            val INIT = FibMatrix(_init)
            private val _init: Array<Array<BigInteger>>
                get() = arrayOf(arrayOf(BigInteger.ZERO, BigInteger.ONE)
                    ,arrayOf(BigInteger.ONE, BigInteger.ONE))
        }

        operator fun get(i:Int,j:Int) :BigInteger{
            return _rawMatrix[i][j]
        }

        operator fun times(otherMat: FibMatrix) = FibMatrix(arrayOf(
            arrayOf(this[0,0] * otherMat[0,0] + this[0,1] * otherMat[1,0],  this[0,0] * otherMat[0,1] + this[0,1] * otherMat[1,1]),
            arrayOf(this[1,0] * otherMat[0,0] + this[1,1] * otherMat[1,0],  this[1,0] * otherMat[0,1] + this[1,1] * otherMat[1,1]))
        )
        fun pow(n:Int):FibMatrix = (1..n).toList().fold(FibMatrix(this._rawMatrix)){acc, i -> this*this}

        val prev:BigInteger
            get() = this[0,0]

        val cur:BigInteger
            get() = this[0,1]

        val next:BigInteger
            get() = this[1,1]

        override fun toString(): String = _rawMatrix.map { values: Array<BigInteger> ->
            values.map { "%10s".format(it) }.joinToString ("\t")
        }.joinToString("\n") + "\n\n"
    }

    fun perimeter(n: Int):BigInteger{

        var fibMatrix = FibMatrix()
        var sum = BigInteger.TWO
        return when(BigInteger.valueOf(n.toLong() )){
            BigInteger.ZERO -> fibMatrix.prev
            BigInteger.ONE -> fibMatrix.cur
            else -> {
                for(i in 1 until  n) {
                    fibMatrix *= FibMatrix.INIT
                    sum += fibMatrix.next
                }
                sum
            }
        } * BigInteger.valueOf(4)
    }

    @Test
    fun fibMatrix(){
        var mat = FibMatrix()
        print(mat)

        mat = mat * FibMatrix.INIT
        print(mat)

        mat = mat.pow(2)

        print(mat)

    }
    @Test
    fun basicTests() {
        assertEquals(BigInteger.valueOf(80), perimeter(5))
        assertEquals(BigInteger.valueOf(216), perimeter(7))
        assertEquals(BigInteger.valueOf(14098308), perimeter(30))

    }
}