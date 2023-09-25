import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

operator fun Int.Companion.invoke(s:String) = s.toInt()
operator fun Double.Companion.invoke(s:String) = s.toDouble()
operator fun Long.Companion.invoke(s:String) = s.toLong()
class `Tricky Kotlin 0` {


    @Test
    fun testInt() {
        val r = Random(System.currentTimeMillis())
        (0..100).forEach { r.nextInt().let { assertEquals(it, kotlin.Int(it.toString())) } }
    }
}