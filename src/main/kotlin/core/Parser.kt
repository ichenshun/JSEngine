package core


class Parser(private val lexer: Lexer) {
    fun parse(): JsNode {
        return parseProgram()
    }

    // 定义一个函数，解析JavaScript语法树
    private fun parseProgram(): JsNode {
        val elements = mutableListOf<JsSourceElement>()
        while (lexer.currentToken.type != TokenType.EOF) {
            elements.add(parseSourceElement())
        }
        return JsProgram(elements)
    }

    private fun parseSourceElement(): JsSourceElement {
        return if (lexer.currentToken.type == TokenType.KEYWORD_FUNCTION) {
            parseFunctionDeclaration()
        } else {
            parseStatement()
        }
    }

    private fun parseFunctionDeclaration(): JsFunctionDeclaration {
        // 解析函数名
        requireToken(TokenType.KEYWORD_FUNCTION)
        val functionName = requireToken(TokenType.IDENTIFIER)

        // 解析参数列表
        requireToken(TokenType.OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.CLOSE_PAREN)

        // 解析函数体
        requireToken(TokenType.OPEN_BRACE)
        val body = parseFunctionBody()
        requireToken(TokenType.CLOSE_BRACE)

        return JsFunctionDeclaration(functionName, parameters, body)
    }

    private fun parseFormalParameterList(): List<Token> {
        val parameters = mutableListOf<Token>()
        parameters.add(requireToken(TokenType.IDENTIFIER))
        while (lexer.currentToken.type != TokenType.CLOSE_PAREN) {
            requireToken(TokenType.COMMA)
            parameters.add(requireToken(TokenType.IDENTIFIER))
        }
        return parameters
    }

    private fun parseFunctionBody(): List<JsSourceElement> {
        val sourceElements = mutableListOf<JsSourceElement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            sourceElements.add(parseSourceElement())
        }
        return sourceElements
    }

    private fun parseBlockStatement(): JsStatement {
        requireToken(TokenType.OPEN_BRACE)
        val statements = mutableListOf<JsStatement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement())
        }
        requireToken(TokenType.CLOSE_BRACE)
        return JsBlock(statements)
    }

    private fun parseIfStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseWhileStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseReturnStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseExpressionStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseVariableStatement(): JsStatement {
        requireToken(TokenType.KEYWORD_VAR)
        val variableDeclarations  = mutableListOf<JsVariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (lexer.currentToken.type != TokenType.EOF
            && lexer.currentToken.type != TokenType.SEMICOLON
            && lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            requireToken(TokenType.COMMA)
            variableDeclarations.add(parseVariableDeclaration())
        }
        return JsVariableStatement(variableDeclarations)
    }

    private fun parseVariableDeclaration(): JsVariableDeclaration {
        val variableName = requireToken(TokenType.IDENTIFIER)
        var initializer: JsSingleExpression? = null
        if (lexer.currentToken.type == TokenType.EQUAL) {
            eatToken(TokenType.EQUAL)
            initializer = parseSingleExpression()
        }
        return JsVariableDeclaration(variableName, initializer)
    }

    private fun parseSingleExpression(): JsSingleExpression {
        when (lexer.currentToken.type) {
            TokenType.KEYWORD_FUNCTION -> return parseFunctionExpression()
            TokenType.KEYWORD_NEW -> return parseNewExpression()
            TokenType.KEYWORD_DELETE -> return parseDeleteExpression()
        }
    }

    private fun parseFunctionExpression(): JsFunctionExpression {
        requireToken(TokenType.KEYWORD_FUNCTION)
        var functionName: Token? = null
        if (lexer.currentToken.type == TokenType.IDENTIFIER) {
            functionName = lexer.currentToken
        }

        // 解析参数列表
        requireToken(TokenType.OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.CLOSE_PAREN)

        // 解析函数体
        requireToken(TokenType.OPEN_BRACE)
        val body = parseFunctionBody()
        requireToken(TokenType.CLOSE_BRACE)

        return JsFunctionExpression(functionName, parameters, body)
    }

    private fun parseNewExpression(): JsNewExpression {
        requireToken(TokenType.KEYWORD_NEW)
        val expression = parseSingleExpression()
        val arguments = parseArguments()
        return JsNewExpression(expression, arguments)
    }

    private fun parseDeleteExpression(): JsDeleteExpression {
        TODO("Not yet implemented")
    }

    private fun parseArguments(): List<JsNode> {
        TODO("Not yet implemented")
    }

    private fun parseExpressionSequence(): List<JsSingleExpression> {
        TODO("Not yet implemented")
    }

    private fun parseStatement(): JsStatement {
        return when (lexer.currentToken.type) {
            TokenType.OPEN_BRACE -> parseBlockStatement()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.KEYWORD_IF -> parseIfStatement()
            TokenType.KEYWORD_WHILE -> parseWhileStatement()
            TokenType.KEYWORD_RETURN -> parseReturnStatement()
            TokenType.IDENTIFIER -> parseExpressionStatement()
            else -> throw IllegalStateException("Unexpected token type ${lexer.currentToken.type}")
        }
    }

    private fun eatToken(tokenType: TokenType) {
        if (lexer.currentToken.type == tokenType) {
            lexer.nextToken()
        }
    }

    private fun requireToken(tokenType: TokenType): Token {
        val currentToken = lexer.currentToken
        if (currentToken.type == tokenType) {
            lexer.nextToken()
            return currentToken
        } else {
            throw IllegalStateException("Expected token type $tokenType but got ${lexer.currentToken.type}")
        }
    }
}