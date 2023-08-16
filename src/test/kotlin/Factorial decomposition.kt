import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Factorial decomposition` {
    //https://www.codewars.com/kata/5a045fee46d843effa000070/kotlin

    fun decomp(num: Int):String{

        var _nam = num
        return mutableMapOf<Int,Int>()
            .also{map->
                var _tmp = _nam
                while (_nam > 1){
                    _tmp = _nam
                    (2 .. num).associateBy(keySelector = {
                        if(_tmp % it == 0) it else null
                    }, valueTransform = {
                        var count = 0
                        while (_tmp % it == 0) {
                            _tmp /= it
                            count++
                        }
                        count
                    }).filter { entry -> entry.key != null && entry.value != 0 }
                        .forEach(action = {entry -> map.merge(entry.key!!, entry.value) { oldVal, newVal -> oldVal + newVal }})
                    _nam--
                }
            }.let {map->
                map.keys.sorted()
                    .map { "${it}${if(map[it] == 1) "" else("^" + map[it])}" }
                    .joinToString(" * ")
            }

    }

    private fun testing(n:Int, expect:String) {
        val actual = decomp(n)
        assertEquals(expect, actual)
    }
    @Test
    fun test() {
        testing(17, "2^15 * 3^6 * 5^3 * 7^2 * 11 * 13 * 17");
        testing(5, "2^3 * 3 * 5");
        testing(22, "2^19 * 3^9 * 5^4 * 7^3 * 11^2 * 13 * 17 * 19");
        testing(14, "2^11 * 3^5 * 5^2 * 7^2 * 11 * 13");
        testing(25, "2^22 * 3^10 * 5^6 * 7^3 * 11^2 * 13 * 17 * 19 * 23");

    }
}