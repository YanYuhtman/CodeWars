import org.junit.jupiter.api.Test
import java.lang.NullPointerException
import java.util.Stack
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.assertEquals

fun brainFuckParse(command:String, input:String):String{
    println("Executing BrainFuck for code: $command and input: $input")
    val output = StringBuilder()
    val tape = Array(10000){Char(0)}
    var tapePointer = 0
    var codePointer = -1
    var inputPointer = 0

    while (++codePointer < command.length){
        when(command[codePointer]){
            '>' -> tapePointer++
            '<' -> tapePointer--
            '+' -> tape[tapePointer]++
            '-' -> tape[tapePointer]--
            '.' -> output.append(tape[tapePointer])
            ',' -> tape[tapePointer] = input[inputPointer++]
            '[' -> {

                if(tape[tapePointer] == Char(0)){
                    var counter = 1
                    while (counter > 0)
                        counter += (if(command[++codePointer] == '[') 1 else if (command[codePointer] == ']') -1 else 0)
                }
            }
            ']' -> {
                if(tape[tapePointer] != Char(0)){
                    var counter = 1
                    while (counter > 0)
                        counter += (if(command[--codePointer] == ']') 1 else if (command[codePointer] == '[') -1 else 0)
                }
            }
        }
    }
    println(output.map { "%04d".format(it.code and 0xFF) })
    println(output.map { "\\u%04x".format(it.code and 0xFF) })
    return output.map { (it.code and 0xFF).toChar() }.joinToString("")
}

const val EOT = '\u0004'
const val EOL = '\u000A'
const val CHAR_QUOTE = '\''
const val STRING_QUOTE = '\"'
val WHITESPACE_CHARS = charArrayOf(' ','\t','\r')
const val VARIABLE_SIZE = 2
const val CMP_SIZE = 6
fun generateTempVariableId(original:String) = "${Random.nextBytes(1)}#$original"
const val ARRAY_OP_SIZE = 10
class `To BrainFuck Transpiler` {
    //https://www.codewars.com/kata/59f9cad032b8b91e12000035

    enum class TokenType{
        BASIC,
        ID,
        KEYWORD,
        OPERATOR,
    }
    enum class Token(val type:TokenType, val id:String){
        DUMMY(TokenType.BASIC, ""),
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
    data class TokenProps(val token:Token, val startPos:Int, val endPos:Int=startPos, var id:String = token.id){
        constructor(value:Int):this(Token.NUMBER,0,0,value.toString())
        constructor(char: Char):this(Token.CHARACTER,0,0,"$char")
        constructor(v: String):this(Token.VAR_NAME,0,0,v)
        fun toInt():Int = when(this.token){
            Token.NUMBER -> this.id.toInt()
            Token.CHARACTER -> this.id[0].code
            else-> throw ParserException("Unsupported cast of ${this.token} to Int")
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

        private fun skipWhiteSpaces() { while (isWhiteSpace(curChar)) nextChar() }

        private fun skipComment() {if(curChar == '/' && peek() == '/' || curChar == '-' && peek() == '-' || curChar == '#' || source.substring(curIndex).startsWith("rem")) while (nextChar() != EOL && curChar != EOT);}

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
                    peek() in (WHITESPACE_CHARS + EOL + '/' + '-' + '#' + EOT + ']')-> {
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
                                TokenProps(Token.VAR_NAME, startPos, endPos, declaration.uppercase())
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
        private var programTokens:MutableMap<TokenProps,TokenProps> = mutableMapOf()

        val interpreter = Interpreter(debug)

        private fun mapProgramTokens():TokenProps{
            val blockStack:Stack<TokenProps> = Stack<TokenProps>().apply { push(currentToken) }
            while (currentToken.token != Token.EOT)
                when(nextToken().token){
                    Token.IFEQ,Token.IFNEQ,Token.WNEQ,Token.PROC -> blockStack.push(currentToken)
                    Token.END -> programTokens[blockStack.pop()] = currentToken
                    else -> {}
                }
            programTokens.put(blockStack.peek(),currentToken)
            setTokenIndex(0)
            return programTokens[blockStack.pop()]!!
        }
        init {
            if(lexer.tokens.isEmpty() || lexer.tokens.last().token != Token.EOT)
                throw ParserException("There is no tokens for parsing")
            nextSignificantToken()
            program(mapProgramTokens())
        }

        private fun setTokenIndex(tokenIndex:Int){currentTokenIndex = tokenIndex; currentToken = lexer.tokens[currentTokenIndex] }
        private fun nextToken():TokenProps = let{currentToken = if(currentToken.token == Token.EOT) currentToken else lexer.tokens[++currentTokenIndex];currentToken}
        private fun nextSignificantToken() = run { while (nextToken().token == Token.EOL);}

        private fun isEndOfStatement(token:Token = currentToken.token) = token == Token.EOL || token == Token.EOT
        private fun peekToken():TokenProps = lexer.tokens[currentTokenIndex + 1]

        private fun program(endToken:TokenProps){
            while (currentToken != endToken)
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
                Token.ADD,Token.SUB,Token.MUL,Token.DIV,Token.MOD,Token.CMP -> mulArgumentsOperator(3)
                Token.DIVMOD -> mulArgumentsOperator(4)
                Token.B2A,Token.A2B -> mulArgumentsOperator(4)
                Token.LSET,Token.LGET -> listArgumentsOperator()
                Token.WNEQ -> whileNEQ()

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
                    if(size == null || size < 1)
                        throw ParserException("Positive number is expected $currentToken")
                    if(nextToken().token != Token.RBRAKET)
                        throw ParserException("${Token.LBRAKET} token expected")
                    interpreter.mapVariable(id,ARRAY_OP_SIZE + size * VARIABLE_SIZE + 2)

                }else
                    interpreter.mapVariable(id,VARIABLE_SIZE)
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
                        Token.NUMBER,Token.CHARACTER -> interpreter.set(varId, currentToken.toInt())
                        else -> throw ParserException("")
                    }
                    Token.INC -> when (nextToken().token) {
                        Token.NUMBER -> interpreter.inc(varId, currentToken.toInt())
                        else -> throw ParserException("")
                    }
                    Token.DEC -> when (nextToken().token) {
                        Token.NUMBER -> interpreter.dec(varId, currentToken.toInt())
                        else -> throw ParserException("")
                    }
                    else -> {}
                }
            }catch (e : ParserException){
                throw ParserException("Una operator: ${operator.token} does not support $currentToken as argument")
            }


        }

        private fun mulArgumentsOperator(numberOfArguments:Int = 2){
            val operator = currentToken
            val list:Array<TokenProps> = Array(numberOfArguments){ TokenProps(Token.DUMMY,0,0,"") }
            for(i in 0 until list.lastIndex) {
                list[i] = nextToken()
                when(list[i].token){
                    Token.VAR_NAME->{}
                    Token.NUMBER,Token.CHARACTER -> {
                        val value = list[i].toInt()
                        list[i] = TokenProps(Token.VAR_NAME,list[i].startPos,list[i].endPos,generateTempVariableId("bVar$i"))
                        interpreter.set(list[i].id,value)
                    }
                    else -> throw ParserException("For binary operation argument $i token ${list[i].token} is invalid")
                }
            }
            list[list.lastIndex] = nextToken(); if(list[list.lastIndex].token != Token.VAR_NAME) throw ParserException("For operator: $operator ${list.lastIndex} argument last argument must be a VARIABLE")
            when(operator.token){
                Token.ADD-> interpreter.add(list)
                Token.SUB->interpreter.sub(list)
                Token.MUL->interpreter.mul(list)
                Token.DIVMOD->{if(list[list.lastIndex-1].token != Token.VAR_NAME) throw ParserException("For operator: $operator ${list.lastIndex-1} argument last argument must be a VARIABLE")
                    interpreter.divMod(list)}
                Token.DIV->interpreter.div(list)
                Token.MOD->interpreter.mod(list)
                Token.CMP->interpreter.cmp(list)
                Token.A2B,Token.B2A-> {
                    for(i in 1 .. list.lastIndex)
                        if(list[i].token != Token.VAR_NAME) throw ParserException("For operator: $operator ${i} argument last argument must be a VARIABLE")
                    if(operator.token == Token.B2A) interpreter.b2a(list) else interpreter.a2b(list)
                }
                else -> throw ParserException("Operator: $operator is not supported")
            }
        }
        private fun listArgumentsOperator(){
            val list:Array<TokenProps> = Array(4){ TokenProps(Token.DUMMY,0,0,"")}
            list[0] = currentToken
            list[1] = nextToken()
            if(currentToken.token != Token.VAR_NAME)
                throw ParserException("Expected argument for variable name ${list[0]}")

            list[2] = nextToken()
            if( list[2].token != Token.NUMBER && currentToken.token != Token.VAR_NAME)
                throw ParserException("Expected number argument token as list index (first) for operator ${list[0]}")

            list[3] = nextToken()
            if(list[0].token == Token.LGET && list[3].token != Token.VAR_NAME)
                throw ParserException("Expected variable argument token as set value (second) for operator ${list[0]}")
            interpreter.lSetGet(list)
        }

        private fun whileNEQ(){
           val (flagPtr,flagName) = interpreter.mapVariable(generateTempVariableId(currentToken.token.id),CMP_SIZE)
           val tokens:Array<TokenProps> = arrayOf(currentToken,nextToken(),nextToken())
           if(tokens[1].token != Token.VAR_NAME || (tokens[2].token != Token.VAR_NAME && tokens[2].token != Token.NUMBER))
               throw ParserException("Not supported argument for ${tokens[0].token} token")
           interpreter.whileNEQ(arrayOf(tokens[0],tokens[1],tokens[2], TokenProps(flagName)))
           nextSignificantToken()
           program(programTokens[tokens[0]]!!)
           interpreter.whileNEQ(arrayOf(currentToken,tokens[1],tokens[2],TokenProps(flagName)));
        }


    }
    class InterpreterException(message:String):Exception(message)
    class Interpreter(val debug:Boolean = false){
        private val memoryMap:MutableMap<String,Int> = mutableMapOf()
        private var freeMemPointer = 0
        private var currentMemPointer = 0
        private val output:StringBuilder = StringBuilder()

        private fun moveToPointer(id:String) = moveToPointer(memoryMap[id] ?: throw InterpreterException("Variable id: $id is not mapped in memory"))
        private fun moveToPointer(pointer:Int):StringBuilder{
            if(pointer == currentMemPointer)
                return output
            val oIndex = output.length
            val start = currentMemPointer
            val (addition,symbol) = if(pointer >= currentMemPointer) (1 to '>') else (-1 to '<')
            while (currentMemPointer != pointer) {
                currentMemPointer += addition
                output.append(symbol)
            }
            if(debug) println("Moving pointer from $start to $currentMemPointer: ${output.substring(oIndex)}" )
            return output
        }

        fun mapVariable(id:String, size:Int = VARIABLE_SIZE, ptr:Int = freeMemPointer):Pair<Int,String>{
            if(debug) println("Mapping token $id to index $ptr with size: $size")
            memoryMap[id] = ptr
            if(ptr + size > freeMemPointer)
                freeMemPointer = ptr + size
            return ptr to id
        }
        private fun <T:Any> mapConstant(value:T, size: Int = VARIABLE_SIZE, ptr: Int = freeMemPointer):TokenProps{
            val token = when{
                value is Int -> TokenProps(value as Int)
                value is Char -> TokenProps(value as Char)
                 else -> throw InterpreterException("Type ${value::class} is not supported for constant mapping")
            }
            mapVariable(value.toString(), size)
            set(value.toString(), value)
            return token
        }

        fun ioRead(id:String){
            moveToPointer(id)
            output.append(",")
        }
        fun ioWriteString(str:String){
            moveToPointer(mapVariable(generateTempVariableId("str"),2).first)
            generateOutputFor(str)
        }
        private fun generateOutputFor(str:String){
            var currentMemCelValue = 0
            val ouIndex = output.length
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
            if(debug) println("Generated string output for $str: ${output.substring(ouIndex)}")

        }
        private fun addition(pointer:Int, aValue: Int, optimized:Boolean = true) {moveToPointer(pointer);addition(aValue,optimized)
        }
        private fun addition(aValue:Int, optimized:Boolean = true,output: StringBuilder? = this.output):String{
            if(aValue == 0) return ""

            val output = if(output == null) StringBuilder() else output
            val oIndex = output.length
            val symbol = if (aValue > 0) '+' else '-'
            val add = abs(aValue)
            if(optimized && add > 4) {
                val mult = floor(sqrt(abs(add).toDouble())).toInt()
                val reminder = if (mult == 0) add else add % (mult * mult)
                output.append('>').append(CharArray(mult) { '+' }).append("[<")
                    .append(CharArray(mult) { symbol }).append(">-]<")
                    .append(CharArray(reminder) { symbol })
            }else
                output.append(CharArray(add) { symbol })

            if(debug) println("Optimized addition of $aValue : ${output.substring(oIndex)}")
            return output.toString()

        }
        fun ioWriteVariable(id:String){
            moveToPointer(id)
            output.append(".")
            if (debug) println("Writing variable $id to output")
        }


        fun <T:Any> set(id:String,value:T){
            if(debug) println("Setting variable $id from variable $value")
            when{
                value is String -> {
                    clear(id)
                    copy(memoryMap[value]!!,memoryMap[id]!!)
                }
                value is Int ->  {clear(id); inc(id,value and 0xFF) }
                value is Char -> {clear(id) ;inc(id,value.code)}
                else -> throw InterpreterException("Argument type ${value::class} is not supported for SET function ")
            }
        }
        fun inc(id:String, number: Int){
            moveToPointer(id)
            addition(number)
        }
        private fun clear(id:String) = mapVariable(id,VARIABLE_SIZE)//clear(memoryMap[id]!!)

        private fun clear(pointer:Int){
            moveToPointer(pointer)
            output.append("[-]")
            if(debug) println("Clearing pointer index: $pointer")
        }

        private fun copy(fromId:String, toId: String) = copy(memoryMap[fromId]!!,memoryMap[toId]!!)
        private fun copyToTempVariable(original:TokenProps):TokenProps{
            val tmpToken = original.copy()
            tmpToken.id = generateTempVariableId(tmpToken.id)
            mapVariable(tmpToken.id,VARIABLE_SIZE)
            copy(original.id,tmpToken.id)
            if (debug) println("Copying var ${original.id} to tempVariable ${tmpToken.id}")
            return tmpToken
        }
        private fun copy(fromPtr:Int, toPtr:Int):Int{
            var oIndex = this.output.length
            moveToPointer(fromPtr)
            output.append("[->+")
            currentMemPointer++
            moveToPointer(toPtr).append("+")
            moveToPointer(fromPtr).append("]")
            recombine(fromPtr)//.append(">[-<+>]<")
            if(debug) println("Copy $fromPtr to $toPtr: ${output.substring(oIndex)}" )
            return toPtr
        }

        private fun recombine(id:String) = recombine(memoryMap[id]!!)
        private fun recombine(ptr:Int) = moveToPointer(ptr).append(">[-<+>]<")

        private fun sub(fromPtr: Int, thatPtr:Int){
            var oIndex = this.output.length
            moveToPointer(thatPtr)
            output.append("[->+")
            currentMemPointer++
            moveToPointer(fromPtr + 1).append("+<-")
            currentMemPointer--
            moveToPointer(thatPtr).append("]")
            if(debug) println("sub fromPtr: $fromPtr, thatPtr: $thatPtr ${output.substring(oIndex)}" )

        }

        fun dec(id:String, number: Int) = inc(id,-number)

        fun add(tokens:Array<TokenProps>){
            var oIndex = output.length
            for(i in 0..1)
                when(tokens[i].token){
                    Token.VAR_NAME -> copy(memoryMap[tokens[i].id]!!,freeMemPointer)
                    Token.NUMBER,Token.CHARACTER-> addition(freeMemPointer,tokens[i].toInt())
                    else->throw InterpreterException("Unsupported token: ${tokens[i]} for addition")
                }
            mapVariable(tokens[2].id,VARIABLE_SIZE)
            if(debug) println("Operator ADD: ${output.substring(oIndex)}")
        }
        fun sub(tokens:Array<TokenProps>){
            var oIndex = output.length
            when(tokens[1].token){
                Token.NUMBER,Token.CHARACTER -> {
                    copy(memoryMap[tokens[0].id]!!,freeMemPointer)
                    addition(freeMemPointer,-(tokens[1].toInt()))
                }
                Token.VAR_NAME -> {
                    sub(memoryMap[tokens[0].id]!!,memoryMap[tokens[1].id]!!)
                    copy(memoryMap[tokens[0].id]!!,freeMemPointer)

                    recombine(memoryMap[tokens[0].id]!!)
                    recombine(memoryMap[tokens[1].id]!!)

                }
                else->throw InterpreterException("Unsupported token: ${tokens[1]} for subtraction ")
            }
            mapVariable(tokens[2].id,VARIABLE_SIZE)
            if(debug) println("Operator SUB: ${output.substring(oIndex)}")
        }

        fun mul(tokens:Array<TokenProps>){
            var oIndex = output.length
            if(tokens[0].token == Token.VAR_NAME && tokens[0].id == tokens[1].id)
                tokens[1] = copyToTempVariable(tokens[1])

            moveToPointer(tokens[1].id).append("[->+")
            currentMemPointer++
            copy(memoryMap[tokens[0].id]!!,freeMemPointer)
            moveToPointer(tokens[1].id).append("]")
            recombine(memoryMap[tokens[1].id]!!)
            mapVariable(tokens[2].id,VARIABLE_SIZE)

            if(debug) println("Operator MUL: ${output.substring(oIndex)}")


        }
        fun mod(tokens:Array<TokenProps>) = divMod(tokens,2)
        fun div(tokens:Array<TokenProps>) = divMod(tokens,1)
        fun divMod(tokens:Array<TokenProps>) = divMod(tokens, 0)
        private fun divMod(tokens:Array<TokenProps>, mode:Int = 0){
            var oIndex = output.length
            val nPtr = copy(memoryMap[tokens[0].id]!!,freeMemPointer)
            freeMemPointer+=VARIABLE_SIZE
            val dPtr = copy(memoryMap[tokens[1].id]!!,freeMemPointer)
            freeMemPointer+=VARIABLE_SIZE
            moveToPointer(freeMemPointer).append("+")

            moveToPointer(nPtr).append("[->>-[>>+>>>>]>>[[-<<+>>]+>>+>>>>]<<<<<<<<<<]>>>>-")
            currentMemPointer+=4
            when(mode) {
                0-> {
                    mapVariable(tokens[3].id, VARIABLE_SIZE, currentMemPointer)
                    mapVariable(tokens[2].id, VARIABLE_SIZE, currentMemPointer + 2)
                }
                1-> mapVariable(tokens[2].id, VARIABLE_SIZE, currentMemPointer + 2)
                2-> {mapVariable(tokens[2].id, VARIABLE_SIZE, currentMemPointer); freeMemPointer+=2}
            }

            if(debug) println("Operator DIVMOD: ${output.substring(oIndex)} ")

        }
        fun b2a(tokens: Array<TokenProps>){
            val _10Token = mapConstant(10)
            val _100Token = mapConstant(100)
            val _48Token = mapConstant(48)

            mod(arrayOf(tokens[0],_10Token,tokens[3]))
            add(arrayOf(tokens[3],_48Token,tokens[3]))

            div(arrayOf(tokens[0],_10Token,tokens[2]))
            mod(arrayOf(tokens[2],_10Token,tokens[2]))
            add(arrayOf(tokens[2],_48Token,tokens[2]))

            div(arrayOf(tokens[0],_100Token,tokens[1]))
            add(arrayOf(tokens[1],_48Token,tokens[1]))
        }
        fun a2b(tokens: Array<TokenProps>){
            val _10Token = mapConstant(10)
            val _100Token = mapConstant(100)
            val _48Token = mapConstant(48)

            val tmpTokens = mutableListOf<TokenProps>()
            for(i in 0..2)
                tmpTokens.add(TokenProps(mapVariable(generateTempVariableId("ta2b$i")).second))


            sub(arrayOf(tokens[2],_48Token,tmpTokens[0]))

            sub(arrayOf(tokens[1],_48Token,tmpTokens[1]))
            mul(arrayOf(tmpTokens[1],_10Token,tmpTokens[1]))


            sub(arrayOf(tokens[0],_48Token,tmpTokens[2]))
            mul(arrayOf(tmpTokens[2],_100Token,tmpTokens[2]))

            add(arrayOf(tmpTokens[0],tmpTokens[1],tokens[3]))
            add(arrayOf(tokens[3],tmpTokens[2],tokens[3]))


        }

        private fun setCmpValue(token:TokenProps,toPtr:Int,){
            when(token.token){
                Token.VAR_NAME -> copy(memoryMap[token.id]!!,toPtr)
                Token.NUMBER -> addition(toPtr,token.toInt())
                else -> throw InterpreterException("Token type ${token.token} for argument of ${Token.CMP} is not supported")
            }
        }
        fun cmp(tokens: Array<TokenProps>, ptr: Int = freeMemPointer, remap:Boolean = true){
            var oIndex = output.length
            setCmpValue(tokens[0],ptr)
            setCmpValue(tokens[1],ptr+1)
            moveToPointer(ptr)
            //sign(x,y) a[0] = x, a[1] = y (single cell value) a[5] = R
            //init at x
            output.append( """
                
                   >>>>>R[-]<<<<<
                   x[ >>temp0+
                        <y[- >temp0[-] >temp1+ <<y]
                        >temp0[- >>z+ <<temp0]
                        >temp1[- <<y+ >>temp1]
                   <<y- <x- ]
                >y[>>>>R-<[z->R++<]tmp1<<<[-]]>>>>
            """.trimIndent())
            currentMemPointer = ptr + CMP_SIZE - 1
            if(remap)
                mapVariable(tokens[2].id,VARIABLE_SIZE,currentMemPointer)
            if(debug) println("Compare of [${tokens.map { it.id }.joinToString(",")}]: ${output.substring(oIndex)}")
        }
        fun whileNEQ(tokens:Array<TokenProps>){
          var oIndex = output.length
          when(tokens[0].token){
              Token.WNEQ -> {
                  cmp(tokens.copyOfRange(1,tokens.size), ptr = memoryMap[tokens.last().id]!!, remap = false)
                  output.append("[")
              }
              Token.END -> {
                  cmp(tokens.copyOfRange(1,tokens.size), ptr = memoryMap[tokens.last().id]!!, remap = false)
                  output.append("]")
              }
              else-> throw InterpreterException("Illegal token ${tokens[0].token} for WNEQ loop")
          }
          if(debug) println("While not EQ of[${tokens.map { it.id }.joinToString(",")}]: ${output.substring(oIndex)}")
        }


        private fun setListValue(listPrt:Int, index:Any, value:Any){
            var oIndex = output.length
            //I = 2: flag/i t i0 = 9 i1 = 11(etc) //size = 10 + i * 2 + 1
            when(index){
                is Int -> addition(listPrt + 2, index, true)
                is TokenProps -> copy(memoryMap[index.id]!!,listPrt + 2)
            }
            when(value){
                is Int -> addition(listPrt + 4,value, true)
                is TokenProps -> copy(memoryMap[value.id]!!, listPrt + 4)
            }
            moveToPointer(listPrt + 4)
//            >>I+  i + flag = 1
//            >>++++ value = 4
            output.append("""
                [<<[-<<+>>>>>>>>+<<<<<<]<<[->>+<<]>>>>  
                >>>>i+[-[+>>]+[<<]>>-]>>[[-]>>]
                <+>
                +[<<[>>-]>>[-<<+>>]<<]<< 
                -] 
                <<[-]<<
            """.trimIndent())
            currentMemPointer = listPrt
            if(debug) println("Setting list $listPrt value with arguments [$index, $value ]: ${output.substring(oIndex)}")
        }
        private fun getListValue(listPtr:Int, index:Any, outValue:TokenProps){
            var oIndex = output.length
            when (index) {
                is Int -> addition(listPtr + 2, index, true)
                is TokenProps -> {copy(memoryMap[index.id]!!, listPtr + 2); moveToPointer(listPtr + 2)}
            }

            output.append('+')
            //result at cell 1
//            >>+I+ //flag plus index at cell 2
            output.append("""
                [[-<<+>>>>>>>>+<<<<<<]<<[->>+<<]>>>> //copy index 
                >>>>i[-[+>>]+[<<]>>-]>>[[-]>>]
                <[-<
                +[<<[>>-]>>[-<<+>>]<<]<<
                <+>>]<]
                +[<<[>>-]>>[-<<+>>]<<]<<[-]<< //move back to flag and clear

            """.trimIndent())
            currentMemPointer = listPtr

            clear(memoryMap[outValue.id]!!)
            copy(listPtr + 1,memoryMap[outValue.id]!!)

            clear(listPtr + 1)
            if(debug) println("Getting list $listPtr value with arguments [$index, $outValue ]: ${output.substring(oIndex)}")
        }

        fun lSetGet(tokens: Array<TokenProps>){
            if(tokens[0].token == Token.LSET) {
                when(tokens[2].token) {
                    Token.NUMBER-> {
                        when(tokens[3].token) {
                            Token.NUMBER-> {
                                moveToPointer(memoryMap[tokens[1].id]!! + ARRAY_OP_SIZE + tokens[2].id.toInt()*VARIABLE_SIZE - 1)
                                clear(currentMemPointer)
                                addition(tokens[3].id.toInt())
                            }
                            Token.VAR_NAME-> setListValue(memoryMap[tokens[1].id]!!,tokens[2].id.toInt(),tokens[3])
                            else ->{throw InterpreterException("Unexpected third token ${tokens[1]}")}
                        }
                    }
                    Token.VAR_NAME->setListValue(memoryMap[tokens[1].id]!!, when(tokens[2].token){ Token.NUMBER->tokens[2].id.toInt() else-> tokens[2] },
                            when(tokens[3].token){ Token.NUMBER->tokens[3].id.toInt() else-> tokens[3] } )
                    else -> throw InterpreterException("Unexpected second token ${tokens[1]}")
                }

            }else{
                if(tokens[2].token == Token.VAR_NAME)
                    getListValue(memoryMap[tokens[1].id]!!,tokens[2],tokens[3])
                else
                    getListValue(memoryMap[tokens[1].id]!!,tokens[2].id.toInt(),tokens[3])

            }
        }
        override fun toString() = output.toString()

    }



    @Test
    fun brainFuckTest(){
        assertEquals("Hello World!\n",brainFuckParse("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.",""))
        assertEquals("Hello, World!",brainFuckParse(">++++++++[<+++++++++>-]<.>++++[<+++++++>-]<+.+++++++..+++.>>++++++[<+++++++>-]<++.------------.>++++++[<+++++++++>-]<+.<.+++.------.--------.>>>++++[<++++++++>-]<+.",""))
        assertEquals("Hello World!\n",brainFuckParse("+[>[<-[]>+[>+++>[+++++++++++>][>]-[<]>-]]++++++++++<]>>>>>>----.<<+++.<-..+++.<-.>>>.<<.+++.------.>-.<<+.<.",""))
//        assertEquals("",brainFuckParse("[->-[>+>>]>[[-<+>]+>+>>]<<<<<].>.>.>.>.>","/u0005/u0003/u0001/")) //FAILURE
    }

    fun kcuf(code: String): String {
        return Parser(Lexer(code),true).interpreter.toString()
    }
    fun Check(_RawCode : String,Input : String = "",Expect : String = "",Message : String = "")
    {
        val RawCode = _RawCode.trimIndent()
        println(RawCode)
//        println("<b>Input :</b> ${Input.toCharArray().map{it.toInt()}}")
//        println("<b>Expected output :</b> ${Expect.toCharArray().map{it.toInt()}}")
        val Code = kcuf(RawCode)
//        println("<b>Output code length :</b> ${Code.length}")
        assertEquals(Expect,brainFuckParse(Code,Input),Message)
    }
    @Test
    fun parserTest(){

        Check("""
		var X//This is a comment
		read X--This is also a comment
		msg "BByeeg" X#No doubt it is a comment
		rem &&Some comment~!@#$":<
		""","?","BByeeg?")

    }
    @Test
    fun `FixedTest 0 | Basic 1 | set var get var`(){
        Check("""
            var A B //Testing copy by variable 
            set A 'x'
            set B A
            msg A B
            var C D
            read C //Testing copy by code
            set D C
            msg C D
        ""","X","xxXX")
    }

    @Test
    fun `FixedTest 0 | Basic 1 | Works for set, inc, dec`()
    {
        Check("""
		var A B
		sEt A 'a'
		msg a B
		set B 50
		msG A b
		inc A 10
		dec B -20
		msg A B
		""","","a\u0000a2kF")
    }
    @Test
    fun `FixedTest 0 | Basic 2 | Works for kinds of numbers`()
    {
        Check("""
		var X
		set X  114514
		msg X
		set X -114514
		msg X
		set X 'X'
		msg X
		""","","\u0052\u00ae\u0058")
    }

    @Test
    fun `FixedTest 0 | Basic 3 | Works for add, sub, mul`()
    {
        Check("""
		var A B C
		read A
		read B
		add a b c
		msg a b c
		sub a b a
		msg a b c
		//mul b a c
		//msg a b c
		""","0\u0007","\u0030\u0007\u0037\u0029\u0007\u0037")//\u0029\u0007\u001f")
    }
    @Test
    fun `FixedTest 0 | Basic 3 | Works for add, sub`(){
        Check("""
		var A B C
        var D
        read A
        read B
        set D A
        sub A '1' D
        sub A B C
        msg A B C
        sub A B A
        msg A B C
        set B C 
        msg A B C
        sub A C A
        msg A B C D
        
        ""","0\u0007","\u0030\u0007\u0029\u0029\u0007\u0029\u0029\u0029\u0029\u0000\u0029\u0029\u00ff")
    }

    @Test
    fun `FixedTest 0 | Basic 3 | Works for mul`(){
        Check("""
		var A B C
        read A
        read B

        mul A B C
        var D
        mul A A D 

        msg A B C D
        ""","\u0002\u0003","\u0002\u0003\u0006\u0004")
    }

    @Test
    fun `FixedTest 0 | Basic 4 | Works for divmod, div, mod`()
    {
        Check("""
		var A B C D
		set A 79
		set B 13    
        divmod A B C D
		msg A B C D
		div C D C
		msg A B C D
		mod A D A
		msg A B C D
		""","","\u004f\u000d\u0006\u0001\u004f\u000d\u0006\u0001\u0000\u000d\u0006\u0001")
    }

    @Test
    fun `FixedTest 0 | test set`(){
        Check("""
            var a 
            set a 247
            msg a
             ""","",Char(247).toString())
    }
    @Test
    fun `FixedTest 0 | Basic 6 | Works for a2b, b2a`()
    {
        Check("""
		var A B C D
		set a 247
        b2a A B C D
		msg A B C D
		inc B 1
		dec C 2
		inc D 5
		a2b B C D A
		msg A B C D // A = (100 * (2 + 1) + 10 * (4 - 2) + (7 + 5)) % 256 = 76 = 0x4c
		""","","\u00f7\u0032\u0034\u0037\u004c\u0033\u0032\u003c")
    }

    @Test
    fun `FixedTest 0 | Basic 7 | Works for lset, lget`()
    {
        Check("""
		var L  [ 20 ]  I X
		lset L 10 80
		set X 20
		lset L 5 X
		set X 9
		lset L X X
		set I 4
		lget L I X
		msg X
		lget L 5 X
		msg X
		lget L 9 X
		msg X
		lget L 10 X
		msg X
		lget L 19 X
		msg X
		""","","\u0000\u0014\u0009\u0050\u0000")
    }

    @Test
    fun `FixedTest 0 cmp`()
    {
        Check("""
            var R ONE SIX
            set ONE 1
            set SIX 6 
            cmp 2 1 R
            msg R
            cmp 2 5 R 
            msg R 
            cmp 6 6 R
            msg R 
            cmp ONE SIX R
            msg R
            """, "", "\u0001\u00ff\u0000\u00ff")
    }

    @Test
    fun `FixedTest 0 | Basic 8 | Works for ifeq, ifneq, wneq`()
    {
        Check("""
		var F L [5] X
		set F 0
		add 10 10 X
		wneq F 5
            lset L F X
			inc F 1
			dec X 1
		end
        //L == [20,19,18,17,16]

		wneq F 0
			inc F -1
			lget L F X
			msg X
		end
//
//		set F 10
//		wneq F 0
//			ifeq F 10
//				set F 5
//			end
//			dec F 1
//			lget L F X
//			ifneq X 18
//				msg F X
//			end
//		end
//		ifeq F 0
//			ifneq X 50
//				msg ";-)"
//			end
//		end
		""","","\u0010\u0011\u0012\u0013\u0014\u0004\u0010\u0003\u0011\u0001\u0013\u0000\u0014;-)")
    }


}