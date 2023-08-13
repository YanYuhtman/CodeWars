import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Int32ToIPv4{
    fun Int32ToIPv4(ip: UInt):String{
        return mutableListOf<UInt>()
            .also {
                val bitSet:UInt = 255u
                for(i in 0 until 4)
                    it.add(0,(ip and (bitSet shl i*8)) shr i*8 )
            }.joinToString ( "." )

    }
    @Test
    fun test(){
        assertEquals("128.32.10.1",Int32ToIPv4(2149583361u))
    }
}