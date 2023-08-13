import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PersistenceNumber {
    fun persistence(num: Int) : Int {
        var n = num
        var count = 0
        while (n / 10 != 0){
            n = persistenceStep(n)
            count++
        }
        return count
    }
    fun persistenceStep(num: Int) : Int {
        var mult = 1
        var n = num
        do{
            mult *= n % 10
            n /= 10
        }while (n != 0)
        return mult
    }
    @Test
    fun `Basic Tests`() {
        assertEquals(3, persistence(39))
        assertEquals(0, persistence(4))
        assertEquals(2, persistence(25))
        assertEquals(4, persistence(999))
    }

}