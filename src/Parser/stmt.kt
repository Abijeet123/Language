package Parser

import Parser.Expr

abstract class stmt {
  interface Visitor<R> {
   fun visitExpressionStmt(stmt: Expression) : R?
   fun visitPrintStmt(stmt: Print) : R?
  }
 class Expression(val expression : Expr) : stmt() {

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitExpressionStmt(this);
    }
  }
 class Print(val expression : Expr) : stmt() {

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitPrintStmt(this);
    }
  }

  abstract fun <R> accept(visitor: Visitor<R>) : R?
}
