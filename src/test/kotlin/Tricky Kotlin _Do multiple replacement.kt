import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class `Tricky Kotlin _Do multiple replacement` {
    //https://www.codewars.com/kata/5a537ad4145c4615350000ff

    fun replaceParenthesis(s:String) : String{
        return s.map {c->val p="}>)(<{";if(c in p)p[5-p.indexOf(c)] else c}.joinToString("")

    }

    @Test
    fun test(){
        assertEquals("You {{need}} <extra> time ( or money )", replaceParenthesis("You }}need{{ >extra< time ) or money ("))
    }

}