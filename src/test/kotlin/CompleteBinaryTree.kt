import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CompleteBinaryTree{

    fun completeBinaryTree(arr:IntArray):IntArray{
        if (arr.size == 1) return arr
        if (arr.size == 2) return intArrayOf(arr.get(1), arr.get(0))

        val n = 1 shl Math.ceil(Math.log(arr.size.toDouble()) / Math.log(2.0)).toInt()
        val arrayList: MutableList<Int> = arr.toMutableList()

        val result  = mutableListOf<Int>()

        var j = 0
        while (!arrayList.isEmpty()) {
            var i = if (j == 0) arrayList.size - (n - arrayList.size) else arrayList.size - 1
            while (i >= 0) {
                result.add(0, arrayList[i])
                arrayList.removeAt(i)
                i -= 2
            }
            j++
        }

        return result.toIntArray()
    }
    fun completeBinaryTree1(arr:IntArray):IntArray{
        val n = 1 shl Math.ceil(Math.log(arr.size.toDouble()) / Math.log(2.0)).toInt()

        val tmpList = arr.toMutableList()

        val output = mutableListOf<List<Int>>(
            (tmpList.mapIndexedNotNull { index, i -> if (index < tmpList.size - (n - tmpList.size - 1) && index % 2 == 0) i else null }
                .also { mapped -> tmpList.removeIf { it in mapped } })
        )
        while (tmpList.isNotEmpty()) {
            output.add(
                (tmpList.mapIndexedNotNull { index, i -> if (index % 2 == 0) i else null })
                    .also { mapped2 -> tmpList.removeIf { it in mapped2 } })
        }
        var tmp =  output.reversed().flatten().toIntArray()
        if(tmp.size != arr.size)
            println(tmp.toList().mapNotNull { if(it in arr) null else it })
        return tmp
    }
    fun bfs(arr: IntArray) : IntArray{
        if(arr.size <= 1)
            return arr
        val output:IntArray = IntArray(arr.size)
        val indexQueue: Queue<Pair<Int, Int>> = LinkedList<Pair<Int,Int>>()

        val n = Math.ceil(Math.log(arr.size.toDouble())/Math.log(2.0))
        val rootIndex =  arr.size - (Math.pow(2.0,n-2).toInt() - 1) - 1

        output[0] = arr[rootIndex]
        var indexArray = 1
        indexQueue.add(Pair(0,rootIndex))
        indexQueue.add(Pair(rootIndex + 1, arr.size))

        while (indexQueue.isNotEmpty()){
            var itemCount = indexQueue.size
            do {
                var i = indexQueue.poll()!!
                var rootIndex = i.first + (i.second - i.first) / 2
                output[indexArray++] = arr[rootIndex]

                if(i.first != rootIndex)
                    indexQueue.add(Pair(i.first, rootIndex))
                if(i.second != rootIndex + 1)
                    indexQueue.add(Pair(rootIndex + 1, i.second))

            }while (--itemCount > 0)
        }

        return output
    }
    @Test
    fun `test single node tree`() {
        val input = intArrayOf(1)
        val expected = intArrayOf(1)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with two nodes`() {
        val input = intArrayOf(1, 2)
        val expected = intArrayOf(2, 1)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with six nodes`() {
        val input = intArrayOf(1, 2, 3, 4, 5, 6)
        val expected = intArrayOf(4, 2, 6, 1, 3, 5)
        assertContentEquals(expected, completeBinaryTree(input))
    }

    @Test
    fun `test tree with ten nodes`() {
        val input = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expected = intArrayOf(7, 4, 9, 2, 6, 8, 10, 1, 3, 5)
        assertContentEquals(expected, completeBinaryTree(input))
    }
    @Test
    fun randomTest(){
        for(i in 0 .. 100) {
            val length = 8000 + (Math.random() * 1000).toInt()
            val tree = completeBinaryTree((1..length).shuffled().toList().toIntArray())
            assertEquals(length,tree.size)

        }
    }
}