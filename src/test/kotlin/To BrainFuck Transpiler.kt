import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.reflect.KClass
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
    enum class Token(val type:TokenType, val id:String){
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

        SET(TokenType.OPERATOR, "set"),
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

    data class TokenProps(val token:Token, val startPos:Int, val endPos:Int=startPos, val id:String = token.id){
        public infix fun <B:Any> to(that: KClass<B>): B {
            try {
                return when (that) {
                    Int::class -> this.id.toInt()
                    Char::class-> this.id[0]
                    else -> throw ParserException("Unsupported type cast $this")
                } as B
            }catch (e:Exception){
                throw ParserException("Invalid casting type exception of $this")
            }

        }
    }
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
                    peek() in (WHITESPACE_CHARS + EOL + '/' + '-' + '#' + EOT)-> {
                        val endPos = curIndex + 1
                        val declaration = source.substring(startPos,endPos)
                        Token.values().forEach {
                            if(it.id.equals(declaration,true)) {
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
    class Parser(val lexer:Lexer, debug: Boolean = false){
        private var currentTokenIndex:Int = -1
        private var currentToken:TokenProps = TokenProps(Token.EOL,0,0)

        val interpreter = Interpreter(debug)

        init {
            if(lexer.tokens.isEmpty() || lexer.tokens.last().token != Token.EOT)
                throw ParserException("There is no tokens for parsing")
            nextSignificantToken()
            program()
        }

        private fun nextToken():TokenProps = let{currentToken = if(currentToken.token == Token.EOT) currentToken else lexer.tokens[++currentTokenIndex];currentToken}
        private fun nextSignificantToken() = run { while (nextToken().token == Token.EOL);}

        private fun isEndOfStatement(token:Token = currentToken.token) = token == Token.EOL || token == Token.EOT
        private fun peekToken():TokenProps = lexer.tokens[currentTokenIndex + 1]

        private fun program(){
            while (currentToken.token != Token.EOT)
                statement()

        }
        private fun statement(){
            if(currentToken.token.type != TokenType.KEYWORD && currentToken.token.type != TokenType.OPERATOR && currentToken.token != Token.EOL)
                throw ParserException("Token type misplaced: $currentToken")

            when(currentToken.token){
                Token.VAR -> variableDeclaration()
                Token.READ -> ioRead()
                Token.MSG -> ioWrite()
                Token.SET,Token.INC,Token.DEC -> unaryOperator()
                else -> {}//throw  ParserException("Misplaced token: $currentToken")
            }
            nextSignificantToken()

        }
        private fun variableDeclaration(){
            while (!isEndOfStatement(nextToken().token)){
                if(currentToken.token != Token.VAR_NAME)
                    throw ParserException("Declaration variable name expected instead of: $currentToken")
                val id = currentToken.id
                if(peekToken().token == Token.LBRAKET){
                    nextToken();nextToken()
                    val size = currentToken.id.toIntOrNull()
                    if(size == null || size < 0)
                        throw ParserException("Positive number is expected $currentToken")
                    interpreter.mapVariable(id,size*2)

                }else
                    interpreter.mapVariable(id,2)
            }
        }
        private fun ioRead(){
            if(nextToken().token != Token.VAR_NAME)
                 throw ParserException("Variable name expected: $currentToken")
            interpreter.ioRead(currentToken.id)

        }
        private fun ioWrite(){
            while (!isEndOfStatement(nextToken().token)) {
                    when {
                        currentToken.token == Token.STRING -> interpreter.ioWriteString(currentToken.id)
                        currentToken.token == Token.CHARACTER -> interpreter.ioWriteString("${currentToken.id}")
                        currentToken.token == Token.VAR_NAME -> interpreter.ioWriteVariable(currentToken.id)
                    }
                }
        }

        private fun unaryOperator(){
            val operator = currentToken
            if(nextToken().token != Token.VAR_NAME)
                throw ParserException("Unary operator: ${operator.token} Expected a variable instead of  ${currentToken}")

            var varId = currentToken.id
            try {
                when (operator.token) {
                    Token.SET -> when (nextToken().token) {
                        Token.VAR_NAME -> interpreter.set(varId, currentToken.id)
                        Token.NUMBER -> interpreter.set(varId, currentToken to Int::class)
                        Token.CHARACTER -> interpreter.set(varId, currentToken to Char::class)
                        else -> throw ParserException("")
                    }
                    Token.INC -> when (nextToken().token) {
                        Token.NUMBER -> interpreter.inc(varId, currentToken to Int::class)
                        else -> throw ParserException("")
                    }
                    Token.DEC -> when (nextToken().token) {
                        Token.NUMBER -> interpreter.dec(varId, currentToken to Int::class)
                        else -> throw ParserException("")
                    }
                    else -> {}
                }
            }catch (e : ParserException){
                throw ParserException("Una operator: ${operator.token} does not support $currentToken as argument")
            }


        }


    }
    class InterpreterException(message:String):Exception(message)
    class Interpreter(val debug:Boolean = false){
        private val memoryMap:MutableMap<String,Int> = mutableMapOf()
        private val varValues:MutableMap<String,Int> = mutableMapOf()
        private var freeMemPointer = 0
        private var currentMemPointer = 0
        private val output:StringBuilder = StringBuilder()

        private fun moveToPointer(id:String) = moveToPointer(memoryMap[id] ?: throw InterpreterException("Variable id: $id is not mapped in memory"))
        private fun moveToPointer(pointer:Int){
            val output = StringBuilder()
            val start = currentMemPointer
            val (addition,symbol) = if(pointer >= currentMemPointer) (1 to '>') else (-1 to '<')
            while (currentMemPointer != pointer) {
                currentMemPointer += addition
                output.append(symbol)
            }
            if(debug) println("Moving pointer from $start to $currentMemPointer: $output" )
            this.output.append(output)
        }
        fun mapVariable(id:String, size:Int):Int{
            if(debug) println("Mapping variable $id to index $freeMemPointer with size: $size")
            memoryMap.put(id,freeMemPointer)
            freeMemPointer += size
            return freeMemPointer - size
        }
        fun ioRead(id:String){
            moveToPointer(id)
            output.append(",")
        }
        fun ioWriteString(str:String){
            moveToPointer(mapVariable("#$str",2))
            generateOutputFor(str)
        }
        private fun generateOutputFor(str:String){
            var currentMemCelValue = 0
            val output = StringBuilder()
            str.forEach {
                if(currentMemCelValue == it.code)
                    output.append(".")
                else {
                    val (sign, symbol) = if (it.code >= currentMemCelValue) (1 to '+') else (-1 to '-')
                    val delta = abs(it.code - currentMemCelValue)
                    if(delta > 4) {
                        val mult = floor(sqrt(abs(delta).toDouble())).toInt()
                        val reminder = if (mult == 0) delta else delta % (mult * mult)
                        output.append('>').append(CharArray(mult) { '+' }).append("[<")
                            .append(CharArray(mult) { symbol }).append(">-]<")
                            .append(CharArray(reminder) { symbol })
                    }else
                        output.append(CharArray(delta) { symbol })
                    output.append('.')
                    currentMemCelValue = it.code
                }
            }
            if(debug) println("Generated string output for $str: $output")
            this.output.append(output)
        }
        private fun optimizedAddition(aValue:Int){
            if(aValue == 0) return
            val output = StringBuilder()
            val symbol = if (aValue > 0) '+' else '-'
            val add = abs(aValue)
            if(add > 4) {
                val mult = floor(sqrt(abs(add).toDouble())).toInt()
                val reminder = if (mult == 0) add else add % (mult * mult)
                output.append('>').append(CharArray(mult) { '+' }).append("[<")
                    .append(CharArray(mult) { symbol }).append(">-]<")
                    .append(CharArray(reminder) { symbol })
            }else
                output.append(CharArray(add) { symbol })
            if(debug)
                println("Optimized addition of $aValue : $output")
            this.output.append(output)
        }
        fun ioWriteVariable(id:String){
            try {
                moveToPointer(id)
                output.append(".")
                if(debug) println("Writing variable $id to output")
            }catch (e:InterpreterException){
                println(e.message + "\nwriting to output as text!!!")
                ioWriteString(id)
            }

        }
        fun set(setId:String, getId:String){
            varValues[getId]?.let {
                set(setId,it)
            }?: throw InterpreterException("Variable copy is not supported")
        }
        fun set(setId: String,char: Char) = inc(setId,char.code)
        fun set(setId:String, number:Int) = inc(setId,number)
        fun inc(id:String, number: Int){
            moveToPointer(id)
            optimizedAddition(number)
        }
        fun dec(id:String, number: Int) = inc(id,-number)

        override fun toString() = output.toString()
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
    @Test
    fun parserTest(){

        val source = """
		var X//This is a comment
		read X--This is also a comment
		msg "BByeeg" X#No doubt it is a comment
		rem &&Some comment~!@#$":<
		""".trimIndent()
        //,"?","Bye?")

        val result = Parser(Lexer(source)).interpreter.toString()
        val a = 1

    }

    @Test
    fun `FixedTest 0 | Basic 1 | Works for set, inc, dec`()
    {
       var source = """
		var A B
		sEt A 'a'
		msg a B
		set B 50
		msG A b
		inc A 10
		dec B -20
		msg A B
		""".trimIndent()

        val result = Parser(Lexer(source),true).interpreter.toString()
        println("result: $result" )
        val a = 1
//        ,"","a\u0000a2kF")
    }




}