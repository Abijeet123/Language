package Scanner

import interpreter.error


class Scanner(val source : String) {
    var tokens : MutableList<Token> = mutableListOf()
    var start : Int = 0
    var current : Int = 0
    var line : Int = 1

    val keywords : MutableMap<String, TokenType> = mutableMapOf(
        "and" to TokenType.AND,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "for" to TokenType.FOR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "nil" to TokenType.NIL,
        "or" to TokenType.OR,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "var" to TokenType.VAR,
        "val" to TokenType.VAL,
        "while" to TokenType.WHILE
    )


    fun isAtEnd() : Boolean{
        return  current >= source.length
    }

    fun advance(): Char{
        return source[current++]
    }

    fun addToken(type : TokenType){
        addToken(type,null)
    }

    fun addToken(type : TokenType, literal : Any?){
        var text = source.substring(start,current)
        tokens.add(Token(type, text, literal, line))
    }

    fun match(expected : Char) : Boolean {
        if (isAtEnd()) return  false
        if (source[current] != expected) return  false
        current++
        return true
    }

    fun string(){
        while (peek() != '"' && !isAtEnd()){
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()){
            error(line, "Unterminated string")
            return
        }
        advance()
        var value : String = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING,value)
    }

    fun peek() : Char?{
        if (isAtEnd()) return null
        return source[current]
    }

    fun isDigit(c : Char): Boolean {
        return c >= '0' && c <= '9'
    }

    fun peekNext() : Char?{
        if (current + 1 >= source.length) return null
        return source[current + 1]
    }

    fun number() {
        while (peek()?.let { isDigit(it) } == true) advance()

        if (peek() == '.' && peekNext()?.let { isDigit(it) } == true){
            advance()

            while (peek()?.let { isDigit(it) } == true) advance()
        }
        addToken(TokenType.NUMBER,source.substring(start,current).toDouble())
    }

    fun isAlpha(c : Char) : Boolean{
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c =='_'
    }

    fun identifier(){
        while(peek()?.let { isAlphaNumeric(it) } == true) advance()
        val text : String = source.substring(start,current)
        keywords.get(text)?.let { addToken(it)  }
        if (keywords.get(text) == null) addToken(TokenType.IDENTIFIER)
    }

    fun isAlphaNumeric(c : Char) : Boolean{
        return isDigit(c) || isAlpha(c)
    }



    fun scanToken() {
        val c : Char = advance()
        when (c){
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            '%' -> addToken(TokenType.MOD)
            ';' -> addToken(TokenType.SEMI_COLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if(match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if(match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if(match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if(match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> if (match('/')){
                while (peek() != '\n' && !isAtEnd()) advance()
            }else{
                addToken(TokenType.SLASH)
            }
            '\n' -> line++
            ' ' -> {}
            '\r' -> {}
            '\t' -> {}
            '"' -> string()
            else -> if (isDigit(c)) number() else if(isAlpha(c)){ identifier()} else error(
                line,
                "Unexpected character."
            )
         }
    }

    fun scanTokens() {
        while (!isAtEnd()){
            start = current;
            scanToken()
        }
        tokens.add(Token(TokenType.EOF,"",null,line))
    }


}




