package Parser

import Parser.Expr.Assign
import Scanner.*
//import scanner.report
import java.lang.RuntimeException
import interpreter.*
import java.util.ArrayList
import java.util.Arrays


//import scanner.report

//import Scanner.report


class parser(val tokens : List<Token>) {
    class ParseError : RuntimeException(){

    }
    var current = 0

    fun assignment(): Expr? {
        val expr = or()
        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()
            if (expr is Expr.Variable) {
                val name = expr.name
                return Assign(name, value)
            }
            error(equals, "Invalid assignment target.")
        }
        return expr
    }

    fun or() : Expr?{
        var expr = and()

        while (match(TokenType.OR)){
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    fun and() : Expr?{
        var expr = equality()

        while (match(TokenType.AND)){
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr,operator,right)
        }
        return expr
    }

    fun expression() : Expr? {
        return assignment()
    }
    fun equality() : Expr? {
        var expr = comparision()

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            val operator = previous()
            val right = comparision()
            expr =  Expr.Binary(expr,operator,right)
        }
        return expr
    }
    fun comparision() : Expr?{
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

    fun term() : Expr?{
        var expr = factor()

        while (match(TokenType.MINUS,TokenType.PLUS,TokenType.MOD)){
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    fun factor() : Expr?{
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
        if (match(TokenType.IDENTIFIER)) return Expr.Variable(previous())
        throw error(peek(),"Expect expression")
    }
    fun unary() : Expr?{
        if (match(TokenType.BANG,TokenType.MINUS)){
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator,right)
        }
        return call()
    }

    fun call(): Expr? {
        var expr: Expr? = primary()
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else {
                break
            }
        }
        return expr
    }

    fun finishCall(callee: Expr?): Expr? {
        val arguments: MutableList<Expr> = ArrayList()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                expression()?.let { arguments.add(it) }
            } while (match(TokenType.COMMA))
        }
        val paren = consume(
            TokenType.RIGHT_PAREN,
            "Expect ')' after arguments."
        )
        return Expr.Call(callee, paren, arguments)
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
        if (match(TokenType.WHILE)) return whileStatement()
        if (match(TokenType.FOR)) return forStatement()
        if (match(TokenType.IF)) return ifStatement()
        if (match(TokenType.PRINT)) return printStatement()
        if (match(TokenType.LEFT_BRACE))
            return stmt.Block(block())
        return expressionStatement()
//        if (match(TokenType.LEFT_BRACE))
//            return stmt.Block(block())
    }

    fun whileStatement() : stmt?{
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'while'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN,"Expect ')' after condition.")
        val body : stmt? = statement()
        return stmt.While(condition,body)
    }
    fun forStatement() : stmt? {
        consume(TokenType.LEFT_PAREN,"Expect '(' after for.")
        var initializer : stmt?;
        if (match(TokenType.SEMI_COLON)){
            initializer = null
        }else if (match(TokenType.VAR)){
            initializer = varDeclaration()
        }else{
            initializer = expressionStatement()
        }

        var condition :Expr? = null
        if (!check(TokenType.SEMI_COLON)){
            condition = expression()
        }
        consume(TokenType.SEMI_COLON,"Expect ';' after loop condition")

        var increment : Expr? = null
        if (!check(TokenType.RIGHT_PAREN)){
            increment = expression()
        }
        consume(TokenType.RIGHT_PAREN,"Exprect ')' after for clauses.")

        var body : stmt ?= statement()

        if (increment != null) {
            body = stmt.Block(
                Arrays.asList(
                    body,
                    stmt.Expression(increment)
                )
            )
        }

        if (condition == null) condition = Expr.Literal(true)
        body = stmt.While(condition,body)

        if (initializer != null)
            body = stmt.Block(Arrays.asList(initializer,body))

        return body
    }
    fun ifStatement() : stmt{
        consume(TokenType.LEFT_PAREN,"Expect '(' after if.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if.")
        val thenBranch = statement()
        var elseBranch : stmt? = null
        if (match(TokenType.ELSE)){
            elseBranch = statement()
        }
        return stmt.If(condition,thenBranch,elseBranch)
    }

    fun block(): MutableList<stmt?>? {
        val statements: MutableList<stmt?> = ArrayList<stmt?>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
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

    fun varDeclaration(): stmt? {
        val name: Token = consume(TokenType.IDENTIFIER, "Expect variable name.")
        var initializer: Expr? = null
        if (match(TokenType.EQUAL)) {
            initializer = expression()
        }
        consume(TokenType.SEMI_COLON, "Expect ';' after variable declaration.")
        return stmt.Var(name, initializer)
    }
    fun declaration() : stmt? {

            if (match(TokenType.VAR)) {
                return varDeclaration()
//                return statement()
            }
            if (match(TokenType.FUN)) return function("function")
        return statement()
//            synchronize() to do after doing error reporting
//            return null

    }

    fun function(kind : String) : stmt.FUN{
        val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
        consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        var parameters : MutableList<Token> = mutableListOf()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                    consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before $kind body.")
        val body: MutableList<stmt?>? = block()
        return stmt.FUN(name, parameters, body)
    }
    fun parse() : MutableList<stmt>? {

//            return expression()
            val statements : MutableList<stmt> = mutableListOf()
            while (!isAtEnd()){
                declaration()?.let { statements.add(it) }
         //       return statements
            }
        return statements
        }
    }






