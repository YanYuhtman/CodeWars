import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Playing with passphrases` {

    //https://www.codewars.com/kata/559536379512a64472000053


    fun playPass(s: String, n: Int): String {
        return s.mapIndexed { index,ch->
            if (ch.isLetter()) {
                val start = if (ch.isUpperCase()) 65 else 97; ((ch.toInt() - start + n) % 26 + start).toChar()
                    .let { if(index%2 == 0) it.uppercase() else it.lowercase()}
            } else if(ch.isDigit()){
                ((9 - "$ch".toInt() )%10 + 48).toChar()
            }else
                ch
        }.joinToString("").reversed()
    }

    @Test
    fun basicTests() {
        assertEquals("cCbB!!02890!", playPass("!90179!!AAbb", 1))
        assertEquals("!!!VpZ FwPm j ", playPass(" I LOVE YOU!!!", 1))
        assertEquals("!!!uOy eVoL I", playPass("I LOVE YOU!!!", 0))
        assertEquals("zDdCcBbB", playPass("AAABBCCY", 1))

    }
}