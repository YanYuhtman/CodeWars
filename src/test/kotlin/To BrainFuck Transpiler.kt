import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.Stack
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFails

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
            '+' -> tape[tapePointer] = Char((tape[tapePointer] + 1).code and 0xFF)
            '-' -> tape[tapePointer] = Char((tape[tapePointer] - 1).code and 0xFF)
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
    println(output.map { "%04d".format(it.toInt() and 0xFF) })
    println(output.map { "\\u%04x".format(it.toInt() and 0xFF) })
    return output.map { (it.toInt() and 0xFF).toChar() }.joinToString("")
}

const val EOT = '\u0004'
const val EOL = '\u000A'
const val CHAR_QUOTE = '\''
const val STRING_QUOTE = '\"'
val WHITESPACE_CHARS = charArrayOf(' ','\t','\r')
const val VARIABLE_SIZE = 3
const val CMP_SIZE = 8
fun generateTempVariableId(original:String) = "${Random.nextBytes(1)}#$original"
const val ARRAY_OP_SIZE = 10
const val ARRAY_VAR_SIZE = 2
const val OP_CELL = "opCell"
class `To BrainFuck Transpiler` {
    //https://www.toInt()wars.com/kata/59f9cad032b8b91e12000035
    enum class TokenType{
        BASIC,
        ID,
        KEYWORD,
        OPERATOR,
    }
    enum class Token(val type:TokenType, var id:String){
        DUMMY(TokenType.BASIC, ""),
        EOT(TokenType.BASIC, "$EOT"),
        EOL(TokenType.BASIC, "$EOL"),
        VAR_NAME(TokenType.ID, ""),
        STRING(TokenType.BASIC, ""),
        CHARACTER(TokenType.BASIC, ""),
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
    enum class VarType{ PARAM, LIST, PROCEDURE}
    data class TokenProps(val token:Token, val startPos:Int, val endPos:Int=startPos, var id:String = token.id){
        constructor(value:Int):this(Token.NUMBER,0,0,value.toString())
        constructor(char: Char):this(Token.CHARACTER,0,0,"$char")
        constructor(v: String):this(Token.VAR_NAME,0,0,v)
        fun toInt():Int = when(this.token){
            Token.NUMBER -> this.id.toInt()
            Token.CHARACTER -> this.id[0].toInt()
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

        private fun skipComment() {if(curChar == '/' && peek() == '/' || curChar == '-' && peek() == '-' || curChar == '#' || source.substring(curIndex).startsWith("rem", true)) while (nextChar() != EOL && curChar != EOT);}

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
                    peek() in (WHITESPACE_CHARS + EOL + '/' + '-' + '#' + EOT + '[' +']' + '"' + '\'')-> {
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
                                if (!declaration.matches("[_\$A-Za-z]+[_\$A-Za-z\\d]*".toRegex()))
                                    throw LexerException("Declaration: $declaration at [$startPos,$endPos] does not match variable pattern")
                                TokenProps(Token.VAR_NAME, startPos, endPos, declaration.toUpperCase())
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

    enum class VariableType{ UNKNOWN, PARAM, LIST, PROC_NAME  }
    class ParserException(message:String):Exception(message)
    class Parser(val lexer:Lexer, debug: Boolean = false){
        private var currentTokenIndex:Int = -1
        private var currentToken:TokenProps = TokenProps(Token.EOL,0,0)
        private var programTokens:MutableMap<TokenProps,TokenProps> = mutableMapOf()
        private var procDeclarations:MutableMap<String,TokenProps> = mutableMapOf()
        private var declaredVariables:MutableMap<String,VarType> = mutableMapOf()

        val interpreter = Interpreter(debug)

        private fun mapProgramTokens():TokenProps{
            val blockStack:Stack<TokenProps> = Stack<TokenProps>().apply { push(currentToken) }
            while (currentToken.token != Token.EOT)
                when(nextToken().token){
                    Token.IFEQ,Token.IFNEQ,Token.WNEQ -> blockStack.push(currentToken)
                    Token.PROC -> {
                        blockStack.push(currentToken)
                        val procToken = currentToken
                        procDeclarations.putIfAbsent(nextToken().id,procToken)?.let {
                            throw ParserException("Procedure name ${currentToken.id} for token: $procToken must be unique")
                        }
                        declaredVariables[currentToken.id] = VarType.PROCEDURE
                    }
                    Token.END -> programTokens[blockStack.pop()] = currentToken
                    else -> {}
                }
            programTokens.put(blockStack.peek(),currentToken)
            setTokenIndex(0)
            return programTokens[blockStack.pop()]!!
        }
        init {
            interpreter.mapVariable(OP_CELL)
            if(lexer.tokens.isEmpty() || lexer.tokens.last().token != Token.EOT)
                throw ParserException("There is no tokens for parsing")
            nextSignificantToken()
            program(mapProgramTokens())
        }
        private fun nextToken(token:TokenProps){ lexer.tokens.forEachIndexed {index, tokenProps -> if(token === tokenProps) setTokenIndex(index) }}
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
                Token.WNEQ -> conditionStatement (interpreter::whileNEQ)
                Token.IFEQ -> conditionStatement (interpreter::_ifEQ)
                Token.IFNEQ -> conditionStatement (interpreter::_ifNEQ)
                Token.PROC -> nextToken(programTokens[currentToken]!!)
                Token.CALL -> callProc()

                else -> {}//throw  ParserException("Misplaced token: $currentToken")
            }
            nextSignificantToken()

        }
        private fun variableDeclaration(){
            while (!isEndOfStatement(nextToken().token)){
                if(currentToken.token != Token.VAR_NAME)
                    throw ParserException("Declaration variable name expected instead of: $currentToken")
                val id = currentToken.id
                declaredVariables.putIfAbsent(id,VarType.PARAM)?.let { throw ParserException("Duplicated variable $id declaration") }
                if(peekToken().token == Token.LBRAKET){
                    nextToken();nextToken()
                    val size = currentToken.id.toIntOrNull()
                    if(size == null || size < 1)
                        throw ParserException("Positive number is expected $currentToken")
                    if(nextToken().token != Token.RBRAKET)
                        throw ParserException("${Token.LBRAKET} token expected")
                    interpreter.initVariable(id,ARRAY_OP_SIZE + size * 2 + 2)
                    declaredVariables[id] = VarType.LIST
                }else
                    interpreter.initVariable(id,VARIABLE_SIZE)
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
                    Token.NUMBER,Token.CHARACTER -> {}
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
            if(currentToken.token != Token.VAR_NAME || declaredVariables[currentToken.id]!! != VarType.LIST)
                throw ParserException("Expected argument for variable name ${list[0]}")

            list[2] = nextToken()
            if( list[2].token != Token.NUMBER && currentToken.token != Token.VAR_NAME)
                throw ParserException("Expected number argument token as list index (first) for operator ${list[0]}")

            list[3] = nextToken()
            if(list[0].token == Token.LGET && list[3].token != Token.VAR_NAME)
                throw ParserException("Expected variable argument token as set value (second) for operator ${list[0]}")
            if(list[3].token == Token.VAR_NAME && declaredVariables[list[3].id]!! != VarType.PARAM)
                throw ParserException("Expected variable of type ${VarType.PARAM} instead of ${declaredVariables[list[3].id]!!}")
            interpreter.lSetGet(list)
        }


        private fun conditionStatement(statement:(tokens: Array<TokenProps>)->Unit){
            val (flagPtr,flagName) = interpreter.mapVariable(generateTempVariableId(currentToken.token.id),CMP_SIZE+1)
            val tokens:Array<TokenProps> = arrayOf(currentToken,nextToken(),nextToken())
            if(tokens[1].token != Token.VAR_NAME || (tokens[2].token != Token.VAR_NAME && tokens[2].token != Token.NUMBER))
                throw ParserException("Not supported argument for ${tokens[0].token} token")
            statement(arrayOf(tokens[0],tokens[1],tokens[2], TokenProps(flagName)))
            nextSignificantToken()
            program(programTokens[tokens[0]]!!)
            statement(arrayOf(currentToken,tokens[1],tokens[2], TokenProps(flagName)))
        }

        private fun collectProcVars():MutableList<TokenProps>{
            var params = mutableListOf<TokenProps>()
            while (nextToken().token !in arrayOf(Token.EOL,Token.EOT)){
                if(currentToken.token != Token.VAR_NAME)
                    throw ParserException("Calling procedure supports only ${Token.VAR_NAME}, got: ${currentToken.token} ")
                params.add(currentToken)
            }
            return params
        }
        private fun callProc(){
            val procName = nextToken().id
            val params = collectProcVars()
            val backPoint = currentToken

            nextToken(procDeclarations[procName]!!)
            nextToken() //skip proc name
            val arguments = collectProcVars()

            if(params.size != arguments.size) throw ParserException("Inconsistent argument count for call $procName")

            interpreter.pushMemoryStack()
            for(i in params.indices)
                interpreter.mapVariableAlias(arguments[i].id,params[i])
            program(programTokens[procDeclarations[procName]!!]!!)

            interpreter.popMemoryStack()
            nextToken(backPoint)
        }
    }
    class InterpreterException(message:String):Exception(message)
    class Interpreter(val debug:Boolean = false){
        object memoryMap{
            lateinit var it:Interpreter
            operator fun get(id:String):Int? = it.memoryStack.fold(-1) { acc, mutableMap -> mutableMap[id] ?: acc }
            operator fun set(id:String,ptr:Int) { it.memoryStack[0][id] = ptr }

            fun remove(id:String) {it.memoryStack[0].remove(id)}
        }

        private val memoryStack:Stack<MutableMap<String,Int>> = Stack()
        private var freeMemPointer = 0
        private var currentMemPointer = 0
        private val output:StringBuilder = StringBuilder()

        init { memoryMap.it = this; this.memoryStack.push(mutableMapOf()) }

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

        fun initVariable(id:String, size:Int = VARIABLE_SIZE) {
            if(memoryStack[0].contains(id)) throw InterpreterException("Duplicate venerable init")
            mapVariable(id,size)
        }
        fun mapVariable(id:String, size:Int = VARIABLE_SIZE, ptr:Int = freeMemPointer):Pair<Int,String>{
            if(debug) println("Mapping token $id to index $ptr with size: $size")
            memoryMap[id] = ptr
            if(ptr + size > freeMemPointer)
                freeMemPointer = ptr + size
            return ptr to id
        }
        fun mapVariableAlias(id:String,token:TokenProps) {memoryStack.peek()[id] = memoryMap[token.id]!!}
        fun pushMemoryStack() = memoryStack.push(mutableMapOf())
        fun popMemoryStack() = memoryStack.pop()
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
            var prevChar = '\u0000'
            str.forEach lbl@{
                if(it == '\\' && prevChar != '\\') {
                    prevChar = it
                    return@lbl
                }
                val ch = if (prevChar == '\\') { when(it){'n'->'\n'; 'r'->'\r'; 't'->'\t' ;else->it}} else it
                prevChar = ch

                if(currentMemCelValue == ch.toInt())
                    output.append(".")
                else {
                    val (sign, symbol) = if (ch.toInt() >= currentMemCelValue) (1 to '+') else (-1 to '-')
                    val delta = abs(ch.toInt() - currentMemCelValue)
                    if(delta > 4) {
                        val mult = floor(sqrt(abs(delta).toDouble())).toInt()
                        val reminder = if (mult == 0) delta else delta - (mult * mult)
                        output.append('>').append(CharArray(mult) { '+' }).append("[<")
                            .append(CharArray(mult) { symbol }).append(">-]<")
                            .append(CharArray(reminder) { symbol })
                    }else
                        output.append(CharArray(delta) { symbol })
                    output.append('.')
                    currentMemCelValue = ch.toInt()
                }
            }
            if(debug) println("Generated string output for $str: ${output.substring(ouIndex)}")

        }
        private fun addition(pointer:Int, aValue: Int, optimized:Boolean = true) {moveToPointer(pointer);addition(aValue,optimized) }
        private fun addition(aValue:Int, optimized:Boolean = true,output: StringBuilder? = this.output):String{
            if(aValue == 0) return ""

            val output = if(output == null) StringBuilder() else output
            val oIndex = output.length
            val symbol = if (aValue > 0) '+' else '-'
            val add = abs(aValue)
            if(optimized && add > 4) {
                val mult = floor(sqrt(abs(add).toDouble())).toInt()
                val reminder = if (mult == 0) add else add - (mult * mult)
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
                value is Char -> {clear(id) ;inc(id,value.toInt())}
                else -> throw InterpreterException("Argument type ${value::class} is not supported for SET function ")
            }
        }
        fun inc(id:String, number: Int){
            moveToPointer(id)
            addition(number)
        }
        private fun clear(id:String) = clear(memoryMap[id]!!)

        private fun clear(pointer:Int){
            moveToPointer(pointer)
            output.append("[-]")
            if(debug) println("Clearing pointer index: $pointer")
        }

        private fun move(fromId:String, toId: String) = move(memoryMap[fromId]!!,memoryMap[toId]!!)
        private fun move(fromPtr: Int, toPtr:Int):Int{
            var oIndex = this.output.length
            clear(toPtr)
            moveToPointer(fromPtr).append("[-")
            moveToPointer(toPtr).append('+')
            moveToPointer(fromPtr).append(']')
            if(debug) println("Move $fromPtr to $toPtr: ${output.substring(oIndex)}" )
            return toPtr
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
            recombine(fromPtr)
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
                    Token.VAR_NAME -> copy(tokens[i].id,OP_CELL)
                    Token.NUMBER,Token.CHARACTER-> addition(memoryMap[OP_CELL]!!,tokens[i].toInt())
                    else->throw InterpreterException("Unsupported token: ${tokens[i]} for addition")
                }
            move(OP_CELL,tokens[2].id)
            if(debug) println("Operator ADD: ${output.substring(oIndex)}")
        }
        fun sub(tokens:Array<TokenProps>){
            var oIndex = output.length
            when(tokens[1].token){
                Token.NUMBER,Token.CHARACTER -> {
                    copy(tokens[0].id,OP_CELL)
                    addition(memoryMap[OP_CELL]!!,-(tokens[1].toInt()))
                }
                Token.VAR_NAME -> {
                    sub(memoryMap[tokens[0].id]!!,memoryMap[tokens[1].id]!!)
                    copy(tokens[0].id,OP_CELL)

                    recombine(memoryMap[tokens[0].id]!!)
                    recombine(memoryMap[tokens[1].id]!!)

                }
                else->throw InterpreterException("Unsupported token: ${tokens[1]} for subtraction ")
            }
            move(OP_CELL,tokens[2].id)
            if(debug) println("Operator SUB: ${output.substring(oIndex)}")
        }

        fun mul(tokens:Array<TokenProps>){
            var oIndex = output.length
            if(tokens[0].token == Token.VAR_NAME && tokens[0].id == tokens[1].id)
                tokens[1] = copyToTempVariable(tokens[1])

            moveToPointer(tokens[1].id).append("[->+")
            currentMemPointer++
            copy(tokens[0].id,OP_CELL)
            moveToPointer(tokens[1].id).append("]")
            recombine(memoryMap[tokens[1].id]!!)

            move(OP_CELL,tokens[2].id)

            if(debug) println("Operator MUL: ${output.substring(oIndex)}")


        }
        fun mod(tokens:Array<TokenProps>) = divMod(tokens,2)
        fun div(tokens:Array<TokenProps>) = divMod(tokens,1)
        fun divMod(tokens:Array<TokenProps>) = divMod(tokens, 0)
        private fun divMod(tokens:Array<TokenProps>, mode:Int = 0){
            var oIndex = output.length
            val nPtr = freeMemPointer
            for(i in 0..1){
                when(tokens[i].token){
                    Token.VAR_NAME->{copy(memoryMap[tokens[i].id]!!,freeMemPointer);freeMemPointer+=VARIABLE_SIZE}
                    Token.NUMBER->{addition(freeMemPointer,tokens[i].toInt());freeMemPointer+=VARIABLE_SIZE}
                    else->throw InterpreterException("Unsupported token ${tokens[i]} for divMod operation")
                }
            }

            moveToPointer(freeMemPointer).append("+")

            moveToPointer(nPtr).append("[->>>-[>>>+>>>>>>]>>>[[-<<<+>>>]+>>>+>>>>>>]<<<<<<<<<<<<<<<]>>>>>>-")
            currentMemPointer+=(2*VARIABLE_SIZE)
            when(mode) {
                0-> {
                    move(currentMemPointer,memoryMap[tokens[3].id]!!)
                    move(currentMemPointer + VARIABLE_SIZE,memoryMap[tokens[2].id]!!)
                }
                1-> {move(currentMemPointer + VARIABLE_SIZE,memoryMap[tokens[2].id]!!); freeMemPointer+=VARIABLE_SIZE}
                2-> {move(currentMemPointer,memoryMap[tokens[2].id]!!); freeMemPointer+=(2*VARIABLE_SIZE)}
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

        private fun setCmpValue(token:TokenProps,toPtr:Int){
            clear(toPtr)
            when(token.token){
                Token.VAR_NAME -> copy(memoryMap[token.id]!!,toPtr)
                Token.NUMBER,Token.CHARACTER -> addition(toPtr,token.toInt())
                else -> throw InterpreterException("Token type ${token.token} for argument of ${Token.CMP} is not supported")
            }
        }
        fun cmp(tokens: Array<TokenProps>, ptr: Int = freeMemPointer, remap:Boolean = true){
            var oIndex = output.length
            setCmpValue(tokens[0],ptr)
            setCmpValue(tokens[1],ptr+1)
            moveToPointer(ptr)

            //++++ > +++ > p >s> n > f   <<<<<
            //size = 6 i(0)=x i(1)=y i(4) = result
            output.append("""
                >>+p<<
                x[>>-p]>>[
                      <y[>-p
                        <[-]++<+>> x=0 y=0 set y=2 x=1 ]>
                        [-s>>>-f<< //don't compare ]
                      
                    >]<<<<
                    
                
                x[->>+>>-<<<<]
                >y[->->>+
                    >+f<
                    n[>-f]>f[-<<+s>>>]
                    <<<<<]
                >>>>+f<<          
                s[-
                    >[+>-f2]>[-f2>>>]<<<<
                    r0[>>r1[<<r0+>>r1-]]
                    <+f<[->-f>-<<]>f[-]
                ]
                >>f[-<<<+>> r1[<<r0[>>r1+<<r0-]]>>>]<<
                >
            """.trimIndent())
            currentMemPointer+=4
            if(remap)
                mapVariable(tokens[2].id,VARIABLE_SIZE,currentMemPointer)
            if(debug) println("Compare of [${tokens.map { it.id }.joinToString(",")}]: ${output.substring(oIndex)}")

        }

        private fun sCmp(tokens: Array<TokenProps>,neq:Boolean, ptr: Int = freeMemPointer){
            var oIndex = output.length
            setCmpValue(tokens[0],ptr)
            setCmpValue(tokens[1],ptr+2)
            if(!neq) moveToPointer(ptr + 4).append('+')
//            ++++ >> ++++ >> +f (1 eq 0 neq) <<<<
            moveToPointer(ptr).append("x[-x>>-y<<]>>>>[<<[>>-f<<[+]]>>[-f<<+>>]]<<")
            currentMemPointer+=2
            if(ptr == freeMemPointer)
                mapVariable(tokens[2].id,VARIABLE_SIZE,currentMemPointer)
            if(debug) println("SCompare of [${tokens.map { it.id }.joinToString(",")}]: ${output.substring(oIndex)}")
        }
        fun whileNEQ(tokens:Array<TokenProps>){
          var oIndex = output.length
          when(tokens[0].token){
              Token.WNEQ -> {
                  sCmp(tokens.copyOfRange(1,tokens.size),true, ptr = memoryMap[tokens.last().id]!!)
                  output.append("[")
              }
              Token.END -> {
                  sCmp(tokens.copyOfRange(1,tokens.size),true, ptr = memoryMap[tokens.last().id]!!)
                  output.append("]")
              }
              else-> throw InterpreterException("Illegal token ${tokens[0].token} for WNEQ loop")
          }
          if(debug) println("While not EQ of[${tokens.map { it.id }.joinToString(",")}]: ${output.substring(oIndex)}")
        }

        private fun _if(tokens:Array<TokenProps>, eq: Boolean){
            when(tokens[0].token){
                Token.IFEQ,Token.IFNEQ->{
                    sCmp(tokens.copyOfRange(1,tokens.size),!eq, ptr = memoryMap[tokens.last().id]!!)
//                    if(eq) output.append("""
//
//                        x[<temp0+>x[-]]+
//                        <temp0[>x-<temp0-]
//                        >
//                    """.trimIndent())
                    output.append("[")
                }
                Token.END-> {moveToPointer(memoryMap[tokens.last().id]!! + 2).append("[-]]")}
                else-> throw InterpreterException("Statement token ${tokens[0].token} is not supported")
            }
        }
        fun _ifEQ(tokens:Array<TokenProps>) = _if(tokens,true)
        fun _ifNEQ(tokens:Array<TokenProps>) = _if(tokens,false)
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
                is Int -> {addition(listPtr + 2, index, true)}
                is TokenProps -> {copy(memoryMap[index.id]!!, listPtr + 2); moveToPointer(listPtr + 2)}
            }

            output.append('+')
            //TODO: the lget method clears L[i]. To fix this significant change is needed ... if this functionality needed
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
                                moveToPointer(memoryMap[tokens[1].id]!! + ARRAY_OP_SIZE + tokens[2].id.toInt()*ARRAY_VAR_SIZE - 1)
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
                lSetGet(arrayOf(TokenProps(Token.LSET,0,0,Token.LSET.id),tokens[1],tokens[2],tokens[3]))
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
        println("$code")
        val start = System.nanoTime()
        val result = Parser(Lexer(code)).interpreter.toString()
        println("Executed within ${(System.nanoTime() - start)/1_000_000.0} ms")
        return result
    }
    fun Check(_RawCode : String,Input : String = "",Expect : String = "",Message : String = "")
    {
        val RawCode = _RawCode.trimIndent()
        println(RawCode)
        println("Input : ${Input.toCharArray().map{"\\u%04x".format(it.toInt() and 0xFF) }}")
        println("Expected output : ${Expect.toCharArray().map{"\\u%04x".format(it.toInt() and 0xFF) }}")
        val Code = kcuf(RawCode)
//        println("<b>Output code length :</b> ${Code.length}")
        assertEquals(Expect,brainFuckParse(Code,Input),Message)
    }
    @Test
    fun parserTest(){

        Check("""
        msg  "It is V now\n"
		var X//This is a comment
		read X--This is also a comment
		msg "BByeeg" X#No doubt it is a comment
		rem &&Some comment~!@#$":<
		""","?","It is V now\nBByeeg?")

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
    fun `FixedTest 0 | Basic 1 | addPtimized`()
    {
        Check("""
            var a 
            set a 8
            msg a
            ""","",Char(8).toString())


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
		mul b a c
		msg a b c
		""","0\u0007","\u0030\u0007\u0037\u0029\u0007\u0037\u0029\u0007\u001f")
    }
    @Test
    fun `FixedTest 0 | Basic 4 | Works for add, sub, mul`() {
        Check("""
            var A B 
            set A 2
            set B 3
            add A B A
            msg A
        ""","","\u0005")
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
        
        lget L 5 X
		msg X
		""","","\u0000\u0014\u0009\u0050\u0000\u0014")
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
    fun `FixedTest 1 cmp`(){
        Check("""
            var X K
            read X
            cmp 80 X K
            msg X K
            cmp X 'z' K
            msg X K
            cmp X X K
            msg X K
        """,arrayOf(128).map { it.toChar() }.joinToString("")
            ,arrayOf(128, 255, 128, 1, 128, 0).map { it.toChar() }.joinToString("")
        )
    }
    @Test
    fun `cmp failing test1`(){
        Check("""
            var __defineGetter__  hasOwnProperty __lookupGetter__ __lookupSetter__ propertyIsEnumerable constructor toString toLocaleString valueOf isPrototypeOf
            reAd __defineGetter__
            rEad constructor
            call __PROto__ constructor __defineGetter__
            msg constructor __defineGetter__ valueOf
            
            proc __proto__ __defineSetter__ constructor
                cmp __defineSetter__ constructor valueOf 
        """,  arrayOf(0,1).map { it.toChar() }.joinToString("")
            ,arrayOf(1,0,1).map { it.toChar() }.joinToString("")
        )
    }
    @Test
    fun `cmp failing test2`(){
        Check("""
            var __defineGetter__  hasOwnProperty __lookupGetter__ __lookupSetter__ propertyIsEnumerable constructor toString toLocaleString valueOf isPrototypeOf
            reAd __defineGetter__
            rEad constructor
            call __PROto__ constructor __defineGetter__
            msg constructor __defineGetter__ valueOf
            
            proc __proto__ __defineSetter__ constructor
                cmp __defineSetter__ constructor valueOf
            end
        """,  arrayOf(0,255).map { it.toChar() }.joinToString("")
            ,arrayOf(255,0,1).map { it.toChar() }.joinToString("")
        )
    }

    @Test
    fun `FixedTest 0 | Basic 8 | Works for ifeq, ifneq`(){
        Check("""
            var C V
            set c 10
            set v -1
            ifeq c 10
                msg c
            end
            ifeq c v
                msg c
            end

            ifneq c 0
                msg c
            end

            ifneq c v
                msg v
            end
            ""","","\u000a\u000a\u00FF")
    }

    @Test
    fun `FixedTest 0 | Basic 8 |scmp wneq`(){
        Check("""
            var F
            set F 0
            wneq F 5
                msg F
                inc F 1
            end 
        ""","","\u0000\u0001\u0002\u0003\u0004")
    }
    @Test
    fun `FixedTest 0 | Basic 8 | Works for ifeq, ifneq, wneq`()
    {
        Check("""
		var F L[5] X
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

		set F 10
		wneq F 0
			ifeq F 10
				set F 5
			end
			dec F 1
			lget L F X
			ifneq X 18
				msg F X
			end
		end
		ifeq F 0
			ifneq X 50
				msg ";-)"
			end
		end
		""","","\u0010\u0011\u0012\u0013\u0014\u0004\u0010\u0003\u0011\u0001\u0013\u0000\u0014;-)")
    }

    @Test
    fun `FixedTest 0 | Basic 9 | Works for proc`()
    {
        Check("""
		var A B T
		set A 'U'
		set B 'V'

		msg"Outer Before : "A B"\n"
		call swap B A
		msg"Outer After : "A B"\n"

		proc swap x y
			msg "Inner Before : "x y"\n"
			set T x
			call say T
			set x y
			set y T
			msg "Inner After : "x y"\n"
		end
		proc say x
			msg "It is " x " now\n"
		end
		""","","Outer Before : UV\n" +
                "Inner Before : VU\n" +
                "It is V now\n" +
                "Inner After : UV\n" +
                "Outer After : VU\n")
    }

    @Test
    fun `FixedTest 1 | Invalid 03 | Duplicate var names`()
    {
        assertFails("Duplicated var names") {
            Check(
                """
                var Q
                var q[20]
                """, "", ""
            )
        }
    }

    @Test
    fun `FixedTest 1 | Invalid 07 | Expect a variable but got a list`()
    {
        assertFails("Expect a variable but got a list")
        {
            Check(
                """
                    var L[40] X[20]
                    LSet L 0 X
                    """, "", ""
            )
        }
    }
    @Test
    fun `FixedTest 1 | Invalid 08 | Expect a list but got a variable`()
    {
        assertFails("Expect a list but got a variable") {
            Check(
                """
                var L X
                LGet L 0 X
                """, "", ""
            )
        }
    }
    @Test
    fun `Random test 1`() {
        Check("""
            VaR jhVcnckykSvQGYCYYhdAm2UD${'$'} MNfrZgAbhaSLH5ebGyFViVDgcVbmUqdz_MbICI WT5W_8Kr90lP5nqTRTonTVn wYoxouQx5oKICFHAAuYza
            rEad jHvCnckYkSVqgYCYYHdAM2ud${'$'}
            ReAD MnfrZGabhaslh5ebGYfvIVdGcvBMuQDz_MBici
            set WT5w_8kr90lP5nqtRtonTVn 0
            WnEq wT5w_8Kr90LP5NQtRtoNtVn 2
            	Add JHvcNckykSvQgYcYYhdAm2uD${'$'} MNfRzgaBhaslh5EbGyfVIvDgCVBmUQDZ_mbIcI wyOXOUqX5okicFhaaUYzA
            	mul JhvcnCkYKSVqGycyYhdAm2UD${'$'} wYOxouqX5okICFHaAUYZa mNfRZGABhaSLH5EbgYFviVdGcVBmUqDz_MBicI
            	suB wyoxOUqx5oKICfhaAuYzA MnfrzgABhaSlH5ebGyfvIvdgcvBmUQDz_mBicI jhvcNcKYKsVqGYcyYHDam2UD${'$'}
            	INc wT5w_8kR90lP5NqTrtOnTVN 257
            eND
            MsG jHvCncKyKSvQgYCyYHdaM2UD${'$'} mNFrZGaBHASlH5ebgyFvivdgcVBmUqdz_mbicI " -- " wt5W_8kr90LP5nQTRTOntvN wyoxOuqx5okicfhaAuYza
        """
            , arrayOf(172, 95).map { it.toChar() }.joinToString("")
            , arrayOf(222, 45, 32, 45, 45, 32, 2, 11).map { it.toChar() }.joinToString("")
        )

    }

    @Test
    fun `Random test 1|renamed variables`() {
        Check("""
              var V1 V2 V3 V4
              
//              mul 167 11 V2
//              sub 11 45 V1
              
            read V1
            read V2
            set V3 0
            wneq V3 2
                add V1 V2 V4
                mul V1 V4 V2
                sub V4 V2 V1
                inc V3 257
                
//                msg V1 V2
                
            end
            msg V1 V2 " -- " V3 V4
        """
            , arrayOf(172, 95).map { it.toChar() }.joinToString("")
            , arrayOf(222, 45, 32, 45, 45, 32, 2, 11).map { it.toChar() }.joinToString("")
        )
    }

    @Test
    fun `Random test 2`() {
        Check("""
           vAR wyh8E2ygclicos2HQZBM8XW9dALkjT    [       21        ]         Hp9pxvwdL6fjNb3LUGoPYtLH4C9T cpIX__kAn44HKmQvvAh46QRB6AzhcSYNmyOf9
            sET hp9pxVWDL6FjnB3lUGOPYtLH4c9T 21
            Wneq HP9PXvwDL6fJNb3LUgOPYTLH4C9t 0
                Dec Hp9PXvWDl6FjNb3LUGoPYtLH4c9T 1
                Read cpix__KaN44hkmQvVah46qRb6azhcSyNMYOF9
                LsET Wyh8E2ygcLICoS2HqZBm8Xw9DAlKjt hp9pxVwDl6Fjnb3luGoPyTLH4C9t cpiX__kan44hkMQVVah46QRb6aZHCSynmYof9
            End
            lGET wyh8E2yGCLICos2hqzBm8Xw9dALkJT 8 cpiX__kan44HkmqvVAH46qRB6AZhcSyNMyOf9
            MSg cPiX__KaN44hkmqVVAH46qrB6aZhCsynMYOf9
            LgET WyH8E2ygcliCOs2HQzbm8xW9DALKJT 19 cpix__kAN44hkMqvvAH46QRB6AZhcSyNMyoF9
            mSg CPIx__KAn44hkmqVVah46qrb6aZHCSYNmYof9
            lgEt wYh8e2YgcLICOS2HQzBM8XW9dalKjt 18 CPix__kan44hkMqVVaH46qrb6AzhCSYnMYOf9
            msG CpiX__kan44HKmQVvAh46Qrb6aZHcsYnMYOf9
            lgEt wYH8e2ygcLiCOs2HqZBm8xW9dalkJT 20 cPIX__kaN44HKmQVVAH46QrB6aZhCSYNmYOf9
            mSg Cpix__kaN44HkmQvVaH46qRB6AZhCsYNMyOf9
            LgEt wyH8e2YgclICOs2HqzBm8xW9DAlkJt 7 cPIx__KAN44hKmQvvah46QrB6AZHCSynmyof9
            msg Cpix__KAn44HkmQvvah46qrB6aZHcSYnmYOF9 
        """
            ,arrayOf(117, 147, 34, 64, 243, 210, 18, 70, 0, 213, 52, 152, 18, 140, 96, 99, 39, 52, 97, 140, 139).map { it.toChar() }.joinToString("")
        , arrayOf(18, 147, 34, 117, 140).map { it.toChar() }.joinToString(""))
    }

    @Test
    fun `Random test 2| renamed`() {
        Check("""
             var L    [       21        ]         V2 V3 T
             set V2 21
             wneq V2 0
                 dec V2 1
                 read V3
                 lset L V2 V3
//                 lget L V2 T
//                 msg T
             end
//             wneq V2 21
//                 lget L V2 T
//                 msg T
//                 inc V2 1
//             end
                 
                     
//             set V2 0
//             lget L V2 T
//             msg T
                    
             lget L 8 V3
             msg V3
//                    lget L 9 V3
//                    msg V3
//             lget L 19 V3
//             msg V3
//             lget L 18 V3
//             msg V3
//             lget L 20 V3
//             msg V3
//             lget L 7 V3
//             msg V3 
        """
            ,arrayOf(117, 147, 34, 64, 243, 210, 18, 70, 0, 213, 52, 152, 18, 140, 96, 99, 39, 52, 97, 140, 139).map { it.toChar() }.joinToString("")
//            ,arrayOf(117, 147, 34, 64, 243, 210, 18, 70, 0, 213, 52, 152, 18, 140, 96, 99, 39, 52, 97, 140, 139).map { it.toChar() }.reversed().joinToString("")
            , arrayOf(18).map { it.toChar() }.joinToString("")
           // , arrayOf(18, 147, 34, 117, 140).map { it.toChar() }.joinToString("")
            )
    }


    @Test
    fun `Random test 3`(){
        Check("vaR JmkBQiNv5SgOnfJeQJu7VMvmEeV\$12U6DBP    [       20        ]         SXoZqY062kkDIIZt_KYun QrZF7as\$tD9ZSrNbHaYbQfGdvvUVI6abpFh\n" +
                "SeT sxOZqy062kKDIIzt_kYun 20\n" +
                "wNeq SXOzqy062KkdIIzT_KYun 0\n" +
                "\tDec sXOZQY062kkDiIzt_kYun 1\n" +
                "\tREAD QrzF7AS\$tD9zsRnbHayBQfgdVvUvI6AbpFH\n" +
                "\tLSet jmkBqiNV5sgoNfJeqjU7VMvmEEv\$12u6dbp SXOzQY062kkDIIZt_KyuN qrZf7as\$tD9ZSRnbhAyBqfgDvVuVI6abpfh\n" +
                "eNd\n" +
                "lGET JmkbqInV5sGOnFJeqJu7VmvmEev\$12u6dBP 5 QRZF7AS\$td9ZSrNbHaYBQFgdvvuvi6AbPfh\n" +
                "msG QrZF7as\$TD9ZsrNbhAYBQFgdVvUVi6aBpFH\n" +
                "lGeT jMkbqiNv5sGonFjeqju7VMVMeeV\$12u6dBP 3 qRzf7AS\$td9zsrNBhaYBqfgDVvUVI6abpFh\n" +
                "mSg QRzf7aS\$td9zsRnbHAyBqFgDVVuvi6abPFh\n" +
                "LGEt JMKBqiNv5sgOnfJEqJU7vmvmEEV\$12U6DbP 14 QRZf7aS\$Td9zSrNBhAybqFGdVVuvI6AbpFH\n" +
                "msg QRzf7AS\$td9zsRNbHaYBqFGdvvUvI6aBpFH\n" +
                "lGet JMkbqiNv5SgOnFJeqJU7VmVMeEv\$12U6dBp 2 qrZF7as\$Td9ZsrNBHAybQfGDvvuVI6AbpFh\n" +
                "MSg qRZf7aS\$TD9zSrNbhaYbqfgDVvuvi6aBpFH\n" +
                "lgET JMkBQInV5sGOnFJeQJU7vMvmeev\$12U6DbP 8 qRzF7As\$TD9ZsrNBhaYBQfGDVVUVi6aBPFh\n" +
                "MSG qrZF7aS\$Td9ZsRnbhayBqFgDvVuvi6AbpFH", arrayOf(124, 238, 205, 98, 195, 83, 190, 114, 2, 244, 247, 64, 48, 134, 187, 153, 97, 71, 81, 94).map { it.toChar() }.joinToString("")
            ,arrayOf(187, 97, 83, 71, 64).map { it.toChar() }.joinToString(""))
    }
    @Test
    fun `Random test 3|renamed`(){
        Check("""
            var L    [       20        ]         V1 V2
            set V1 20
            wneq V1 0
                dec V1 1
                read V2
                lset L V1 V2
            end
            lget L 5 V2
            msg V2
            lget L 3 V2
            msg V2
            lget L 14 V2
            msg V2
            lget L 2 V2
            msg V2
            lget L 8 V2
            msg V2
        """, arrayOf(124, 238, 205, 98, 195, 83, 190, 114, 2, 244, 247, 64, 48, 134, 187, 153, 97, 71, 81, 94).map { it.toChar() }.joinToString("")
            ,arrayOf(187, 97, 83, 71, 64).map { it.toChar() }.joinToString(""))
    }

    @Test
    fun `Random test 4`(){
        Check("Var uVnsFlXO1OrhqNfx\$VDzzrNjR47F0Ff1Rjhn    [       21        ]         EA0whPQ524RJpGq57GexxWLJpQp4efRM Wd0oEhijt\$033EVw6BmLGDc6xhj_UNdP\n" +
                "seT EA0wHPq524rJpGQ57gEXxwljpqP4EfRm 21\n" +
                "WNEQ ea0WHPq524rjpGq57GexXWLjPqP4EFrm 0\n" +
                "\tDeC EA0whPq524RJpgQ57gexxWLJPqP4EfrM 1\n" +
                "\tread wD0oehIjT\$033evW6BmlgdC6xhj_undp\n" +
                "\tlseT UvnSFLXO1OrhqnFx\$VdzzRnJR47f0Ff1RjHn Ea0whpQ524RjpGQ57gEXXwlJpQP4efrM WD0OEhIjT\$033EVw6BmLGdC6XHJ_unDp\n" +
                "end\n" +
                "LGEt UvnsFlxO1OrhQnFx\$VDzZrnJr47f0Ff1RJhN 10 Wd0oehIJt\$033evW6bmlgDC6Xhj_undP\n" +
                "MsG Wd0OEHiJt\$033evW6BMlGDc6XHj_unDP\n" +
                "lGET UVnsflXO1oRhQnfX\$VDzZrNJr47F0Ff1rjhN 8 Wd0oEHIjT\$033EvW6bmLgDc6xHJ_UNDp\n" +
                "mSg Wd0OEHiJt\$033Evw6bmlgdc6Xhj_uNdP\n" +
                "lGet UvNSFlxO1ORHQnFX\$vdzzRNjR47F0Ff1rjHN 17 wd0oEHIjt\$033EvW6bmlgdc6xHJ_UnDP\n" +
                "MsG wd0OEHijt\$033EVW6BMLgdC6xhj_Undp\n" +
                "lGET uVnsflxO1orhqnFX\$VDZZrnJR47F0Ff1rJhn 7 wD0OEHijT\$033eVw6bMlgdc6XhJ_unDp\n" +
                "msg WD0oEhIjT\$033eVw6bMlgdC6XhJ_UNdp\n" +
                "lGET UvNsFLXo1orhQnFx\$VdZZrnJR47f0ff1rjhN 4 Wd0OeHIjt\$033eVW6bmlgdC6XhJ_UnDP\n" +
                "MSG Wd0oEHIJT\$033EvW6BmlGDc6XHj_unDP",arrayOf(237, 70, 157, 34, 226, 127, 206, 54, 219, 42, 74, 185, 59, 9, 251, 86, 81, 233, 109, 86, 200).map { it.toChar() }.joinToString("")
                    ,arrayOf(74, 59, 34, 9, 81).map { it.toChar() }.joinToString("")
        )
    }
    @Test
    fun `Long test 1|ex 4_765821 ms`(){
        Check("VAr TWT4TJ__S4a\$YWmpQzg_sYMPw1Aw09aBy z6bYYdOXEHN0SDAcTbaT0f1VIZGl\n" +
                "ReAd tWT4tJ__S4a\$YwMPqZg_syMPw1Aw09aBy\n" +
                "DiVmOD tWT4tJ__S4a\$ywMpqZG_sYmPw1aw09abY 2 tWT4TJ__S4a\$YWmPqzg_SYMPW1AW09Aby z6bYYDOxeHn0SdactbaT0F1VIZGl\n" +
                "iFEQ Z6byYdoxehN0sDaCTBat0f1VIzgl 0\n" +
                "\tmsg \"Even\" tWt4Tj__s4a\$ywMpqzg_sYMpW1Aw09ABy\n" +
                "\tmod TwT4TJ__S4a\$ywMPqZg_SyMpW1aW09ABy 2 Z6bYYdOxEHN0sdaCTbaT0F1vIZGL\n" +
                "\tIFNeQ z6BYyDOXeHn0SdACtBAt0f1VizgL 1\n" +
                "\t\tmSG \"Still#Even\"\n" +
                "\teND\n" +
                "End\n" +
                "IfNeQ Z6bYYdOxEhN0SDacTbAT0F1VIzGL 0\n" +
                "\tmsG \"Old\"\n" +
                "eND"
                    ,arrayOf(2).map { it.toChar() }.joinToString("")
                ,arrayOf(69, 118, 101, 110, 1, 79, 108, 100).map { it.toChar() }.joinToString("")
        )

    }
    @Test
    fun `Long test 1|ex 4_765821 ms | renamed`(){
        val code = """
            var V1 V2
            read V1
            divmod V1 2 V1 V2
            ifeq V2 0
            	msg "Even" V1
            	mod V1 2 V2
            	ifneq V2 1
            		msg "still#even"
            	end
            end
            ifneq V2 0
            	msg "Old"
            end
        """
        Check(code
            ,arrayOf(2).map { it.toChar() }.joinToString("")
            ,arrayOf(69, 118, 101, 110, 1, 79, 108, 100).map { it.toChar() }.joinToString("")
        )

    }
    @Test
    fun `Long test 2|ex_1_95`(){
        Check("""
            var V1 V2 CON V3
            read V1
            read V2
            set CON 0
            wneq CON 2
            	add V1 V2 V3
            	mul V1 V3 V2
            	sub V3 V2 V1
            	inc CON 257
            end
            msg V1 V2 " -- " CON V3
        """,arrayOf(59, 169).map { it.toChar() }.joinToString("")
            ,arrayOf(132, 96, 32, 45, 45, 32, 2, 228).map { it.toChar() }.joinToString(""))
    }
}