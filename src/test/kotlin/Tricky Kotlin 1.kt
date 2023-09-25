import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.random.Random
import kotlin.reflect.typeOf

operator fun <T> String.invoke(any:T):T = any

class `Tricky Kotlin 1` {

    @Test
    fun testString() {
        assertEquals("wow, I am the argument!", "this is a string, invoke it!"("wow, I am the argument!"))
        assertEquals("s", "this is a string, invoke it!"("s"))
    }

    @Test
    fun testInt() {
        val r = Random(System.currentTimeMillis())
        (0..100).forEach { r.nextInt().let { assertEquals(it, "this is another string"(it)) } }
    }

    @Test
    fun testLong() {
        val r = Random(System.currentTimeMillis())
        (0..100).forEach { r.nextLong().let { assertEquals(it, "oh come on!"(it)) } }
    }

    @Test
    fun testFile() {
        val r = Random(System.currentTimeMillis())
        (0..100).forEach { r.nextLong().let { assertEquals(File(it.toString()), "oh come on!"(File(it.toString()))) } }
    }
}