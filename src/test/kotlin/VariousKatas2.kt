import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockSequence {


    fun S_n(a1:Long, d:Long, n:Long):Long{
        return ((2*a1 + d * (n - 1)) * n) / 2
    }
    fun A_n(a1:Long, d:Long, n:Long):Long{
        return a1 + d * (n - 1)
    }


    fun findN(n:Int, multyplayer:Int):Int{
        if(n - 9 * multyplayer > 0)
            return findN(n - 9 * multyplayer,multyplayer*10 + 1)
        else {

            val digits = multyplayer.toString().length
            var a_n = if(digits == 1) n.toLong() else (n / digits + n%digits).toLong()
            val reminder = (if(n%digits == 0) digits else n%digits) - 1
            val A_n =  A_n(Math.pow(10.0,(digits -1).toDouble()).toLong(), 1, a_n)
            println("N = $n n = $a_n a_n=$A_n reminder=$reminder")
                return A_n.toString()[reminder].digitToInt()

        }
    }
    fun chuncksSum(a1:Long, d:Long, a_n:Long, prev_a_nSum:Long ):Long{
       return ((prev_a_nSum * (a_n - a1 + 1)) + S_n(1,1,(a_n - a1 +1))*d)
    }
    fun solve(n:Long):Int{
        var a1 = 1L
        var d = 1L
        var a_n = 9L
        var prev_a_nSum = 0L
        var current_a_nSum = 0L
        var tmpN = n
        var N = n
        while (tmpN > 0){
            N = tmpN
            current_a_nSum = prev_a_nSum
            tmpN -= chuncksSum(a1,d,a_n,prev_a_nSum)
            prev_a_nSum += (a_n - a1 + 1) * d
            println("Sum ${prev_a_nSum}")
            a1 *= 10
            d++
            a_n = a1 *10 - 1
        }
        val prevChunkSum = getPrevChunksSum(a1/10,d -1,a1/10,a_n/10, current_a_nSum,N)
        return findN((N - prevChunkSum).toInt(), 1)
    }

    fun getPrevChunksSum(a1: Long, d:Long, a_nLeft: Long, a_nRight: Long, current_a_nSum:Long, n:Long, ):Long{
        var m = (a_nLeft+a_nRight)/2L
        var midSum = chuncksSum(a1,d,m,current_a_nSum)

        if(a_nLeft >= a_nRight) {
            while (midSum >= n)
                midSum = chuncksSum(a1,d,m-1,current_a_nSum)
            return midSum
        }

        if(midSum > n)
            return getPrevChunksSum(a1, d, a_nLeft,m - 1, current_a_nSum,n)
        else
            return getPrevChunksSum(a1, d, m + 1, a_nRight, current_a_nSum,n)

    }
    private fun runTest(n:Long,sol:Int) = assertEquals(sol,solve(n))

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
//        runTest(98L,1)
//        runTest(99L,2)
//        runTest(100L,1)
//        runTest(101L,3)
//        runTest(2100L,2)
//        runTest(31000L,2)
//        runTest(999999999999999999L,4)
    //        runTest(999999999999999999L,0) //CUSTOM
//        runTest(1000000000000000000L,1)
        runTest(999999999999999993L,7)
        //        runTest(1000000000000000001L,1) //CUSTOM
        //        runTest(1000000000000000002L,2) //CUSTOM
        //        runTest(1000000000000000003L,3) //CUSTOM

    }
    fun solveNew(n:Long):Int{
        var a1 = 1L
        var d = 1L
        var a_n = 9L
        var N = n
        var tmpN = n
        var A_nSum = 0L
        var prev_a_nSum = 0L
        while (tmpN > 0){
            N = tmpN
            tmpN -= ((prev_a_nSum * (a_n - a1 + 1)) + S_n(1,1,(a_n - a1 +1))*d)
            prev_a_nSum += (a_n - a1 + 1) * d
            println("Sum ${prev_a_nSum}")
            a1 *= 10
            d++
            a_n = a1 *10 - 1
        }
//        println()
//        N = n
        var border = a1/10 /*1L*/
        var sum = 0
        while (true){
//            print("[")
            sum = 0
            for (i in 1 ..  border) {
//                print("$i,")
                N -= i.toString().length
                sum++
                if(N == 0L)
                    return (i%10).toInt()
                else if(N < 0) {
                    return (i / (10 * -N)).toInt()
                }
            }
            border++
        }
//        println()
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

    fun solve2(n: Long): Int {
        val gI = floor((1L + sqrt(1.0 + 8.0 * (n - 1.0)))/2).toLong()
        return (n - ((gI - 1L) * gI)/2).toInt()
    }

    fun i_from_S_n(a1:Long, d:Long, S_n:Long):Long{
        return ((d + 2*a1) + Math.floor(Math.sqrt(Math.pow((2*a1-d).toDouble(),2.0) + 8 * S_n * d)).toLong())/(2 * d)

    }

    fun findNOld1(n: Int) :Int{
        println("Entering findN")
        var newN = n
        var sequenceStart = 1
        var multiplayer = 1
        var tmp = n
        var m2 = 1

        while(true){
            tmp -= 9 * multiplayer
            if(tmp < 1) {
                break
            }
            newN = tmp
            multiplayer = multiplayer * 10 + 1
            m2 *= 10
            sequenceStart *= 10
        }

        if(m2 > 1 && newN > m2) {
            sequenceStart += (newN / multiplayer.toString().length)
            newN = newN %  multiplayer.toString().length
        }
//        while (newN - multiplayer > 0) {
//            newN -= multiplayer
//            sequenceStart+=multiplayer
//        }


//        val k =  Math.floor((-1 + Math.sqrt(1.0 + 8.0*n/9))/2.0).toInt()
//        val newN = n - 9*(k + 1)*k/2
//        val multiplayer = if(k > 0) 10 * k.toString().length else 1
        return (sequenceStart .. 9*multiplayer).joinToString("")[newN - 1].digitToInt()
    }
    fun findNOld(n:Int) : Int{
        var strBuilder = StringBuilder()
        var start = 1
        while (true){
            strBuilder.append((start .. Math.min(9,n-start) * start).joinToString (""))
//                .append("][")

            start *=10
            if(strBuilder.length >= n)
                break
        }
        return strBuilder.toString()[n-1].digitToInt()
    }
}