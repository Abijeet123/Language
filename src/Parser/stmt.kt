package Parser

import Scanner.*

abstract class stmt {
  interface Visitor<R> {
   fun visitBlockstmt(stmt : Block) : R?
   fun visitExpressionstmt(stmt : Expression) : R?
   fun visitPrintstmt(stmt : Print) : R?
   fun visitVarstmt(stmt : Var) : R?
  }
 class Block(val stmt: MutableList<stmt?>?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitBlockstmt(this);
    }
  }
 class Expression(val expression : Expr) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitExpressionstmt(this);
    }
  }
 class Print(val expression : Expr) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitPrintstmt(this);
    }
  }
 class Var(val name: Token, val initializer: Expr?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitVarstmt(this);
    }
  }

abstract  fun <R> accept(visitor: Visitor<R>) : R?
}
