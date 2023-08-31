import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

//https://www.codewars.com/kata/5abab55b20746bc32e000008/kotlin
data class Blob(var y: Int, var x: Int, var size: Int) : Comparable<Blob>{
    init {
        if(size < 1 || size > 20)
            throw IllegalArgumentException("Blob size $size is illegal")
    }

    operator fun plus(blob:Blob):Blob = if(blob.y != this.y || blob.x != this.x)
        throw IllegalArgumentException("These blobs $this and $blob can't be fused")
    else
        Blob(y,x,blob.size + this.size)


    override fun compareTo(other: Blob) = if(this.y > other.y) -1 else {
            if(this.y < other.y) 1 else{
                if(this.x > other.x) -1 else{
                    if(this.x < other.x) 1
                    else 0
                }
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Blob

        if (y != other.y) return false
        return x == other.x
    }
    private fun signum(num:Int) = if(num < 0) -1 else if(num > 0) 1 else 0
    fun getDirectionTo(blob:Blob) = (signum(blob.y - this.y) to signum(blob.x - this.x))

    private fun calcDistance(point0:Pair<Number,Number>, point1:Pair<Number,Number>) =
        /*Math.floor(*/Math.sqrt(Math.pow(point1.first.toDouble() - point0.first.toDouble(),2.0) + Math.pow(point1.second.toDouble() - point0.second.toDouble(),2.0))/*).toInt() - 1*/
    fun approximateDistance(blob: Blob, direction: Pair<Int,Int>): Pair<Number,Number> {
        if(direction.second == 0)
            return Pair(blob.x - this.x,0)

        val m:Double = direction.first/direction.second.toDouble()
        val b:Double = this.y - m * this.x

        val y_3:Double = m * blob.x + b
        val x_3:Double = (blob.y - b)/m

        val d1 = Math.abs(y_3 - this.y).toInt()//calcDistance((y_3 to blob.x),(this.y to this.x ))
        val d2 = Math.abs(x_3 - this.x).toInt()//calcDistance((blob.y to x_3),(this.y to this.x ))

        if(d1 < d2)
            return Pair(d1,Math.abs(y_3 - blob.y).toInt())
        else
            return Pair(d2,Math.abs(x_3 - blob.x).toInt())

    }
    fun move(potentialTargets: List<Blob>){
        if(potentialTargets.isEmpty())
            return
        potentialTargets.map {  }

    }


}


class Blobservation(val height: Int, val width: Int = height) {
    init {
        if(height < 8 || height > 50 || width < 8 || width > 50)
            throw IllegalArgumentException("Height or width board values [$height:$width] are invalid")
    }

    private var mBlobs = mutableListOf<Blob>()

    // if invalid arguments are given to either `populate` or `move` methods, throw an IllegalArgumentException
    fun populate(blobs: Array<Blob>) {
        if(blobs.isEmpty())
            throw IllegalArgumentException("Blob array must contain at least one Blob")
        blobs.forEach { if(it.y < 0 || it.y > width || it.x < 0 || it.x > this.height)
            throw IllegalArgumentException("Blob [${it.y}:${it.x} is out of board")
        }
        mBlobs.addAll(blobs)
        fuseBlobs()
    }
    private fun fuseBlobs(){
        mBlobs = mBlobs.sorted().fold(mutableListOf<Blob>()){ acc, blob ->
            if(acc.isEmpty() || acc.last() != blob) acc.apply { add(blob) }
            else acc.apply { acc[acc.lastIndex] += blob}
        }
    }

    fun move(n: Int = 1) {
        if(n < 1) throw IllegalArgumentException("Moves number must be > 0")
        for(i in 1..n) {
            mBlobs.forEach { tBlob ->
                tBlob.move(mBlobs.filter { tBlob.size > it.size })
            }
            fuseBlobs()
        }
    }


    fun printState(): List<IntArray> {
        TODO()
    }
}


fun printCheck(user: Blobservation, exp: Array<IntArray>) =
    assertTrue(exp.contentDeepEquals(user.printState().toTypedArray()))




class ExampleTests {
    @Test fun testDirections(){
        var blob0 = Blob(1,1,1)
        assertEquals(Pair(-1,-1),blob0.getDirectionTo(Blob(0,0,1)))
        assertEquals(Pair(-1,0),blob0.getDirectionTo(Blob(0,1,1)))
        assertEquals(Pair(-1,1),blob0.getDirectionTo(Blob(0,2,1)))
        assertEquals(Pair(0,-1),blob0.getDirectionTo(Blob(1,0,1)))
        assertEquals(Pair(0,1),blob0.getDirectionTo(Blob(1,2,1)))
        assertEquals(Pair(1,-1),blob0.getDirectionTo(Blob(2,0,1)))
        assertEquals(Pair(1,0),blob0.getDirectionTo(Blob(2,1,1)))
        assertEquals(Pair(1,1),blob0.getDirectionTo(Blob(2,2,1)))
        assertEquals(Pair(0,0),blob0.getDirectionTo(Blob(1,1,1)))

        blob0 = Blob(4,3,1)
        assertEquals(Pair(-1,1), blob0.getDirectionTo(Blob(0,7,1)))

    }

    @Test fun testDistances(){
        val blob0 = Blob(4,3,1)
        var blob1 = Blob(7,0,1)
        assertEquals(Pair(3,0), blob0.approximateDistance(blob1,blob0.getDirectionTo(blob1)))
        blob1 = Blob(0,7,1)
        assertEquals(Pair(4,0), blob0.approximateDistance(blob1,blob0.getDirectionTo(blob1)))
        blob1 = Blob(2,0,1)
        assertEquals(Pair(2,1), blob0.approximateDistance(blob1,blob0.getDirectionTo(blob1)))
    }

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