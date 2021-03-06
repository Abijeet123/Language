package Parser

import Scanner.*

abstract class stmt {
  interface Visitor<R> {
   fun visitBlockstmt(stmt : Block) : R?
   fun visitExpressionstmt(stmt : Expression) : R?
   fun visitFUNstmt(stmt : FUN) : R?
   fun visitIfstmt(stmt : If) : R?
   fun visitPrintstmt(stmt : Print) : R?
   fun visitVarstmt(stmt : Var) : R?
   fun visitWhilestmt(stmt : While) : R?
  }
 class Block(val stmt: MutableList<stmt?>?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitBlockstmt(this);
    }
  }
 class Expression(val expression: Expr?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitExpressionstmt(this);
    }
  }
 class FUN(var name: Token, var params: MutableList<Token>, var body: MutableList<stmt?>?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitFUNstmt(this);
    }
  }
 class If(val condition: Expr?, val thenBranch: stmt?, val elseBranch: stmt?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitIfstmt(this);
    }
  }
 class Print(val expression: Expr?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitPrintstmt(this);
    }
  }
 class Var(val name: Token, val initializer: Expr?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitVarstmt(this);
    }
  }
 class While(val condition: Expr?, val body: stmt?) :stmt(){

    override fun <R>accept(visitor : Visitor<R>):R? {
      return visitor.visitWhilestmt(this);
    }
  }

abstract  fun <R> accept(visitor: Visitor<R>) : R?
}
