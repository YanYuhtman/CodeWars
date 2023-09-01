import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

//https://www.codewars.com/kata/54bd79a7956834e767001357/kotlin

internal class XorWowRandom internal constructor(
    private var x: Int,
    private var y: Int,
    private var z: Int,
    private var w: Int,
    private var v: Int,
    private var addend: Int,
    private val dontRepeat: Boolean
) /*: Random(), Serializable */{

    internal constructor(seed1: Int, seed2: Int, dontRepeat: Boolean = false) :
            this(seed1, seed2, 0, 0, seed1.inv(), (seed1 shl 10) xor (seed2 ushr 4),dontRepeat)


    init {
        require((x or y or z or w or v) != 0) { "Initial state must have at least one non-zero element." }

        // some trivial seeds can produce several values with zeroes in upper bits, so we discard first 64
        if(!dontRepeat)
            repeat(64) { nextInt() }
    }

    /*override*/ fun nextInt(): Int {
        // Equivalent to the xorxow algorithm
        // From Marsaglia, G. 2003. Xorshift RNGs. J. Statis. Soft. 8, 14, p. 5
        var t = x
        t = t xor (t ushr 2)
        x = y
        y = z
        z = w
        val v0 = v
        w = v0
        t = (t xor (t shl 1)) xor v0 xor (v0 shl 4)
        v = t
        addend += 362437
        return t + addend
    }

//    override fun nextBits(bitCount: Int): Int =
//        nextInt().takeUpperBits(bitCount)

    private companion object {
        private const val serialVersionUID: Long = 0L
    }
}
internal fun doubleFromParts(hi26: Int, low27: Int): Double =
    (hi26.toLong().shl(27) + low27) / (1L shl 53).toDouble()


fun getSeedUniquifier(forwardTime:Long = 0):Long = seedUniquifier() xor (System.nanoTime() + forwardTime);
fun getSeedUniquifierWithCustomNanos(nanos:Long = 0):Long = (8682522807148012L * 1181783497276652981L) xor (nanos);

fun seedUniquifier(): Long {
    // L'Ecuyer, "Tables of Linear Congruential Generators of
    // Different Sizes and Good Lattice Structure", 1999
    while (true) {
        val current = seedUniquifier.get()
        val next = current * 1181783497276652981L
        if (seedUniquifier.compareAndSet(current, next)) return next
    }
}
val seedUniquifier = AtomicLong(8682522807148012L)

private fun seedUniquifier2(): Long {
    // L'Ecuyer, "Tables of Linear Congruential Generators of
    // Different Sizes and Good Lattice Structure", 1999
    while (true) {
        val current = seedUniquifier2.get()
        val next = current * 1181783497276652981L
        if (seedUniquifier2.compareAndSet(current, next)) return next
    }
}


private val seedUniquifier2 = AtomicLong(8682522807148012L)

var customRandom: kotlin.random.Random? = null
fun guess(): Double {
    if (customRandom != null)
        return customRandom!!.nextDouble()
    else {
        val nanoTime = System.nanoTime()
        val systemRandom = kotlin.random.Random.nextDouble()
        val nanoAfterTime = System.nanoTime()
        for (i in nanoTime..nanoAfterTime) {
            customRandom = kotlin.random.Random(3447679086515839964L xor i)
            if (systemRandom == customRandom!!.nextDouble()) {
                return customRandom!!.nextDouble()
            }
        }
    }
    throw IllegalStateException("I'm not suppose to get here")
}


class PsychicTest {

    @Test
    fun adjustToDefaultRandom(){


        repeat(100) { seedUniquifier()}
        val fixedUniqifier1 = seedUniquifier();
        var nanos = System.nanoTime()-100000
        var customRandom1 = Random( fixedUniqifier1 xor System.nanoTime())
        val nextCustom1Int = Random.nextInt()//customRandom1.nextInt()

        var customRandom2 = Random(0)


        var quit = false
        var fixedUniqifier2 = 0L
        do {
            fixedUniqifier2 = seedUniquifier2()
            for (i in 1..10000) {
                customRandom2 = Random(fixedUniqifier2 xor (nanos+i))
                if(nextCustom1Int == customRandom2.nextInt()) {
                    quit = true
                    break;
                }
            }
        } while (!quit)

        assertEquals(fixedUniqifier1,fixedUniqifier2)
        assertEquals(Random.nextInt(),customRandom2.nextInt())





    }
    @Test
    fun seedQualifier(){


        val myRandom = Random(getSeedUniquifier());
        val nextDefaultRandomInt = Random.nextInt()
        while (myRandom.nextInt() != nextDefaultRandomInt);
        assertEquals(Random.nextInt(),myRandom.nextInt())


    }

    @Test
    fun defaultKotlinRandomBehaviour(){
        val seed = System.currentTimeMillis()
        val random = Random(0)
        val kotlinInt = random.nextInt()

        val seedPart0 = (seed ushr 32).toInt()
        val seedPart1 = (seed and  0xFFFFFFFF).toInt()
        val wowRandom = XorWowRandom(seedPart1, seedPart0)
        while (wowRandom.nextInt() != kotlinInt);


        assertEquals(wowRandom.nextInt(),random.nextInt())


    }
    @Test
    fun testRandomKotlinClass(){
        val seed = System.currentTimeMillis()

        val seedPart0 = (seed ushr 32).toInt()
        val seedPart1 = (seed and  0xFFFFFFFF).toInt()
        assertEquals(seed, (seedPart0.toLong() shl 32) or seedPart1.toLong(), "Combination failed")

        val wowInt = XorWowRandom(seedPart1, seedPart0).nextInt()
        val kotlinInt = Random(seed).nextInt()
        assertEquals(wowInt,kotlinInt)



        val random = Random(0)
        val kotlinInt2 = random.nextInt()
        val wowInt2 = XorWowRandom(0,kotlinInt2,dontRepeat = true).nextInt()
        assertEquals(wowInt2,random.nextInt(),"Seeding next int failed")

    }
    @Test
    fun `The Psychic should guess correctly`() {
        assertEquals(guess(), kotlin.random.Random.nextDouble())
    }
}