import org.junit.jupiter.api.Test

const val EOT = '\u0004'
const val EOL = '\u000A'
const val CHAR_QUOTE = '\''
const val STRING_QUOTE = '\"'
class `To BrainFuck Transpiler` {
    //https://www.codewars.com/kata/59f9cad032b8b91e12000035

    enum class TokenType{
        BASIC,
        KEYWORD,
        OPERATOR,
    }
    enum class Token(val type:TokenType, val token:String){
        BLANK(TokenType.BASIC,""),
        EOT(TokenType.BASIC, ""),
        EOL(TokenType.BASIC, ""),
        VARIABLE(TokenType.BASIC, ""),
        STRING(TokenType.BASIC, ""),
        CHARACTER(TokenType.BASIC, ""),
        DIGIT(TokenType.BASIC, ""),
        NUMBER(TokenType.BASIC, ""),

        VAR(TokenType.KEYWORD, "var"),
        SET(TokenType.KEYWORD, "set"),

        INC(TokenType.OPERATOR, "inc"),
        DEC(TokenType.OPERATOR, "dec"),
        SUB(TokenType.OPERATOR, "sub"),
        MUL(TokenType.OPERATOR, "mul"),
        DIV(TokenType.OPERATOR, "div"),
        DIVMOD(TokenType.OPERATOR, "divmod"),
        MOD(TokenType.OPERATOR, "mod"),

        CMP(TokenType.OPERATOR, "cmp"),
        A2B(TokenType.OPERATOR, "a2b"), //ASCII to byte
        B2A(TokenType.OPERATOR, "b2a"), //BYTE to ASCII

        LSET(TokenType.OPERATOR, "lset"), //List set
        LGET(TokenType.OPERATOR, "lget"), //List get

        IFEQ(TokenType.KEYWORD, "ifeq"),
        IFNEQ(TokenType.KEYWORD, "ifneq"),
        WNEQ(TokenType.KEYWORD, "wneq"),
        PROC(TokenType.KEYWORD, "proc"),
        END(TokenType.KEYWORD, "end"),
        CALL(TokenType.KEYWORD, "call"),

        READ(TokenType.KEYWORD, "read"),
        MSG(TokenType.KEYWORD, "msg"),

        REM(TokenType.KEYWORD, "rem"),
    }

    data class TokenProps(val token:Token, val startPos:Int, val endPos:Int=startPos){}
    class LexerException(message:String):Exception(message)
    class Lexer(private val source:String){

        private var curIndex = -1
        private var curChar:Char = EOT
        private val resolvedTokens:MutableList<TokenProps> = mutableListOf()
        val tokens:List<TokenProps>
            get() = resolvedTokens

        init {
            nextChar()
            while (getToken().token != Token.EOT);
        }

        private fun isWhiteSpace(char:Char) = char == ' ' || char == '\t' || char == '\r'
        private fun nextChar() = let { curChar = if(++curIndex >= source.length) EOT else source[curIndex];curChar}

        private fun nextCharOrThrow(message: String = "",vararg chars:Char) = let { if(nextChar() in chars) throw LexerException("$message\nCharacter '$curChar'on position: $curIndex is invalid");curChar }

        private fun peek() = if(curIndex + 1 >= source.length) EOT else source[curIndex + 1]

        private fun skipWhiteSpaces() = run { while (isWhiteSpace(curChar)) nextChar() }

        private fun skipComment() = run {if(curChar == '/' && peek() == '/' || curChar == '-' && peek() == '-' || curChar == '#') while (nextChar() != EOL && curChar != EOT);}

        private fun getToken():TokenProps{
            var token:TokenProps? = null
            while (token == null) {
                skipWhiteSpaces()
                skipComment()
                var startPos = curIndex

                when (curChar) {
                    EOT -> token = TokenProps(Token.EOT, curIndex)
                    EOL -> token = TokenProps(Token.EOL, curIndex)
                    CHAR_QUOTE -> {
                        while (nextCharOrThrow("Character resolution failed", EOL, EOT) != CHAR_QUOTE);
                        val charPos = startPos + 1
                        val ch = source.subSequence(charPos, curIndex)
                        if (ch.length != 1)
                            throw LexerException("Character $ch at $charPos is invalid")
                        token = TokenProps(if (ch[0].isDigit()) Token.DIGIT else Token.CHARACTER, charPos)

                    }

                    STRING_QUOTE -> {
                        while (nextCharOrThrow("String resolution failed", EOL, EOT) != STRING_QUOTE);
                        token = TokenProps(Token.STRING, startPos + 1, curIndex)
                    }

                    else -> {

                    }
                }
                nextChar()
            }

            println(token)
            return token
        }
    }

    @Test
    fun test_Lexer(){
        var source = """
            var character = 'C'
            var digit = '0'
            car string = "String string string"
            var q w e\n
            read q\n
            read w\n
            add q w e\n
            msg q " " w " " e\n
            """.trimIndent()

        var source1 = """
            var character = 'C'
            """.trimIndent()

       Lexer(source1).tokens//.map(::println)
        val a =1


    }




}