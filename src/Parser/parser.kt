package Parser

import Scanner.*
//import scanner.report
import java.lang.RuntimeException
import Parser.stmt
import interpreter.report

//import scanner.report

//import Scanner.report


class parser(val tokens : List<Token>) {
    class ParseError : RuntimeException(){

    }
    var current = 0

    fun expression() : Expr {
        return equality()
    }
    fun equality() : Expr {
        var expr = comparision()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            val operator = previous()
            val right = comparision()
            expr =  Expr.Binary(expr,operator,right)
        }
        return expr
    }
    fun comparision() : Expr{
        var expr = term()

        while (match(
                TokenType.GREATER,TokenType.GREATER_EQUAL,
                TokenType.LESS,TokenType.LESS_EQUAL
            )){
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun term() : Expr{
        var expr = factor()

        while (match(TokenType.MINUS,TokenType.PLUS)){
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun factor() : Expr{
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)){
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return  expr
    }
    fun primary() : Expr{
        if (match(TokenType.FALSE)) return  Expr.Literal(false)
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.NIL)) return Expr.Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)){
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.LEFT_PAREN)){
            var expr = expression()
            consume(TokenType.RIGHT_PAREN,"Expect ')' after expression.")
            return Expr.Grouping(expr)
        }
        throw error(peek(),"Expect expression")
    }
    fun unary() : Expr{
        if (match(TokenType.BANG,TokenType.MINUS)){
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator,right)
        }
        return primary()
    }

    fun consume(token : TokenType, message : String) : Token{
        if (check(token)) return advance()
        throw error(peek(),message)
    }
    fun error(token : Token, message: String) : ParseError{
        if (token.type == TokenType.EOF){
           report(token.line ,"at end",message)
        }
        report(token.line,"at ${token.lexeme} ' ",message)
        return ParseError()
    }
    fun match(vararg types : TokenType) : Boolean{
        for (type in types){
            if (check(type)){
                advance()
                return true;
            }
        }
        return false
    }
    fun check(type : TokenType) : Boolean{
        if (isAtEnd()) return false
        return peek().type == type
    }
    fun advance() : Token{
        if (!isAtEnd()) current++
        return previous()
    }
    fun isAtEnd() : Boolean{
        return peek().type == TokenType.EOF
    }
    fun peek() : Token{
        return tokens.get(current)
    }

    fun previous() : Token{
        return tokens.get(current - 1)
    }


    fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type === TokenType.SEMI_COLON) return
            when (peek().type) {
                TokenType.CLASS, TokenType.FUN, TokenType.VAR, TokenType.FOR, TokenType.IF, TokenType.WHILE, TokenType.PRINT, TokenType.RETURN -> return
            }
            advance()
        }
    }
    fun statement() : stmt? {
        if (match(TokenType.PRINT)) return printStatement()
        return expressionStatement()
    }

    fun printStatement() : stmt{
        val value = expression()
        consume(TokenType.SEMI_COLON,"Expect ';' after expression.")
        return stmt.Print(value)
    }

    fun expressionStatement(): stmt? {
        val expr = expression()
        consume(TokenType.SEMI_COLON, "Expect ';' after expression.")
        return stmt.Expression(expr)
    }
    fun parse() : MutableList<stmt>? {
        try {
//            return expression()
            val statements : MutableList<stmt> = mutableListOf()
            while (!isAtEnd()){
                statement()?.let { statements.add(it) }
                return statements
            }
        }catch (error : ParseError){
            return null
        }
        return null
    }


}


