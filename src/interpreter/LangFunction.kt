package interpreter

import Parser.stmt
import java.lang.reflect.GenericDeclaration

class LangFunction(stmt: stmt.FUN) : LangCallable {
    final  var declaration : stmt.FUN = stmt
//    fun LangFunction(declaration : stmt.FUN){
//        this.declaration = declaration
//    }
//    fun LangFunction(declaration: stmt.FUN){
//        this.declaration = declaration
//    }

    override fun call(interpreterr: interpreter?, arguments: List<Any?>?): Any? {
        for (i in 0 until declaration.params.size){
            if (interpreterr != null) {
                interpreterr.envv.define(declaration.params[i], arguments?.get(i))
            }
        }
        if (interpreterr != null) {
            interpreterr.executeBlock(declaration.body,interpreterr.envv)
        }
        return null
    }

    override fun arity(): Int {
        return declaration.params.size
    }
    override fun toString(): String {
        return "<fn " + declaration.name.lexeme + ">"
    }
}

/*
package interpreter

import Parser.stmt
import java.lang.reflect.GenericDeclaration

class LangFunction(stmt: stmt.FUN) : LangCallable {
    final lateinit var declaration : stmt.FUN
    fun LangFunction(declaration : stmt.FUN){
        this.declaration = declaration
    }

    override fun call(interpreterr: interpreter?, arguments: List<Any?>?): Any? {
        for (i in 0 until declaration.params.size){
            if (interpreterr != null) {
                interpreterr.envv.define(declaration.params[i], arguments?.get(i))
            }
        }
        if (interpreterr != null) {
            interpreterr.executeBlock(declaration.body,interpreterr.envv)
        }
        return null
    }

    override fun arity(): Int {
        return declaration.params.size
    }
    override fun toString(): String {
        return "<fn " + declaration.name.lexeme + ">"
    }
}
 */