package interpreter

import Scanner.Token
import Scanner.TokenType
import javax.management.RuntimeErrorException

class enviornment(var enclosing : enviornment? = null) {
//    var enclosing : enviornment = enviornment()



    var values : MutableMap<String,Any?> = mutableMapOf()

    fun define(name: Token, value: Any?){
        values.put(name.lexeme,value)
    }
    fun gett(name : Token) : Any?{
        if (values.containsKey(name.lexeme)) return values.get(name.lexeme)

        if(this.enclosing != null) return this.enclosing!!.gett(name)

         throw RuntimeErrorException(null,"Undefined variable ' ${name.lexeme} .")
    }

    fun assign(name  : Token, value : Any?){
        if (values.containsKey(name.lexeme)){
            values.put(name.lexeme,value)
            return
        }
        if (this.enclosing != null){
            this.enclosing!!.assign(name,value)
            return
        }
        throw RuntimeErrorException(null, "Undefined variable ' ${name.lexeme}")
    }


}