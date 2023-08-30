import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

//https://www.codewars.com/kata/5abab55b20746bc32e000008/kotlin
data class Blob(var x: Int, var y: Int, var size: Int) {

}


class Blobservation(height: Int, width: Int = height) {
    // if invalid arguments are given to either `populate` or `move` methods, throw an IllegalArgumentException
    fun populate(blobs: Array<Blob>) {
        TODO()
    }

    fun move(n: Int = 1) {
        TODO()
    }

    fun printState(): List<IntArray> {
        TODO()
    }
}


fun printCheck(user: Blobservation, exp: Array<IntArray>) =
    assertTrue(exp.contentDeepEquals(user.printState().toTypedArray()))


class ExampleTests {
    @Test
    fun `Eaxmple Test 1`() {
        val generation = arrayOf(
            Blob(0, 4, 3),
            Blob(0, 7, 5),
            Blob(2, 0, 2),
            Blob(3, 7, 2),
            Blob(4, 3, 4),
            Blob(5, 6, 2),
            Blob(6, 7, 1),
            Blob(7, 0, 3),
            Blob(7, 2, 1)
        )
        val blobState1 = arrayOf(
            intArrayOf(0, 6, 5),
            intArrayOf(1, 5, 3),
            intArrayOf(3, 1, 2),
            intArrayOf(4, 7, 2),
            intArrayOf(5, 2, 4),
            intArrayOf(6, 7, 3),
            intArrayOf(7, 1, 3),
            intArrayOf(7, 2, 1)
        )
        val blobState2 = arrayOf(
            intArrayOf(1, 5, 5),
            intArrayOf(2, 6, 3),
            intArrayOf(4, 2, 2),
            intArrayOf(5, 6, 2),
            intArrayOf(5, 7, 3),
            intArrayOf(6, 1, 4),
            intArrayOf(7, 2, 4)
        )
        val blobState3 = arrayOf(intArrayOf(4, 3, 23))

        val blobs = Blobservation(8)
        blobs.populate(generation)
        blobs.move()
        printCheck(blobs, blobState1)
        blobs.move()
        printCheck(blobs, blobState2)
        blobs.move(1000)
        printCheck(blobs, blobState3)
    }

    @Test
    fun `Example Test 2`() {
        val generation1 = arrayOf(
            Blob(3, 6, 3),
            Blob(8, 0, 2),
            Blob(5, 3, 6),
            Blob(1, 1, 1),
            Blob(2, 6, 2),
            Blob(1, 5, 4),
            Blob(7, 7, 1),
            Blob(9, 6, 3),
            Blob(8, 3, 4),
            Blob(5, 6, 3),
            Blob(0, 6, 1),
            Blob(3, 2, 5)
        )
        val generation2 = arrayOf(
            Blob(5, 4, 3),
            Blob(8, 6, 15),
            Blob(1, 4, 4),
            Blob(2, 7, 9),
            Blob(9, 0, 10),
            Blob(3, 5, 4),
            Blob(7, 2, 6),
            Blob(3, 3, 2)
        )
        val invalidPopulation = arrayOf(Blob(4, 6, 3), Blob(3, 2, -1))
        val blobState1 = arrayOf(
            intArrayOf(0, 6, 1),
            intArrayOf(1, 1, 1),
            intArrayOf(1, 6, 2),
            intArrayOf(2, 1, 5),
            intArrayOf(2, 6, 7),
            intArrayOf(4, 2, 6),
            intArrayOf(6, 7, 3),
            intArrayOf(7, 1, 2),
            intArrayOf(7, 4, 4),
            intArrayOf(7, 7, 1),
            intArrayOf(8, 7, 3)
        )
        val blobState2 = arrayOf(
            intArrayOf(0, 6, 7),
            intArrayOf(1, 5, 3),
            intArrayOf(2, 2, 6),
            intArrayOf(4, 1, 6),
            intArrayOf(6, 1, 2),
            intArrayOf(6, 4, 4),
            intArrayOf(6, 6, 7)
        )
        val blobState3 = arrayOf(
            intArrayOf(2, 4, 13),
            intArrayOf(3, 3, 3),
            intArrayOf(6, 1, 8),
            intArrayOf(6, 2, 4),
            intArrayOf(6, 4, 7)
        )
        val blobState4 = arrayOf(
            intArrayOf(1, 4, 4),
            intArrayOf(2, 4, 13),
            intArrayOf(2, 7, 9),
            intArrayOf(3, 3, 5),
            intArrayOf(3, 5, 4),
            intArrayOf(5, 4, 3),
            intArrayOf(6, 1, 8),
            intArrayOf(6, 2, 4),
            intArrayOf(6, 4, 7),
            intArrayOf(7, 2, 6),
            intArrayOf(8, 6, 15),
            intArrayOf(9, 0, 10)
        )
        val blobState5 = arrayOf(
            intArrayOf(2, 4, 9),
            intArrayOf(3, 3, 13),
            intArrayOf(3, 6, 9),
            intArrayOf(4, 4, 4),
            intArrayOf(5, 3, 4),
            intArrayOf(5, 4, 10),
            intArrayOf(6, 2, 6),
            intArrayOf(7, 2, 8),
            intArrayOf(7, 5, 15),
            intArrayOf(8, 1, 10)
        )
        val blobState6 = arrayOf(
            intArrayOf(4, 3, 22),
            intArrayOf(5, 3, 28),
            intArrayOf(5, 4, 9),
            intArrayOf(6, 2, 29)
        )
        val blobState7 = arrayOf(intArrayOf(5, 3, 88))

        val blobs = Blobservation(10, 8)
        blobs.populate(generation1)
        blobs.move()
        printCheck(blobs, blobState1)
        blobs.move(2)
        printCheck(blobs, blobState2)
        blobs.move(2)
        printCheck(blobs, blobState3)

        blobs.populate(generation2)
        printCheck(blobs, blobState4)
        blobs.move()
        printCheck(blobs, blobState5)
        blobs.move(3)
        printCheck(blobs, blobState6)
        assertFails("Invalid input for the `move` method should trigger an IllegalArgumentException",
            { blobs.move(-3) })
        blobs.move(30)
        printCheck(blobs, blobState7)
        assertFails("Invalid elements given when calling `populate` method should trigger an IllegalArgumentException",
            { blobs.populate(invalidPopulation) })
    }
}