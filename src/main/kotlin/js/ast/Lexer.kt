package js.ast


class Lexer(private val stream: CharStream) {
    // 操作符索引
    private val operatorIndexMap = mapOf(
        '[' to TokenType.OPERATOR_OPEN_BRACKET,
        ']' to TokenType.OPERATOR_CLOSE_BRACKET,
        '(' to TokenType.OPERATOR_OPEN_PAREN,
        ')' to TokenType.OPERATOR_CLOSE_PAREN,
        '{' to TokenType.OPERATOR_OPEN_BRACE,
        '}' to TokenType.OPERATOR_CLOSE_BRACE,
        ';' to TokenType.OPERATOR_SEMICOLON,
        ',' to TokenType.OPERATOR_COMMA,
        '=' to mapOf(
            null to  TokenType.OPERATOR_ASSIGN,
            '=' to mapOf(
                null to TokenType.OPERATOR_EQUAL,
                '=' to TokenType.OPERATOR_IDENTITY_EQUAL
            ),
            '>' to TokenType.OPERATOR_ARROW,
        ),
        '?' to mapOf(
            null to  TokenType.OPERATOR_QUESTION_MARK,
            '.' to TokenType.OPERATOR_QUESTION_MARK_DOT,
            '?' to mapOf(
                null to TokenType.OPERATOR_NULL_COALESCE,
                '=' to TokenType.OPERATOR_NULL_COALESCE_ASSIGN
            ),
        ),
        ':' to TokenType.OPERATOR_COLON,
        '.' to  mapOf(
            null to  TokenType.OPERATOR_DOT,
            '.' to mapOf(
                '.' to TokenType.OPERATOR_ELLIPSIS
            )
        ),
        '+' to mapOf(
            null to TokenType.OPERATOR_PLUS,
            '+' to TokenType.OPERATOR_PLUS_PLUS,
            '=' to TokenType.OPERATOR_PLUS_ASSIGN
        ),
        '-' to mapOf(
            null to TokenType.OPERATOR_MINUS,
            '-' to TokenType.OPERATOR_MINUS_MINUS,
            '=' to TokenType.OPERATOR_MINUS_ASSIGN,
        ),
        '~' to TokenType.OPERATOR_BIT_NOT,
        '!' to mapOf(
            null to TokenType.OPERATOR_NOT,
            '=' to mapOf(
                null to TokenType.OPERATOR_NOT_EQUAL,
                '=' to TokenType.OPERATOR_IDENTITY_NOT_EQUAL
            ),
        ),
        '*' to mapOf(
            null to TokenType.OPERATOR_MULTIPLY,
            '=' to TokenType.OPERATOR_MULTIPLY_ASSIGN,
            '*' to mapOf(
                null to TokenType.OPERATOR_POWER,
                '=' to TokenType.OPERATOR_POWER_ASSIGN
            )
        ),
        '/' to mapOf(
            null to TokenType.OPERATOR_DIVIDE,
            '=' to TokenType.OPERATOR_DIVIDE_ASSIGN,
        ),
        '%' to mapOf(
            null to TokenType.OPERATOR_MODULUS,
            '=' to TokenType.OPERATOR_MODULUS_ASSIGN,
        ),
        '#' to TokenType.OPERATOR_HASHTAG,
        '>' to mapOf(
            null to TokenType.OPERATOR_MORE_THAN,
            '>' to mapOf(
                null to TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC,
                '>' to mapOf(
                    null to TokenType.OPERATOR_RIGHT_SHIFT_LOGICAL,
                    '=' to TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC_ASSIGN
                ),
                '=' to TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC_ASSIGN
            ),
            '=' to TokenType.OPERATOR_MORE_THAN_EQUALS,
        ),
        '<' to mapOf(
            null to TokenType.OPERATOR_LESS_THAN,
            '<' to mapOf(
                null to TokenType.OPERATOR_LEFT_SHIFT_ARITHMETIC,
                '=' to  TokenType.OPERATOR_LEFT_SHIFT_ARITHMETIC_ASSIGN
            ),
            '=' to TokenType.OPERATOR_LESS_THAN_EQUALS,
        ),
        '&' to mapOf(
            null to TokenType.OPERATOR_BIT_AND,
            '&' to TokenType.OPERATOR_AND,
            '=' to TokenType.OPERATOR_BIT_AND_ASSIGN
        ),
        '^' to mapOf(
            null to TokenType.OPERATOR_BIT_XOR,
            '=' to TokenType.OPERATOR_BIT_XOR_ASSIGN
        ),
        '|' to mapOf(
            null to TokenType.OPERATOR_BIT_OR,
            '|' to TokenType.OPERATOR_OR,
            '=' to TokenType.OPERATOR_BIT_OR_ASSIGN
        )
    )

    private val keywords = mapOf(
        "break" to TokenType.KEYWORD_BREAK,
        "do" to TokenType.KEYWORD_DO,
        "instanceof" to TokenType.KEYWORD_INSTANCEOF,
        "typeof" to TokenType.KEYWORD_TYPEOF,
        "case" to TokenType.KEYWORD_CASE,
        "else" to TokenType.KEYWORD_ELSE,
        "new" to TokenType.KEYWORD_NEW,
        "var" to TokenType.KEYWORD_VAR,
        "catch" to TokenType.KEYWORD_CATCH,
        "finally" to TokenType.KEYWORD_FINALLY,
        "return" to TokenType.KEYWORD_RETURN,
        "void" to  TokenType.KEYWORD_VOID,
        "continue" to TokenType.KEYWORD_CONTINUE,
        "for" to TokenType.KEYWORD_FOR,
        "switch" to TokenType.KEYWORD_SWITCH,
        "while" to TokenType.KEYWORD_WHILE,
        "debugger" to TokenType.KEYWORD_DEBUGGER,
        "function" to TokenType.KEYWORD_FUNCTION,
        "this" to TokenType.KEYWORD_THIS,
        "with" to TokenType.KEYWORD_WITH,
        "default" to TokenType.KEYWORD_DEFAULT,
        "if" to TokenType.KEYWORD_IF,
        "throw" to TokenType.KEYWORD_THROW,
        "delete" to TokenType.KEYWORD_DELETE,
        "in" to TokenType.KEYWORD_IN,
        "try" to TokenType.KEYWORD_TRY,
        "as" to TokenType.KEYWORD_AS,
        "from" to TokenType.KEYWORD_FROM,
        "of" to TokenType.KEYWORD_OF,
        "class" to TokenType.KEYWORD_CLASS,
        "enum" to TokenType.KEYWORD_ENUM,
        "extends" to TokenType.KEYWORD_EXTENDS,
        "super" to TokenType.KEYWORD_SUPER,
        "const" to TokenType.KEYWORD_CONST,
        "export" to TokenType.KEYWORD_EXPORT,
        "import" to TokenType.KEYWORD_IMPORT,
        "async" to TokenType.KEYWORD_ASYNC,
        "await" to TokenType.KEYWORD_AWAIT,
        "yield" to TokenType.KEYWORD_YIELD,
        "implements" to TokenType.KEYWORD_IMPLEMENTS,
        "let" to TokenType.KEYWORD_LET,
        "private" to TokenType.KEYWORD_PRIVATE,
        "public" to TokenType.KEYWORD_PUBLIC,
        "interface" to TokenType.KEYWORD_INTERFACE,
        "package" to TokenType.KEYWORD_PACKAGE,
        "protected" to TokenType.KEYWORD_PROTECTED,
        "static" to TokenType.KEYWORD_STATIC,
    )
    var currentToken: Token = nextToken()

    fun nextToken(): Token {
        val char = stream.currentChar
        if (char == null) {
            currentToken = Token(TokenType.EOF, "")
        } else if (char.isLetter()) {
            currentToken = parseIdentifierToken()
        } else if (char.isOpcode()) {
            currentToken = parseOperatorToken()
        } else if (char.isStringLeader()) {
            currentToken = parseStringLiteralToken(char)
        } else if (char.isDigit()) {
            currentToken = parseNumberLiteralToken()
        } else if (char.isWhitespace()) {
            skipWhitespaceChars()
            currentToken = nextToken()
        } else {
            throw IllegalArgumentException("Invalid character: $char")
        }
        return currentToken
    }

    private fun parseNumberLiteralToken(): Token {
        stream.mark()
        while (stream.nextChar()?.isDigit() == true) {}
        return Token(TokenType.NUMBER_LITERAL, stream.substring())
    }

    private fun parseIdentifierToken(): Token {
        stream.mark()
        while (stream.nextChar()?.isLetterOrDigit() == true) {}
        val identifier = stream.substring()
        val type = keywords[identifier] ?: TokenType.IDENTIFIER
        return Token(type, identifier)
    }

    private fun parseOperatorToken(): Token {
        stream.mark()
        var indexMap: Map<*, *> = operatorIndexMap
        while (stream.currentChar?.isOpcode() == true) {
            val type = indexMap[stream.currentChar] ?: break
            if (type is TokenType) {
                stream.nextChar() // Consume the operator character
                return Token(type, stream.substring())
            } else {
                indexMap = type as Map<*, *>
                stream.nextChar() // Consume the operator character
            }
        }
        return Token(indexMap[null] as TokenType, stream.substring())
    }

    private fun parseStringLiteralToken(leadChar: Char): Token {
        // 跳过""字符
        stream.nextChar()
        stream.mark()
        while (stream.currentChar != leadChar) {
            stream.nextChar()
        }
//        {

//            val char = stream.currentChar
//            if (char == leadChar) {
//                return Token(TokenType.STRING_LITERAL, stream.substring())
//            }
//            if (char == null || char == '\n') {
//                // 需要返回词法分析错误
//            }
            // 需要考虑连接行
            // 需要考虑转义字符
//        }
        val substring = stream.substring()
        stream.nextChar()
        return Token(TokenType.STRING_LITERAL, substring)
    }

    private fun skipWhitespaceChars() {
        while (stream.nextChar()?.isWhitespace() == true) {}
    }

    private fun Char.isOpcode(): Boolean {
        return operatorIndexMap.contains(this)
    }

    private fun Char.isStringLeader(): Boolean {
        return this == '"' || this == '\''
    }
}