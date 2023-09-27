import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

class `Tricky Kotlin 5` {
    //https://www.codewars.com/kata/59eb8739fc3c49b67a00007d



    data class Box(var x: Int, var y: Int, var width: Int, var height: Int) {
        fun area() = width * height
        fun rightDown() = width + x to height + y
        fun rightUp() = width + x to height
        fun leftDown() = width to (height + y)

    }

    var x = 0
    var y = 0
    var width = 0
    var height = 0
    inline fun box(block:()->Unit){
    }
    inline fun b(block:Box.()->Unit){
        block.invoke(Box(1,1,1,1))
    }
    @Test
    fun testBox() {
        val rand = Random(System.currentTimeMillis())
        val xx = rand.nextInt(100)
        val yy = rand.nextInt(100)
        val box = Box(xx, yy, xx + rand.nextInt(1000), yy + rand.nextInt(1000))
        val testerBox = box.copy()
//        println(this::box.map { "$it" }.joinToString("\n"))
        fun boxEquals(testerBox: Box, box: Box) {
            assertEquals(testerBox, box)
            assertEquals(testerBox.area(), box.area())
            assertEquals(testerBox.leftDown(), box.leftDown())
            assertEquals(testerBox.rightDown(), box.rightDown())
            assertEquals(testerBox.rightUp(), box.rightUp())
        }
        box {
            var r = rand.nextInt(10)
            boxEquals(testerBox, box)
            x -= r
            testerBox.x -= r
            boxEquals(testerBox, box)
            r = rand.nextInt(10)
            y -= r
            testerBox.y -= r
            boxEquals(testerBox, box)
            r = rand.nextInt(10)
            width += r
            testerBox.width += r
            boxEquals(testerBox, box)
            r = rand.nextInt(10)
            height += r
            testerBox.height += r
            boxEquals(testerBox, box)
        }
    }
}


