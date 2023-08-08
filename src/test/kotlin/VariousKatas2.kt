import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockSequence {
    fun solve(n: Long): Int {
        val gI = floor((1L + sqrt(1.0 + 8.0 * (n - 1.0)))/2).toLong()
        return (n - ((gI - 1L) * gI)/2).toInt()
    }

    fun S_n(a1:Long, d:Long, n:Long):Long{
        return (2*a1 + d *(n - 1)) * n / 2
    }
    fun A_n(a1:Long, d:Long, n:Long):Long{
        return a1 + d * (n - 1)
    }
    fun i_from_S_n(a1:Long, d:Long, S_n:Long):Long{
       return ((d + 2*a1) + Math.floor(Math.sqrt(Math.pow((2*a1-d).toDouble(),2.0) + 8 * S_n * d)).toLong())/(2 * d)

    }
    fun findN(n:Int) : Int{
        var strBuilder = StringBuilder()
        var start = 1
        var counter = 0
        while (true){
            strBuilder.append((start .. Math.min(9,n-start) * start).joinToString (""))
//                .append("][")

            start *=10
            if(strBuilder.length >= n)
                break
        }
        return strBuilder.toString()[n].digitToInt()
    }
    fun solve2(n:Long):Int{
        var a1 = 1L
        val d = 1L
        var next_n = 1L
        var prev_a_nSum = 0L
        var checkSum = 0L
        var lastSum = 0L
        var sumMultiplier = 0L
        do{
            lastSum = checkSum
            prev_a_nSum += A_n(a1,d,next_n -1) * sumMultiplier
            a1 = next_n
            next_n *=10
            sumMultiplier++
            checkSum = lastSum + S_n(1,d,next_n - 1) * sumMultiplier
        }while (checkSum < n)

        val result = findCunckIndex(a1,d,prev_a_nSum, n - lastSum, 0, next_n -1L)
        return findN((n - lastSum - result.first - 1).toInt())
    }
    fun findCunckIndex(a1:Long, d:Long, prev_a_nSum:Long, n:Long, left: Long, right:Long):Pair<Long,Long>{
        val sumMultilayer = a1.toString().length
        val interval = Pair((prev_a_nSum * left) + S_n(1,d,left) * sumMultilayer,(prev_a_nSum * (left+1)) + S_n(1,d,left + 1)*sumMultilayer)
        if(n > interval.first && n <= interval.second )
            return Pair(interval.first,left + 1)

        if(n > interval.second)
            return  findCunckIndex(a1, d, prev_a_nSum, n, (left+right)/2, right)
        else
            return findCunckIndex(a1,d,prev_a_nSum, n,left/2,left)
    }
    fun solve0(n:Long):Int{
        var n1 = n
        var runningNumber = 0
        print("[")
        while (true){
            runningNumber++
            for(v in 1 .. runningNumber) {
                print("${v}")
                n1 -= 1 * (if(v/10 > 0) 2 else 1)
                if(n1 <= 0L)
                    return v
            }
            print(",")
        }
    }
    private fun runTest(n:Long,sol:Int) = assertEquals(sol,solve2(n))

    @Test fun someTests() {
//        runTest(1L,1)
//        runTest(2L,1)
//        runTest(3L,2)
//        runTest(7L,1)
//        runTest(8L,2)
//        runTest(9L,3)
//        runTest(10L,4)
//        runTest(11L,1)
//        runTest(37,1)
//        runTest(45,9)
//        runTest(46,1)
//        runTest(47,2)
//        runTest(48,3)
//        runTest(49,4)
//        runTest(50,5)
//        runTest(51,6)
        runTest(100L,1)
//        runTest(2100L,2)
//        runTest(31000L,2)
//        runTest(999999999999999999L,4)
//        runTest(1000000000000000000L,1)
//        runTest(999999999999999993L,7)
    }
}