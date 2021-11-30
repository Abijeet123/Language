package scanner

enum class TokenType{
    //single character token
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, DOT, COMMA, PLUS, MINUS, SEMI_COLON, SLASH, STAR,

    //One or two character token
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    //Literals
    IDENTIFIER, STRING, NUMBER

    //Keywords
    , AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, VAL, WHILE,

    EOF
}