import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class `Human readable duration format` {

    //https://www.codewars.com/kata/52742f58faf5485cae000b9a/train/kotlin

    //For the purpose of this Kata, a year is 365 days and a day is 24 hours.

    object TimeFormatter {
        enum class TimeType(val inSeconds:Int){
            YEAR (60 * 60 * 24 * 365),
            DAY (60 * 60 * 24),
            HOUR (60 * 60),
            MINUTE (60),
            SECOND (1)

        }
        fun readableFormat(timeType:TimeType, value: Int):String = when(timeType){
            TimeType.YEAR -> "$value ${if(value == 1) "year" else "years"}"
            TimeType.DAY -> "$value ${if(value == 1) "day" else "days"}"
            TimeType.HOUR -> "$value ${if(value == 1) "hour" else "hours"}"
            TimeType.MINUTE -> "$value ${if(value == 1) "minute" else "minutes"}"
            TimeType.SECOND -> "$value ${if(value == 1) "second" else "seconds"}"
        }

        fun formatDuration(seconds: Int): String {
            if(seconds <= 0)
                return "now"
            var _seconds = seconds
            val _output = StringBuilder()
            TimeType.values().forEach  label@{
                if(_seconds/it.inSeconds == 0)
                    return@label
                if(_output.length > 0)
                    _output.append((if (_seconds % it.inSeconds == 0) " and " else ", " ))
                _output.append(readableFormat(it,_seconds / it.inSeconds))
                _seconds %= it.inSeconds
            }
            return _output.toString()
        }
    }
    @Test
    fun testFormatDurationExamples() {
        // Example Test Cases
        assertEquals("1 second", TimeFormatter.formatDuration(1))
        assertEquals("1 minute and 2 seconds", TimeFormatter.formatDuration(62))
        assertEquals("2 minutes", TimeFormatter.formatDuration(120))
        assertEquals("1 hour", TimeFormatter.formatDuration(3600))
        assertEquals("1 hour, 1 minute and 2 seconds", TimeFormatter.formatDuration(3662))
    }
}