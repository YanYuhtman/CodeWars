package org.blobstarvation_1_3

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails


//https://www.codewars.com/kata/5abab55b20746bc32e000008/kotlin

data class Blob(var x: Int, var y: Int, var size: Int){

}

    class Blobservation(val height: Int, val width: Int = height) {
        init {
            if(height < 8 || height > 50 || width < 8 || width > 50)
                throw IllegalArgumentException("Height or width board values [$height:$width] are invalid")
        }

        private fun signum(num:Int) = if(num < 0) -1 else if(num > 0) 1 else 0

        fun getDirectionTo(source:Blob, target:Blob) = (signum(target.x - source.x) to signum(target.y - source.y))

        fun approximateDistance(source:Blob, target: Blob): Pair<Int,Int> {
            val direction = getDirectionTo(source,target)
            if(direction.first == 0)
                return Pair(0,Math.abs(target.y - source.y))
            if(direction.second == 0)
                return Pair(0,Math.abs(target.x - source.x))


            val m:Double = direction.first/direction.second.toDouble()
            val b:Double = source.x - m * source.y

            val y_3:Double = m * target.y + b
            val x_3:Double = (target.x - b)/m

            val d1 = Math.abs(y_3 - source.x).toInt()
            val d2 = Math.abs(x_3 - source.y).toInt()

            return if(d1 < d2)
                Pair(d1,Math.abs(y_3 - target.x).toInt())
            else
                Pair(d2,Math.abs(x_3 - target.y).toInt())

        }

        fun getClockDirection(direction: Pair<Int,Int>) = when(direction){
            (-1 to 0) -> 0; (-1 to 1) -> 45; (0 to 1) -> 90; (1 to 1) -> 135; (1 to 0) -> 180; (1 to -1) -> 225; (0 to -1) -> 270; (-1 to -1) -> 315
            else -> throw IllegalArgumentException("Unable to resolve degrees upon direction $direction")
        }

        fun moveBlob(blob:Blob, potentialTargets: List<Blob>){
            if(potentialTargets.isEmpty())
                return

            potentialTargets.associateWith {
                Pair(this.approximateDistance(blob,it).let { it.first + it.second }, getDirectionTo(blob,it))
            }
                .run {
                    filter { map -> map.value.first == this.minBy { it.value.first }!!.value.first }
                }.run { filter { map -> map.key.size == this.maxBy { it.key.size }!!.key.size } }
                .run {
                    this.map { it.key }.sortedBy { getClockDirection(this[it]!!.second) }
                        .firstOrNull()?.let {
                            val direction = this[it]!!.second
                            blob.x += direction.first
                            blob.y += direction.second
                        }
                }
        }
        val blobComparator = Comparator<Blob> { source: Blob, target: Blob ->
            if (source.x < target.x) -1 else {
                if (source.x > target.x) 1 else {
                    if (source.y < target.y) -1 else {
                        if (source.y > target.y) 1
                        else 0
                    }
                }
            }
        }


        private var mBlobs = mutableListOf<Blob>()

        // if invalid arguments are given to either `populate` or `move` methods, throw an IllegalArgumentException
        fun populate(blobs: Array<Blob>) {
            if(blobs.isEmpty())
                throw IllegalArgumentException("Blob array must contain at least one Blob")
            blobs.forEach { if(it.x < 0 || it.x > height || it.y < 0 || it.y > this.width || it.size < 1)
                throw IllegalArgumentException("Blob [${it.x}:${it.y} is out of board")
            }
            mBlobs.addAll(blobs)
            fuseBlobs()
        }
        private fun fuseBlobs(){
            mBlobs = mBlobs.sortedWith(blobComparator).fold(mutableListOf<Blob>()){ acc, blob ->
                if(acc.isEmpty() || blobComparator.compare(acc.last(), blob) != 0) acc.apply { add(blob) }
                else acc.apply {
                    acc[acc.lastIndex] = Blob(blob.x,blob.y,blob.size +  acc[acc.lastIndex].size)}
            }
        }

        fun move(n: Int = 1) {
            if(n < 1) throw IllegalArgumentException("Moves number must be > 0")

            for(i in 1..n) {
                val tempMap = mBlobs.map { it.copy() }
                mBlobs.forEach { tBlob ->
                    moveBlob(tBlob,tempMap.filter { tBlob.size > it.size })
                }
                fuseBlobs()
            }
        }


        fun printState(): List<IntArray> = mBlobs.map { intArrayOf(it.x,it.y,it.size) }

    }

class ExampleTests {
    @Test
    fun testDirections(){

        val blobStarvation = Blobservation(8)
        var blob0 = Blob(1,1,1)

        assertEquals(Pair(-1,-1),blobStarvation.getDirectionTo(blob0,Blob(0,0,1)))
        assertEquals(Pair(-1,-1),blobStarvation.getDirectionTo(blob0,Blob(0,0,1)))
        assertEquals(Pair(-1,0),blobStarvation.getDirectionTo(blob0,Blob(0,1,1)))
        assertEquals(Pair(-1,1),blobStarvation.getDirectionTo(blob0,Blob(0,2,1)))
        assertEquals(Pair(0,-1),blobStarvation.getDirectionTo(blob0,Blob(1,0,1)))
        assertEquals(Pair(0,1),blobStarvation.getDirectionTo(blob0,Blob(1,2,1)))
        assertEquals(Pair(1,-1),blobStarvation.getDirectionTo(blob0,Blob(2,0,1)))
        assertEquals(Pair(1,0),blobStarvation.getDirectionTo(blob0,Blob(2,1,1)))
        assertEquals(Pair(1,1),blobStarvation.getDirectionTo(blob0,Blob(2,2,1)))
        assertEquals(Pair(0,0),blobStarvation.getDirectionTo(blob0,Blob(1,1,1)))

        blob0 = Blob(4,3,1)
        assertEquals(Pair(-1,1), blobStarvation.getDirectionTo(blob0,Blob(0,7,1)))

    }

    @Test
    fun testDistances(){
        val blobservation = Blobservation(8)

        val blob0 = Blob(4,3,1)
        var blob1 = Blob(7,0,1)
        var direction = blobservation.getDirectionTo(blob0,blob1)
        var distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(3,0),distance)
        assertEquals(225,blobservation.getClockDirection(direction))

        blob1 = Blob(0,7,1)
        direction = blobservation.getDirectionTo(blob0,blob1)
        distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(4,0), distance)
        assertEquals(45,blobservation.getClockDirection(direction))

        blob1 = Blob(2,0,1)
        direction = blobservation.getDirectionTo(blob0,blob1)
        distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(2,1), distance)
        assertEquals(315,blobservation.getClockDirection(direction))

        blob1 = Blob(3,7,1)
        direction = blobservation.getDirectionTo(blob0,blob1)
        distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(1,3),distance)
        assertEquals(45,blobservation.getClockDirection(direction))

        blob1 = Blob(0,4,1)
        direction = blobservation.getDirectionTo(blob0,blob1)
        distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(1,3), distance)
        assertEquals(45,blobservation.getClockDirection(direction))

        blob1 = Blob(7,2,1)
        direction = blobservation.getDirectionTo(blob0,blob1)
        distance = blobservation.approximateDistance(blob0,blob1)
        assertEquals(Pair(1,2), distance)
        assertEquals(225,blobservation.getClockDirection(direction))
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
fun printCheck(user: Blobservation, exp: Array<IntArray>) =
    Assertions.assertTrue(exp.contentDeepEquals(user.printState().toTypedArray()))