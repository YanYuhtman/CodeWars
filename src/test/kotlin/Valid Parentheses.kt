import org.junit.jupiter.api.Test
import java.util.Stack
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class `Valid Parentheses` {

    fun check(str:String) = str.fold(Stack<Char>())
    {acc, c -> if(acc.isNotEmpty() && acc.peek() == '(' && c == ')') acc.pop() else acc.add(c);acc}
        .size == 0
    @Test
    fun test(){
        assertTrue(check("((((()"))
        assertTrue(check("()"))
        assertFalse(check("())"))
        assertFalse(check(")("))
        assertFalse(check(")()"))
        assertTrue(check("((()))"))

        assertFalse(check("())()"))
        assertFalse(check("())(()"))
        assertFalse(check("()(((())()))(()())(())))()"))
    }
}