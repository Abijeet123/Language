package interpreter
import Parser.*
import Scanner.Token
import Scanner.TokenType
import Parser.stmt
import java.lang.Exception








class interpreter : Expr.Visitor<Any>, stmt.Visitor<Any>{

    override fun visitExpressionStmt(stmt: stmt.Expression): Any? {
        val value = evaluate(stmt.expression)
        return value
    }

    override fun visitPrintStmt(stmt: stmt.Print): Any? {
        val value = evaluate(stmt.expression)
//        println(value.toString())
        return value.toString();
    }



    override fun visitLiteralExpr(expr: Expr.Literal?): Any? {
        if (expr != null) {
            return expr.value
        }
        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any {
        return evaluate(expr.expression)!!
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        var right : Any? = evaluate(expr.right)
        when(expr.operator.type){
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator,right)
                return -right.toString().toDouble()
            }
            TokenType.BANG -> !isTruthy(right)
        }
        return null
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)
        when(expr.operator.type){
            TokenType.MINUS -> {    checkNumberOperand(expr.operator,left,right)
                                    return left.toString().toDouble() - right.toString().toDouble()
                                }
            TokenType.PLUS -> { if (left is Double && right is Double)
                return left + right
                if (left is String && right is String) return left + right
            }
            TokenType.SLASH -> {checkNumberOperand(expr.operator, left,right)
                return left.toString().toDouble() / right.toString().toDouble()
            }
            TokenType.STAR -> {
                checkNumberOperand(expr.operator,left,right)
                if (left is Double && right is Double)
                return left * right
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperand(expr.operator,left,right)
                if (left is Double && right is Double) // check later
                    return left >= right
            }
            TokenType.GREATER -> {
                checkNumberOperand(expr.operator,left,right)
                if (left is Double && right is Double) // check later
                return left > right
            }
            TokenType.LESS -> {
                checkNumberOperand(expr.operator,left,right)
                if (left is Double && right is Double) // check later
                    return left < right
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperand(expr.operator,left,right)
                if ( left is Double && right is Double) // check later
                    return left <= right
            }
            TokenType.BANG_EQUAL -> return !isEqual(left,right)
            TokenType.EQUAL_EQUAL -> return isEqual(left,right)
        }
        return null
    }

    fun isEqual(left: Any?, right: Any?) : Boolean{
        if(left == null && right == null){
            return true
        }
        if (left == null) return false
        return left.equals(right)
    }
    fun isTruthy(obj : Any?) : Boolean{
        if (obj == null) return false
        if (obj is Boolean) return obj
        return false
    }
    fun evaluate(expr : Expr) : Any?{
        return expr.accept(this)
    }

    fun checkNumberOperand(operator : Token, left: Any?, right : Any?){
        if (left is Double && right is Double) return
        throw Exception("Operand must be a number")
    }
    fun checkNumberOperand(operator : Token, right : Any?){
        if (right is Double) return
        throw Exception("Operand must be a number.")
    }
    fun execute(statement  : stmt) : Any?{
        return statement.accept(this)
    }
    fun interpret(statements : MutableList<stmt>) {
            for (statement in statements){
                val value = execute(statement)
                println(value.toString())
            }
    }

}




