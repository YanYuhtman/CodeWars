import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpinWords{
    fun spinWords(text:String): String{
        return text.split(' ')
            .map { if(it.length >= 5) it.reversed() else it}
            .joinToString ( " " )
    }
    @Test
    fun test(){
        assertEquals("Hey wollef sroirraw",  spinWords("Hey fellow warriors"))
        assertEquals("This is a test",  spinWords("This is a test"))
        assertEquals("This is rehtona test",  spinWords("This is another test"))
    }

}