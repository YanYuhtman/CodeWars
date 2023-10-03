import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

const val EOT = '\u0004'
const val EOL = '\u000A'
const val CHAR_QUOTE = '\''
const val STRING_QUOTE = '\"'
val WHITESPACE_CHARS = charArrayOf(' ','\t','\r')
class `To BrainFuck Transpiler` {
    //https://www.codewars.com/kata/59f9cad032b8b91e12000035

    enum class TokenType{
        BASIC,
        ID,
        KEYWORD,
        OPERATOR,
    }
    enum class Token(val type:TokenType, val token:String){
        EOT(TokenType.BASIC, "$EOT"),
        EOL(TokenType.BASIC, "$EOL"),
        VAR_NAME(TokenType.ID, ""),
        STRING(TokenType.BASIC, ""),
        CHARACTER(TokenType.BASIC, ""),
//        DIGIT(TokenType.BASIC, ""),
        NUMBER(TokenType.BASIC, ""),

        LBRAKET(TokenType.KEYWORD, "["),
        RBRAKET(TokenType.KEYWORD, "]"),
        COMMA(TokenType.KEYWORD, ","),
        VAR(TokenType.KEYWORD, "var"),
        SET(TokenType.KEYWORD, "set"),

        INC(TokenType.OPERATOR, "inc"),
        DEC(TokenType.OPERATOR, "dec"),
        ADD(TokenType.OPERATOR, "add"),
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

    data class TokenProps(val token:Token, val startPos:Int, val endPos:Int=startPos, val id:String = token.token){}
    class LexerException(message:String):Exception(message)
    class Lexer(private val source:String, val debug:Boolean = false){

        private var curIndex = -1
        private var curChar:Char = EOT
        private val resolvedTokens:MutableList<TokenProps> = mutableListOf()
        val tokens:List<TokenProps>
            get() = resolvedTokens

        init {
            nextChar()
            while (getToken().token != Token.EOT);
        }

        private fun isWhiteSpace(char:Char) = WHITESPACE_CHARS.any{it == char}
        private fun nextChar() = let { curChar = if(++curIndex >= source.length) EOT else source[curIndex];curChar}
        private fun nextWhiteSpace():Int{
            curIndex = source.indexOfAny(WHITESPACE_CHARS,curIndex)
            if(curIndex == -1)
                curIndex = source.length
            curChar = source.elementAt(curIndex);
            return curIndex
        }

        private fun nextCharOrThrow(message: String = "",vararg chars:Char) = let { if(nextChar() in chars) throw LexerException("$message\nCharacter '$curChar'on position: $curIndex is invalid");curChar }

        private fun peek() = if(curIndex + 1 >= source.length) EOT else source[curIndex + 1]

        private fun skipWhiteSpaces() = run { while (isWhiteSpace(curChar)) nextChar() }

        private fun skipComment() = run {if(curChar == '/' && peek() == '/' || curChar == '-' && peek() == '-' || curChar == '#' || source.substring(curIndex).startsWith("rem")) while (nextChar() != EOL && curChar != EOT);}

        private fun skipCommentsAndWhiteSpaces(){skipWhiteSpaces();skipComment();skipWhiteSpaces()}

        private fun getToken():TokenProps{
            var token:TokenProps? = null
            skipCommentsAndWhiteSpaces()
            var startPos = curIndex
            while (token == null) {
                when {
                    curChar == EOT -> token = TokenProps(Token.EOT, curIndex)
                    curChar == EOL -> token = TokenProps(Token.EOL, curIndex)
                    curChar == '[' -> token = TokenProps(Token.LBRAKET, curIndex)
                    curChar == ']' -> token = TokenProps(Token.RBRAKET, curIndex)
                    curChar == ',' -> token = TokenProps(Token.COMMA, curIndex)
                    curChar == CHAR_QUOTE -> {
                        while (nextCharOrThrow("Character resolution failed", EOL, EOT) != CHAR_QUOTE);
                        val charPos = startPos + 1
                        val ch = source.subSequence(charPos, curIndex)
                        if (ch.length != 1)
                            throw LexerException("Character $ch at $charPos is invalid")
                        token = TokenProps(Token.CHARACTER, charPos, id=source.substring(charPos,curIndex))

                    }

                    curChar == STRING_QUOTE -> {
                        while (nextCharOrThrow("String resolution failed", EOL, EOT) != STRING_QUOTE);
                        token = TokenProps(Token.STRING, startPos + 1, curIndex, source.substring(startPos+1,curIndex))
                    }
                    peek() in (WHITESPACE_CHARS + EOL + '/' + '-' + '#')-> {
                        val endPos = curIndex + 1
                        val declaration = source.substring(startPos,endPos)
                        Token.values().forEach {
                            if(it.token.equals(declaration,true)) {
                                token = TokenProps(it,startPos,endPos)
                                return@forEach
                            }
                        }
                        if(token == null) {
                            token = if(declaration.matches("-?\\d+".toRegex()))
                                TokenProps(Token.NUMBER,startPos,curIndex,declaration)
                            else {
                                if (!declaration.matches("[_\$A-Za-z]+\\d*".toRegex()))
                                    throw LexerException("Declaration: $declaration at [$startPos,$endPos] does not match variable pattern")
                                TokenProps(Token.VAR_NAME, startPos, endPos, declaration)
                            }
                        }
                    }
                }
                nextChar()
            }
            if (debug)
                println(token)

            resolvedTokens.add(token!!)
            return token!!
        }
    }


    class ParserException(message:String):Exception(message)
    class Parser(val lexer:Lexer){
        private var currentTokenIndex:Int = -1
        private lateinit var currentToken:TokenProps

        init {
            if(lexer.tokens.isEmpty() || lexer.tokens.last().token != Token.EOT)
                throw ParserException("There is no tokens for parsing")
            nextToken()
        }

        private fun nextToken():TokenProps = let{currentToken = lexer.tokens[++currentTokenIndex];currentToken}
        private fun peekToken():TokenProps = lexer.tokens[currentTokenIndex + 1]

        private fun program(){
            while (currentToken.token != Token.EOT)
                statement()
        }
        private fun statement(){

        }





    }

    @Test
    fun test_Lexer(){
        var source = """
            var num -222
             var num 222
            var digit '0'
            var string "String string string"
            var q w e
            read q
            read w
            add q w e
            msg q " " w " " e
    
            """.trimIndent()

//        assertEquals(28, Lexer(source, true).tokens.size)
//        println()
//        source = """
//            var L  [ 20, 21 ]  I X
//            var character 'C'
//            """.trimIndent()
//
//        assertEquals(13, Lexer(source, true).tokens.size)


        source = """
            var X//This is a comment
            read X--This is also a comment
            mSg "Bye" X#No doubt it is a comment
            rem &&Some comment~!@#${'$'}":<
            """.trimIndent()
        assertEquals(13, Lexer(source, true).tokens.size)


    }




}