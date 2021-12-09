package Parser

import Scanner.*

abstract class Expr {
  interface Visitor<R> {
   fun visitBinaryExpr(expr : Binary) : R?
   fun visitCallExpr(expr : Call) : R?
   fun visitGroupingExpr(expr : Grouping) : R?
   fun visitLiteralExpr(expr : Literal) : R?
   fun visitUnaryExpr(expr : Unary) : R?
   fun visitLogicalExpr(expr : Logical) : R?
   fun visitVariableExpr(expr : Variable) : R?
   fun visitAssignExpr(expr : Assign) : R?
  }
 class Binary(val left: Expr?, val operator: Token, val right: Expr?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitBinaryExpr(this);
    }
  }
 class Call(var calle: Expr?, var paren: Token, var arguments: MutableList<Expr>) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitCallExpr(this);
    }
  }
 class Grouping(val expression: Expr?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitGroupingExpr(this);
    }
  }
 class Literal(val value : Any?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitLiteralExpr(this);
    }
  }
 class Unary(val operator: Token, val right: Expr?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitUnaryExpr(this);
    }
  }
 class Logical(val left: Expr?, val operator: Token, val right: Expr?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitLogicalExpr(this);
    }
  }
 class Variable(val name : Token) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitVariableExpr(this);
    }
  }
 class Assign(val name: Token, val value: Expr?) :Expr(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitAssignExpr(this);
    }
  }

abstract  fun <R> accept(visitor: Visitor<R>) : R?
}
