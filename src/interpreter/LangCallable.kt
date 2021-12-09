package interpreter

interface LangCallable {
    fun call(interpreterr: interpreter?, arguments: List<Any?>?): Any?
    fun arity() : Int
}