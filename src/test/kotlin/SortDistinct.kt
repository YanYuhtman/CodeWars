import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SortDistinct(){

    fun countDistinct(text:String) : Int{
        return text.uppercase().toCharArray()
            .groupBy { it }
            .mapNotNull { if(it.value.size > 1) it else null }
            .count()
    }

    @Test
    fun testDistrinct(){
        assertEquals(0,countDistinct("abcdefg"))
        assertEquals(1, countDistinct("abcdefga"))
        assertEquals(2, countDistinct("ab11cdefga"))
    }

}