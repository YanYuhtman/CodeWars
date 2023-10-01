import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class `Esolang Interpreters #4` {
    //https://www.codewars.com/kata/5861487fdb20cff3ab000030

    fun codeToBoolfuck(code: String) = code.map {
        when (it) {
            '+' -> ">[>]+<[+<]>>>>>>>>>[+]<<<<<<<<<"
            '-' -> ">>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]<<<<<<<<<"
            '<' -> "<<<<<<<<<"
            '>' -> ">>>>>>>>>"
            ',' -> ">,>,>,>,>,>,>,>,<<<<<<<<"
            '.' -> ">;>;>;>;>;>;>;>;<<<<<<<<"
            '[' -> ">>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]"
            ']' -> ">>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]"
            else -> ""
        }
    }.joinToString("")

    fun inputFromBoolFuck(input: String):String{
        return ""
    }
    fun String.toBoolfuck(unicode:Boolean = false):String {
        if(this == "")
            return ""
        val charSize:Int = if (unicode) 15 else 7
        val sb = StringBuilder()
        this.forEach {
            for (i in 0.. charSize)
                sb.append((it.code shr i) and 0x1)

        }
        return sb.toString()
    }
    fun String.fromBoolfuck(unicode:Boolean = false):String {
        if(this == "")
            return ""
        val charSize:Int = if (unicode) 15 else 7 + 1
        val sb = StringBuilder()
        var charCode = 0
        this.forEachIndexed {i, d->
            if(i != 0 && i % charSize == 0){
                sb.append(charCode.toChar())
                charCode = 0
            }
            charCode = charCode or (d.digitToInt() shl (i % charSize))
        }
        sb.append(charCode.toChar())
        return sb.toString()
    }




    fun interpret(code: String, input: String): String {
        var input:StringBuilder =  StringBuilder(input.toBoolfuck())
        val output = StringBuilder()
        var iIndex = -1
        var pValue:Array<Char> = Array(8){'0'}
        var pIndex = 0
        var cIndex = 0
        while (cIndex < code.length) {
            val inBounds = iIndex >= 0 && iIndex < input.length
            when (code[cIndex++]) {
                ',' -> pValue[pIndex] = (if (inBounds) input[iIndex] else '0')
                '+' -> pValue[pIndex] = if(pValue[pIndex] == '0') '1' else '0'
                ';' -> output.append(pValue[pIndex])
                '>' -> {iIndex++; pIndex = (pIndex + 1).mod(8)}
                '<' -> {iIndex--; pIndex = (pIndex - 1).mod(8)}
                '[' -> {
                    if (pValue[pIndex] == '0') {
                        val stack = Stack<Char>()
                        while (cIndex + 1 < code.length) {
                            val command = code[++cIndex]
                            if (command == ']') {
                                if (stack.isEmpty()) break else stack.pop()
                            } else if (command == '[') stack.add(command)
                        }
                    }
                }

                ']' -> {
                    if (pValue[pIndex] == '1') {
                        val stack = Stack<Char>()
                        while (cIndex > 0) {
                            val command = code[--cIndex]
                            if (command == '[') {
                                if (stack.isEmpty()) break else stack.pop()
                            } else if (command == ']') stack.push(command)
                        }
                    }
                }
            }
        }
        return output.toString().fromBoolfuck()
    }

    @Test
    fun testInputConverter(){
        assertEquals("","".fromBoolfuck())
        assertEquals("10000110", "a".toBoolfuck())
        assertEquals("1000011010000110", "aa".toBoolfuck())
        assertEquals("a", "10000110".fromBoolfuck())
        assertEquals("aa", "1000011010000110".fromBoolfuck())
        assertEquals("a", "1000011".fromBoolfuck())
        assertEquals("H","00010010".fromBoolfuck())

    }

    @Test
    fun testEmpty() {
        assertEquals(interpret("", ""), "")
        assertEquals(interpret(codeToBoolfuck(""), ""), "")
    }

    @Test
    fun testSingleCommands() {
        assertEquals(interpret("<", ""), "")
        assertEquals(interpret(">", ""), "")
        assertEquals(interpret("+", ""), "")
        assertEquals(interpret(".", ""), "")
        assertEquals(interpret(";", ""), "\u0000")
    }

    @Test
    fun testIO() {
                assertEquals(interpret(codeToBoolfuck(",."), "*"), "*")
    }

    @Test
    fun testHelloWorld() {
                                   // "0001"
        assertEquals(interpret(";;;+;+;;+;+;",""),"H")
        assertEquals(interpret(";;;+;+;;+;+;+;+;+;+;;+;;+;;;+;;+;+;;+;;;+;;+;+;;+;+;;;;+;+;;+;;;+;;+;+;+;;;;;;;+;+;;+;;;+;+;;;+;+;;;;+;+;;+;;+;+;;+;;;+;;;+;;+;+;;+;;;+;+;;+;;+;+;+;;;;+;+;;;+;+;+;", ""), "Hello, world!\n")
    }

    @Test
    fun testBasic() {

        assertEquals(interpret(">,>,>,>,>,>,>,>,<<<<<<<[>]+<[+<]>>>>>>>>>[+]+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]<<<<<<<<;>;>;>;>;>;>;>;<<<<<<<,>,>,>,>,>,>,>,<<<<<<<[>]+<[+<]>>>>>>>>>[+]+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]", "Codewars\u00ff"), "Codewars")
//        assertEquals(interpret(">,>,>,>,>,>,>,>,>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>;>;>;>;>;>;>;>;>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]>,>,>,>,>,>,>,>,>+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]", "Codewars"), "Codewars")
//        assertEquals(interpret(">,>,>,>,>,>,>,>,>>,>,>,>,>,>,>,>,<<<<<<<<+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>>>>>>>>>>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]>[>]+<[+<]>>>>>>>>>[+]>[>]+<[+<]>>>>>>>>>[+]<<<<<<<<<<<<<<<<<<+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]>>>>>>>>>>>>>>>>>>>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+<<<<<<<<[>]+<[+<]>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]<<<<<<<<<<<<<<<<<<<<<<<<<<[>]+<[+<]>>>>>>>>>[+]>>>>>>>>>>>>>>>>>>+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]<<<<<<<<<<<<<<<<<<+<<<<<<<<+[>+]<[<]>>>>>>>>>[+]+<<<<<<<<+[>+]<[<]>>>>>>>>>]<[+<]>>>>>>>>>>>>>>>>>>>;>;>;>;>;>;>;>;<<<<<<<<", "\u0008\u0009"), "\u0048")
    }
}