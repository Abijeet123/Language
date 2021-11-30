package scanner

class Token(val type : TokenType, val lexeme : String, var literal : Any?, var line: Int){
    override fun toString() : String {
        val ret : String = "$type $lexeme $literal"
        return ret
    }
}