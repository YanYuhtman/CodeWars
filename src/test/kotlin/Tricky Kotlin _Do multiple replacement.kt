import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class `Tricky Kotlin _Do multiple replacement` {
    //https://www.codewars.com/kata/5a537ad4145c4615350000ff

    val map = mutableMapOf<Char,Char>().apply{"}{)(><".chunked(2).forEach{put(it[0],it[1]);put(it[1],it[0])}}

//    fun f(s:String)={""}
    fun replaceParenthesis(s:String) : String{
//        println(input.filter {c->!c.isLetter()}.replace(" ","").)
//        println(input.let{it.filter {c->!c.isLetter()}.replace(" ","")
//            .fold(""){acc, c -> if(acc == "$c") it.replace("$c",acc) else "$c"}
//        }
//        )
//          println("}{)(><".reversed()) //foldRight("") { acc, c -> if("$acc" != c)  c + acc else acc + c }.reversed())
//            return mutableMapOf<Char,Char>().run{"}{)(><".chunked(2).forEach{put(it[0],it[1]);put(it[1],it[0])};s.fold(""){a,c->if(contains(c))"$a${this[c]}" else "$a$c"}}


            return s.replace(Regex("([^\\w ]+)([\\w ]+)([^\\w ]+)")){it.groupValues.reversed().joinToString("")}






//        println(input.runningFold(""){ acc, c -> "$c" })
    }

    @Test
    fun test(){
        assertEquals("You {{need}} <extra> time ( or money )", f("You }}need{{ >extra< time ) or money ("))
    }
}